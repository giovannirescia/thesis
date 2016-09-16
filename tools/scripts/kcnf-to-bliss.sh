#!/bin/bash
TO_KCNF=/home/ezequiel/.Phd/code/kcnf-converter/to_kcnf.sh
TO_BLISS=/home/ezequiel/.Phd/code/bliss-generator/to_bliss.sh
EXEC_BLISS=/home/ezequiel/.Phd/code/bliss-generator/exec_bliss.sh
GET_STATS=/home/ezequiel/.Phd/code/bliss-generator/src/bliss_proccess.py



to_kcnf (){
echo
echo "==============================================="
echo "Translating formulas to KCNF..."
echo "Input Folder: " $1
echo "Output Folder: " $2
echo "==============================================="
echo 
cd $(dirname $TO_KCNF)
$TO_KCNF $1 $2
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
$TO_BLISS $1 $2
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
$EXEC_BLISS $1 $2
}

get_stats () {
echo
echo "==============================================="
echo "Generating the statistics and .symm files..."
echo "Input Folder: " $1
echo "Output File: " $1"/stats.csv"
echo "==============================================="
echo
cd $(dirname $GET_STATS)
python $GET_STATS $1
}

#no options: execute the process: to kcnf -> to bliss -> execute bliss -> get stats
#            the source directory must contain .intohylo formulas 
#-b        : execute the process: to bliss -> execute bliss -> get stats
#            the source directory must contain kncf.intohylo formulas
#-e        : execute the process: execute bliss -> get stats
#            the source directory must contain .bliss files
#-s        : execute the process: get stats
#            the source directory must contain .bliss files with statistics.
INIT=1
PWD=$(pwd)"/"
while getopts besd:o:h flag;
do
    case $flag in
        b)
            INIT=2;;
        e)
            INIT=3;;
        s)
            INIT=4;;
        d)
            IN_PATH=$OPTARG;;
        o)
            PWD=$OPTARG;;
        h)
            echo "-d: source folder"
            echo "-o: output folder"
            echo "no options: execute the whole process"
            echo "-b: execute the process: to bliss -> execute bliss -> get stats"
            echo "-e: execute the process: execute bliss -> get stats"
            echo "-s: execute the process: get stats"
            exit;;
        ?)
            echo "bad arguments"
            exit
    esac
done

if [ -z "$IN_PATH" ]
then
    echo "you must specify a source folder: -d <folder>."
    exit
fi

FOLDER=`basename $IN_PATH`

if [ $INIT = 1 ] 
then
    KCNF_FOLDER=$PWD$FOLDER"_kcnf"
    BLISS_FOLDER=$PWD$FOLDER"_bliss"
    BLISS_OUT_FOLDER=$PWD$FOLDER"_bliss_o"
    to_kcnf $IN_PATH $KCNF_FOLDER 
    to_bliss $KCNF_FOLDER $BLISS_FOLDER
    exec_bliss $BLISS_FOLDER $BLISS_OUT_FOLDER
    get_stats $BLISS_OUT_FOLDER
fi
if [ $INIT = 2 ]
then
    KCNF_FOLDER=$IN_PATH
    BLISS_FOLDER=$PWD$FOLDER"_bliss"
    BLISS_OUT_FOLDER=$PWD$FOLDER"_bliss_o"
    to_bliss $KCNF_FOLDER $BLISS_FOLDER
    exec_bliss $BLISS_FOLDER $BLISS_OUT_FOLDER
    get_stats $BLISS_OUT_FOLDER

fi
if [ $INIT = 3 ]
then
    BLISS_FOLDER=$IN_PATH
    BLISS_OUT_FOLDER=$PWD$FOLDER"_bliss_o"
    exec_bliss $BLISS_FOLDER $BLISS_OUT_FOLDER
    get_stats $BLISS_OUT_FOLDER
fi
if [ $INIT = 4 ]
then
    get_stats $IN_PATH
fi


