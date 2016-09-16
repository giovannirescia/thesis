#!/bin/bash

FILTER="symm"
while getopts f:s:o:x: flag;
do
    case $flag in
        f)
            F_FOLDER=$OPTARG;;
        s)
            S_FOLDER=$OPTARG;;
        o)
            O_FOLDER=$OPTARG;;
        x)
            FILTER=$OPTARG;;
        ?)
            echo "bad arguments"
            exit
    esac
done

if [ -z "$F_FOLDER" ]
then
    echo "you must specify a formulas folder: -f <folder>."
    exit
fi
if [ -z "$S_FOLDER" ]
then
    echo "you must specify a symmetries folder: -s <folder>."
    exit
fi
if [ -z "$O_FOLDER" ]
then
    echo "you must specify an output folder: -o <folder>."
    exit
fi

rm -rf $O_FOLDER
mkdir $O_FOLDER

if [ -z "$FILTER" ]
then
    find $F_FOLDER -type f -name *.intohylo -print0 | xargs -0I{} cp {} $O_FOLDER
    find $S_FOLDER -type f -name *.symm -print0 | xargs -0I{} cp {} $O_FOLDER
    #cp $F_FOLDER"/"*.intohylo $O_FOLDER
    #cp $S_FOLDER"/"*.symm $O_FOLDER
else 
    for symm in $(find $S_FOLDER -type f -name *.$FILTER); do
    #for symm in $(ls $S_FOLDER/*.$FILTER); do
        g=$(basename $symm)
        f=${g%.$FILTER}
        #form=$F_FOLDER/$f.intohylo
        cp $symm $O_FOLDER
	printf -v form %q $f.intohylo
	#echo $form
	find $F_FOLDER -type f -name $form -print0 | xargs -0I{} cp {} $O_FOLDER
        #cp $form $O_FOLDER
    done
fi

#tar -zcvf $O_FOLDER.tar.gz $O_FOLDER
#rm -rf $O_FOLDER
