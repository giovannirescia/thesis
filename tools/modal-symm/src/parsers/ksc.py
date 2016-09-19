'''
Created on Oct 14, 2011

@author: eorbe
'''
from pyparsing import *


def m_lit(s, l, toks):
    return '#'

def negated_m_lit(s, l, toks):
    return '-#'

def cvtInt(s, l, toks):
    return int(toks[0])

def _bnf():
    _or = Literal('v').suppress()
    _and = Literal('^').suppress()
    lpar = Literal('(').suppress()
    rpar = Literal(')').suppress()
    box = Literal('#(0)').setParseAction(m_lit) | Literal('-#(0)').setParseAction(negated_m_lit) 
    wff = Literal('wff').suppress()
    eq = Literal ('=').suppress()
    litnum = '1234567890'
    clause = Forward()
    nlit = Combine(Optional('-') + Word(litnum, litnum)).setParseAction(cvtInt)
    mlit = box + lpar + clause + rpar 
    lit = nlit | mlit
    clause << _or + Group(lpar + OneOrMore(lit) + rpar)
    kcnf = wff + eq + _and + lpar + OneOrMore(clause) + rpar
    return kcnf

def _parse(f):
    return _bnf().parseString(f)

def _loader(filename):
    f = open(filename, 'r')
    c = f.readlines()
    return ''.join(c)


def parse_ksc(filename):
    f = _loader(filename)
    pf = _parse(f)
    return pf
    
if __name__ == "__main__":
    pass
#    #f = _loader('/Users/eorbe/Documents/phd/research/modal-logics/code/modal-symm/src/tests/c030v03p00d04s00.ksc')
#    #f = 'wff = ^(v(1 02 -30))'
#    #f = 'wff = ^(v(1 2 3) v(-3 4 -5))'
#    f = 'wff = ^(v(1 2 3) v(-3 4 -#(0)(v(-40 -6)) ))'
#    #f = 'wff = ^(v(-6 -#(0)(v(1 2 -#(0)(v(1 2))  -4)) -5))'
#    #f = 'wff = ^( v(-#(0)(v(#(0)( v(#(0)( v(-#(0)( v(1  -2  3  )) -#(0)( v(1  -2  -3  )) -#(0)( v(-1  2  -3  )) )) #(0)( v(#(0)( v(1  2  -3  )) -#(0)( v(1  2  3  )) -#(0)( v(-1  -2  -3  )) )) -#(0)( v(-#(0)( v(-1  -2  3  )) #(0)( v(1  2  -3  )) #(0)( v(-1  2  3  )) )) ))#(0)( v(#(0)( v(#(0)( v(-1  -2  -3  )) -#(0)( v(-1  2  3  )) #(0)( v(-1  2  -3  )) )) -#(0)( v(-#(0)( v(1  -2  -3  )) -#(0)( v(-1  -2  -3  )) -#(0)( v(-1  2  -3  )) )) #(0)( v(#(0)( v(1  -2  -3  )) #(0)( v(-1  -2  3  )) #(0)( v(1  2  3  )) )) ))-#(0)( v(-#(0)( v(#(0)( v(1  2  3  )) -#(0)( v(-1  2  3  )) -#(0)( v(1  2  -3  )) )) -#(0)( v(-#(0)( v(-1  -2  3  )) -#(0)( v(1  2  3  )) #(0)( v(1  -2  3  )) )) #(0)( v(-#(0)( v(1  -2  -3  )) -#(0)( v(1  2  -3  )) -#(0)( v(-1  -2  3  )) )) )))) -#(0)(v(-#(0)( v(-#(0)( v(-#(0)( v(-1  2  -3  )) #(0)( v(1  2  3  )) #(0)( v(1  2  -3  )) )) #(0)( v(-#(0)( v(1  -2  -3  )) -#(0)( v(-1  2  -3  )) -#(0)( v(-1  2  3  )) )) #(0)( v(#(0)( v(-1  2  -3  )) #(0)( v(-1  -2  3  )) -#(0)( v(-1  -2  -3  )) )) ))-#(0)( v(#(0)( v(-#(0)( v(-1  -2  3  )) -#(0)( v(1  -2  3  )) -#(0)( v(-1  -2  -3  )) )) -#(0)( v(#(0)( v(1  -2  -3  )) -#(0)( v(1  2  3  )) #(0)( v(1  2  -3  )) )) #(0)( v(#(0)( v(-1  2  3  )) #(0)( v(-1  -2  -3  )) #(0)( v(-1  -2  3  )) )) ))-#(0)( v(#(0)( v(#(0)( v(-1  -2  3  )) -#(0)( v(-1  2  3  )) #(0)( v(-1  -2  -3  )) )) #(0)( v(#(0)( v(1  -2  -3  )) -#(0)( v(-1  -2  3  )) -#(0)( v(1  2  3  )) )) #(0)( v(-#(0)( v(-1  -2  -3  )) #(0)( v(1  2  3  )) #(0)( v(1  -2  -3  )) )) )))) -#(0)(v(#(0)( v(-#(0)( v(-#(0)( v(1  -2  3  )) -#(0)( v(-1  2  -3  )) #(0)( v(-1  -2  -3  )) )) -#(0)( v(-#(0)( v(-1  2  3  )) #(0)( v(-1  -2  -3  )) -#(0)( v(-1  -2  3  )) )) #(0)( v(-#(0)( v(1  -2  3  )) -#(0)( v(-1  2  3  )) -#(0)( v(1  2  -3  )) )) ))-#(0)( v(#(0)( v(-#(0)( v(-1  -2  3  )) #(0)( v(1  -2  -3  )) -#(0)( v(1  2  3  )) )) #(0)( v(#(0)( v(-1  2  -3  )) -#(0)( v(-1  -2  -3  )) #(0)( v(-1  -2  3  )) )) -#(0)( v(-#(0)( v(1  -2  3  )) #(0)( v(1  -2  -3  )) #(0)( v(1  2  -3  )) )) ))#(0)( v(#(0)( v(-#(0)( v(1  -2  -3  )) #(0)( v(-1  -2  3  )) #(0)( v(-1  2  -3  )) )) #(0)( v(-#(0)( v(-1  2  3  )) #(0)( v(1  -2  -3  )) -#(0)( v(-1  -2  3  )) )) #(0)( v(-#(0)( v(1  2  3  )) -#(0)( v(1  -2  3  )) -#(0)( v(-1  -2  -3  )) )) )))) ))'
#    p = _bnf().parseString(f)
#    #p = parse_ksc('/Users/eorbe/Documents/phd/research/modal-logics/code/modal-symm/src/tests/c030v03p00d04s00.ksc')
#    print len(p)
#    print p
