module Sy4NCL.BMLGraph where

--import Data.List
import Sy4NCL.Graph
import Sy4NCL.Utils
import Sy4NCL.CommandLine
import HyLo.Formula
import HyLo.Signature.Simple
import Control.Monad.State
import System.CPUTime( getCPUTime )
import Debug.Trace (trace)

buildGraph ::  Params ->
               [Formula NomSymbol PropSymbol RelSymbol] -> 
               GraphState NodeId
buildGraph p fs = do start <- lift $ getCPUTime
                     _ <-buildGraph' (graphType p) fs 
                     end <- lift $getCPUTime
                     let elapsedTime = fromInteger (end - start) / 1000000000000.0
                     s <- get
                     put s {buildTime = elapsedTime}
                     return 0
                   
-- Each element of the input list is a "top clause"
buildGraph' ::  Int ->
                [Formula NomSymbol PropSymbol RelSymbol] -> 
                GraphState NodeId
buildGraph' _ []      = return 0
buildGraph' ly (f:fs)  = addTopClauseNode >>= 
                     (\pid -> parse f 0 ly pid) >> 
                     buildGraph' ly fs
      

parse               :: Formula NomSymbol PropSymbol RelSymbol -> 
                       Depth ->
                       Int -> 
                       NodeId -> 
                       GraphState NodeId
-- Adding support to Universal operator and IBox
parse (IBox _ f)                  d ly pid = addClauseNode 5 (d+1) >>=
                                             (addEdge' pid) >>=
                                             (parse f (d+1) ly)                                           
parse (Neg (IBox _ f))            d ly pid = addClauseNode 6 (d+1) >>=
                                             (addEdge' pid) >>=
                                             (parse f (d+1) ly)
parse (A f)                       d ly pid = addClauseNode 7 (d+1) >>=
                                             (addEdge' pid) >>=
                                             (parse f (d+1) ly)
--
parse (Box _ f)                   d ly pid = addClauseNode 2 (d+1) >>= 
                                          (addEdge' pid) >>= 
                                          (parse f (d+1) ly)
parse (Neg (Box _ f))             d ly pid = addClauseNode 3 (d+1) >>= 
                                          (addEdge' pid) >>= 
                                          (parse f (d+1) ly)
parse (Prop (PropSymbol v))       d ly pid = addLiteralNode v (d*ly) pid
parse (Neg (Prop (PropSymbol v))) d ly pid = addLiteralNode (negate v) (d*ly) pid
parse (f1 :|: f2)                 d ly pid = (sequence $ map parse' fList) >>= 
                                          (\pids -> return $ last pids)
                                               where parse' f = parse f d ly pid
                                                     fList = listerize f1 f2
parse _ _ _ _= error "Formula not in CNF"                                                    



addTopClauseNode :: GraphState NodeId
addTopClauseNode = addClauseNode 1 0

addClauseNode :: Color -> Depth -> GraphState NodeId
addClauseNode c d = do nid <- nextNid
                       _ <- upColorCount c 1
                       s <- get
                       let (g,_) = addNode (graph s) nid d c
                       put s { graph = g  }
                       return nid 

addLiteralNode :: LitId  -> Depth -> NodeId -> GraphState NodeId                       
addLiteralNode lid d pid = 
  do s <- get
     let m = mapping s
     let exist = litNodeExist m d lid
     case exist of
        Nothing   -> do nid1 <- nextNid
                        nid2 <- nextNid
                        _ <- upColorCount 10 2 -- Changed by Giovanni, was 4 instead of 7, didn't do the trick :/
                        s' <- get
                        let (g1,n1) = addNode (graph s) nid1 d 10
                        let (g2,n2) = addNode g1 nid2 d 10
                        let g3 = addEdge g2 nid1 nid2
                        let g4 = addEdge g3 pid nid1
                        let m1 = addLitMapping m d lid n1
                        let m2 = addLitMapping m1 d (negate lid) n2
                        put s' {graph = g4, mapping = m2}
                        return nid1         
        Just node -> do let (Sy4NCL.Graph.N nid _ _ )= node
                        let g = addEdge (graph s) pid nid     
                        put s {graph = g}
                        return nid
                        
addEdge' :: NodeId -> NodeId -> GraphState NodeId
addEdge' nid1 nid2 = do s <- get
                        let g = addEdge (graph s) nid1  nid2
                        put s { graph = g  }
                        return nid2
