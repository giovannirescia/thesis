#!/usr/bin/env python
'''
Created on Oct 18, 2011

@author: eorbe
'''
import os.path
from graphs.graphA2 import calculate_symmetries
from  utils.misc import print_msg
from parsers.ksc import parse_ksc
from parsers.alc import parse_alc
from parsers.intohylo_kcnf import parse_intohylo_kcnf
from parsers.intohylo2 import parse_intohylo
import time
import signal
import sys
from pyparsing import ParseException



RESULTS_ROOT = '/Users/eorbe/Documents/phd/research/modal-logics/code/modal-symm/src/results'
BENCHMARK_FOLDERS = ['/Users/eorbe/Documents/phd/research/modal-logics/code/modal-symm/src/results/test5_kcnf-1320203896/']
CSV_HEADER = ['PROBLEM', 'NOF_NODES', 'NOF_CLAUSE_NODES', 'NOF_LITERAL_NODES',
              'GRAPH_GENERATORS', 'FORMULA_GENERATORS', 'GROUP_SIZE', 'SEARCH_TIME' , 'PROBLEM_FILE']

stats = []     
results_folder = os.path.join(RESULTS_ROOT, ('run-%s' % int(time.time())))

    
def list_files(folder, ext):
    sfiles = []
    for root, _, files  in os.walk(folder):
        for f in files:
            _, e = os.path.splitext(f)
            if e == ext:
                sfiles.append(os.path.join(root, f))
    return sfiles
    
def dump_csv(data, rfolder):
    if len(data) > 0:
        fname = os.path.join(rfolder, 'stats.csv')
        f = open(fname, 'w')
        f.write(','.join(CSV_HEADER) + '\n')
        for s in data:
            f.write(','.join([str(e) for e in s]) + '\n')
        print_msg(('Stats file: %s' % fname))
    

def sig_handler(signum, frame):
    global stats
    global results_folder
    print_msg('Cancelling the job...')
    print_msg('Dumping .csv file')
    dump_csv(stats, results_folder)
    print_msg('Bye...')
    exit(0)

signal.signal(signal.SIGTERM, sig_handler)
signal.signal(signal.SIGINT, sig_handler)
if not os.path.exists(results_folder):
    os.mkdir(results_folder)

folder = None
if len(sys.argv) > 2:
    format = '.' + sys.argv[1]
    folder = sys.argv[2]
else:
    print_msg('No format or benchmark folder specified. Available formats: ksc | alc | intohylo | cnf (hgen formulas) .')
    exit(0)
#format = 
   
print_msg(('Looking for %s files in %s' % (format, folder)))
problems = list_files(folder, format)
print_msg(('%s problems were find' % len(problems)))
for p in problems:
    problem = os.path.basename(p).upper()
    problem_folder = os.path.basename(os.path.dirname(p))
    problem_folder = os.path.join(results_folder, problem_folder)
    try:
        if not os.path.exists(problem_folder):
            os.mkdir(problem_folder)
        print_msg(('Problem: %s...' % problem))
        print_msg('Loading file: %s ...' % p)
        if format == '.ksc':
            formula = parse_ksc(p)
        elif format == '.alc':
            formula = parse_alc(p)
        elif format == '.cnf':
            formula = parse_intohylo_kcnf(p)
        elif format == '.intohylo':
            pass
        else:
            print_msg('Unrecognized format. Available formats: ksc | alc | intohylo| cnf .')
            exit(0)
        data = calculate_symmetries(problem, formula, problem_folder)
        data.append(p.replace(',', ';'))
        stats.append(data)
        formula = None
    except ParseException, e:
        print_msg(('**************************PARSE EXCEPTION in problem: %s *******************************' % problem))
        print_msg(e)
    except Exception, e:
        print_msg(('**************************EXCEPTION in problem: %s *******************************' % problem))
        print_msg(e)

dump_csv(stats, results_folder)


