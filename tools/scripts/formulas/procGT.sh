#!/bin/bash
DATA_FOLDER=$1

SEARCH_SYMMS=/home/ezequiel/.Phd/code/scripts/formulas/search-symms.sh

#rename all formulas.
find $DATA_FOLDER/tests -type f -name *.cnf -print0 | xargs -0I{} mv {} {}.intohylo

for batch in $(find $DATA_FOLDER/tests -type d -name "batch*")
do
#find symmetries
    $SEARCH_SYMMS -s 1 -t 100 -c 3 -f $batch -o $DATA_FOLDER/tests/
done

for batch in $(find $DATA_FOLDER/tests -type d -name "*_typed_symms")
do
    count=$(find $batch -type f -name "*.symm" | wc -l)
    echo $batch" "$count
done