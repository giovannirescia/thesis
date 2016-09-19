'''
Created on Oct 14, 2011

@author: eorbe
'''
from pyparsing import *

def p_lit(s, l, toks):
    return int(toks[0][1:])
    
def negated_p_lit (s, l, toks):
    return (toks[0][1] * -1)

def m_lit(s, l, toks):
    toks[0] = '#'
    return toks
    
def negated_m_lit(s, l, toks):   
    toks[1] = '-#'
    return toks[1:]
     
def _bnf():
    _or = Literal('OR').suppress()
    _and = Literal('AND').suppress()
    neg = Literal('NOT')
    lpar = Literal('(').suppress()
    rpar = Literal(')').suppress()
    box = Literal('ALL R1')  
    litnum = '1234567890'
    pvar = Word('C' + litnum, litnum).setParseAction(p_lit)
    npvar = (lpar + Group(neg + pvar) + rpar).setParseAction(negated_p_lit)
    plit = pvar | npvar
    clause = Forward()
    mvar = (lpar + box + clause + rpar).setParseAction(m_lit)
    nmvar = (lpar + neg + mvar + rpar).setParseAction(negated_m_lit)
    mlit = mvar | nmvar
    lit = plit | mlit
    clause << lpar + _or + Group(OneOrMore(lit)) + rpar
    alccnf = lpar + _and + OneOrMore(clause) + rpar
    return alccnf

def _parse(f):
    return _bnf().parseString(f)

def _loader(filename):
    f = open(filename, 'r')
    c = f.readlines()
    return ''.join(c)


def parse_alc(filename):
    f = _loader(filename)
    pf = _parse(f)
    return pf

if __name__ == "__main__":
    fs = ['(AND (OR C1))', '(AND (OR (NOT C1)))', '(AND (OR C1 C2))',
          '(AND (OR (NOT C1) C2))', '(AND (OR C1 (NOT C2)))', '(AND (OR (NOT C1) (NOT C2)))',
          '(AND (OR C1 C2 C3))', '(AND (OR C1 C2 (NOT C3)))', '(AND (OR (ALL R1 (OR C1))))',
          '(AND (OR (NOT (ALL R1 (OR C1)))))', '(AND (OR (ALL R1 (OR (NOT C1)))))',
          '(AND (OR (NOT (ALL R1 (OR (NOT C1))))))', '(AND (OR (NOT (ALL R1 (OR (NOT C1)))) C2))',
          '(AND (OR (NOT (ALL R1 (OR (NOT C1)))) (NOT C2)))', '(AND (OR (NOT (ALL R1 (OR (NOT C1) C2)))))',
          '(AND (OR (NOT (ALL R1 (OR (NOT (ALL R1 (OR (ALL R1 (OR (NOT (ALL R1 (OR (NOT C6))))))))))))))',
          '(AND (OR (NOT (ALL R1 (OR (NOT (ALL R1 (OR (ALL R1 (OR (NOT (ALL R1 (OR (NOT C6)))))))))))) (NOT C2)))',
          '(AND (OR (NOT (ALL R1 (OR (NOT (ALL R1 (OR (ALL R1 (OR (NOT (ALL R1 (OR (NOT C6)))))))))))) (ALL R1 (OR (NOT (ALL R1 (OR (NOT C4))))))))',
          '(AND (OR C1 (NOT C5) (ALL R1 (OR (NOT C6))) (ALL R1 (OR (NOT (ALL R1 (OR (NOT C4) (ALL R1 (OR C9 C8)))))))) (OR (NOT (ALL R1 (OR C1)))))']
    
    #fs = ['begin -[R1]-P1 v P2 end']
    
    for f in fs:
        #print f 
        print _bnf().parseString(f)
