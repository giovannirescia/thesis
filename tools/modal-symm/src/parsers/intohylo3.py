'''
Created on Nov 2, 2011

@author: eorbe
'''
from pyparsing  import *
import fparser
import collections


def _operatorPrecedence(baseExpr, opList):
    ret = Forward()
    lastExpr = baseExpr | (Optional(Suppress('(')) + ret + Optional(Suppress(')')))
    for i, operDef in enumerate(opList):
        opExpr, arity, rightLeftAssoc, pa = (operDef + (None,))[:4]
        thisExpr = Forward()#.setName("expr%d" % i)
        if rightLeftAssoc == opAssoc.LEFT:
            matchExpr = Group(lastExpr + OneOrMore(opExpr + lastExpr))
        elif rightLeftAssoc == opAssoc.RIGHT:
            matchExpr = Group(opExpr + thisExpr)
        else:
            raise ValueError("operator must indicate right or left associativity")
        if pa:
            matchExpr.setParseAction(pa)
        thisExpr << (matchExpr | lastExpr)
        lastExpr = thisExpr
    ret << lastExpr
    return ret

def neg_var(s, l, toks):
    return int(toks[1]) * (-1)
    
def var(s, l, toks):
    return int(toks[0][1:])

def box(s, l, toks):
    return '#'

def neg_box(s, l, toks):
    return '-#'

def rep_and(s, l, toks):
    return '&'

def rep_or(s, l, toks):
    return '|'


def _bnf():

    _begin = Literal('begin').suppress()
    _end = Literal('end').suppress()
    _top = Literal('true')
    _bottom = Literal('false')
    _or = Literal('v').setParseAction(rep_or)
    _and = Literal('^').setParseAction(rep_and)
    _neg = Literal('-')
    _modal_op = Literal('[R1]').setParseAction(box) | Literal('-[R1]').setParseAction(neg_box)
    _var = Word('P1234567890', '1234567890').setParseAction(var)
    _lit = _var | (_neg + _var).setParseAction(neg_var)
     
    base_expr = _top | _bottom | _lit
    formula = _operatorPrecedence(base_expr, [(_modal_op, 1, opAssoc.RIGHT),
                                              (_and, 2, opAssoc.LEFT),
                                             (_or, 2, opAssoc.LEFT)])
    intohylo = _begin + formula + _end
    #intohylo = _begin + delimitedList(formula, ';') + _end 
    
    return intohylo

def _parse(f):
    return _bnf().parseString(f)

def _loader(filename):
    f = open(filename, 'r')
    c = f.readlines()
    return ''.join(c)


def parse_intohylo(filename):
    f = _loader(filename)
    pf = _parse(f)
    return pf

if __name__ == "__main__":
    fs = ['begin ([R1]((P1 ^ -[R1]P2) v -[R1](-P1 v [R1]P2) v P2) end']
    #fs = ['begin -[R1]-P1 v P2 end']
    
    for f in fs:
        print f 
        print _bnf().parseString(f)
        print ''

#    f = '/Users/eorbe/Documents/phd/research/modal-logics/benchmarks/lwb_k/lwb_k_bnf/k_ph_n.txt.21.bnf.intohylo'
#    print parse_intohylo_kcnf(f)
    
