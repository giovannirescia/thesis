#!/usr/bin/python
'''
Created on Feb 13, 2012

@author: eorbe
'''
import re
import os.path
import argparse
import datetime

header=['problem','status','timeout','Total_time','Closed',
        'R_Dia','R_Disj','R_SemBr',
       'R_At','R_Down','R_Exist',
       'R_DiscardDown','R_DiscardDiaDone',
       'R_DiscardDiaSymBlocked','BadSymDias','R_DiscardDiaBlocked',
       'R_DiscardDisjTrivial','R_ClashDisj','R_RoleInc',
       'R_LazyBranch']

rules=['R_Dia','R_Disj','R_SemBr',
       'R_At','R_Down','R_Exist',
       'R_DiscardDown','R_DiscardDiaDone',
       'R_DiscardDiaSymBlocked','R_DiscardDiaBlocked',
       'R_DiscardDisjTrivial','R_ClashDisj','R_RoleInc',
       'R_LazyBranch']

def list_files(folder, ext=None):
    sfiles = []
    for root, _, files  in os.walk(folder):
        for f in files:
            _, e = os.path.splitext(f)
            if (ext is None) or (e == ext):
                sfiles.append(os.path.join(root, f))
    return sfiles

def get_filename(path, ext=False):
    fname = os.path.basename(path)
    if ext:
        return os.path.splitext(fname)[0]
    return fname

def print_msg(msg, silent=False):
    if not silent:
        dt = datetime.datetime.today().strftime('%H:%M:%S.%f')
        print '%s - %s : %s ' % (dt, os.getpid(), msg)

def get_htab_stats_args():
    parser = argparse.ArgumentParser(description='Process htab out and get stats.')
    parser.add_argument('-i', '--file', help=('File to load.'))
    parser.add_argument('-f', '--folder', help=('Folder containing files to process.'))
    parser.add_argument('-o', '--output', default=os.getcwd(), help='Destination folder of the generated files.')
    parser.add_argument('-s', '--silent', action='store_true', help='Suppress all the output from the tool.')
    return parser

def list_to_file(fname, content, sep='\n'):
    f = open(fname, 'w')
    f.write(sep.join(content))
    return fname

def dump_csv(filename, data):
    temp=[]
    for d in data:
        temp.append([d['problem'],d['status'],d['timeout'],d['Total'],d['Closed'],
                     d['R_Dia'],d['R_Disj'],d['R_SemBr'],
                     d['R_At'],d['R_Down'],d['R_Exist'],
                     d['R_DiscardDown'],d['R_DiscardDiaDone'],
                     d['R_DiscardDiaSymBlocked'],d['Bad'],d['R_DiscardDiaBlocked'],
                     d['R_DiscardDisjTrivial'],d['R_ClashDisj'],d['R_RoleInc'],
                     d['R_LazyBranch']])
    
    content = [','.join(header)] + map(lambda x: ','.join(map(lambda y: str(y), x)), temp)
    list_to_file(filename, content)

def load_file(f):
    with open(f) as f:
        return f.readlines()
    
def split_problems(lns):
    data={}
    key=None
    
    for l in lns:
        l=l.rstrip('\n')
        if l != '':
            if l.endswith('.intohylo'):
                
                key=os.path.basename(l)
                data[key]=[]
                continue
            data[key].append(l)
    return data


def parse_problem(data):
    stats = dict.fromkeys((rules+['status','timeout','Closed','Total','Bad']),0)
    s='\n'.join(data)
    pattern = re.compile('(\w+)\s.+:\s+(\d+\.*\d*e?\-?\d*)')
    pattern2 = re.compile('The formula is (\w+)')
    pattern3 = re.compile('Timeout')
#    l=[ (k,float(v)) for (k,v) in re.findall(pattern, s)]
#    print l
    stats.update([ (k,float(v)) for (k,v) in re.findall(pattern, s)])
    sat = pattern2.search(s)
    if not sat is None:
        stats['status']=sat.groups()[0]
    else:
        timeout = pattern3.search(s)
        if not timeout is None:
            stats['timeout']=1
        else:
            stats['status'] = -2
            stats['timeout']= -2
            print "ERROR: Can't determine the problem status.!!!!"
    return stats

def process_file(filename):
    lns = load_file(filename)
    plns=split_problems(lns)
    data=[]
    for k,d in plns.iteritems():
        stats=parse_problem(d)
        stats['problem']=k
        data.append(stats)
    return data
        
        
        
parser = get_htab_stats_args()

# parse command line arguments
args = parser.parse_args()

files = []
if not args.folder is None:
    files = list_files(args.folder, '.out')
elif not args.file is None:
    files.append(args.file) 
else:
    parser.error('You must provide a .out file or a folder to process.')

print_msg('===========================================')
print_msg(('Starting get_htab_stats.py'))
print_msg('===========================================')
print_msg(('# Files to process: %s' % len(files)))
print_msg(('Output Folder: %s' % args.output))
for p in files:
    try:
        print_msg('-------------------------------------------',args.silent)
        print_msg(('Processing file: %s' % p),args.silent)
        problem = get_filename(p,True)
        data = process_file(p)
        stats_out = ('%s.csv' % problem) 
        path_out = os.path.join(args.output, stats_out) 
        dump_csv(path_out, data)
        print_msg(('Stats file: %s' % path_out),args.silent)
    except:
        raise
print_msg('-------------------------------------------',args.silent)    

