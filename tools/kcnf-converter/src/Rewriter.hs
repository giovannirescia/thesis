module Rewriter (bnf,kcnf,rewrConst,rename)

 where

 import HyLo.InputFile 
 import HyLo.Formula
 import HyLo.Formula.Rewrite
 import HyLo.Signature.Simple
 import qualified Data.Generics.UniplateStr as Uniplate

 import Data.Maybe
 import qualified Data.Map as Map
 import qualified Data.Traversable as T

 import Control.Monad.State

 rename :: [Formula NomSymbol (Rewr PropSymbol) RelSymbol] -> OldInputFile
 rename fs = map (mapSig id map_prop id) fs
    where map_prop p = fromMaybe (error "rename?!") (Map.lookup p ren_props)
          ren_props  = evalState (T.mapM renProp all_props) new_props
          new_props  = [PropSymbol (max_orig + 1)..]
          max_orig   = maximum [n | f <- fs,
                                    Prop (Orig (PropSymbol n)) <- Uniplate.universe f]
          all_props  = Map.fromList [(p,p) | f <- fs, Prop p <- Uniplate.universe f]
          renProp rp  = case rp of
                           (Orig p) -> return p
                           (Rewr _) -> do (p:ps) <- get
                                          put ps
                                          return p
 

 -- Replace every occurrence of the TOP and BOTTOM symbols with an equivalent
 -- expression.
 rewrConst :: Formula n PropSymbol r -> Formula n PropSymbol r 
 rewrConst = Uniplate.rewrite rewrC
     where rewrC Top = Just (sym :|: Neg sym)
           rewrC Bot = Just (sym :&: Neg sym)
           rewrC _   = Nothing
           sym = Prop $ PropSymbol 1

 -- Rewrites a formula F to Box Normal Form (bnf).
 -- Only works with normal modal logic formulas.
 bnf :: Formula n p r -> Formula n p r
 bnf f@Top                = f
 bnf f@Bot                = f
 bnf f@(Neg Top)          = f
 bnf f@(Neg Bot)          = f
 bnf f@Prop{}             = f
 bnf f@(Neg Prop{})       = f
 --
 bnf (Neg (Neg f))        = bnf f
 bnf      (f1 :&: f2)     = (bnf       f1)           :&: (bnf        f2)
 bnf (Neg (f1 :&: f2))    = (bnf $ Neg f1)           :|: (bnf $ Neg  f2)
 bnf      (f1 :|: f2)     = (bnf       f1)           :|: (bnf        f2)
 bnf (Neg (f1 :|: f2))    = (bnf $ Neg f1)           :&: (bnf $ Neg  f2)
 bnf      (f1 :-->: f2)   = (bnf $ Neg f1)           :|: (bnf        f2)
 bnf (Neg (f1 :-->: f2))  = (bnf       f1)           :&: (bnf $ Neg  f2)
 bnf      (f1 :<-->: f2)  = (bnf $      f1 :-->: f2) :&: (bnf $      f2 :-->: f1)
 bnf (Neg (f1 :<-->: f2)) = (bnf $ Neg (f1 :-->: f2)):|: (bnf $ Neg (f2 :-->: f1))
 bnf      (Diam r f)      = Neg (Box r (bnf (Neg f)))
 bnf (Neg (Diam r f))     = Box  r $ bnf (Neg f)
 bnf      (Box r f)       = Box  r $ bnf      f
 bnf (Neg (Box r f))      = Neg (Box r (bnf f))

--Given a list of formulas in PNF (Pulenta Normal Form),
--rewrites them to BNF (Box Normal Form). This ensures that
--the resulting formula is in KCNF (K Conjuntive Normal Form).
 kcnf :: Eq n => [Formula n p r] -> [Formula n (Rewr p) r]
 kcnf = map bnf . pnf 
