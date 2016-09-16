#!/bin/bash
SPARTACUS=../tests/shtab
DIR=$1
echo $SPARTACUS --csvheader > spartacus.csv;
for f in $(ls $DIR/*.intohylo)
 do
   echo $f
   $SPARTACUS --intohyloFile= $f --timeout=10 --csv  >> spartacus.csv  2>&1 ;
 done
