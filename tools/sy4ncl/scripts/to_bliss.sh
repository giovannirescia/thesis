#!/bin/bash
APP=/home/ezequiel/Research/code/sy4ncl/sy4ncl
IN_FOLDER=$1
OUT_FOLDER=$2
GRAPH_TYPE=$3
TIMEOUT=$4
AV_CORES=$5

procs=0

# limit the memory usage to 12GB
ulimit -m 2560000
# limit virtual memory usage 
ulimit -v 2560000
# set virtual memory limit to be hard limit, so that process will be killed when exceeding this limit
ulimit -H -v


# FD 3 will be tied to a named pipe.
mkfifo pipe; exec 3<>pipe

# This is the job we're running.
doTask() {
	g=$(basename $1)
	p=${g%.*}
	np=$p.bliss
	if [ ! -e $OUT_FOLDER/$np ]
	then
	    2>&1
   	    timeout $TIMEOUT $APP -f $1 -t=$GRAPH_TYPE -d $OUT_FOLDER +RTS -K200M > to_bliss.out
   	    retcode=$?
	    if [ $retcode -eq 0 ] 
	    then 
		echo "("$2"/"$3"): "$g" (OK) "
	    else
		rm -f $OUT_FOLDER/$np
		[[ $retcode -eq 124 ]] && echo "("$2"/"$3"): "$g" (TIMEOUT) "
		[[ ! $retcode -eq 124 ]] && [[ ! $retcode -eq 0 ]] && echo "("$2"/"$3"): "$g" (STATUS:"$retcode") "
	    fi
	fi
	echo >&3
}

if [ ! -d $OUT_FOLDER ]; then
    mkdir $OUT_FOLDER
fi

files=($(find $IN_FOLDER -type f -name *.intohylo -not -empty))
fileid=0
nfiles=${#files[@]}

if [ $nfiles -gt 0 ] 
then                                                                                                                         
 # Start off with $AV_CORES instances of it.
 # Each time an instance terminates, write a newline to the named pipe.
while([ $procs -lt $AV_CORES ])
do
    if [ $fileid -lt $nfiles ]
    then
	doTask ${files[$fileid]} $((fileid + 1)) $nfiles &
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
	doTask ${files[$fileid]} $((fileid + 1)) $nfiles &
	let fileid+=1
    else
	break
    fi
done <&3
wait
fi
rm -f pipe
rm -f to_bliss.out
