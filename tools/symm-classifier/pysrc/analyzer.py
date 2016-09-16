'''
Created on Nov 2, 2012

@author: ezequiel
'''
import parser
from stats import SymmStats
import stats
import utils
    
def load_vars_file(filename):
    filterp = lambda x: x if x != [0] else []
    intc = lambda x: int(x) if x != '' else 0
    varstable = {}
    vars_data = parser.parse_vars_analyzer_file(filename)
    for key, data in vars_data.iteritems():
        key = utils.get_filename(key,True)
        pvars = {}
        for val in data:
            md = val[0]
            avars = map(intc, val[5].strip('[]').split(','))
            bvars = map(intc, val[6].strip('[]').split(','))
            cvars = map(intc, val[7].strip('[]').split(','))
            dvars = map(intc, val[8].strip('[]').split(','))
            pvars[md] = map(filterp, [avars, bvars, cvars, dvars])
        varstable[key] = pvars
    return varstable

def typify_symmetries(vtypes, symms):
    sts = SymmStats()
    typed_symms = []
    for s in symms:
        typed_seq = typify_symm_seq(vtypes, s,sts)
        typed_symms.append(typed_seq)
    
    return typed_symms,sts
        
def typify_symm_seq(vtypes, symm_seq,sts):
    seq_type = 'safe'
    #typed_seq = {}
    for md, symm in symm_seq['perms'].iteritems():
        symm_type, typed_symm, isSafe = typify_symm(vtypes[md], symm,sts)
        
        symm_seq['perms'][md] = typed_symm

#        if seq_type == 'safe' and symm_type.find('A') != -1:
        if seq_type == 'safe' and not isSafe:
            seq_type = 'unsafe'
    
    symm_seq['type'] = seq_type
    #stats update
    sts.update_seq_type(seq_type)
    
    return symm_seq

def typify_symm(var_types, symm,sts):
    symm_type = None
    perm_types = [0, 0, 0]
    typed_symm = []
    isSafe = True
    for cycle in symm:
        typed_cycle = []
        
        var_type = _typify_cycle_vars(cycle, var_types) 
        perm_type = _typify_cycle_perm(cycle)
        
        typed_cycle = [var_type, perm_type] + cycle
        
        typed_symm.append(typed_cycle)
        
        if symm_type is None:
            symm_type = var_type
        elif symm_type.find(var_type) == -1:
            symm_type += var_type

        if var_type == 'A':
            if perm_type == 'S':
                perm_types[0] += 1
            elif perm_type == 'P':
                perm_types[1] += 1
                isSafe = False
            else:
                perm_types[2] += 1
                isSafe = False
        
            
    symm_type = ''.join(sorted(symm_type))   
    
    #stats update
    sts.update_symm_type(symm_type)
    if symm_type.startswith('A'):
        if perm_types[1]>0: sts.update_perm_type('P')
        elif perm_types[2]>0:sts.update_perm_type('C')
        else:sts.update_perm_type('S')
    
    typed_symm = [symm_type] + typed_symm
    return symm_type,  typed_symm, isSafe

def _typify_cycle_perm(cycle):
    if len(cycle) > 2:
        raise Exception(('Cycle is not a transposition: %s' % cycle))
    l1 = cycle[0]
    l2 = cycle[1]
    if abs(l1) != abs(l2):
        if _sgn(l1) == _sgn(l2): return 'S'
        else: return 'C'
    else: return 'P'

def _typify_cycle_vars(cycle , var_types):
    l1 = abs(cycle[0])
    if l1 in var_types[0]: return 'A'
    if l1 in var_types[1]: return 'B'
    if l1 in var_types[2]: return 'C'
    if l1 in var_types[3]: return 'D'
    return 'undef'

def _sgn(number):
    return cmp(number, 0)


#v = load_vars_file('/home/ezequiel/.Phd/code/symm-classifier/out/var_analyzer2.txt')
#s = parser.parse_symm_file('/home/ezequiel/.Phd/code/symm-classifier/out/k_branch_n.txt.1.kcnf.symm')
#vt=v['k_branch_n.txt.1.kcnf.intohylo']
#a,t = typify_symmetries(vt, s)
#print a
#print stats.CSV__SYMMS_HEADER
#print t.to_list()
#utils.dump_typified_symm_file('',a)
            
