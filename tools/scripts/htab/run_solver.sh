#!/bin/bash

BLOCKING=0
while getopts s:f:t:c:o:p:b flag;
do
    case $flag in
    s)
    	SOLVER=$OPTARG;;
    b) 
    	BLOCKING=1;;
    p)
    	PARAMS="$OPTARG";;	
    f)
        PROBLEM_FOLDER=$OPTARG;;
	t)  
	    TIMEOUT=$OPTARG;;
	c)  
	    AV_CORES=$OPTARG;;
	o)  
		CONF_ID=$OPTARG;;
    esac
done

procs=0

# limit the memory usage to 12GB
ulimit -m 12288000
# limit virtual memory usage 
ulimit -v 12288000
# set virtual memory limit to be hard limit, so that process will be killed when exceeding this limit
ulimit -H -v


# FD 3 will be tied to a named pipe.
mkfifo pipe; exec 3<>pipe

# compact_files(){
#     raw_file="conf-raw.out"
#     cat 0*.tmp >> $raw_file
#     c=1
#     while [  $c -lt $1 ]; do
# 	conf_file="conf-"$c$".out"
#         cat $c*.tmp >> $conf_file
#         let c+=1
#     done
#     rm *.tmp
# }

# This is the job we're running.
doTask() {
	CMD=$SOLVER' -t '$TIMEOUT' -f '$1
  	g=$(basename $1)
   	p=${g%.*}
	if [ $BLOCKING -eq 1 ]
	then
    	s="${1/.intohylo}".typed.c.symm
    	CMD=$CMD' -s '$s
	fi
	if [ "$PARAMS" ]
	then
		CMD=$CMD' '$PARAMS
	fi	
	out=$CONF_ID"-$p.tmp"
	echo $1 >> $out
	2>&1
	$CMD >> $out		
    retcode=$?
	[[ $retcode -eq 1 || $retcode -eq 2  ]] && echo "Processing: " $g" (OK) "
	[[ $retcode -eq 3 ]] && echo "Processing: " $g" (TIMEOUT) "
	[[ ! $retcode -eq 3 ]] && [[ ! $retcode -eq 1 ]] && [[ ! $retcode -eq 2 ]] && echo "Processing: " $g" (STATUS:"$retcode") "
	echo >&3
}

files=($(find $PROBLEM_FOLDER -type f -name *.intohylo -not -empty))
fileid=0
nfiles=${#files[@]}

echo "# FILES: " $nfiles
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

conf_file="conf-"$CONF_ID".out"

if [ -e $conf_file ] 
then
    rm $conf_file
fi
cat $CONF_ID*.tmp >> $conf_file
rm $CONF_ID*.tmp
echo "DONE"