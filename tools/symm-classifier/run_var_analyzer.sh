#!/bin/bash
APP=bin/var_analyzer
IN_FOLDER=$1
OUT_FOLDER=$2
AV_CORES=$3
OUT_FILE=$OUT_FOLDER/var_analyzer.txt
VAR_DATA=$OUT_FOLDER/vardata

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
	g=$(basename $1)
	echo "Processing: "$g
	p=${g%.*} 
	echo $g >> $VAR_DATA/$p.out
	$APP $1 +RTS -K150M >> $VAR_DATA/$p.out
	echo >&3
}

if [ ! -d "$OUT_FOLDER" ]; then
    mkdir $OUT_FOLDER
fi
if [ ! -d $VAR_DATA ]; then
    mkdir $VAR_DATA
fi

files=($(find $IN_FOLDER -type f -name *.intohylo))
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
cat $VAR_DATA/*.out >> $OUT_FILE
