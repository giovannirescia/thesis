module Main (main
       , module HyLo.InputFile
       , module HyLo.Formula
       , module HyLo.Formula.Rewrite
       , module System.Environment
       , module Rewriter)

where

import HyLo.InputFile 
import HyLo.Formula hiding (unit_tests)
import HyLo.Formula.Rewrite hiding (unit_tests)
import System.Environment
import Rewriter
import Data.List
import qualified Data.Set

main :: IO()
main = getArgs >>= return . head >>= 
       readFile >>= return . parseOldFormat 
       >>= putStrLn . show' . remDup . rename . filt . simp . kcnf . rw
   where filt x = filter (\x -> x /= Top && x /= (A Top)) x
         rw x = map rewrConst x
         simp x = map simplify x
         remDup x = Data.Set.toList $ Data.Set.fromList x
         show' x = "begin \n" ++ concat' x ++ "\nend"
          where concat' x = intercalate ";" (map show x)
