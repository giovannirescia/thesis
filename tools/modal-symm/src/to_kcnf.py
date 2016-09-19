'''
Created on Oct 31, 2011

@author: eorbe
'''
import os.path
from  utils.misc import print_msg
from parsers.intohylo3 import parse_intohylo
from parsers.kcnf_converter2 import intohylo_to_intohylo_kcnf
import time
import signal
import sys
from pyparsing import ParseException

sys.setrecursionlimit(2000)
RESULTS_ROOT = '/Users/eorbe/Documents/phd/research/modal-logics/code/modal-symm/src/results'
#BENCHMARK_FOLDERS = ['/Users/eorbe/Documents/phd/research/modal-logics/code/modal-symm/src/tests/test1']
    
def list_files(folder, ext):
    sfiles = []
    for root, _, files  in os.walk(folder):
        for f in files:
            _, e = os.path.splitext(f)
            if e == ext:
                sfiles.append(os.path.join(root, f))
    return sfiles
    
def dump(data, problem, rfolder):
    if len(data) > 0:
        fname = os.path.join(rfolder, ('%s.cnf' % problem))
        f = open(fname, 'w')
        f.write(data)
        print_msg(('CNF file: %s' % fname))
        f.close()

def sig_handler(signum, frame):
    global stats
    global results_folder
    print_msg('Cancelling the job...')
    print_msg('Bye...')
    exit(0)

#signal.signal(signal.SIGTERM, sig_handler)
#signal.signal(signal.SIGINT, sig_handler)

folder = None
if len(sys.argv) > 1:
    folder = sys.argv[1]
else:
    print_msg('No folder specified.')
    exit(0)

results_folder = os.path.join(RESULTS_ROOT, ('%s_kcnf-%s' % (os.path.basename(folder), int(time.time()))))

if not os.path.exists(results_folder):
    os.mkdir(results_folder)
   
print_msg(('Looking for intohylo files in %s' % folder))
problems = list_files(folder, '.intohylo')
print_msg(('%s problems were find' % len(problems)))

problems.sort()
for p in problems:
    problem = os.path.basename(p).upper()
    try:
        print_msg(('=========================================================================================='))
        print_msg(('Problem: %s...' % problem))
        print_msg('Loading file: %s ...' % p)
        formula = parse_intohylo(p)
        print_msg('Transforming to KCNF format ...')
        kcnf_formula = intohylo_to_intohylo_kcnf(formula[0])
        dump(kcnf_formula, problem, results_folder)
        formula = None
    except KeyboardInterrupt:
        print_msg(('**************************CANCELLING problem: %s *******************************' % problem))
    except ParseException, e:
        print_msg(('**************************PARSE EXCEPTION in problem: %s *******************************' % problem))
        print_msg(e)
    except Exception, e:
        print_msg(('**************************EXCEPTION in problem: %s *******************************' % problem))
        print_msg(e)


