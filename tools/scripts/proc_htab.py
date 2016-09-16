'''
Created on Feb 13, 2012

@author: eorbe
'''
import re

def normalize_data(keys,data):
    for (d1,d2) in data:
        for k in keys:
            if k not in d1:
                d1[k]=0
            if k not in d2:
                d2[k]=0
    return data
    
def normalize_keys(data):
    keys=set(['SAT', 'Nsymm', 'Closed', 'Timeout','Total'])
    for (d1,d2) in data:
        k1=set(d1.keys())
        k2=set(d2.keys())
        keys=keys.union(k1)
        keys=keys.union(k2)
    return keys
        

def dump_csv(data):
    keys=data[0][0].keys()
    keys.sort()
    header=','.join(keys)
    print header
    pat = '%s,' * len(keys)
    pat = pat[:-1]
    for (d1,d2) in data:
        d1_l=[]
        d2_l=[]
        for k in keys:
            d1_l.append(d1[k])
            d2_l.append(str(d2[k]))
        print pat % tuple(d1_l)
        print ','.join(d2_l)

def load_file(f):
    with open(f) as f:
        return f.readlines()


def split_problems(lns):
    problems = {}
    symm = []
    not_symm = []
    p_name = None
    is_symm = False
    is_not_symm = False
    for i, l in enumerate(lns):
        if is_symm:
            symm.append(l)
        if is_not_symm:
            not_symm.append(l)
        if l == '--- con simetrias ---\n':
            is_symm = True
            is_not_symm = False
            p_name = lns[i - 1][:-1]
            continue
        if l == '--- sin usar simetrias ---\n':
            is_symm = False
            is_not_symm = True
            symm = symm[:-1] #remove --- sin usar simetrias --- from the list
            continue
        if is_not_symm and l.startswith('Total time:'):
            problems[p_name] = (symm, not_symm)
            is_not_symm = False
            symm = []
            not_symm = []
    return problems

def parse_out(n,t,lns):
    data = {'Problem':n,'isSymm':t, 'SAT':None, 'Nsymm':0, 'Closed':0, 'Timeout':False, 'Total':0, }
    nsymm = 0
    #pattern = re.compile('(\w+)\s.+:\s+(\d+\.*\d*)')
    pattern = re.compile('(\w+)\s.+:\s+(\d+\.*\d*e?\-?\d*)')
    pattern2 = re.compile('The formula is (\w+)')
    for l in lns:
        if l == '\n': continue
        if (l.startswith('---') or 
            l.startswith('=') or 
            l.startswith('*') or
            l.startswith('(') or
            l.startswith('Task') or
            l.startswith('All') or
            l.startswith('One')): continue
        if l == 'Rule applications:\n': continue
        if l.startswith('SYM on'):
            nsymm = nsymm + 1
        elif l.startswith('The formula is'):
            sat = pattern2.search(l).groups()
            data['SAT'] = True if sat[0] == 'satisfiable' else False
        elif l == 'Timeout.\n':
            data['Timeout'] = True
        else:
            #print l
            g = pattern.search(l).groups()
            data[g[0]] = float(g[1])
    data['Nsymm'] = nsymm
    return data
    
        
    
    
lns = load_file('/Users/eorbe/Documents/phd/research/modal-logics/code/scripts/lwb_k_transitive.out')
ps = split_problems(lns)
data = []
for k, v in ps.iteritems():
    data.append((parse_out(k,1,v[0]), parse_out(k,0,v[1])))
keys = normalize_keys(data)
data = normalize_data(keys,data)
dump_csv(data)
