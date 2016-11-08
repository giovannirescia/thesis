#!/bin/bash

if [ -f ~/.bash_profile ]
then
    source ~/.bash_profile
fi

# run this from parent dir
mkdir -p output/final-output
cd output/mappings
echo ""
echo "----------------------------"
echo ""
echo "Mapping back..."
echo ""
echo "" > ../general_info/mappings.csv
n=$(ls -1 | wc -l)
i=1

for f in ./*.mapping; do
    raw=$(echo `basename $f .mapping`);
    echo "$i / $((n)): $raw"
    a=$(($(date +%s%N)/1000000))
    ext=$(echo ".symm");
    scala ../../scripts/mapper.scala "$f" ../bliss-proc-output/"$raw$ext" > ../final-output/$raw;
    b=$(($(date +%s%N)/1000000))
    echo "$a $b $raw" | awk '{printf ""$3"; %.3f\n", ($2-$1)/1000}' >> ../general_info/mappings.csv
    i=$((i+1))
done
echo ""
