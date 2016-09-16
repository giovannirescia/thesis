'''
Created on Oct 20, 2011

@author: eorbe
'''
from pyparsing import ParseResults
#===============================================================================
# PREDICATES
#===============================================================================

def _is_var(phi):
    return isinstance(phi, int)

def _is_neg_op(op):
    return op == '~'

def _is_or_op(op):
    return (op == '|')

def _is_and_op(op):
    return (op == '&')

def _is_box_op(op):
    return (op == '#') or (op == '-#')

def _is_diam_op(op):
    return (op == '<>')

def _is_neg(phi):
    return not _is_var(phi) and _is_neg_op(phi[0])

def _is_or(phi):
    return not _is_var(phi) and len(phi) >= 3 and _is_or_op(phi[1])

def _is_and(phi):
    return not _is_var(phi) and len(phi) >= 3 and _is_and_op(phi[1])

def _is_diam(phi):
    return not _is_var(phi) and _is_diam_op(phi[0])

def _is_box(phi):
    return not _is_var(phi) and _is_box_op(phi[0])

def _is_neg_box(phi):
    return _is_neg(phi) and _is_box(phi[1])

def _is_box_distributable(phi):
    return _is_box(phi) and _is_and(phi[1])

def _is_de_morgan(phi):
    return _is_neg(phi) and (_is_and(phi[1]) or _is_or(phi[1])) 

def _is_double_neg(phi):
    return _is_neg(phi) and _is_neg(phi[1])

def _is_atom(phi):
    return _is_var(phi) or _is_box(phi)

def _is_bnf(phi): 
    if _is_var(phi): return True
    if phi == 'false': return True
    if _is_de_morgan(phi): return False
    if _is_neg(phi):
        return _is_bnf(phi[1])
    if _is_box(phi): 
        return _is_bnf(phi[1])
    if _is_diam(phi): return False
    if _is_or(phi) or _is_and(phi):
        for i in range(len(phi)):
            if i % 2 == 0:
                if not _is_bnf(phi[i]): return False
        return True

def _is_kcnf_clause(phi):
    if _is_var(phi): return True
    if _is_box(phi): return _is_kcnf_clause(phi[1])
    if _is_neg(phi): return _is_atom(phi[1]) and _is_kcnf_clause(phi[1])
    if not _is_or(phi): return False
    for i in range(len(phi)): 
        if i % 2 == 0:
            if not _is_kcnf_clause(phi[i]): return False
    return True

def _is_kcnf(phi):
    assert _is_bnf(phi), 'phi is not in BNF.'

    if _is_and(phi):
        for i in range(len(phi)):
            if i % 2 == 0:
                if not _is_kcnf_clause(phi[i]): return False
            else:
                if not _is_and_op(phi[i]): return False
        return True
    else:
        return _is_kcnf_clause(phi)

#===============================================================================
# REWRITE FUNCTIONS
#===============================================================================
def _unfold_and(phi):
    if not _is_and(phi): return phi
    conjs = []
    for i in range(len(phi)):
        if i % 2 == 0:
            if _is_and(phi[i]):
                conjs = conjs + _unfold_and(phi[i])
            else:
                conjs.append(phi[i])
        else:
            conjs.append(phi[i])
    return conjs

def _unfold_or(phi):
    if _is_box(phi): 
        phi[1] = _unfold_or(phi[1])
    if not _is_or(phi): return phi
    conjs = []
    for i in range(len(phi)):
        if i % 2 == 0:
            if _is_or(phi[i]):
                conjs = conjs + _unfold_or(phi[i])
            else:
                conjs.append(phi[i])
        else:
            conjs.append(phi[i])
    return conjs
                
                
def _remove_double_neg(phi):
    return phi[1][1]

def _negate(phi):
    if _is_var(phi):
        return phi * (-1)
    else:
        return ParseResults(['~', phi])

def _diamond_to_box(phi):
    return _negate(ParseResults(['#', _negate(phi)]))

def _de_morgan(phi):
    sub_phi = phi[1]
    op = '|' if sub_phi[1] == '&' else '&'
    #negate each element of the | or &.   
    for i in range(len(sub_phi)):
        if i % 2 == 0:
            sub_phi[i] = _negate(sub_phi[i])
        else:
            sub_phi[i] = op
    return sub_phi

def _remove_neg_box(phi):
    assert _is_neg_box(phi)
    if phi[1][0] == '#':
        return ParseResults(['-#', phi[1][1]])
    else:
        return ParseResults(['#', phi[1][1]])
            
def _distribute_box_over_and(phi):
    if _is_var(phi):return phi
    if _is_box(phi[1]): phi[1] = _distribute_box_over_and(phi[1])
    if _is_or(phi[1]): phi[1] = _distribute_or_over_and(phi[1])
    if _is_or(phi[1]): 
        for i in range(len(phi[1])):
            if i % 2 == 0:
                phi[1][i] = _distribute_box_over_and(phi[1][i])
    if _is_and(phi[1]):
        new_atoms = []
        for i in range(len(phi[1])):
            if i % 2 == 0:
                at = _distribute_box_over_and(ParseResults([phi[0], phi[1][i]]))
                new_atoms.append(at)
            else:
                new_atoms.append('&')
        return ParseResults(new_atoms)
    return phi

def _distribute_or_over_and(phi):
    if not _is_or(phi): return phi
    ands = []
    disj = []
    
    #first try to distribute all the boxes
    for i in range(len(phi)):
        if i % 2 == 0:
            t = _distribute_box_over_and(phi[i])
            phi[i] = t
    
    #identify all the ands 
    for i in range(len(phi)):
        if i % 2 == 0:
            if _is_and(phi[i]): 
                ands.append(phi[i])
            else:
                disj.append(phi[i])
    clauses = [disj]
    
    if len(ands) == 0: return phi
    
    #grab a conjunction
    for a in ands:
        clauses_tmp = []
        #grab a conjunct and add it to the clauses
        for c in a:
            if c != '&':
                for cl in clauses:
                    new_cl = list(cl)
                    new_cl.append(c)
                    clauses_tmp.append(new_cl) 
        clauses = list(clauses_tmp)
    
    
    clauses_tmp = []
    for c in clauses:
        new_c = []
        for i in range(((2 * len(c)) - 1)):
            if i % 2 == 0:
                new_c.append(c[(i / 2)])
            else:
                new_c.append('|')
        t = ParseResults(new_c)
        t2 = ParseResults(_unfold_or(t))
        clauses_tmp.append(t2)
        clauses_tmp.append('&')       
    return ParseResults(clauses_tmp[:-1])
    
    

#===============================================================================
# MAIN FUNCTIONS
#===============================================================================
# TO BNF
# 1) <>phi ==> -[]-phi
# 2) -(phi & psi) ==> -phi | -psi
# 3) -(phi | psi) ==> -phi & -psi
# 4) --phi ==> phi

def _to_bnf(phi):
    if _is_var(phi): return phi
    if phi == 'false': return ParseResults([1, '&', -1])
    if phi == 'true': return ParseResults([1, '|', -1])

    if _is_neg(phi):
        phi[1] = _to_bnf(phi[1]) 
    
    if _is_box(phi):    
        phi[1] = _to_bnf(phi[1])
        
    #Apply 1
    if _is_diam(phi):
        phi = _to_bnf(_diamond_to_box(phi[1]))

    #Apply 2 and 3
    if _is_de_morgan(phi):
        phi = _de_morgan(phi)
 
    #Apply 4
    if _is_double_neg(phi):
        phi = _remove_double_neg(phi)
    
    if _is_neg_box(phi):
        phi = _remove_neg_box(phi)
        
    #if is | or & apply the conversion to its disjuncts (conjuncts)
    if _is_or(phi) or _is_and(phi):
        for i in range(len(phi)):
            if i % 2 == 0:
                phi[i] = _to_bnf(phi[i])
    
    assert _is_bnf(phi), ('_to_bnf: phi is not in BNF: %s' % phi)
                
    return phi

def _to_kcnf(phi):
    assert _is_bnf(phi), ('_to_kcnf: phi is not in BNF: %s' % phi)
    if _is_var(phi): return phi
    if _is_box(phi): phi = _distribute_box_over_and(phi)
    if _is_or(phi): phi = _distribute_or_over_and(phi)
    if _is_and(phi):
        for i in range(len(phi)):
            if i % 2 == 0:
                phi[i] = _to_kcnf(phi[i])
    
    phi = ParseResults(_unfold_and(phi))
    assert _is_kcnf(phi), ('_to_kcnf: phi is not in KCNF: %s' % phi)
    return phi


def _dump_kcnf_var(var):
    if var > 0:
        return 'P%s' % var
    else:
        return '-P%s' % abs(var)
    
def _dump_kcnf_box(phi):
    phi_str = '[R1](%s)' if phi[0] == '#' else '-[R1](%s)'
    phi_str = phi_str % _dump_kcnf_clause(phi[1]) 
    return phi_str
    
def _dump_kcnf_clause(clause):
    if _is_var(clause): return _dump_kcnf_var(clause)
    if _is_box(clause): return _dump_kcnf_box(clause) 
    if _is_or(clause):
        d = []
        for i, e in enumerate(clause):
            if i % 2 == 0:
                d.append(_dump_kcnf_clause(e))
        return ' v '.join(d)
        
def _dump_intohylo_kcnf(phi):
    assert _is_kcnf(phi), ('_dump_intohylo_kcnf: phi is not in KCNF: %s' % phi)
    if _is_and(phi):
        c = []
        for i, e in enumerate(phi):
            if i % 2 == 0:
                c.append(_dump_kcnf_clause(e))
        return 'begin\n' + ';\n'.join(c) + '\n end'
    else:
        return 'begin\n' + _dump_kcnf_clause(phi) + '\n end'
    
def intohylo_to_intohylo_kcnf(phi):
    bnf = _to_bnf(phi)
    kcnf = _to_kcnf(bnf)
    return _dump_intohylo_kcnf(kcnf) 


#if __name__ == "__main__":
#    fs = ['begin p1 end',
#          'begin ~p1 end',
#          'begin ~~p2 end',
#          'begin ~(p1 & p2) end',
#          'begin ~(p1 | p2) end',
#          'begin ~(~p1 & ~p2) end',
#          'begin ~(~p1 | ~p2) end',
#          'begin ~((p1 | p2)  & (p3 & p4)) end',
#          'begin <r1>p1 end',
#          'begin ~<r1>p1 end',
#          'begin <r1>~p1 end',
#          'begin ~<r1>~p1 end',
#          'begin ~(<r1>p1 & ~<r1>p2) end',
#          'begin ~ ([r1]([r1]p1 | [r1]<r1>~ p1) & <r1>(<r1>p3 & [r1]<r1>~ p4) & <r1>false)  end',
#          'begin (p1 | p2 | [r1]p3)  end',
#          'begin  (p1 | ~ p3 | [r1]p2 | (p4 & p5) | (p6 & p7) ) end',
#          'begin  (p1 | ~<r1>(p1 | p2 | ~ p3) | [r1]p2) end',
#          'begin  (p1 | ~<r1>~<r1>(p1 | p2 | ~ p3)) end',
#          'begin  (p1 | ~<r1>(p1 | ~<r1>(p1 | p2 | ~ p3) | p2)) end',
#          'begin (p100 & ~ p101 & (~ p101 | p100) & (~ p102 | p101) & (~ p100 | ((~ p1 | [r1](~ p100 | p1)) & (p1 | [r1](~ p100 | ~ p1)))) &  (~ p101 | ((~ p2 | [r1](~ p101 | p2)) & (p2 | [r1](~ p101 | ~ p2)))) &  (~ (p100 & ~ p101) | (<r1>(p101 & ~ p102 & p2) & <r1>(p101 & ~ p102 & ~ p2))) &  [r1]((~ p101 | p100) & (~ p102 | p101) & (~ p100 | ((~ p1 | [r1](~ p100 | p1)) & (p1 | [r1](~ p100 | ~ p1)))) & (~ p101 | ((~ p2 | [r1](~ p101 | p2)) & (p2 | [r1](~ p101 | ~ p2)))) & (~ (p100 & ~ p101) | (<r1>(p101 & ~ p102 & p2) & <r1>(p101 & ~ p102 & ~ p2))))) end',
#          'begin ~ (~ ([r1]([r1](~ [r1]p1 | [r1][r1]p1) & (~ [r1](~ [r1](~ p1 | [r1]p1) | p1) | (~ <r1>[r1]p1 | p1))) & [r1][r1]([r1](~ [r1]p1 | [r1][r1]p1) & (~ [r1](~ [r1](~ p1 | [r1]p1) | p1) | (~ <r1>[r1]p1 | p1))) & ~ [r1][r1][r1](~ [r1](~ [r1](~ p1 | [r1]p1) | p1) | (~ <r1>[r1]p1 | [r1]p1))) | (<r1><r1><r1>~ ((~ [r1](~ [r1](~ p1 | [r1]p1) | p1) | [r1][r1](~ [r1](~ p1 | [r1]p1) | p1)) & [r1](~ [r1]p1 | [r1][r1]p1) & (~ [r1](~ [r1](~ p1 | [r1]p1) | p1) | (~ <r1>[r1]p1 | p1)) & (~ [r1](~ [r1](~ (~ p1 | [r1]p1) | [r1](~ p1 | [r1]p1)) | (~ p1 | [r1]p1)) | (~ <r1>[r1](~ p1 | [r1]p1) | (~ p1 | [r1]p1)))))) end']
#
##    fs = ['begin (p100 & ~ p101 & (~ p101 | p100) & (~ p102 | p101) & (~ p100 | ((~ p1 | [r1](~ p100 | p1)) & (p1 | [r1](~ p100 | ~ p1)))) & (~ p101 | ((~ p2 | [r1](~ p101 | p2)) & (p2 | [r1](~ p101 | ~ p2)))) & (~ (p100 & ~ p101) | (<r1>(p101 & ~ p102 & p2) & <r1>(p101 & ~ p102 & ~ p2))) & [r1]((~ p101 | p100) & (~ p102 | p101) & (~ p100 | ((~ p1 | [r1](~ p100 | p1)) & (p1 | [r1](~ p100 | ~ p1)))) & (~ p101 | ((~ p2 | [r1](~ p101 | p2)) & (p2 | [r1](~ p101 | ~ p2)))) & (~ (p100 & ~ p101) | (<r1>(p101 & ~ p102 & p2) & <r1>(p101 & ~ p102 & ~ p2))))) end',
##          'begin (p100 & ~ p101 & (~ p101 | p100) & (~ p102 | p101) & (~ p103 | p102) & (~ p100 | ((~ p1 | [r1](~ p100 | p1)) & (p1 | [r1](~ p100 | ~ p1)))) & (~ p101 | ((~ p2 | [r1](~ p101 | p2)) & (p2 | [r1](~ p101 | ~ p2)))) & (~ p102 | ((~ p3 | [r1](~ p102 | p3)) & (p3 | [r1](~ p102 | ~ p3)))) & (~ (p100 & ~ p101) | (<r1>(p101 & ~ p102 & p2) & <r1>(p101 & ~ p102 & ~ p2))) & (~ (p101 & ~ p102) | (<r1>(p102 & ~ p103 & p3) & <r1>(p102 & ~ p103 & ~ p3))) & [r1]((~ p101 | p100) & (~ p102 | p101) & (~ p103 | p102) & (~ p100 | ((~ p1 | [r1](~ p100 | p1)) & (p1 | [r1](~ p100 | ~ p1)))) & (~ p101 | ((~ p2 | [r1](~ p101 | p2)) & (p2 | [r1](~ p101 | ~ p2)))) & (~ p102 | ((~ p3 | [r1](~ p102 | p3)) & (p3 | [r1](~ p102 | ~ p3)))) & (~ (p100 & ~ p101) | (<r1>(p101 & ~ p102 & p2) & <r1>(p101 & ~ p102 & ~ p2))) & (~ (p101 & ~ p102) | (<r1>(p102 & ~ p103 & p3) & <r1>(p102 & ~ p103 & ~ p3)))) & [r1][r1]((~ p101 | p100) & (~ p102 | p101) & (~ p103 | p102) & (~ p100 | ((~ p1 | [r1](~ p100 | p1)) & (p1 | [r1](~ p100 | ~ p1)))) & (~ p101 | ((~ p2 | [r1](~ p101 | p2)) & (p2 | [r1](~ p101 | ~ p2)))) & (~ p102 | ((~ p3 | [r1](~ p102 | p3)) & (p3 | [r1](~ p102 | ~ p3)))) & (~ (p100 & ~ p101) | (<r1>(p101 & ~ p102 & p2) & <r1>(p101 & ~ p102 & ~ p2))) & (~ (p101 & ~ p102) | (<r1>(p102 & ~ p103 & p3) & <r1>(p102 & ~ p103 & ~ p3))))) end',
##          'begin (p100 & ~ p101 & (~ p101 | p100) & (~ p102 | p101) & (~ p103 | p102) & (~ p104 | p103) & (~ p100 | ((~ p1 | [r1](~ p100 | p1)) & (p1 | [r1](~ p100 | ~ p1)))) & (~ p101 | ((~ p2 | [r1](~ p101 | p2)) & (p2 | [r1](~ p101 | ~ p2)))) & (~ p102 | ((~ p3 | [r1](~ p102 | p3)) & (p3 | [r1](~ p102 | ~ p3)))) & (~ p103 | ((~ p4 | [r1](~ p103 | p4)) & (p4 | [r1](~ p103 | ~ p4)))) & (~ (p100 & ~ p101) | (<r1>(p101 & ~ p102 & p2) & <r1>(p101 & ~ p102 & ~ p2))) & (~ (p101 & ~ p102) | (<r1>(p102 & ~ p103 & p3) & <r1>(p102 & ~ p103 & ~ p3))) & (~ (p102 & ~ p103) | (<r1>(p103 & ~ p104 & p4) & <r1>(p103 & ~ p104 & ~ p4))) & [r1]((~ p101 | p100) & (~ p102 | p101) & (~ p103 | p102) & (~ p104 | p103) & (~ p100 | ((~ p1 | [r1](~ p100 | p1)) & (p1 | [r1](~ p100 | ~ p1)))) & (~ p101 | ((~ p2 | [r1](~ p101 | p2)) & (p2 | [r1](~ p101 | ~ p2)))) & (~ p102 | ((~ p3 | [r1](~ p102 | p3)) & (p3 | [r1](~ p102 | ~ p3)))) & (~ p103 | ((~ p4 | [r1](~ p103 | p4)) & (p4 | [r1](~ p103 | ~ p4)))) & (~ (p100 & ~ p101) | (<r1>(p101 & ~ p102 & p2) & <r1>(p101 & ~ p102 & ~ p2))) & (~ (p101 & ~ p102) | (<r1>(p102 & ~ p103 & p3) & <r1>(p102 & ~ p103 & ~ p3))) & (~ (p102 & ~ p103) | (<r1>(p103 & ~ p104 & p4) & <r1>(p103 & ~ p104 & ~ p4)))) & [r1][r1]((~ p101 | p100) & (~ p102 | p101) & (~ p103 | p102) & (~ p104 | p103) & (~ p100 | ((~ p1 | [r1](~ p100 | p1)) & (p1 | [r1](~ p100 | ~ p1)))) & (~ p101 | ((~ p2 | [r1](~ p101 | p2)) & (p2 | [r1](~ p101 | ~ p2)))) & (~ p102 | ((~ p3 | [r1](~ p102 | p3)) & (p3 | [r1](~ p102 | ~ p3)))) & (~ p103 | ((~ p4 | [r1](~ p103 | p4)) & (p4 | [r1](~ p103 | ~ p4)))) & (~ (p100 & ~ p101) | (<r1>(p101 & ~ p102 & p2) & <r1>(p101 & ~ p102 & ~ p2))) & (~ (p101 & ~ p102) | (<r1>(p102 & ~ p103 & p3) & <r1>(p102 & ~ p103 & ~ p3))) & (~ (p102 & ~ p103) | (<r1>(p103 & ~ p104 & p4) & <r1>(p103 & ~ p104 & ~ p4)))) & [r1][r1][r1]((~ p101 | p100) & (~ p102 | p101) & (~ p103 | p102) & (~ p104 | p103) & (~ p100 | ((~ p1 | [r1](~ p100 | p1)) & (p1 | [r1](~ p100 | ~ p1)))) & (~ p101 | ((~ p2 | [r1](~ p101 | p2)) & (p2 | [r1](~ p101 | ~ p2)))) & (~ p102 | ((~ p3 | [r1](~ p102 | p3)) & (p3 | [r1](~ p102 | ~ p3)))) & (~ p103 | ((~ p4 | [r1](~ p103 | p4)) & (p4 | [r1](~ p103 | ~ p4)))) & (~ (p100 & ~ p101) | (<r1>(p101 & ~ p102 & p2) & <r1>(p101 & ~ p102 & ~ p2))) & (~ (p101 & ~ p102) | (<r1>(p102 & ~ p103 & p3) & <r1>(p102 & ~ p103 & ~ p3))) & (~ (p102 & ~ p103) | (<r1>(p103 & ~ p104 & p4) & <r1>(p103 & ~ p104 & ~ p4))))) end ', ]
##    
##    fs = ['begin ~ (~ (p100 & ~ p101 & (~ p101 | p100) & (~ p102 | p101) & (~ p100 | ((~ p1 | [r1](~ p100 | p1)) & (p1 | [r1](~ p100 | ~ p1)))) & (~ p101 | ((~ p2 | [r1](~ p101 | p2)) & (p2 | [r1](~ p101 | ~ p2)))) & (~ (p100 & ~ p101) | (<r1>(p101 & ~ p102 & p2) & <r1>(p101 & ~ p102 & ~ p2))) & [r1]((~ p101 | p100) & (~ p102 | p101) & (~ p100 | ((~ p1 | [r1](~ p100 | p1)) & (p1 | [r1](~ p100 | ~ p1)))) & (~ p101 | ((~ p2 | [r1](~ p101 | p2)) & (p2 | [r1](~ p101 | ~ p2)))) & (~ (p100 & ~ p101) | (<r1>(p101 & ~ p102 & p2) & <r1>(p101 & ~ p102 & ~ p2))))) | ~ [r1]p2) end ']
##    fs = ['begin ~ ([r1]([r1]p1 | [r1]<r1>~ p1)    | <r1>[r1]false | <r1>([r1]p1 & <r1><r1>~ p1) | <r1>([r1]<r1>p1 & <r1><r1>[r1]~ p1) | <r1>([r1]p1 & [r1]~ p1) | <r1>([r1]([r1]~ p1 | p1) & <r1><r1>(<r1>p1 & ~ p1)) | <r1>([r1](<r1>~ p1 | p1) & <r1><r1>([r1]p1 & ~ p1))) end']
#    for f in fs:
#        pf = _bnf().parseString(f)
##        print pf[0]
##        bnf_f = _to_bnf(pf[0])
##        print bnf_f
##        k_f = _to_kcnf(bnf_f)
#        k_f = intohylo_to_intohylo_kcnf(pf[0])
#        print  k_f
#        kcnf = _dump_intohylo_kcnf(k_f)
#        print kcnf
#        print intohylo_kcnf._parse(kcnf)
    
