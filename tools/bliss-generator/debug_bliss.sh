#!/bin/bash
app1=bin/to_bliss
app2=bin/bliss
app3=src/bliss_proccess.py

f=$1
#out_folder="/home/ezequiel/.Phd/benchmarks/test/poly"
out_folder=$(pwd)"/out"
tmp_file=$out_folder"/tmp.bliss"

echo $f

#generates bliss graph
g=$(basename $f)
p=${g%.*}
np=$out_folder/$p".bliss"
    #echo $np
$app1 $f > $np

echo $np

#execute bliss and redirect output to temp fil
#echo "Calculating automorphisms for: " $f
$app2 $np > $tmp_file
    
#create new file name
#g=$(basename $np)
#p=${g%.*}
#np1=$out_folder/$g

#copy source file to out dir
#cp $np $np1
 
echo "c ====== STATS ====== " >> $np 
#loop through tmp file to get the stats, then append
#them to the file in the out idr.
while read LINE; do
  echo "c $LINE" >> $np 
done < $tmp_file
rm $tmp_file

# generate stats and dump symm file
python $app3 $out_folder
#echo "Results Available at: " $np


