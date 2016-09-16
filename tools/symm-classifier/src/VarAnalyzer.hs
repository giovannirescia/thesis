
module VarAnalyzer (classifyVars,printMap) where

import HyLo.Formula
import HyLo.Signature.Simple
import qualified Data.Set as S
import qualified Data.Map as M
import qualified Data.Sequence as Q 
import qualified Data.Foldable as Foldable

type Trail = Q.Seq Int
type TrailSet = S.Set Trail
type VarTrails = M.Map Int TrailSet
type FormTrails = M.Map Int VarTrails
type MDepth = Int
type VarId = Int
type VarType = Int
type VarTypes   = (S.Set Int,S.Set Int,S.Set Int,S.Set Int)
type VarTypesMap = M.Map Int VarTypes

box  = 0
diam = 1

classifyVars :: [Formula n PropSymbol r] -> VarTypesMap
classifyVars [] = M.empty
classifyVars fs = M.map trailsToType formTrails  
  where formTrails = foldr combine M.empty trails
        trails     = map buildTrail fs
        combine m1 m2 = M.unionWith combineAtDepth m1 m2 
        combineAtDepth vtr1 vtr2 = M.unionWith combineVarTrails vtr1 vtr2
        combineVarTrails trs1 trs2 = S.union trs1 trs2  
                 
buildTrail :: Formula n PropSymbol r -> FormTrails
buildTrail fr =  _buildTrail fr 0 M.empty Q.empty
        where _buildTrail (Box _ f) md ft s                   = _buildTrail f (md+1) ft (s Q.|> box)
              _buildTrail (Neg (Box _ f)) md ft s             = _buildTrail f (md+1) ft (s Q.|> diam)
              _buildTrail (f1 :|: f2) md ft s                 = _buildTrail f2 md (_buildTrail f1 md ft s) s
              _buildTrail (Prop (PropSymbol v)) md ft s       = updateFormTrails ft md v s 
              _buildTrail (Neg (Prop (PropSymbol v))) md ft s = updateFormTrails ft md v s  


trailsToType:: VarTrails -> VarTypes
trailsToType vtr= M.foldlWithKey updateVarsTypes emptyTuple vtr
        where emptyTuple = (S.empty,S.empty,S.empty,S.empty) 


updateVarsTypes:: VarTypes -> VarId -> TrailSet -> VarTypes
updateVarsTypes (a,b,c,d) vid trs  = let t = (getVarType trs) in
                                      case t of
                                      0 -> ((S.insert vid a),b,c,d) 
                                      1 -> (a,(S.insert vid b),c,d)
                                      2 -> (a,b,(S.insert vid c),d)
                                      3 -> (a,b,c,(S.insert vid d))

getVarType:: TrailSet-> VarType
getVarType trs 
        | ((S.size trs) >= 2) = 0 
        | otherwise = checkLast (Q.viewr tr)
        where tr = head $ S.elems trs
              checkLast Q.EmptyR = 3  
              checkLast (prefx Q.:> a) 
                        | (a == diam) = 3
                        | otherwise  = checkPrefx (Q.viewr prefx)
                                where checkPrefx Q.EmptyR = 1
                                      checkPrefx (prefx2 Q.:> b)
                                                 | (b == diam) = 2
                                                 | otherwise =  checkPrefx (Q.viewr prefx2) 
                        
                
        
updateFormTrails :: FormTrails -> MDepth -> VarId -> Trail -> FormTrails
updateFormTrails ft md vid tr = M.alter _updateFtr md ft
        where _updateFtr Nothing  = Just (updateVarTrails M.empty vid tr)
              _updateFtr (Just vtr) = Just (updateVarTrails vtr vid tr)

updateVarTrails :: VarTrails -> VarId -> Trail -> VarTrails
updateVarTrails vtr vid tr = M.alter (_updateVtr tr) vid vtr
        where _updateVtr t Nothing = Just (S.singleton t)
              _updateVtr t (Just s)  = Just (S.insert t s) 

printTrail :: Trail -> String
printTrail tr = "<" ++ (show' tr) ++ ">"
        where show' s = Foldable.foldl' conc "" s
              conc a b = a ++ (show b) ++","   

printMap :: VarTypesMap -> String
printMap m = M.foldrWithKey show' "" m
  where show' k a b = "md:" ++ (show k) ++ " ==> " ++ printTuple a ++ "\n"  ++ b


printTuple :: VarTypes -> String
printTuple (av,bv,cv,dv) = "A= " ++ show' av ++ " - B= " ++ show' bv ++ " - C= " ++ show' cv ++ " - D= " ++ show' dv 
  where show' x = (show $ S.size x) ++ ":" ++ (show $ S.elems x)