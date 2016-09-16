#!/bin/bash
APP=bliss
IN_FOLDER=$1
TIMEOUT=$2
AV_CORES=$3

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
   #create new file name
	g=$(basename $1)
	p=${g%.*}
	np=$IN_FOLDER/$p.stats
	grep --quiet "Total time" $np 
	RETVAL=$?                                                                                                                                     
	if [ $RETVAL -ne 0 ]
	then
   #execute bliss and redirect output to np file
	    timeout $TIMEOUT $APP $1 >> $np
	    retcode=$?
	    if [ $retcode -eq 0 ]
  	    then
     	        echo "("$2"/"$3"): "$g" (OK) "
	    else
	 	[[ $retcode -eq 124 ]] && echo "("$2"/"$3"): "$g" (TIMEOUT) "
		[[ ! $retcode -eq 124 ]] && [[ ! $retcode -eq 0 ]] && echo "("$2"/"$3"): "$g" (STATUS:"$retcode") "
	    fi
	else
	    echo "("$2"/"$3"): "$g" (SKIPPED) "
        fi
	echo >&3
}

if [ ! -d $OUT_FOLDER ]; then
    mkdir $OUT_FOLDER
fi

files=($(find $IN_FOLDER -type f -name *.bliss -not -empty))
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
