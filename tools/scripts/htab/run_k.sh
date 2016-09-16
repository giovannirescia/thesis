#!/bin/bash
HTAB_SYMM=/home/ezequiel/.Phd/code/htab/dist/build/htab/htab
HTAB_RAW=/home/ezequiel/.Phd/code/htab/dist/build/htab/htab
PARAMS_FILE=./htab.params
FORMULAS_FOLDER=$1
TIMEOUT=$2
 #loop through params file to get the parameters, 

compact_files(){
    raw_file="conf-raw.out"
    cat 0*.tmp >> $raw_file
    c=1
    while [  $c -lt $1 ]; do
	conf_file="conf-"$c$".out"
        cat $c*.tmp >> $conf_file
        let c+=1
    done
    rm *.tmp
}
		
proc=0
i=
for f in $(ls $FORMULAS_FOLDER/*.intohylo)
do
    echo $f
    g=$(basename $f)
    p=${g%.*}
    s="${f/.intohylo}".typed.c.symm
    i=1
    while read l; do
	if [[ ! $l == %* ]]
	then
	    (
	    symm_out=$i"-$p.tmp"
	    echo $f >> $symm_out
	    $HTAB_SYMM -t $TIMEOUT -f $f -s $s $l >> $symm_out  2>&1 
	    )&
	    proc=$[proc+1]
	    i=$[i+1]
	fi 
    done < $PARAMS_FILE
    (
    raw_out="0-$p.tmp"
    echo $f >> $raw_out
    $HTAB_RAW  -t $TIMEOUT -f $f >> $raw_out  2>&1 
    )&
    proc=$[proc+1]
    if [ $proc -eq 6 ]
    then
	wait
    	proc=0
    fi
done
wait
compact_files $i