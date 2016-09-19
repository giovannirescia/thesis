#!/usr/bin/python
'''
Created on Nov 3, 2012

@author: ezequiel
'''
import sys
sys.path.append((sys.path[0]+'/pysrc'))
import os
from utils import print_msg,get_filename,dump_csv,classify_symms_args,list_files,dump_typified_symm_file2,dump_typified_symm_file
import stats
import parser as myparser
import analyzer

parser = classify_symms_args()

# parse command line arguments
args = parser.parse_args()

problems = []
if not args.symmfolder is None:
    problems = list_files(args.symmfolder, '.symm')
else:
    parser.error('You must provide a folder to process.')

print_msg('===========================================')
print_msg(('Starting classify_symms.py'))
print_msg('===========================================')
print_msg(('Variable File: %s' % args.varfile ))
print_msg(('Input Folder: %s ' % args.symmfolder))
print_msg(('Output Folder: %s' % args.folder))
print_msg(('Human Readable: %s' % (not args.computer)))
print_msg('-------------------------------------------',args.silent)
print_msg(('%s files were find' % len(problems)),args.silent)

print_msg(('Loading variables file: %s' % args.varfile),args.silent)
vars_t = analyzer.load_vars_file(args.varfile)

stats_list = []
for p in problems:
    try:
        print_msg('-------------------------------------------',args.silent)
        print_msg(('Processing file: %s' % p),args.silent)
        problem = get_filename(p,True)
        #load symmetries file
        symms = myparser.parse_symm_file(p)
        #get vars classification by key
        vars_p = vars_t[problem] 
        print_msg(('Typifying symmetries'),args.silent) 
        typed_symms, sts = analyzer.typify_symmetries(vars_p, symms)
        stats_list.append((problem,sts))
        #saving typified symms
        symm_file_out = ('%s.typed.symm' % problem) if not args.computer else('%s.typed.c.symm' % problem)
        path_out = os.path.join(args.folder, symm_file_out) 
        if args.computer:   
            dump_typified_symm_file2(path_out, typed_symms)
        else:
            dump_typified_symm_file(path_out, typed_symms)
        print_msg(('Typed symmetries file: %s' % symm_file_out),args.silent)
    except:
        raise
#generate stats
print_msg('-------------------------------------------',args.silent)
print_msg('Generating statistics',args.silent)
data = []
for (p,s) in stats_list:
    data.append(([p] + s.to_list()))

print_msg('Generating stats file',args.silent)
# get file name without extension
fname_out = 'symm-stats.csv' 
fpath_out = os.path.join(args.folder, fname_out)

dump_csv(fpath_out, stats.CSV__SYMMS_HEADER, data)

print_msg(('Stats file: %s' % fpath_out),args.silent)

print_msg('-------------------------------------------',args.silent)
