'''
Created on Oct 19, 2011

@author: eorbe
'''
from pyparsing import *
import sys

def neg2(s, l, toks):
    if isinstance(toks[0][1], int):
        return toks[0][1] * (-1)
    else:
        return toks

def neg(s, l, toks):
    return int(toks[1][1:]) * (-1)
    
def var(s, l, toks):
    return int(toks[0][1:])

def box(s, l, toks):
    return '#'
def diam(s, l, toks):
    return '<>'

def dummy(s, l, toks):
    pass

def operatorPrecedence3(baseExpr, opList):
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






def _bnf():

    _begin = Literal('begin').suppress()
    _end = Literal('end').suppress()
    _top = Literal('true')
    _bottom = Literal('false')
    _or = Literal('|')
    _and = Literal('&')
    _neg = Literal('~')
    _modal_op = Literal('[r1]').setParseAction(box) | Literal('<r1>').setParseAction(diam)
    _var = Word('p1234567890', '1234567890').setParseAction(var) | (_neg + Word('p1234567890', '1234567890')).setParseAction(neg) 
     
    base_expr = _top | _bottom | _var
    formula = operatorPrecedence3(base_expr, [(_modal_op, 1, opAssoc.RIGHT),
                                             (_neg, 1, opAssoc.RIGHT, neg2),
                                             (_and, 2, opAssoc.LEFT),
                                             (_or, 2, opAssoc.LEFT)])
    intohylo = _begin + formula + _end
    
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

if __name__ == '__main__':
    print sys.getrecursionlimit()
    sys.setrecursionlimit(2000)
    #f = 'begin ~ ([r1][r1](p1 | [r1]<r1>~ p1) & <r1><r1>p3 & ~[r1]<r1>~ p4 & <r1>false)  end'
    f = 'begin ~ ([r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1](p1 & p2 & p3 & p4 & p5 & p6 & p7 & p8 & p9 & p10 & p11 & p12 & p13 & p14 & p15 & p16 & p17 & p18 & p19 & p20 & p21 & p22 & p23 & p24 & p25 & p26 & p27 & p28 & p29 & p30 & p31 & p32 & p33 & p34 & p35 & p36 & p37 & p38 & p39 & p40 & p41 & p42 & p43 & p44 & p45 & p46 & p47 & p48 & p49) | <r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>(<r1>((p1 & p2) | (~ p2 & ~ p1))) | [r1]p3 | <r1><r1>((p2 & p3) | (~ p3 & ~ p2))) | [r1]p4 | <r1><r1><r1>((p3 & p4) | (~ p4 & ~ p3))) | [r1]p5 | <r1><r1><r1><r1>((p4 & p5) | (~ p5 & ~ p4))) | [r1]p6 | <r1><r1><r1><r1><r1>((p5 & p6) | (~ p6 & ~ p5))) | [r1]p7 | <r1><r1><r1><r1><r1><r1>((p6 & p7) | (~ p7 & ~ p6))) | [r1]p8 | <r1><r1><r1><r1><r1><r1><r1>((p7 & p8) | (~ p8 & ~ p7))) | [r1]p9 | <r1><r1><r1><r1><r1><r1><r1><r1>((p8 & p9) | (~ p9 & ~ p8))) | [r1]p10 | <r1><r1><r1><r1><r1><r1><r1><r1><r1>((p9 & p10) | (~ p10 & ~ p9))) | [r1]p11 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p10 & p11) | (~ p11 & ~ p10))) | [r1]p12 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p11 & p12) | (~ p12 & ~ p11))) | [r1]p13 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p12 & p13) | (~ p13 & ~ p12))) | [r1]p14 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p13 & p14) | (~ p14 & ~ p13))) | [r1]p15 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p14 & p15) | (~ p15 & ~ p14))) | [r1]p16 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p15 & p16) | (~ p16 & ~ p15))) | [r1]p17 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p16 & p17) | (~ p17 & ~ p16))) | [r1]p18 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p17 & p18) | (~ p18 & ~ p17))) | [r1]p19 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p18 & p19) | (~ p19 & ~ p18))) | [r1]p20 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p19 & p20) | (~ p20 & ~ p19))) | [r1]p21 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p20 & p21) | (~ p21 & ~ p20))) | [r1]p22 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p21 & p22) | (~ p22 & ~ p21))) | [r1]p23 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p22 & p23) | (~ p23 & ~ p22))) | [r1]p24 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p23 & p24) | (~ p24 & ~ p23))) | [r1]p25 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p24 & p25) | (~ p25 & ~ p24))) | [r1]p26 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p25 & p26) | (~ p26 & ~ p25))) | [r1]p27 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p26 & p27) | (~ p27 & ~ p26))) | [r1]p28 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p27 & p28) | (~ p28 & ~ p27))) | [r1]p29 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p28 & p29) | (~ p29 & ~ p28))) | [r1]p30 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p29 & p30) | (~ p30 & ~ p29))) | [r1]p31 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p30 & p31) | (~ p31 & ~ p30))) | [r1]p32 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p31 & p32) | (~ p32 & ~ p31))) | [r1]p33 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p32 & p33) | (~ p33 & ~ p32))) | [r1]p34 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p33 & p34) | (~ p34 & ~ p33))) | [r1]p35 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p34 & p35) | (~ p35 & ~ p34))) | [r1]p36 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p35 & p36) | (~ p36 & ~ p35))) | [r1]p37 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p36 & p37) | (~ p37 & ~ p36))) | [r1]p38 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p37 & p38) | (~ p38 & ~ p37))) | [r1]p39 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p38 & p39) | (~ p39 & ~ p38))) | [r1]p40 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p39 & p40) | (~ p40 & ~ p39))) | [r1]p41 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p40 & p41) | (~ p41 & ~ p40))) | [r1]p42 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p41 & p42) | (~ p42 & ~ p41))) | [r1]p43 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p42 & p43) | (~ p43 & ~ p42))) | [r1]p44 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p43 & p44) | (~ p44 & ~ p43))) | [r1]p45 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p44 & p45) | (~ p45 & ~ p44))) | [r1]p46 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p45 & p46) | (~ p46 & ~ p45))) | [r1]p47 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p46 & p47) | (~ p47 & ~ p46))) | [r1]p48 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p47 & p48) | (~ p48 & ~ p47))) | [r1]p49 | <r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1><r1>((p48 & p1) | (~ p1 & ~ p48))) | [r1]p50 | [r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1][r1](~ p2 & ~ p4 & ~ p6 & ~ p8 & ~ p10 & ~ p12 & ~ p14 & ~ p16 & ~ p18 & ~ p20 & ~ p22 & ~ p24 & ~ p26 & ~ p28 & ~ p30 & ~ p32 & ~ p34 & ~ p36 & ~ p38 & ~ p40 & ~ p42 & ~ p44 & ~ p46 & ~ p48 & ~ p50 & ~ p52 & ~ p54 & ~ p56 & ~ p58 & ~ p60 & ~ p62 & ~ p64 & ~ p66 & ~ p68 & ~ p70 & ~ p72 & ~ p74 & ~ p76 & ~ p78 & ~ p80 & ~ p82 & ~ p84 & ~ p86 & ~ p88 & ~ p90 & ~ p92 & ~ p94 & ~ p96 & ~ p98)) end'
    #['~', [['#', [['#', 1], '|', ['#', ['<>', -1]]]], '&', ['<>', [['<>', 3], '&', ['#', ['<>', -4]]]], '&', ['<>', 'false']]]
    #['~', [['#', [['#', 1], '|', ['#', ['<>', -1]]]], '&', ['<>', ['<>', 3]], '&', ['#', ['<>', -4]], '&', ['<>', 'false']]]
    #['~', [[['#', [['#', 1], '|', ['#', ['<>', -1]]]], '&', ['<>', ['<>', 3]]], '|', [['#', ['<>', -4]], '&', ['<>', 'false']]]]
    #['~', [['#', [['#', 1], '|', ['#', ['<>', -1]]]], '&', ['<>', ['<>', 3]], '&', ['~', ['#', ['<>', -4]]], '&', ['<>', 'false']]]]
    f = 'begin ([r1]((p1 & ~[r1]p2) | ~[r1](~p1 | [r1]p2) | p2) end'
    print _parse(f)
