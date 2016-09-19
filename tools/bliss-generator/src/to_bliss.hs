module Main (main
       , module HyLo.InputFile
       , module System.Environment
       , module BlissGraph)

where

import HyLo.InputFile 
import System.Environment
import BlissGraph

main :: IO()
main = getArgs >>= return . head >>= 
       readFile >>= return . parseOldFormat 
       >>= runBuilder >>= putStrLn . show . graph 

