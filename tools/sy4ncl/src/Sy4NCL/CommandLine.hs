{-# LANGUAGE DeriveDataTypeable #-}
{-# OPTIONS_GHC -fno-warn-missing-fields #-}
module Sy4NCL.CommandLine where

import System.Console.CmdArgs

data Params = Params { filename    :: String,
                         graphType :: Int,
                         format    :: Int,
                         outDir    :: String}
                         deriving (Show, Data, Typeable)

defaultParams :: Annotate Ann
defaultParams = record Params{} 
                [filename    := "" += name "f" += typFile += help "intohylo input file",
                 graphType   := 1  += name "t" += help "Graph type (0=Non-Layered, default:1=Layered)",
                 format      := 1  += name "o" += help "Output Format (default:1=bliss, 2=saucy",
                 outDir      := "."+= name "d" += help "Output folder (default: current directory)"]
                += verbosity 
                += help "Generates a colored graph from a BML formula" 
                += summary "sy4ncl v0.0.0, (C) Ezequiel Orbe"
                += program "sy4ncl"

checkParams :: Params -> IO Bool
checkParams p
 | null (filename p) =
    do putStrLn $ unlines ["ERROR: No input specified.","Run with --help for usage options"]
       return False
 | otherwise = return True
