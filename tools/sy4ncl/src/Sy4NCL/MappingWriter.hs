module Sy4NCL.MappingWriter where

import Sy4NCL.Graph
import Sy4NCL.Utils
--import Text.Printf
import qualified Data.Map as M
--import Data.List

writeMapping :: GraphInfo -> String
writeMapping inf = str ""
        where str = (showString "<md> <lit> <node_id> \n") . printMapping (mapping inf) . newLine

printMapping :: LitMap -> ShowS
printMapping m = compose $ mpStr --trace ("map: " ++ show m) $ intercalate "\n" mpStr
        where mpStr = map (\(k,v)-> f (shows k) (M.toAscList v)) $ M.toAscList m
              f s ls = compose $ map (\(v,(N nid _ _)) -> s . space . (shows v) . space . (shows nid) . newLine) ls
              
--printMapping :: LitMap -> ShowS
--printMapping m = compose $! mpStr --trace ("map: " ++ show m) $ intercalate "\n" mpStr
--        where lits = map (\(k,v)->(k,M.toAscList v)) $ M.toAscList m
--              lits' = concatMap (\(k,lv)-> map (\(v,n) -> (k,v,n)) lv) lits 
--              mpStr = map (\(k,v,(N nid _ _)) -> mapLine k nid v) lits'
--              mapLine d i v= (shows d) . space . (shows i). space . (shows v) .newLine

--printMapping :: LitMap -> String
--printMapping m = intercalate "\n" mpStr --trace ("map: " ++ show m) $ intercalate "\n" mpStr
--        where lits = map (\(k,v)->(k,M.toAscList v)) $ M.toAscList m
--              lits' = concatMap (\(k,lv)-> map (\(v,n) -> (k,v,n)) lv) lits 
--              mpStr = map (\(k,v,(N nid _ _)) -> printf "%d %d %d" (k::Depth) (nid::Int) (v::LitId)) lits'