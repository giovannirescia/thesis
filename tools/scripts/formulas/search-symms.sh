#!/bin/bash
BASE_PATH=/home/ezequiel/.Phd/code
TO_CNF=$BASE_PATH/kcnf-converter/to_kcnf.sh
TO_BLISS=$BASE_PATH/bliss-generator/to_bliss.sh
EXEC_BLISS=$BASE_PATH/bliss-generator/exec_bliss.sh
GET_STATS=$BASE_PATH/bliss-generator/proc_bliss.sh
TYPE_SYMMS=$BASE_PATH/symm-classifier/pipeline.sh
PACK_FILES=$BASE_PATH/scripts/formulas/pack_benchmark.sh

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
$TO_BLISS $1 $2 $TIMEOUT $AV_CORES
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
$EXEC_BLISS $1 $2 $TIMEOUT $AV_CORES
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

type_symms (){
echo
echo "==============================================="
echo "Typing symmetries..."
echo "Input Folder: " $1
echo "Output Folder: " $2
echo "==============================================="
echo
cd $(dirname $TYPE_SYMMS)
$TYPE_SYMMS $1 $2 $TIMEOUT $AV_CORES
}

pack_symms (){
echo
echo "==============================================="
echo "Packing formulas and symmetries..."
echo "Formulas Folder: " $1
echo "Symm Folder: " $2
echo "Output Folder: " $3
echo "Filter: " $4
echo "==============================================="
echo
cd $(dirname $PACK_FILES)
$PACK_FILES -f $1 -s $2 -o $3 -x $4 
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
while getopts s:b:f:t:c:o:m:h flag;
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
        h)
            echo "-f: FORMULAS folder"
            echo "-m: SYMMETRIES folder"
            echo "-b: BLISS folder"
            echo "-o: OUTPUT folder"
	    echo "-t: TIMEOUT in seconds"
	    echo "-c: AVAILABLE CORES to use "
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


FOLDER=`basename $FORMULAS_FOLDER`
STATS_FOLDER=$OUT$FOLDER"_stats"
if [ ! -d "$STATS_FOLDER" ]; then
    	mkdir $STATS_FOLDER
fi

#CNF_FOLDER=$IN_PATH
#BLISS_FOLDER=$IN_PATH
#BLISS_FOLDER=$IN_PATH
#FORMULAS_FOLDER=$IN_PATH
    
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
    BLISS_OUT_FOLDER=$OUT$FOLDER"_bliss_o"
    exec_bliss $BLISS_FOLDER $BLISS_OUT_FOLDER
    get_symm_stats $BLISS_OUT_FOLDER
    SYMMS_PATH=$BLISS_OUT_FOLDER 
    mv $BLISS_OUT_FOLDER/*.csv $STATS_FOLDER
fi
if [ 3 -gt $STAGE ] || [ 3 -eq $STAGE ]  
then
	
	if [ -z "$SYMMS_PATH" ]
	then
    	echo "you must specify a SYMMETRIES folder: -m <folder>."
    	exit
	fi
	echo $SYMMS_PATH
	count=$(ls $SYMMS_PATH/*.symm | wc -l)
	echo $count
	if [ $count -gt 0 ]
	then
		SYMM_IN_FOLDER=$OUT$FOLDER"_symms"
		SYMM_OUT_FOLDER_TEMP=$OUT$FOLDER"_typed_symms_temp"
		SYMM_OUT_FOLDER=$OUT$FOLDER"_typed_symms"
		if [ ! -d "$SYMM_OUT_FOLDER_TEMP" ]; then
	    	    mkdir $SYMM_OUT_FOLDER_TEMP
		fi
		if [ ! -d "$SYMM_OUT_FOLDER" ]; then
	    	    mkdir $SYMM_OUT_FOLDER
		fi
		pack_symms $FORMULAS_FOLDER $SYMMS_PATH $SYMM_IN_FOLDER "symm"
		type_symms $SYMM_IN_FOLDER $SYMM_OUT_FOLDER_TEMP
		pack_symms $FORMULAS_FOLDER $SYMM_OUT_FOLDER_TEMP $SYMM_OUT_FOLDER "typed.c.symm"
		mv $SYMM_OUT_FOLDER_TEMP/*.csv $SYMM_OUT_FOLDER_TEMP/*.txt  $STATS_FOLDER
		rm -rf $SYMM_OUT_FOLDER_TEMP
    else
		echo "There are no symmetry files (.symm) to process in "$SYMMS_PATH
		exit
	fi
fi

