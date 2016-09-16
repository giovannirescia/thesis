module Sy4NCL.Utils where

import HyLo.Formula
import HyLo.Signature.Simple
import Data.List

orList :: Formula NomSymbol PropSymbol RelSymbol -> 
          [Formula NomSymbol PropSymbol RelSymbol]
orList = reverse . unfoldr next . Just
    where next (Just (a :|: b)) = Just (b, Just a)
          next (Just f)         = Just (f, Nothing)
          next Nothing          = Nothing


listerize :: Formula NomSymbol PropSymbol RelSymbol -> 
             Formula NomSymbol PropSymbol RelSymbol ->
             [Formula NomSymbol PropSymbol RelSymbol]          
listerize f g = concatMap orList $ [f,g]


replaceNth :: Int -> a -> [a] -> [a]
replaceNth _ _ [] = error "empty list"
replaceNth n newVal l = snd $ unzip l' 
        where l' = map (\(i,v) -> if i==n then (i,newVal) else (i,v)) z
              z = zip [0..(length l)] l

space :: ShowS
space = showString " "

newLine :: ShowS
newLine = showString "\n"


compose :: [a -> a] -> a -> a
compose fs v = foldl (flip (.)) id fs $ v