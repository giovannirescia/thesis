module Sy4NCL.StatsWriter where

import Sy4NCL.Graph
import qualified Data.Sequence as S
import Data.Foldable (toList)
import Text.Printf

writeStats:: GraphInfo -> String
writeStats inf = str
        where str = printTime (buildTime inf) ++ "\n" ++
                    printColorCount (colorCount inf) ++ "\n" ++
                    printGraphSize (graph inf)  

printTime :: Double -> String
printTime t = "Computation time: 0 sec"
--printTime t = printf "Computation time: %0.5f sec" (t :: Double)

printColorCount :: S.Seq Color -> String
printColorCount cs = "Color count:" ++ show (toList cs)

printGraphSize :: Graph -> String
printGraphSize  Empty = "|Nodes|: 0\n" ++ "|Edges|: 0\n"
printGraphSize (Graph n e) = printf "|Nodes|: %d\n|Edges|: %d\n" (n'::Int) (e'::Int) 
        where n' = length n
              e' = length e
