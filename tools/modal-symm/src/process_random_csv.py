'''
Created on Oct 23, 2011

@author: eorbe
'''
import csv
import os.path
import numpy
import scipy

def get_ts_stats(data):
   
    nodes = scipy.array([int(r[0]) for r in data])
    cnodes = scipy.array([int(r[1]) for r in data])
    lnodes = scipy.array([int(r[2]) for r in data])
    gens = scipy.array([int(r[4]) for r in data])
    symms = scipy.array([int(r[5]) for r in data])
    times = scipy.array([float(r[6]) for r in data])
    
    #clean the false symmetries
    for i in range(len(gens)):
        if gens[i] == 0:
            symms[i] = 0
    
    has_symm = len(filter(lambda x: x > 1, symms))
    perc_has_symm = float(has_symm) / float(len(symms))
    return [has_symm, perc_has_symm,
            gens.max(), gens.min(), gens.mean(), gens.std(), gens.var(),
            symms.max(), symms.min(), symms.mean(), symms.std(), symms.var() ,
            nodes.max(), nodes.min(), nodes.mean(), nodes.std(), nodes.var() ,
            cnodes.max(), cnodes.min(), cnodes.mean(), cnodes.std(), cnodes.var() ,
            lnodes.max(), lnodes.min(), lnodes.mean(), lnodes.std(), lnodes.var(),
            times.max(), times.min(), times.mean(), times.std(), times.var() ]
        
with open('/Users/eorbe/Documents/phd/research/modal-logics/symmetry/results/random2/stats.csv', 'rb') as f1:
    with open('/Users/eorbe/Documents/phd/research/modal-logics/symmetry/results/random2/stats_proc.csv', 'wb') as f2:
        with open('/Users/eorbe/Documents/phd/research/modal-logics/symmetry/results/random2/ts_stats.csv', 'wb') as f3:
        
            test_sets = {}
            ts_stats = {}
            
            csvf = csv.reader(f1, delimiter = ',', quotechar = '"')
            n_csvf = csv.writer(f2, delimiter = ',', quotechar = '"')
            
            print 'Header:'
            header = csvf.next() 
            print header
            new_header = ['TEST_SET', 'INST', 'NOF_MD0_C', 'MD', 'PV', 'GD', 'CS', 'CP', 'FD', 'NOF_N', 'NOF_CN', 'NOF_LN', 'GG', 'FG', 'GS', 'ST']
            print 'New Header:'
            print new_header
            
            n_csvf.writerow(new_header)
            i = 0
            print 'Content:'
            for r in csvf:
                #get instance id
                inst = os.path.basename(r[-1])
                inst_id = int(((inst.replace('.cnf', '')).rpartition('.'))[2])
                #get test_set_id
                ts_id = os.path.basename(os.path.dirname(r[-1]))
                
                #get test set parameters
                p = ts_id.split('-')
                #get global and modal depth
                gd = int(p[3][2:])
                md = int(p[4][2:])
    
                if gd == md:
                    if not test_sets.has_key(ts_id):
                        test_sets[ts_id] = [None, []]
                        
                        nc = int(p[1][2:])
                        cp = p[2][2:]
                        cs = len(cp.split(';'))
                        pv = int(p[5][2:])
                        fd = 1 if len(p) == 9 else 0
                        test_sets[ts_id][0] = [nc, md, pv, gd, cs, cp, fd]
                        
                    while (len(test_sets[ts_id][1]) < inst_id):
                        test_sets[ts_id][1].append(None)
                    
                    test_sets[ts_id][1][(inst_id - 1)] = r[1:8]
                
                    row = [ts_id] + [inst_id] + test_sets[ts_id][0] + test_sets[ts_id][1][(inst_id - 1)] 
                    n_csvf.writerow(row)
                i = i + 1
    
            print 'Proccessed %s rows' % i
            print 'Proccessed %s test sets' % len(test_sets.keys()) 
            print 'Gathering stats...'
            ts_csvf = csv.writer(f3, delimiter = ',', quotechar = '"')
            ts_csvf.writerow(['test_set', 'NOF_MD0_C', 'MD', 'PV', 'GD', 'CS', 'CP', 'FD',
                                'has_symm', 'perc_has_symm',
                                'gens.max', 'gens.min', 'gens.mean', 'gens.std', 'gens.var',
                                'symms.max', 'symms.min', 'symms.mean', 'symms.std', 'symms.var' ,
                                'nodes.max', 'nodes.min', 'nodes.mean', 'nodes.std', 'nodes.var' ,
                                'cnodes.max', 'cnodes.min', 'cnodes.mean', 'cnodes.std', 'cnodes.var' ,
                                'lnodes.max', 'lnodes.min', 'lnodes.mean', 'lnodes.std', 'lnodes.var' ,
                                'times.max', 'times.min', 'times.mean', 'times.std', 'times.var' ])
            
            i = 0
            for k, v in test_sets.iteritems():
                r = [k] + v[0] + get_ts_stats(v[1])
                ts_csvf.writerow(r)
                i = i + 1
            print 'Proccessed Test Sets: %s' % i
        

