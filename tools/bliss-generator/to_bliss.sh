#!/bin/bash
APP=bin/to_bliss
IN_FOLDER=$1
OUT_FOLDER=$2
TIMEOUT=$3
AV_CORES=$4

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
	p=${g%.*}
	np=$p.bliss
	if [ ! -e $OUT_FOLDER/$np ]
	then
	    2>&1
   	    timeout $TIMEOUT $APP $1 +RTS -K200M > $OUT_FOLDER/$np
   	    retcode=$?
	    if [ $retcode -eq 0 ] 
	    then 
		echo "Processing: "$g" (OK) "
	    else
		rm $OUT_FOLDER/$np
		[[ $retcode -eq 124 ]] && echo "Processing: "$g" (TIMEOUT) "
		[[ ! $retcode -eq 124 ]] && [[ ! $retcode -eq 0 ]] && echo "Processing: "$g" (STATUS:"$retcode") "
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
