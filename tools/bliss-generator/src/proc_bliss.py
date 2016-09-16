import re
import os
import sys
import datetime
#import time


def load_file(f):
    with open(f) as f:
        lns = f.readlines()
        comm = filter(lambda l: l.startswith('c'), lns)
        stats = filter(lambda l: not l.startswith('c #'), comm)
        mapping = filter(lambda l:l.startswith('c #'), comm)
        #nodes = filter(lambda l: l.startswith('n'), lns)
        return stats, [], mapping[1:]

def _process_nodes(ns):
    pattern = 'n\s(\d*)\s(\d*)'
    prog = re.compile(pattern)
    clauses = {'cl':[], 'bcl':[], 'nbcl':[], 'nofn':0}
    literals = []
    for n in ns:
        result = prog.match(n)
        nid, type = map(int, result.groups())
        if type == 1:
            clauses['cl'].append(nid)
        elif type == 2:
            clauses['bcl'].append(nid)
        elif type == 3:
            clauses['nbcl'].append(nid)
        elif type == 4:
            literals.append(nid)
    clauses['nofn'] = len(clauses['cl']) + len(clauses['bcl']) + len(clauses['nbcl'])
    return clauses, literals

def _str_to_tuple(s):
    l = []
    for t in s:
        a, b = map(int, t)
        l.append((a, b))
    return l
    
def _process_stats(sts):
    comp_time = float(re.search('(\d+\.\d+)', sts[0]).group(0))
    aut_time = float(re.search('(\d+\.\d+)', sts[-1]).group(0))
    if sts[-2].find('inf') == -1:
        aut_size = float(re.search('(\d+\.*\d*e?\+?\d*)', sts[-2]).group(0))
    else:
        aut_size = -1
    nof_gens = float(re.search('(\d+\.*\d*e?\+?\d*)', sts[-4]).group(0))
    gens = []
    for g in sts[2:-8]:
        perm = _str_to_tuple(re.findall('\((\d+),(\d+)\)', g))
        gens.append(perm)
    return comp_time, aut_time, aut_size, nof_gens, gens

def _process_var_mapping(mp):
    var_map = {}
    pat = re.compile('(\d+) (\d+) (-?\d+)')
    for m in mp:
        m1 = map(int, pat.search(m).groups())
        key = m1[1]
        del m1[1]
        var_map[key] = m1
    
    return var_map

def _check_spurious_gens(lits, perms):
    sp = []
    lit_perms = []
    for p in perms:
        lit_perm = []
        cl_perm = []
        for c in p:
            if (c[0] in lits) and (c[1] in lits):
                lit_perm.append(c)
            else:
                cl_perm.append(c)
        if len(lit_perm) == 0:
            sp.append(p) 
        else:
            lit_perms.append(lit_perm)
    return len(sp), lit_perms, sp
            
def _translate_generators(n_gens, v_map):
    l_gens = []
    for g in n_gens:
        l_g = []
        for c in g:
            l1 = v_map[c[0]]
            l2 = v_map[c[1]]
            if l1[0] == l2[0]:
                l_g.append((l1[0], l1[1], l2[1]))
            else:
                print_msg('Modal depth doesn''t match')
        l_gens.append(l_g)
    return l_gens

def dump_symm_file(rfolder, filename, gens):
    str_gens = []
    for g in gens:
        str_g = []
        for c in g:
            str_g.append(('%s %s %s' % c))
        str_gens.append(','.join(str_g))
    
    fname = os.path.join(rfolder, ('%s.symm' % filename))
    f = open(fname, 'w')
    for s in str_gens:
        f.write(s + '\n')
    #print_msg(('Symm file: %s' % fname))
#    print fname
    
def get_stats(filename, rfolder):
    problem = os.path.splitext(os.path.basename(filename))[0]
    raw_stats, _, raw_mapping = load_file(filename)
    #clauses, literals = _process_nodes(raw_nodes)
    var_map = _process_var_mapping(raw_mapping)
    stats = _process_stats(raw_stats)
    nof_sp, gens, _ = _check_spurious_gens(var_map.keys(), stats[-1])
    lit_gens = _translate_generators(gens, var_map)
    if len(lit_gens) > 0:
        dump_symm_file(rfolder, problem, lit_gens)
    data = [0,
            0,
            0,
            stats[3],
            (stats[3] - nof_sp),
            stats[2],
            (stats[0] + stats[1]),
            stats[1],
            stats[0]]
#    data = [clauses['nofn'] + len(literals),
#            clauses['nofn'],
#            len(literals),
#            stats[3],
#            (stats[3] - nof_sp),
#            stats[2],
#            (stats[0] + stats[1]),
#            stats[1],
#            stats[0]]
    
    return data

folder = None
if len(sys.argv) > 1:
    file = sys.argv[1]
    folder = sys.argv[2]
else:
    print 'No file specified. '
    exit(0)

problem = os.path.basename(file).upper()
try:
    #print_msg(('Processing: %s...' % problem))
    #print problem   
    data = [problem] + get_stats(file, folder)
    print ','.join(map(str,data))
except Exception, e:
    print problem + "," + str(e)

