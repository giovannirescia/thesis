#!/bin/bash
SOLVER=/home/ezequiel/.Phd/code/htab/dist/build/htab/htab
PARAMS_FILE=./htab.params
FORMULAS_FOLDER=$1
TIMEOUT=$2
AV_CORES=$3

PROC_STATS=/home/ezequiel/.Phd/code/scripts/htab/get_htab_stats.py
RESULTS_FOLDER=/home/ezequiel/Phd/modal-logics/symmetry/symmetry-blocking/results/paper-runs

i=1
while read l; do
	if [[ ! $l == %* ]]
	then
		echo "============== RUNNING CONFIGURATION: " $i
	    ./run_solver.sh  -s $SOLVER -f $FORMULAS_FOLDER -o $i -p "$l" -c $AV_CORES  -b -t $TIMEOUT
		i=$[i+1]
	fi 
done < $PARAMS_FILE
		echo "============== RUNNING CONFIGURATION: 0"
./run_solver.sh  -s $SOLVER -f $FORMULAS_FOLDER -o 0 -c $AV_CORES -t $TIMEOUT

$PROC_STATS -f .


folder=$RESULTS_FOLDER/$(basename $FORMULAS_FOLDER)

if [ ! -d $folder ]; then
    mkdir $folder
fi

mv *.csv $folder
		
echo "DONE"