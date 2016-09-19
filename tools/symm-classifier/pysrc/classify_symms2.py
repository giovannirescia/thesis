'''
Created on Mar 11, 2013

@author: ezequiel
'''
import sys
sys.path.append((sys.path[0]+'/pysrc'))
import os
from utils import print_msg,get_filename,dump_csv,classify_symms_args,list_files,dump_typified_symm_file2,dump_typified_symm_file
import stats
import parser as myparser
import analyzer

if len(sys.argv) > 1:
    varfile = sys.argv[1]
    symmfile = sys.argv[2]
    folder = sys.argv[3]
else:
    print 'No file specified. '
    exit(0)

vars_t = analyzer.load_vars_file(varfile)

stats_list = []
try:
    problem = get_filename(symmfile,True)
    #load symmetries file
    symms = myparser.parse_symm_file(symmfile)
    #get vars classification by key
    vars_p = vars_t[problem] 
    typed_symms, sts = analyzer.typify_symmetries(vars_p, symms)
    #saving typified symms
    symm_file_out = ('%s.typed.c.symm' % problem)
    path_out = os.path.join(folder, symm_file_out) 
    dump_typified_symm_file2(path_out, typed_symms)
    data = [problem] + sts.to_list()
    print ','.join(map(str,data))
except:
    print 'Exception in problem: ' + symmfile
    raise
