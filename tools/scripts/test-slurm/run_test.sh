#!/bin/bash

#SBATCH --time=15:00   # walltime
#SBATCH --ntasks=3   # number of processor cores (i.e. tasks)
##SBATCH -C 'intel'   # features syntax (use quotes): -C 'a&b&c&d'
##SBATCH --mem-per-cpu=1024M   # memory per CPU core
#SBATCH --mail-user=ezequiel.orbe@gmail.com
#SBATCH --mail-type=BEGIN
#SBATCH --mail-type=END
#SBATCH --mail-type=FAIL


set -f
# Configurable variables.
NTASKS=3 #must coincide with the number of task (--ntasks) requested in the SLURM cluster.
FOLDER="tests"
FPATTERN=*.txt
IS_EMPTY="-not -empty"
PREPROC=./pre-proc.sh
PREPROC_PARAMS=
PAYLOAD=./payload.sh
PAYLOAD_PARAMS=
POSTPROC=./post-proc.sh
POSTPROC_PARAMS=


printJobDetails() {
    echo "*******************JOB DETAILS**********************"
    echo "nof parallel-tasks: " $NTASKS
    echo "input folder: "$FOLDER
    echo "file pattern: " $FPATTERN
    echo "pre-processing script: " $PREPROC
    echo "pre-processing params: " $PREPROC_PARAMS
    echo "payload script: " $PAYLOAD
    echo "payload params: " $PAYLOAD_PARAMS
    echo "post-processing script: " $POSTPROC
    echo "post-processing params: " $POSTPROC_PARAMS
    echo "nof files to process: " $1
}

printTotalTime(){
    dt=$(echo "$2 - $1" | bc)
    dd=$(echo "$dt/86400" | bc)
    dt2=$(echo "$dt-86400*$dd" | bc)
    dh=$(echo "$dt2/3600" | bc)
    dt3=$(echo "$dt2-3600*$dh" | bc)
    dm=$(echo "$dt3/60" | bc)
    ds=$(echo "$dt3-60*$dm" | bc)
    printf "Total runtime: %d:%02d:%02d:%02.4f\n" $dd $dh $dm $ds
}


#builds the find command to get the files to process
find_cmd="find $FOLDER -type f -name $FPATTERN $IS_EMPTY"
#execute the find command
files=($($find_cmd))
fileid=0
#number of files found by the find command
nfiles=${#files[@]}
#counter to keep track of the number of process spawned.
procs=0

if [ ! $nfiles -eq 0 ]
then
    #creates the pipe for communication between spawned process and the
    #spawner script. 
    # FD 3 will be tied to a named pipe.
    mkfifo pipe; exec 3<>pipe

    start_time=$(date +%s)
    printJobDetails $nfiles
    if [ ! -z "$PREPROC" ]
	then
	echo "******************PRE-PROCESSING********************"
	echo "Executing pre-processing tasks..."
	$PREPROC $PREPROC_PARAMS
    fi
    echo "*********************PAYLOAD***********************"
    echo "Launching payload tasks..."
    # Start off with $NTASKS instances of the payload.
    # Each time an instance terminates, write a newline to the named pipe.
    while([ $procs -lt $NTASKS ])
    do
	if [ $fileid -lt $nfiles ]
	then
	    $PAYLOAD ${files[$fileid]} $((fileid + 1)) $nfiles $PAYLOAD_PARAMS &
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
	    # Executes: ./payload.sh <file_name> <file_number> <nof_files>    
	    $PAYLOAD ${files[$fileid]} $((fileid + 1)) $nfiles $PAYLOAD_PARAMS &
	    let fileid+=1
	else
	    break
	fi
    done <&3
    #wait for all the tasks to complete
    wait
    #remove the pipe
    rm -f pipe
    end_time=$(date +%s)
    
    #execute the post-processing tasks. Currently we run the
    #post processing tasks in a single core. 
    #TO-DO: add the possibility of run it in parallel.
    if [ ! -z "$POSTPROC" ]
	then
	echo "******************POST-PROCESSING*******************"
	echo "Executing post-processing tasks..."
	$POSTPROC $POSTPROC_PARAMS
	echo "****************************************************"
    fi
    echo "JOB DONE"
    printTotalTime $start_time $end_time
else
    echo "No files to process."
fi
