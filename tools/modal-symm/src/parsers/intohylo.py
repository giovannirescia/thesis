'''
Created on Oct 14, 2011

@author: eorbe
'''
from pyparsing import *
    #_box = Literal('[r1]')
    #_diamond = Literal('<r1>')
#    box_var = _box + _var | _box + neg_var
#    neg_box_var = _neg + box_var
#    diamond_var = _diamond + _var | _diamond + neg_var
#    neg_diamond_var = _neg + diamond_var
#    box_expr = (_box + Optional(_lpar) + expr + Optional(_rpar)) | (_neg + _box + Optional(_lpar) + expr + Optional(_rpar))
#    diamond_expr = (_diamond + Optional(_lpar) + expr + Optional(_rpar)) | (_neg + _diamond + Optional(_lpar) + expr + Optional(_rpar))
    
#    base_operand = _var | neg_var | box_var | neg_box_var | diamond_var | neg_diamond_var | _top | _bottom | neg_expr | box_expr | diamond_expr | Optional(_lpar) + expr + Optional(_rpar) 

def neg(s, l, toks):
    return toks[1] * (-1)
    
def var(s, l, toks):
    return int(toks[0][1:])

def box(s, l, toks):
    return '#'
def diam(s, l, toks):
    return '<>'


def _bnf():

    _begin = Literal('begin').suppress()
    _end = Literal('end').suppress()
    _top = Literal('true')
    _bottom = Literal('false')
    _or = Literal('|')
    _and = Literal('&')
    _neg = Literal('~')
    _lpar = Literal('(')
    _rpar = Literal(')')
    _modal_op = Literal('[r1]').setParseAction(box) | Literal('<r1>').setParseAction(diam)
    _var = Word('p1234567890', '1234567890').setParseAction(var)
    
    expr = Forward()
    neg_var = (_neg + _var).setParseAction(neg)
    
    modal_var = _modal_op + _var | _modal_op + neg_var
    neg_modal_var = _neg + modal_var
    
    neg_expr = _neg + Group(Optional(_lpar) + expr + Optional(_rpar))
    modal_expr = (_modal_op + Group(Optional(_lpar) + expr + Optional(_rpar))) | Group((_neg + _modal_op + Optional(_lpar) + expr + Optional(_rpar)))
    
    base_operand = _var | neg_var | modal_var | neg_modal_var | _top | _bottom | neg_expr | modal_expr | expr
    and_expr = base_operand + ZeroOrMore(_and + base_operand)
    expr << (and_expr + ZeroOrMore(_or + and_expr))
    hylo_form = _begin + expr + _end

    return hylo_form

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
#    fs = ['begin p23 end', 'begin ~p23 end', 'begin ~p23 & p34 end', 'begin ~p23 | p34 end',
#          'begin  [r1]~p23 end', 'begin  ~([r1]p23) end', 'begin  [r1]([r1]p23 & p3) end',
#          'begin [r1][r1][r1]~p23 end',
#          'begin [r1]p23 | <r1>~p3 end']
#
#    fs = ['([r1]p23 )| <r1>~p3)']
#    fs = ['begin p1 & p2 end',
#          'begin p1 | p2 end',
#          'begin p23 | p3 & p4 end', 'begin p23 | p3 & ~p4 end', 'begin p23 | ~(p3 & p4) end',
#          'begin ~[r1]p23 end', 'begin ~[r1]p23 & ~[r1]~p43 end', 'begin [r1](p23 & ~[r1]~p43) end',
#          'begin ~[r1](p23 & ~[r1]~p43) end', 'begin [r1]~[r1]~p43 end',
#          'begin [r1]([r1]p1 | [r1]~ p1) end', 'begin <r1>p43 end', 'begin <r1>~<r1>~p43 end',
#          'begin <r1>~[r1]~p43 end', 'begin [r1](<r1>p1 | [r1]~ p1) end',
#          'begin ~([r1]([r1]p1 & [r1]<r1>~ p1) ) end',
#          'begin ~ ([r1]([r1]p1 | [r1]<r1>~ p1)    | <r1>[r1]false | <r1>([r1]p1 & <r1><r1>~ p1) ) end',
#          'begin  ~ ([r1]([r1]p1 | [r1]<r1>~ p1)    | <r1>[r1]false | <r1>([r1]p1 & <r1><r1>~ p1) | <r1>([r1]<r1>p1 & <r1><r1>[r1]~ p1) | <r1>([r1]p1 & [r1]~ p1) | <r1>([r1]([r1]~ p1 | p1) & <r1><r1>(<r1>p1 & ~ p1)) | <r1>([r1](<r1>~ p1 | p1) & <r1><r1>([r1]p1 & ~ p1))) end',
#          'begin ~[r1]([r1]p1 & [r1]<r1>~ p1)  end', ]
#    for f in fs:
#        print f
#        print _bnf().parseString(f)

    f = 'begin  ~ ([r1]([r1]p1 | [r1]<r1>~ p1)    | <r1>[r1]false | <r1>([r1]p1 & <r1><r1>~ p1) | <r1>([r1]<r1>p1 & <r1><r1>[r1]~ p1) | <r1>([r1]p1 & [r1]~ p1) | <r1>([r1]([r1]~ p1 | p1) & <r1><r1>(<r1>p1 & ~ p1)) | <r1>([r1](<r1>~ p1 | p1) & <r1><r1>([r1]p1 & ~ p1))) end'
    #f = 'begin  p1 & p3 | p4 end'
    b = _bnf().parseString(f)
    print b
    print len(b)
#    e = b[0][0][1][0]
#    print len(e)
#    print e
#    for c in e:
#        print c
#    for c in b[0][1]:
#        print c
#        print len(c)
#        for a in c:
#            print len(a)
#            print a
