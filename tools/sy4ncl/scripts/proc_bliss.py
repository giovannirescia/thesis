import re
import os
import sys
import datetime
#import time


def load_file(f):
    with open(f) as f:
        lns = f.readlines()
        return lns

def _str_to_tuple(s):
    l = []
    for t in s:
        a, b = map(int, t)
        l.append((a, b))
    return l

def _process_var_mapping(mp):
    var_map = {}
    #<md> <lit> <node_id>
    pat = re.compile('(\d+) (-?\d+) (-?\d+)')
    for m in mp[1:]:
        if m.rstrip('\n') != "":
            m1 = map(int, pat.search(m).groups())
            key = m1[2]
            del m1[2]
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

def dump_symm_file(filename, gens):
    str_gens = []
    for g in gens:
        str_g = []
        for c in g:
            str_g.append(('%s %s %s' % c))
        str_gens.append(','.join(str_g))
    
    f = open(filename, 'w')
    for s in str_gens:
        f.write(s + '\n')
    
def _process_stats(sts):
    comp_time = float(re.search('(\d+\.\d+)', sts[0]).group(0))
    (top_nd, box_nd, negb_nd, lits_nd) = map(int, re.search('\[(\d+),(\d+),(\d+),(\d+)\]',sts[1]).groups())
    nodes = int(re.search('(\d+)', sts[2]).group(0))
    edges = int(re.search('(\d+)', sts[3]).group(0))
    
    if len(sts)>4:
        gens = []
        for g in sts[4:-8]:
            perm = _str_to_tuple(re.findall('\((\d+),(\d+)\)', g))
            gens.append(perm)

        nof_gens = float(re.search('(\d+\.*\d*e?\+?\d*)', sts[-4]).group(0))
    
        if sts[-2].find('inf') == -1:
            aut_size = float(re.search('(\d+\.*\d*e?\+?\d*)', sts[-2]).group(0))
        else:
            aut_size = -1
        aut_time = float(re.search('(\d+\.\d+)', sts[-1]).group(0))
    
        return  [nodes, edges, top_nd, box_nd, negb_nd, lits_nd, aut_size, nof_gens, 0, comp_time, aut_time, (comp_time + aut_time),  gens]
    else:
        return  [nodes, edges, top_nd, box_nd, negb_nd, lits_nd, -1, -1, 0, comp_time, -1, -1,  []]
    
def get_stats(stats_file, map_file):
    problem = os.path.splitext(os.path.basename(stats_file))[0]
    
    raw_stats = load_file(stats_file)
    stats = _process_stats(raw_stats)
    
    raw_map = load_file(map_file)
    var_map = _process_var_mapping(raw_map)
    nof_sp, gens, _ = _check_spurious_gens(var_map.keys(), stats[-1])
    
    stats[8] = stats[7] - nof_sp
    lit_gens = _translate_generators(gens, var_map)
    if len(lit_gens) > 0:
        fname = os.path.join(os.path.dirname(stats_file), ('%s.symm' % problem))
        dump_symm_file(fname, lit_gens)
    return [problem] + stats[:-1]

folder = None
if len(sys.argv) > 1:
    stats_file = sys.argv[1]
    map_file = sys.argv[2]
else:
    print 'No file specified. '
    exit(0)

try:
    data = get_stats(stats_file, map_file)
    print ','.join(map(str,data))
except Exception, e:
    print stats_file + "," + str(e)

