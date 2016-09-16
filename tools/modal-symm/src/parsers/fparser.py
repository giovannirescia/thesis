'''
Created on Oct 15, 2011

@author: eorbe
'''
import collections
import intohylo_kcnf

#INPUT FORMAT = [[1, 2, 3], [-3, 4, '-#', [-40, -6]]] <==> (1 v 2 v 3) ^ (-3 v 4 v -[](-40 v -6))

def parse_clause(clause, md = 0, type = 0, clauses = [], vars = [], edges = [], p_clause = None):

    if len(vars) < (md + 1): vars.append({})
    if len(edges) < (md + 1): edges.append({'c-v':[], 'c-c':[], 'v-v':[]})

    id_clause = len(clauses)
    c_data = { 'node-type':'clause', 'cid':id_clause, 'gid':-1, 'type':type, 'md':md , 'parent':p_clause}
    clauses.append(c_data)
    
    if p_clause is not None:
        edges[md]['c-c'].append((p_clause, id_clause))
    
    is_modal_clause = False
    c_type = -1
    #parse clause literals
    for lit in clause:
        if isinstance(lit, int):
            if not vars[md].has_key(lit):
                neg_lit = lit * -1
                vars[md][lit] = {'vid':lit, 'node-type':'var', 'gid':-1, 'md':md}
                vars[md][neg_lit] = {'vid':neg_lit, 'node-type':'var', 'gid':-1, 'md':md}
                edges[md]['v-v'].append((lit, neg_lit))
            edges[md]['c-v'].append((id_clause, lit))
        elif isinstance(lit, str):
            is_modal_clause = True
            if lit == '#': 
                c_type = 1
            elif lit == '-#':
                c_type = 2
        elif isinstance(lit, collections.Iterable) and is_modal_clause:
            parse_clause(lit, (md + 1), c_type, clauses, vars, edges, id_clause)
        else:
            print 'Bad Input Formula:'
            print clause
            print lit
            return None, None, None
        
    return clauses, vars, edges   

def parse_formula(f):
    clauses = []
    vars = []
    edges = []
    for clause in f:
        if isinstance(clause, int): clause = [clause]
        parse_clause(clause, 0, 0, clauses, vars, edges, None)
    return clauses, vars, edges

if __name__ == "__main__":
    #f = [[1, 2, 3], [-3, 4, '-#', [-40, -6]]]
    f = intohylo_kcnf.parse_intohylo_kcnf('/Users/eorbe/Documents/phd/research/modal-logics/code/modal-symm/src/hgen/NI20-NC3-CS[1,1,1,1]-GD5-MD5-PV3-PM1-PP1-FD/hcnfC3-S[1,1,1,1]-M1-GD5-NP3-NN0-NS0.16.cnf')
    c, v, e = parse_formula(f)
    print c
    print v
    print e 

