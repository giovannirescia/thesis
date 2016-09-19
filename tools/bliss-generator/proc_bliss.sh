#!/bin/bash
APP=src/proc_bliss.py
IN_FOLDER=$1
TIMEOUT=$2
AV_CORES=$3

procs=0


# limit the memory usage to 12GB
ulimit -m 12288000
# limit virtual memory usage 
ulimit -v 12288000
# set virtual memory limit to be hard limit, so that process will be killed when exceeding this limit
ulimit -H -v


# FD 3 will be tied to a named pipe.
mkfifo pipe; exec 3<>pipe

# This is the job we're running.
doTask() {
	#create new file name
	g=$(basename $1)	
	p=${g%.*}
	np=$IN_FOLDER/$g.tmp
	timeout $TIMEOUT python $APP $1 $IN_FOLDER > $np
   	retcode=$?
	[[ $retcode -eq 0 ]] && echo "Processing: " $g" (OK) "
	[[ $retcode -eq 124 ]] && echo "Processing: " $g" (TIMEOUT) "
	[[ ! $retcode -eq 124 ]] && [[ ! $retcode -eq 0 ]] && echo "Processing: " $g" (STATUS:"$retcode") "
	echo >&3
}

files=($(find $IN_FOLDER -type f -name *.bliss -not -empty))
fileid=0
nfiles=${#files[@]}

 # Start off with $AV_CORES instances of it.
 # Each time an instance terminates, write a newline to the named pipe.
while([ $procs -lt $AV_CORES ])
do
    if [ $fileid -lt $nfiles ]
    then
	doTask ${files[$fileid]} &
	let procs+=1
	let fileid+=1
    else
	break
    fi
done  

 # Each time we get a line from the named pipe, launch another job.
while read; do
    if [ $fileid -lt $nfiles ]
    then
	doTask ${files[$fileid]} &
	let fileid+=1
    else
	break
    fi
done <&3
wait
rm -f pipe
stats_file=$IN_FOLDER/"stats.csv"
echo "PROBLEM,NOF_NODES,NOF_CLAUSE_NODES,NOF_LITERAL_NODES,GRAPH_GENERATORS,FORMULA_GENERATORS,GROUP_SIZE,TOTAL_TIME,S_TIME,G_TIME" >> $stats_file
cat $IN_FOLDER/*.tmp >> $stats_file
rm $IN_FOLDER/*.tmp
