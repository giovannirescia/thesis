#!/usr/bin/python
import sys
sys.path.append((sys.path[0]+'/pysrc'))
import os
from utils import print_msg,get_filename,process_data_args,dump_csv
import stats
import parser as myparser

parser = process_data_args()

# parse command line arguments
args = parser.parse_args()

print_msg('-------------------------------------------',args.silent)
print_msg(('Processing File: %s' % args.file),args.silent)
#parse input file
raw_data = myparser.parse_vars_analyzer_file(args.file)
#generate stats
print_msg('Generating statistics',args.silent)
data = stats.get_consolidate_vars_stats(raw_data)

print_msg('Generating stats file',args.silent)
# get file name without extension
filename = get_filename(args.file, True)
fname_out = ('%s.csv' % filename)
fpath_out = os.path.join(args.folder, fname_out)

dump_csv(fpath_out, stats.CSV__VARS_HEADER, data)

print_msg(('Output file: %s' % fpath_out),args.silent)

print_msg('-------------------------------------------',args.silent)
