#!/bin/bash
ML_FOLDER=/home/ezequiel/Research/benchmarks/ml/qbflib/test-set-2
OUT_FOLDER=/home/ezequiel/Research/benchmarks/ml/qbflib/tmp
SEARCH_SYMMS=./search-symms.sh
PACK_FILES=./pack_files.sh
TIMEOUT=120
CORES=4
GRAPH_TYPES=(1)
#FOLDERS=( $ML_FOLDER/collapse2 $ML_FOLDER/smolka $ML_FOLDER/ladner )
#for TRANS in $(find $ML_FOLDER -maxdepth 1 -mindepth 1 -type d); do
TRANS=$ML_FOLDER
    echo "Searching symmetries in:"$TRANS
    OUT=$OUT_FOLDER/$(basename $TRANS)/
    for FOLDER in $(find $TRANS -maxdepth 1 -mindepth 1 -type d -not -name "*_*"); do
    	for TYPE in ${GRAPH_TYPES[*]};do
    	    $SEARCH_SYMMS -s 1 -f $FOLDER -o $OUT -g $TYPE -t $TIMEOUT -c $CORES
    	done
	#$PACK_FILES $FOLDER
    done
#done
echo "DONE EVERYTHING"

