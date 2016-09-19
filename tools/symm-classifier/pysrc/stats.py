

CSV__VARS_HEADER = ['PROBLEM', '#A-VARS',
                    '#B-VARS', '#C-VARS',
                    '#(D-VARS\D-VARS-at-0)',
                    '#D-VARS-at-0']

CSV__SYMMS_HEADER = ['PROBLEM','#safe','#unsafe',
                     '#A','#B','#C','#D',
                     '#AB','#AC','#AD','#BC',
                     '#BD','#CD','#ABC','#ABD',
                     '#ACD','#BCD','#ABCD',
                     '#AS','#AP','AC']

def get_consolidate_vars_stats(raw_stats):
    stats = []
    for k, data in raw_stats.iteritems():
        # [problem,#Avars,#Bvars,#Cvars,(#Dvars-#Dvars at md 0),#Dvars at md 0]
        proc_data = [k, 0, 0, 0, 0, 0]
        for r in data:
            proc_data[1] = proc_data[1] + r[1]
            proc_data[2] = proc_data[2] + r[2]
            proc_data[3] = proc_data[3] + r[3]
            if r[0] != 0:
                proc_data[4] = proc_data[4] + r[4]
            else:
                proc_data[5] = r[4]
        stats.append(proc_data)
    return stats
    

        
class SymmStats:
    safe = 0
    unsafe = 0
    symm_types = None
    perm_types = None
    
    def __init__(self):
        self.safe = 0
        self.unsafe = 0
        self.symm_types = {'A':0, 'B':0, 'C':0, 'D':0,
                      'AB':0, 'AC':0, 'AD':0,
                      'BC':0, 'BD':0, 'CD':0,
                      'ABC':0, 'ABD':0, 'ACD':0,
                      'BCD':0, 'ABCD':0}
        self.perm_types = {'P':0, 'S':0, 'C':0}
        
    def update_seq_type(self, stype):
        if stype == 'safe':
            self.safe += 1
        else:
            self.unsafe += 1
    
    def update_symm_type(self, stype):
        self.symm_types[stype] += 1
        
    def update_perm_type(self, ptype):
        self.perm_types[ptype] += 1
        
    def to_list(self):
        data = [self.safe,self.unsafe,self.symm_types['A'],
                self.symm_types['B'],self.symm_types['C'],
                self.symm_types['D'],self.symm_types['AB'],
                self.symm_types['AC'],self.symm_types['AD'],
                self.symm_types['BC'],self.symm_types['BD'],
                self.symm_types['CD'],self.symm_types['ABC'],
                self.symm_types['ABD'],self.symm_types['ACD'],
                self.symm_types['BCD'],self.symm_types['ABCD'],
                self.perm_types['S'],self.perm_types['P'],
                self.perm_types['C']]
        return data
                