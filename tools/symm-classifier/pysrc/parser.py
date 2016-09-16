'''
Created on Nov 2, 2012

@author: ezequiel
'''

import re


def parse_symm_file(filename):
    with open(filename,'r') as f:
        lines = map(lambda x: x.rstrip('\n'), 
                    filter(lambda x: not x.startswith('%%') and x!='\n', 
                           f.readlines()))
        symms = []
        for l in lines:
            s = map(lambda x: map(int,x),
                    map(str.split, l.split(',')))
            symms.append(_break_seq(s))
        return symms
    
def _break_seq(symm_seq):
    perms = {}
    for s in symm_seq:
        md = s[0]
        if not md in perms:
            perms[md] = []
        perms[md].append(s[1:])
    return {'type':None,'perms':perms}

def parse_vars_analyzer_file(filename):
    with open(filename, 'r') as f:
        stats = {}
        content = _preprocess_contents(f.readlines())
        problem = None
        for ln in content:
            key, value = _parse_line(ln)
            if key == 'problem':
                problem = value
                if not value in stats:
                    stats[value] = []
            elif key == 'data':
                data = _process_data(value)
                stats[problem].append(data)
        return stats

def _preprocess_contents(lines):
    lns1 = filter(lambda x: x != '\n', lines)
    lns2 = map(lambda x: x.rstrip('\n'), lns1)
    return lns2
    
def _parse_line(ln):
    if  ln.startswith('md'):  # is a stat line
        return ('data', _parse_var_type_line(ln))
    else:  # is the name of the formula
        return ('problem', ln)

# md:(.*) ==> A= (.*):(.*) - B= (.*):(.*) - C= (.*):(.*) - D= (.*):(.*)
def _parse_var_type_line(ln):
    patt = re.compile('md:(.*) ==> A= (.*):(.*) - B= (.*):(.*) - C= (.*):(.*) - D= (.*):(.*)')
    m = patt.match(ln)
    return m.groups()

def _process_data(data):
    return (int(data[0]),
            int(data[1]),
            int(data[3]),
            int(data[5]),
            int(data[7]),
            data[2],
            data[4],
            data[6],
            data[8])    
