'''
Created on Oct 7, 2011

@author: eorbe
'''
import sys
import os.path
import time

#INPUT FORMAT: p /\ -q /\ (-q v <>p) /\ []-q ==> (('',(p) ),('',(-q)),('',(-q,('<>',(('',(p)))))),('[]',-q))

def parse_clause(clause, md = 0, clauses = [], vars = [], edges = [], p_clause = None):

    if len(vars) < (md + 1): vars.append({})
    if len(edges) < (md + 1): edges.append({'c-v':[], 'c-c':[], 'v-v':[]})
    
    id_clause = len(clauses)
    if clause[0] == '': #prop clause
        c_data = {'md':md, 'cid':id_clause, 'gid':-1, 'type':0, 'parent':p_clause}
    elif clause[0] == '[]': #modal clause
        c_data = {'md':md, 'cid':id_clause, 'gid':-1, 'type':1, 'parent':p_clause}
    elif clause[0] == '-[]': #modal clause
        c_data = {'md':md, 'cid':id_clause, 'gid':-1, 'type':2, 'parent':p_clause}
    else:
        print 'Bad Input Formula:'
        print clause
        return None, None, None
    
    clauses.append(c_data)
    
    if p_clause is not None:
        edges[md]['c-c'].append((p_clause, id_clause))
    
    #parse clause literals
    for lit in clause[1:]:
        if isinstance(lit, list): #is a modal literal
            parse_clause(lit, (md + 1), clauses, vars, edges, id_clause)
        elif isinstance(lit, int): #is a propositional literal
            if not vars[md].has_key(lit):
                vars[md][lit] = {'vid':lit, 'gid':-1, 'md':md, 'ecount':0, 'lit':lit}
                vars[md][(lit * -1)] = {'vid':lit, 'gid':-1, 'md':md, 'ecount':0, 'lit':(lit * -1)}
                edges[md]['v-v'].append((lit, (lit * -1)))
            edges[md]['c-v'].append((id_clause, lit))
        else:
            print 'Bad Input Formula:'
            print clause
            return None, None, None
        
    return clauses, vars, edges   

def parse_formula(f):
    clauses = []
    vars = []
    edges = []
    for clause in f:
        parse_clause(clause, 0, clauses, vars, edges, None)
    return clauses, vars, edges
        

def generate_bliss_file(nodes, edges, mapping):
    folder = os.path.dirname(sys.argv[-1])
    filename = os.path.join(folder, ('graph-%s.bliss' % int(time.time())))
    f = open(filename, 'w')
    p_desc = 'p edge %s %s\n' % (len(nodes), len(edges))
    colors = []
    links = []
    for n in nodes:
        n_desc = 'n %s %s\n' % (n[0], n[1])
        colors.append(n_desc)
    for e in edges:
        e_desc = 'e %s %s\n' % (e[0], e[1])
        links.append(e_desc)
    
    f.writelines(mapping)
    f.write(p_desc)
    f.writelines(colors)
    f.writelines(links)
    f.close()
    print ''.join(mapping)
    print p_desc
    print ''.join(colors)
    print ''.join(links)
    print 'Available at: %s' % filename
    
    
                               
def generate_bliss(clauses, vars, edges):
    gnodes = []
    gedges = []
    mapping = []
    n = 1
    #add clause nodes
    for c in clauses:
        c['gid'] = n 
        gnodes.append((n, c['type']))
        desc = 'c Clause %s => Node %s\n' % (c['cid'], n)
        mapping.append(desc)
        n = n + 1
    
    #add var nodes
    md = -1
    for mdv in vars:
        md = md + 1
        for v in mdv.keys():
            mdv[v]['gid'] = n
            gnodes.append((n, (3 + md)))
            desc = 'c Md: %s - Lit %s => Node %s\n' % (md, v, n)
            mapping.append(desc)
        
            n = n + 1
    
    md = -1
    for mde in edges:
        md = md + 1

        el = mde['v-v']
        for e in el:
            v1 = vars[md][e[0]]
            v2 = vars[md][e[1]]
            gedges.append((v1['gid'], v2['gid']))


        el = mde['c-v']
        for e in el:
            c = clauses[e[0]]
            v = vars[md][e[1]]
            gedges.append((c['gid'], v['gid']))

        el = mde['c-c']
        for e in el:
            c1 = clauses[e[0]]
            c2 = clauses[e[1]]
            gedges.append((c1['gid'], c2['gid']))
     
    return gnodes, gedges, mapping
               
            
def generate_amorph(clauses, vars, edges):
    
    n_nodes = len(clauses) + sum(map(len, vars))
    
    cc_edges = [d['c-c'] for d in edges]
    cv_edges = [d['c-v'] for d in edges]
    
    n_edges = len(cv_edges) + 2 * len(cc_edges)
    cbt = []
    
    gid = -1
    #classify clauses by type    
    for t in range(3):
        cbt.append([c for c in clauses if c['type'] == t])
    for t in cbt:
        for c in t:
            gid += 1
            c['gid'] = gid
    
    
    
    return n_nodes, n_edges 

   
if __name__ == "__main__":
    #g = [[-6, '-#(0)', [1, 2, '-#(0)', [1, 2], -4], -5]]
    f = [['', -6, ['-[]', 1, 2, ['-[]', 1, 2], -4], -5]]
    #f = [['', 1], ['', -1, -2, ['[]', -2]]]
    #f = (('', 1), ('', -1, -2, ('[]', ('-[]', 3, ('[]', -1, 2)))), ('', -2, 3))
    #f = (('-[]', 1, ('-[]', 2), ('-[]', -2)), ('-[]', 2, ('-[]', 1), ('-[]', -1)))
    #f = (('', ('-[]', -1, ('[]', -2), ('[]', 2))), ('', ('-[]', -2, ('[]', -1), ('[]', 1)))) 
    c, v, e = parse_formula(f)
    n, l, m = generate_bliss(c, v, e)
    generate_bliss_file(n, l, m)
    
    #print generate_amorph(c, v, e)
