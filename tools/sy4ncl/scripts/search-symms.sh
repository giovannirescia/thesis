#!/bin/bash
BASE_PATH=/home/ezequiel/Research/code
TO_CNF=$BASE_PATH/kcnf-converter/to_kcnf.sh
TO_BLISS=$BASE_PATH/sy4ncl/scripts/to_bliss.sh
EXEC_BLISS=$BASE_PATH/sy4ncl/scripts/exec_bliss.sh
GET_STATS=$BASE_PATH/sy4ncl/scripts/proc_bliss.sh


TIMEOUT=300
TIMEOUT2=
AV_CORES=6

to_cnf (){
echo
echo "==============================================="
echo "Translating formulas to CNF..."
echo "Input Folder: " $1
echo "Output Folder: " $2
echo "==============================================="
echo 
cd $(dirname $TO_CNF)
$TO_CNF $1 $2 $TIMEOUT $AV_CORES
}

to_bliss (){
echo
echo "==============================================="
echo "Generating bliss graphs..."
echo "Input Folder: " $1
echo "Output Folder: " $2
echo "==============================================="
echo
cd $(dirname $TO_BLISS)
$TO_BLISS $1 $2 $GRAPH_TYPE $TIMEOUT $AV_CORES
}

exec_bliss (){
echo
echo "==============================================="
echo "Calculating symmetries..."
echo "Input Folder: " $1
echo "Output Folder: " $2
echo "==============================================="
echo
cd $(dirname $EXEC_BLISS)
$EXEC_BLISS $1 $TIMEOUT $AV_CORES
}

get_symm_stats () {
echo
echo "==============================================="
echo "Generating the statistics and .symm files..."
echo "Input Folder: " $1
echo "Output File: " $1"/stats.csv"
echo "==============================================="
echo
cd $(dirname $GET_STATS)
$GET_STATS $1 $TIMEOUT2 $AV_CORES
}

#-s 0 or no options: execute the process: to cnf -> to bliss -> execute bliss + get stats -> type symmetries + pack symmetries
#            the source directory must contain .intohylo formulas 
#-s 1        : execute the process: to bliss -> execute bliss + get stats -> type symmetries + pack symmetries
#            the source directory must contain kncf.intohylo formulas
#-s 2        : execute the process: execute bliss + get stats -> type symmetries + pack symmetries
#            the source directory must contain .bliss files
#-s 3        : execute the process: type symmetries + pack symmetries. The source directory must contain
#              symm files and formulas.


OUT=$(pwd)"/"
#OUT="/"
STAGE=0
GRAPH_TYPE=1
while getopts s:b:f:t:c:o:m:g:h flag;
do
    case $flag in
        s)
            STAGE=$OPTARG;;
        f)
            FORMULAS_FOLDER=$OPTARG;;
	b)  
	    BLISS_FOLDER=$OPTARG;;
	m)  
	    SYMMS_PATH=$OPTARG;;
	t)  
	    TIMEOUT=$OPTARG;;
	c)  
	    AV_CORES=$OPTARG;;
        o)
            OUT=$OPTARG;;
	g)
            GRAPH_TYPE=$OPTARG;;
        h)
            echo "-f: FORMULAS folder"
            echo "-m: SYMMETRIES folder"
            echo "-b: BLISS folder"
            echo "-o: OUTPUT folder"
	    echo "-t: TIMEOUT in seconds"
	    echo "-c: AVAILABLE CORES to use "
	    echo "-g: GRAPH TYPE to generate (0:non layered, 1: layered)"
            echo "-s N: execute the process starting at stage N"
            echo "no options: execute the whole process"
            exit;;
        ?)
            echo "bad arguments"
            exit
    esac
done

let TIMEOUT2=$TIMEOUT*2
echo "TIMEOUT: "$TIMEOUT " - TIMEOUT2: "$TIMEOUT2 " - AVAILABLE CORES:" $AV_CORES
if [ -z "$FORMULAS_FOLDER" ]
then
    echo "you must specify a source folder: -f <folder>."
    exit
fi


#FOLDER=$(basename $FORMULAS_FOLDER)
FOLDER=$(basename $FORMULAS_FOLDER)"_"$GRAPH_TYPE
STATS_FOLDER=$OUT$FOLDER"_stats"
if [ ! -d "$STATS_FOLDER" ]; then
    	mkdir -p $STATS_FOLDER
fi

if [ 0 -gt $STAGE ] || [ 0 -eq $STAGE ]  
then
    CNF_FOLDER=$OUT$FOLDER"_cnf"
    to_cnf $FORMULAS_FOLDER $CNF_FOLDER
    FORMULAS_FOLDER=$CNF_FOLDER 
fi

if [ 1 -gt $STAGE ] || [ 1 -eq $STAGE ]  
then
    BLISS_FOLDER=$OUT$FOLDER"_bliss"
    to_bliss $FORMULAS_FOLDER $BLISS_FOLDER
fi
if [ 2 -gt $STAGE ] || [ 2 -eq $STAGE ]  
then
	if [ -z "$BLISS_FOLDER" ]
	then
    	echo "you must specify a BLISS folder: -b <folder>."
    	exit
	fi
    BLISS_OUT_FOLDER=$BLISS_FOLDER
    exec_bliss $BLISS_FOLDER
    get_symm_stats $BLISS_OUT_FOLDER
    mv $BLISS_OUT_FOLDER/*.csv $STATS_FOLDER
fi

