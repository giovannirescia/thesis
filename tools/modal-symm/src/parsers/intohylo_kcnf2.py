'''
Created on Dec 12, 2011

Parse files generate using the to_kcnf haskell tool.

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
            matchExpr = lastExpr + ZeroOrMore(opExpr + lastExpr)
        elif rightLeftAssoc == opAssoc.RIGHT:
            matchExpr = opExpr + Group(thisExpr)
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
    #return int(toks[0][1:])
    return toks

def box(s, l, toks):
    return '#'

def neg_box(s, l, toks):
    return '-#'

def _bnf():

    _begin = Literal('begin')
    _end = Literal('end')
    _top = Literal('true')
    _bottom = Literal('false')
    _or = Literal('v').suppress()
    _neg = Literal('-')
    _modal_op = Literal('[R1]').setParseAction(box) | Literal('-[R1]').setParseAction(neg_box)
    _var = Word('P1234567890', '1234567890').setParseAction(var)
    _lit = _var | (_neg + _var).setParseAction(neg_var)
     
    base_expr = _top | _bottom | _lit
    formula = _operatorPrecedence(base_expr, [(_modal_op, 1, opAssoc.RIGHT),
                                             (_or, 2, opAssoc.LEFT)])
    intohylo = _begin + delimitedList(Group(formula), ';') + _end
    #intohylo = _begin + delimitedList(formula, ';') + _end 
    
    return intohylo.setDebug()

def _parse(f):
    return _bnf().parseString(f)

def _loader(filename):
    f = open(filename, 'r')
    c = f.readlines()
    return ''.join(c)


def parse_intohylo_kcnf(filename):
    f = _loader(filename)
    pf = _parse(f)
    return pf

if __name__ == "__main__":
    fs = ['begin P1 end', 'begin -OP1 end', 'begin P1 v P2 end',
          'begin -P1 v P2 end', 'begin P1 v -P2 end', 'begin -P1 v -P2 end',
          'begin P1 v P2 v P3 end', 'begin P1 v P2 v -P3 end',
          'begin [R1]P1 end', 'begin -[R1]P1 end', 'begin [R1]-P1 end', 'begin -[R1]-P1 end',
          'begin -[R1]-P1 v P2 end', 'begin -[R1]-P1 v -P2 end',
          'begin -[R1](-P1 v P2) end', 'begin -[R1]-[R1][R1]-[R1]-P6 end',
          'begin -[R1]-[R1][R1]-[R1]-P6  v -P2 end', 'begin -[R1]-[R1][R1]-[R1]-P6 v [R1]-[R1]-P4 end',
          'begin P1 v -P5 v [R1]-P6 v [R1]-[R1](-P4 v [R1](P9 v P8)); -[R1]P1 end']
    
    #fs = ['begin -[R1]-P1 v P2 end']
    
    for f in fs:
        print f 
        print _bnf().parseString(f)
