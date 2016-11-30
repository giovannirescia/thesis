module Sy4NCL.Graph 

where
 import qualified Data.Map as M
 import qualified Data.Sequence as S
 import Control.Monad.State
 import HyLo.Signature.Simple hiding (N)
 
 type NodeId = Int
 type Depth  = Int
 type Color  = Int
 type LitId  = Int
 
 data Node = N {-# UNPACK #-}!NodeId {-# UNPACK #-}!Depth {-# UNPACK #-}!Color deriving (Show)
 data Edge = E {-# UNPACK #-}!(NodeId,NodeId) deriving (Show)
 
 data Graph = Empty |
              Graph ![Node] ![Edge]
              deriving (Show)
 
 type LitMap =  M.Map Depth (M.Map LitId Node)

 data GraphInfo = GraphInfo { buildTime :: Double,
                              lastNid   :: NodeId,
                              graph     :: Graph,
                              mapping   :: LitMap,
                              colorCount:: S.Seq Color}
                             deriving (Show) 
 
 type GraphState a = StateT GraphInfo IO a
 
 defaultGraphInfo :: NodeId -> (PropSymbol, RelSymbol) -> GraphInfo
 defaultGraphInfo startId max_sig = GraphInfo{ buildTime = 0,
                                lastNid = startId,
                                graph = Empty,
                                mapping = M.empty,
                                colorCount = s}
        where (PropSymbol _, RelSymbol max_rel) = max_sig
              colors = 3 + (4 * max_rel)
              s = S.replicate colors 0

 -- createSeq :: Int -> [Int] -> S.Seq Int
 -- createSeq 0 xs = S.fromList ([0,0,0]++xs)
 -- createSeq n xs = createSeq (n-1) (xs++[0,0,0,0])
 -- -- initialize with createSeq <num of Relations> []

 addNode :: Graph -> NodeId -> Depth -> Color -> (Graph,Node)
 addNode  Empty nid d c        = let n = (N nid d c)
                                 in (Graph (n:[]) [], n)
 addNode (Graph !ns !es) nid d c = let n = (N nid d c)
                                 in (Graph (n:ns) es, n)
 
 addEdge :: Graph -> NodeId -> NodeId -> Graph
 addEdge Empty nid1 nid2         = Graph [] ((E (nid1, nid2)):[])
 addEdge (Graph !ns !es) nid1 nid2 = Graph ns ((E (nid1, nid2)):es)
 
 nextNid :: GraphState NodeId
 nextNid = do s <- get
              let i = (lastNid s) + 1
              put s { lastNid = i  } 
              return i

 upColorCount :: Color -> Int -> GraphState NodeId
 upColorCount c n = do s <- get
                       let count = (colorCount s)
                       let !i =  (c-1)
                       let !val = (S.index count i) + n
                       let !count' = S.update i val count
                       put s { colorCount = count'  } 
                       return val
 
 litNodeExist :: LitMap -> Depth -> LitId -> Maybe Node             
 litNodeExist m d lid = do lits <- M.lookup d m
                           M.lookup lid lits
 
 addLitMapping :: LitMap -> Depth -> LitId -> Node -> LitMap
 addLitMapping m d lid n = case M.lookup d m of
                            Nothing -> M.insert d (M.insert lid n M.empty) m
                            Just m' -> M.insert d (M.insert lid n m') m
