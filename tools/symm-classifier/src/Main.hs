module Main (main
       , module HyLo.InputFile
       , module HyLo.Formula
       , module HyLo.Formula.Rewrite
       , module System.Environment
       , module VarAnalyzer)
where

import HyLo.InputFile 
import HyLo.Formula hiding (unit_tests)
import HyLo.Formula.Rewrite hiding (unit_tests)
import System.Environment
import VarAnalyzer

main :: IO()
main = getArgs >>= return . head >>= 
       readFile >>= return . parseOldFormat 
       >>= putStrLn . printMap . classifyVars
   