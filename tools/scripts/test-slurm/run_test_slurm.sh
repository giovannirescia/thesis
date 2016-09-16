#!/bin/bash

AV_CORES=2
PROBLEM_FOLDER=tests
procs=0

# limit the memory usage to 12GB
#ulimit -m 12288000
# limit virtual memory usage 
#ulimit -v 12288000
# set virtual memory limit to be hard limit, so that process will be killed when exceeding this limit
#ulimit -H -v


# FD 3 will be tied to a named pipe.
mkfifo pipe; exec 3<>pipe


# This is the job we're running.
doTask() {
    PID=$BASHPID
    echo "proccessing file: "$1
    echo "proccess: "$PID
    echo "proccess: "$PID " - file: " $1  >> $PID.tmp
    echo >&3
}

files=($(find $PROBLEM_FOLDER -type f))
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

cat *.tmp >> log.out
#rm  *.tmp
echo "DONE"
