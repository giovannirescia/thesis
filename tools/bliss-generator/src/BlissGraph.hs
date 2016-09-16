module BlissGraph

where

import Data.List
import HyLo.InputFile 
import HyLo.Formula
import HyLo.Signature.Simple
import qualified Data.Map as M
import Control.Monad.State
import Control.Monad
import System.CPUTime
import Control.Monad.IO.Class
import Text.Printf
import Debug.Trace


type NodeId = Int
type ParentId = Int
type SiblingId = Int
type ModalDepth = Int
type VarId = Int
type Sign = Int

data ClauseType = CL| BCL | NBCL deriving (Show,Eq)     
data ClauseNode = ClauseNode NodeId ClauseType ModalDepth deriving (Show,Eq)

instance Ord ClauseType where
    compare CL CL = EQ
    compare BCL BCL = EQ
    compare NBCL NBCL = EQ
    compare CL BCL = LT
    compare CL NBCL = LT
    compare BCL NBCL = LT
    compare BCL CL = GT
    compare NBCL CL = GT
    compare NBCL BCL = GT

instance Ord ClauseNode where
    compare (ClauseNode nid1 ct1 _) (ClauseNode nid2 ct2 _)  
                        |(ct1 == ct2) = compare nid1 nid2
                        | otherwise = compare ct1 ct2

data LitNode = LitNode NodeId VarId ModalDepth Sign deriving (Show)

type Edge = (NodeId,NodeId)
type VariableNode = M.Map VarId (LitNode,LitNode)
type Literals = M.Map ModalDepth VariableNode
type Clauses = [ClauseNode]
type Edges = [Edge]

data BGraph = BGraph {bgClauses::Clauses, 
                      bgLiterals::Literals,
                      bgEdges:: Edges,
                      bgNumNodes::Int,
                      cpuTime::Double} |
              EmptyBGraph
--              deriving(Show)

instance Show BGraph where
         show EmptyBGraph      = "Empty Graph"
         show (BGraph c l e n t) = printTime t ++
                                   litMap ++
                                   printHeader n (length e) ++ 
                                   (printClauseNodes . sort) c ++
                                   litNodes ++
                                   (printEdges . sort) e
                           where litNodes = fst lits
                                 litMap = "c # <md> <node_id> <lit> \n" ++ (snd lits)
                                 lits = printLiteralNodes l
                                  


data GraphInfo = GraphInfo {nid     :: NodeId,
                            graph   :: BGraph}
                            deriving (Show)

--type BGraphState a = State GraphInfo a
type BGraphState a = StateT GraphInfo IO a
            

--runBuilder :: [Formula n PropSymbol r] -> IO GraphInfo
runBuilder :: [Formula NomSymbol PropSymbol RelSymbol] -> IO GraphInfo
runBuilder f = execStateT (buildGraph' f) (GraphInfo 1 EmptyBGraph)  
                  

--runBuilder :: [Formula n PropSymbol r] -> BGraph
--runBuilder f = graph $ execState (buildGraph f) (GraphInfo 1 EmptyBGraph 0)  

--buildGraph' :: [Formula n PropSymbol r] -> BGraphState NodeId
buildGraph' :: [Formula NomSymbol PropSymbol RelSymbol] -> BGraphState NodeId
buildGraph' f = do start <- lift $ getCPUTime
                   _ <-buildGraph f
                   end <- lift $getCPUTime
                   let diff = (fromIntegral (end - start)) / (10^12)
                   s <- get
                   let (BGraph c l e n _) = (graph s)
                   put s {graph = (BGraph c l e n diff)}
                   return 0
                     
--buildGraph         :: [Formula n PropSymbol r] -> BGraphState NodeId
buildGraph         :: [Formula NomSymbol PropSymbol RelSymbol] -> BGraphState NodeId
buildGraph []      = return 0
buildGraph (f:fs)  = parseFormula f 0 CL 0 >> buildGraph fs


--parseFormula               :: Formula n PropSymbol r -> Int -> ClauseType -> NodeId ->BGraphState NodeId
parseFormula               :: Formula NomSymbol PropSymbol RelSymbol -> Int -> ClauseType -> NodeId ->BGraphState NodeId
parseFormula (f1 :|: f2)                md ct pid = addClause md ct pid >>= 
                                                                 (\x -> sequence $ map (parseEl x) fList) >>
                                                                 return 0
                                               where parseEl i f = parseFormula f md ct i
                                                     fList = listerize f1 f2
                                                     listerize f g = concatMap orList $ [f,g]

parseFormula (Box _ f)                   md _ pid = addBoxClause f BCL pid md 
parseFormula (Neg (Box _ f))             md _ pid = addBoxClause f NBCL pid md
parseFormula (Prop (PropSymbol v))       md _ pid = addLiteral md v 0 pid
parseFormula (Neg (Prop (PropSymbol v))) md _ pid = addLiteral md v 1 pid

--addBoxClause :: Formula n PropSymbol r -> ClauseType -> NodeId -> Int -> BGraphState NodeId
addBoxClause :: Formula NomSymbol PropSymbol RelSymbol -> ClauseType -> NodeId -> Int -> BGraphState NodeId
addBoxClause f ct pid md 
             | pid == 0 = addClause md CL 0 >>= 
                          (\pid1 -> addBoxClause f ct pid1 md) 
             | otherwise = addClause md' ct pid >>=
                            (\x -> sequence $ map (parseEl x) fList) >>
                            return 0
                   where md' = md + 1
                         parseEl i f = parseFormula f md' ct i
                         fList = orList f

orList :: Formula NomSymbol PropSymbol RelSymbol -> [Formula NomSymbol PropSymbol RelSymbol]
orList = reverse . unfoldr next . Just
    where next (Just (a :|: b)) = Just (b, Just a)
          next (Just f)         = Just (f, Nothing)
          next Nothing          = Nothing



addCNode :: BGraph -> NodeId -> ClauseType -> ModalDepth -> BGraph 
addCNode EmptyBGraph nid ct md     = BGraph [(ClauseNode nid ct md)] M.empty [] 1 0
addCNode (BGraph c l e n t) nid ct md  = BGraph ((ClauseNode nid ct md):c) l e (n+1) t

addEdge :: BGraph -> NodeId -> NodeId -> BGraph
addEdge (BGraph c l e n t) n1 n2 = BGraph c l ((n1,n2):e) n t

addLNode :: BGraph -> VarId -> Int -> NodeId -> NodeId -> BGraph
addLNode (BGraph c l e n t) vid md nid1 nid2 = 
         case M.lookup md l of
              Nothing   -> addLNode (BGraph c (M.insert md M.empty l) e n t) vid md nid1 nid2
              Just lits -> let l1 = LitNode nid1 vid md 0
                               l2 = LitNode nid2 vid md 1
                               lits' = M.insert vid (l1,l2) lits
                               f x = Just lits'
                               l' = M.update f md l
                           in  BGraph c l' ((nid1,nid2):e) (n+2) t
                               

litExists :: BGraph -> Int -> VarId -> Maybe (LitNode,LitNode)
litExists EmptyBGraph _ _ = Nothing
litExists (BGraph _ l _ _ _) md vid = do mdLits <- M.lookup md l
                                         M.lookup vid mdLits

addLiteral :: Int -> VarId -> Int -> NodeId -> BGraphState NodeId
addLiteral md vid sgn pid 
           | pid == 0   = addClause md CL 0 >>= 
                           (\pid1 -> addLiteral md vid sgn pid1) 
           | otherwise = do s <- get
                            let g = graph s
                            let ltup = litExists g md vid
                            case ltup of 
                              Nothing -> do let nid1 = nid s
                                            let nid2 = nid1 + 1
                                            let g' = (addLNode g vid md nid1 nid2)
                                            let g'' = (if sgn == 0 
                                                          then (addEdge g' pid nid1)
                                                          else (addEdge g' pid nid2))
                                            put s {nid = (nid2 + 1), graph = g''}
                                            return nid2                                          
                              Just t -> do let LitNode nid1 _ _ _ = fst t 
                                           let LitNode nid2 _ _ _= snd t
                                           let g' = (if sgn == 0 
                                                       then (addEdge g pid nid1)
                                                       else (addEdge g pid nid2))
                                           
                                           put s {graph = g'}                                          
                                           return nid2

                                                     
addClause :: Int -> ClauseType -> NodeId -> BGraphState NodeId
addClause md ct pid = do s <- get
                         let i = nid s
                         let g' = addCNode (graph s) i ct md
                         if pid == 0 
                         then
                             put s { nid = (i +1), graph = g'}
                         else
                             let g'' = addEdge g' pid i 
                             in
                                put s { nid = (i +1), graph = g''}
                         return i
                                  

getNodeId :: BGraphState NodeId
getNodeId = do s <- get
               let i = (nid s)
               put s { nid = (i + 1) } 
               return i



--cmpClause :: ClauseNode -> ClauseNode -> Ordering
--cmpClause ClauseNode nid1 ct1 _ ClauseNode nid2 ct2 _ =
--          |  

printHeader :: Int -> Int -> String
printHeader n e =  "p edge " ++ show n ++ " " ++ show e ++ "\n"

printClauseNodes ::  Clauses -> String
printClauseNodes [] = ""
printClauseNodes (x:xs) = printClause x ++ printClauseNodes xs

printClause :: ClauseNode  -> String
printClause (ClauseNode nid ct _)
                  | ct == CL   = "n " ++ show nid ++ " 1\n"
                  | ct == BCL  = "n " ++ show nid ++ " 2\n"
                  | ct == NBCL = "n " ++ show nid ++ " 3\n"

printLiteralNodes :: Literals -> (String,String)
printLiteralNodes lits = (litstr,litmap)
                  where litstr = foldr (++) "" (map printLiteralTup tups)
                        litmap = foldr (++) "" (map printLiteralMap tups)
                        --tupsStr = map printLiteralTup tups
                        tups = concat $ map M.elems $ M.elems lits
                        

printLiteralTup :: (LitNode,LitNode) -> String
printLiteralTup (l1,l2) = printLiteral l1 ++ printLiteral l2

printLiteralMap :: (LitNode,LitNode) -> String
printLiteralMap (l1,l2) = printLiteralM l1 ++ printLiteralM l2


printLiteral :: LitNode -> String
printLiteral (LitNode nid _ _ _) = "n " ++ show nid ++ " 4\n"

printLiteralM :: LitNode -> String
printLiteralM (LitNode nid vid md sgn) = "c # " ++ show md ++ " " 
                                              ++ show nid ++ " " 
                                              ++ (showV vid sgn) ++ "\n"
                                 where showV v s = (if s == 0 
                                                    then show v
                                                    else "-" ++ show v) 

printEdges :: Edges -> String
printEdges [] = ""
printEdges ((v1,v2):xs) = showEdge v1 v2 ++ printEdges xs
           where showEdge v1 v2 = "e " ++ 
                                  show v1 ++ " " ++ 
                                  show v2 ++ "\n" 
printTime :: Double -> String
printTime t = printf "c Computation time: %0.5f sec\n" (t :: Double)
