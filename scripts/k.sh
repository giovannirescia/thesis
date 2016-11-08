#!/bin/bash

if [ -f ~/.bash_profile ]
then
    source ~/.bash_profile
fi

# run this from parent dir
mkdir -p output/kcnf-output
cd output/translations
echo ""
echo "----------------------------"
echo "Formating formulas into CNF format..."
echo ""
echo "" > ../general_info/to_kcnf.csv
n=$(ls -1 | wc -l)
i=1
for f in ./*.intohylo; do
    raw=$(echo `basename "$f" .intohylo`);
    echo "$i / $((n)): $raw"
    a=$(($(date +%s%N)/1000000))
    echo `./../../tools/kcnf-converter/dist/build/kcnf_converter/kcnf_converter "$f"` > ../kcnf-output/"$f";
    b=$(($(date +%s%N)/1000000))
    echo "$a $b $raw" | awk '{printf ""$3"; %.3f\n", ($2-$1)/1000}' >> ../general_info/to_kcnf.csv
    i=$((i+1))
done
