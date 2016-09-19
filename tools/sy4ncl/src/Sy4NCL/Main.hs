module Sy4NCL.Main where

import Sy4NCL.Graph
import Sy4NCL.CommandLine
import Sy4NCL.BMLGraph as BML
import Sy4NCL.BlissWriter as Bliss
import Sy4NCL.StatsWriter
import Sy4NCL.MappingWriter
import HyLo.Formula
import HyLo.Signature.Simple 
import HyLo.InputFile 
import Control.Monad.State
import System.FilePath

runWithParams:: Params -> IO () 
runWithParams params = 
  do let input = (filename params)
     f <- readFile input
     let fs = parseOldFormat f 
     let builder = getBuilder (graphType params)
     let writer =  getWriter (format params)
     info <- runBuilder params builder fs
     let graphStr = (writer info)
     let statsStr = (writeStats info)
     let mapStr = (writeMapping info)
     let graphFile = getGraphFilename params
     let statsFile = getStatsFilename params
     let mapFile = getMappingFilename params
     writeFile graphFile graphStr
     writeFile statsFile statsStr
     writeFile mapFile mapStr
     putStrLn statsStr
                  
getBuilder :: Int -> 
           (Params -> [Formula NomSymbol PropSymbol RelSymbol] -> 
            GraphState NodeId)
getBuilder _ = BML.buildGraph
--        | g == 1 = BML.buildGraph
--        | otherwise = error "Not supported graph type" 

getWriter :: Int -> 
           (GraphInfo -> 
            String)
getWriter f 
        | f == 1 = Bliss.writeGraph
        | otherwise = error "Not supported output format" 

runBuilder :: Params ->
              (Params -> [Formula NomSymbol PropSymbol RelSymbol] -> GraphState NodeId) ->
              [Formula NomSymbol PropSymbol RelSymbol] -> 
              IO GraphInfo
runBuilder p builder fs 
        | (format p) == 1 = execStateT (builder p fs) (defaultGraphInfo 0)
        | otherwise = execStateT (builder p fs) (defaultGraphInfo 0)

getStatsFilename :: Params -> String
getStatsFilename p = dir </> f' 
        where f' = replaceExtension (takeFileName f) ".stats"
              f = filename p
              dir = outDir p

getMappingFilename :: Params -> String
getMappingFilename p = dir </> f' 
        where f' = replaceExtension (takeFileName f) ".map"
              f = filename p
              dir = outDir p

getGraphFilename :: Params -> String
getGraphFilename p = dir </> f' 
        where f' = replaceExtension (takeFileName f) ".bliss"
              f = filename p
              dir = outDir p