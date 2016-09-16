module Sy4NCL.BlissWriter where

import Sy4NCL.Graph
import Sy4NCL.Utils
--import Text.Printf
--import Data.List
--import qualified Data.ByteString.Lazy.Char8 as B

--import Debug.Trace

writeGraph :: GraphInfo -> String
writeGraph inf =  str ""
        where str =(printHeader g) . (printNodes g) . (printEdges g)
              g = graph inf
              
--writeGraph :: GraphInfo -> String
--writeGraph inf =  str
--        where str = concat [(printHeader g),"\n",                      
--                               (printNodes g),"\n",
--                               (printEdges g)]
--              g = graph inf
--              nwline = B.pack "\n"

--printHeader :: Graph -> B.ByteString
--printHeader Empty = B.empty
--printHeader (Graph n e) =  B.pack hdr  
--        where hdr = "p edge " ++ show n' ++ " " ++ show e' ++"\n"
--              n' = length n
--              e' = length e
--
--printNodes :: Graph -> B.ByteString
--printNodes Empty = B.empty
--printNodes (Graph ns _) = B.intercalate nwline  nStr --trace ("graph:" ++ show g ++"\n") $ intercalate "\n" nStr
--        where nStr = map (\(N nid _ c) -> B.pack $! "n " ++ show nid ++" " ++ show c) ns
--              nwline = B.pack "\n"
--
--
--printEdges :: Graph -> B.ByteString
--printEdges Empty = B.empty
--printEdges (Graph _ es) =   B.intercalate nwline eStr
--        where eStr = map (\(E (nid1,nid2)) -> B.pack $! "e "++ show nid1 ++ " " ++ show nid2) es
--              nwline = B.pack "\n"



printHeader :: Graph -> ShowS
printHeader Empty = showString ""
printHeader (Graph n e) =  p . n'. space . e' . newLine 
        where n' = shows $ length n
              e' = shows $ length e
              p = showString "p edge "
              
printNodes :: Graph -> ShowS
printNodes Empty = showString ""
printNodes (Graph ns _) = compose $! nStr --trace ("graph:" ++ show g ++"\n") $ intercalate "\n" nStr
        where nStr = map (\(N nid _ c) -> nodeLine nid c) ns
              nodeLine i c= showString "n " . (shows i) . space . (shows c) .newLine

printEdges :: Graph -> ShowS
printEdges Empty = showString ""
printEdges (Graph _ es) =  compose $! eStr
        where eStr = map (\(E (nid1,nid2)) -> edgeLine nid1 nid2) es
              edgeLine i j = showString "e " . (shows i) . space . (shows j) . newLine
              
--printHeader :: Graph -> String
--printHeader Empty = ""
--printHeader (Graph n e) =  "p edge " ++ show n' ++ " " ++ show e' 
--        where n' = length n
--              e' = length e
--
--printNodes :: Graph -> String
--printNodes Empty = ""
--printNodes (Graph ns _) = intercalate "\n" $! nStr --trace ("graph:" ++ show g ++"\n") $ intercalate "\n" nStr
--        where nStr = map (\(N nid _ c) -> printf "n %d %d" (nid::Int) (c::Int)) ns
--
--
--printEdges :: Graph -> String
--printEdges Empty = ""
--printEdges (Graph _ es) =  intercalate "\n" $! eStr
--        where eStr = map (\(E (nid1,nid2)) -> printf "e %d %d" (nid1::Int) (nid2::Int)) es
