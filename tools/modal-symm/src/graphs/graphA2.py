'''
Created on Oct 15, 2011

@author: eorbe
'''
import graphs.PyBliss
from graphs import PyBliss
from parsers import fparser
from utils.misc import print_msg, print_report
import os.path
import time

_formula_gens = None
_graph_gens = None

def _process_perm(perm, args = None):
    n = args
    #_graph_gens.append(perm)
    gen = []
    gen2 = []
    for k1, k2 in perm.iteritems():
        if k1 != k2:
            n1 = n[k1][0]
            n2 = n[k2][0]
            gen2.append((k1, k2))
            if n1['node-type'] == 'var' and n2['node-type'] == 'var':
                gen.append(((n1['md'], n2['md']), (n1['vid'], n2['vid'])))
                
    _graph_gens.append(gen2)
    if(len(gen) > 0):
        _formula_gens.append(gen)
            
def _generate_bliss_graph (clauses, vars, edges):
    g = PyBliss.Graph()
    n = 1
    nodes2 = {}
    edges2 = []
    nodes_mapping = []
    for c in clauses:
        l = 'c-%s-%s' % (c['type'], n)
        c['gid'] = l
        nodes2[l] = (c, c['type'])
        g.add_vertex(l, c['type'])
        nodes_mapping.append(('c Md: %s - Clause %s => Node %s\n' % (c['md'], c['cid'], n)))
        n = n + 1

    #add var nodes
    md = -1
    for mdv in vars:
        md = md + 1
        for v in mdv.keys():
            l = 'v%s' % n
            mdv[v]['gid'] = l
            nodes2[l] = (mdv[v], 3) 
            g.add_vertex(l, 3)
            nodes_mapping.append(('c Md: %s - Lit %s => Node %s\n' % (md, v, n)))
            n = n + 1
    
    md = -1
    for mde in edges:
        md = md + 1
        el = mde['v-v']
        for e in el:
            v1 = vars[md][e[0]]['gid']
            v2 = vars[md][e[1]]['gid']
            g.add_edge(v1, v2)
            edges2.append((v1, v2))

        el = mde['c-v']
        for e in el:
            c = clauses[e[0]]['gid']
            v = vars[md][e[1]]['gid']
            g.add_edge(c, v)
            edges2.append((c, v))
            
        el = mde['c-c']
        for e in el:
            c1 = clauses[e[0]]['gid']
            c2 = clauses[e[1]]['gid']
            g.add_edge(c1, c2)
            edges2.append((c1, c2))

    return g , nodes2, edges2, nodes_mapping

def _generate_generators_file(filename):
    f = open(filename, 'w')
    f.write('Formula Generators:\n')
    f.writelines([str(g) + '\n' for g in _formula_gens])
    f.write('Graph Generators:\n')
    f.writelines([str(g) + '\n' for g in _graph_gens])
    f.close()

def _generate_dot_file(filename, graph):
    f = open(filename, 'w')
    graph.write_dot(f)
    f.close()
        
def _generate_bliss_file(filename, nodes, edges, mapping):
    f = open(filename, 'w')
    p_desc = 'p edge %s %s\n' % (len(nodes), len(edges))
    colors = []
    links = []
    for k, v in nodes.iteritems():
        node_label = k[1:] if k[0] == 'v' else k.rpartition('-')[2]
        n_desc = 'n %s %s\n' % (node_label, v[1])
        colors.append(n_desc)
    for e in edges:
        n1_label = e[0][1:] if e[0][0] == 'v' else e[0].rpartition('-')[2]
        n2_label = e[1][1:] if e[1][0] == 'v' else e[1].rpartition('-')[2]
        e_desc = 'e %s %s\n' % (n1_label, n2_label)
        links.append(e_desc)
    
    f.writelines(mapping)
    f.write(p_desc)
    f.writelines(colors)
    f.writelines(links)
    f.close()
    
def calculate_symmetries(problem_name , formula, results_dir, bliss_file = True,
                         dot_file = True):
    
    global _graph_gens
    global _formula_gens
    _graph_gens = []
    _formula_gens = []

    print_msg('Generating Intermediate graph representation...')
    c, v, e = fparser.parse_formula(formula)
    
    print_msg('Generating Bliss graph...')
    g, nodes, edges, mapping = _generate_bliss_graph(c, v, e)

    nn = g.nof_vertices()
    nc = len(c)
    nv = sum(map(len, v))
    print_msg(('Nof Nodes: %s - Nof Clauses: %s - Nof Literals: %s' % (nn, nc, nv)))
    
    bf = ''
    if bliss_file:
        bf = os.path.join(results_dir, (problem_name + '.bliss'))
        print_msg('Generating Bliss file...')
        _generate_bliss_file(bf, nodes, edges, mapping)
        
    df = ''
    if dot_file:
        df = os.path.join(results_dir, (problem_name + '.dot'))
        print_msg('Generating Dot file...')
        _generate_dot_file(df, g)
        
    start = time.time()
    print_msg('Calculating graph automorphisms...')
    gz = g.find_automorphisms(_process_perm, nodes)
    duration = time.time() - start
    
    gf = os.path.join(results_dir, (problem_name + '.gens'))
    print_msg('Generating generators file...')
    _generate_generators_file(gf)
    
    ngg = len(_graph_gens)
    nfg = len(_formula_gens)
    
    _graph_gens = None
    _formula_gens = None
    
    res = [problem_name.replace(',', ';'), nn, nc, nv, ngg, nfg, gz, duration]
    
    print_report(res)
    #print_msg('---------------------------------------------')
    return res
        
    
#def run():
#    f = ksc.parse_ksc('/Users/eorbe/Downloads/mCNF-d06/c030v03p00d06s00.ksc')
#    c, v, e = fparser.parse_formula(f)
#    g, n = generate_bliss_graph(c, v, e)
#    g.find_automorphisms(report, ('Generators', n, e))
#    print len(gens)
#

#if __name__ == "__main__":
###    f = [[1, -2, 3], [-3, 2, '-#', [-40, -6]]]
###    f = (('', ('-[]', -1, ('[]', -2), ('[]', 2))), ('', ('-[]', -2, ('[]', -1), ('[]', 1))))
#    f = [['-#', [-1, '#', [-2], '#', [2]]], ['-#', [-2, '#', [-1], '#', [1]]]]
#    rdir = '/Users/eorbe/Documents/phd/research/modal-logics/code/modal-symm/src/tests'
#    print calculate_symmetries('symm-form', f, rdir, True, True,)
###    f = ksc.parse_ksc('/Users/eorbe/Downloads/mCNF-d06/c210v03p00d06s03.ksc')
###    cProfile.run('run()')
##    run()
    
