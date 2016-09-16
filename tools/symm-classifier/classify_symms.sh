APP=pysrc/classify_symms2.py
VAR_FOLDER=$1
SYMM_FOLDER=$2
OUT_FOLDER=$3
AV_CORES=$4

procs=0

# FD 3 will be tied to a named pipe.
mkfifo pipe; exec 3<>pipe

# This is the job we're running.
doTask() {
	#create new file name
	g=$(basename $1)	
	p=${g%.*}
	np=$OUT_FOLDER/$g.tmp
	echo "Processing:" $g
	python $APP $VAR_FOLDER/$p.out $1 $OUT_FOLDER > $np
	echo >&3
}

files=($(find $SYMM_FOLDER -type f -name *.symm))
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
stats_file=$OUT_FOLDER/"symm-stats.csv"
echo "PROBLEM,#safe,#unsafe,#A,#B,#C,#D,#AB,#AC,#AD,#BC,#BD,#CD,#ABC,#ABD,#ACD,#BCD,#ABCD,#AS,#AP,AC" >> $stats_file
cat $OUT_FOLDER/*.tmp >> $stats_file
rm $OUT_FOLDER/*.tmp
rm -f pipe
