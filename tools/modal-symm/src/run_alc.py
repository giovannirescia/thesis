'''
Created on Oct 18, 2011

@author: eorbe
'''
import os.path
from graphs.graphA2 import calculate_symmetries
from  utils.misc import print_msg
from parsers.alc import parse_alc
import time


RESULTS_ROOT = '/Users/eorbe/Documents/phd/research/modal-logics/code/modal-symm/src/results'
BENCHMARK_FOLDERS = ['/Users/eorbe/Documents/phd/research/modal-logics/code/modal-symm/src/tests']
CSV_HEADER = ['PROBLEM', 'NOF_NODES', 'NOF_CLAUSE_NODES', 'NOF_LITERAL_NODES',
              'GRAPH_GENERATORS', 'FORMULA_GENERATORS', 'GROUP_SIZE', 'GEN_FILE',
              'BLISS_FILE', 'DOT_FILE', 'PROBLEM_FILE']
    
def list_files(folder, ext):
    sfiles = []
    for root, _, files  in os.walk(folder):
        for f in files:
            _, e = os.path.splitext(f)
            if e == ext:
                sfiles.append(os.path.join(root, f))
    return sfiles
    

results_folder = os.path.join(RESULTS_ROOT, ('run-%s' % int(time.time())))
if not os.path.exists(results_folder):
    os.mkdir(results_folder)

stats = []        
for folder in BENCHMARK_FOLDERS:
    print_msg(('Looking for .alc files in %s' % folder))
    problems = list_files(folder, '.alc')
    print_msg(('%s problems were find' % len(problems)))
    for p in problems:
        problem = os.path.basename(p)
        print_msg(('Problem: %s...' % problem.upper()))
        print_msg('Loading file: %s ...' % p)
        alc_formula = parse_alc(p)
        data = calculate_symmetries(problem, alc_formula, results_folder)
        data.append(p)
        stats.append(data)

if len(stats) > 0:
    fname = os.path.join(results_folder, 'stats.csv')
    f = open(fname, 'w')
    f.write(','.join(CSV_HEADER) + '\n')
    for s in stats:
        f.write(','.join([str(e) for e in s]) + '\n')

    print_msg(('Stats file: %s' % fname))
    
