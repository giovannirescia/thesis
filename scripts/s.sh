#!/bin/bash

if [ -f ~/.bash_profile ]
then
    source ~/.bash_profile
fi

# run this from parent dir
mkdir -p output/syncl-output
cd output/kcnf-output
echo ""
echo "----------------------------"
echo ""
echo "Doing the SY4NCL thing..."
echo ""
echo "" > ../general_info/sy4ncl.csv
n=$(ls -1 | wc -l)
i=1

for f in ./*intohylo; do
    raw=$(echo `basename "$f" .intohylo`);
    echo "$i / $((n)): $raw"
    a=$(($(date +%s%N)/1000000))
    ./../../tools/sy4ncl/dist/build/sy4ncl/sy4ncl -f "$f" -t 0;
    b=$(($(date +%s%N)/1000000))
    echo "$a $b $raw" | awk '{printf ""$3"; %.3f\n", ($2-$1)/1000}' >> ../general_info/sy4ncl.csv
    i=$((i+1))
    mv *.bliss *.map *.stats ../syncl-output;
done
