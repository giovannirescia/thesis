#!/bin/bash

if [ -f ~/.bash_profile ]
then
    source ~/.bash_profile
fi
source ~/.bash_profile
# run this from parent dir
mkdir -p output/kcnf-output
cd output/translations
echo ""
echo "----------------------------"
echo "Formating formulas into CNF format..."
echo ""

echo "" > ../general_info/to_kcnf.txt
n=$(ls -1 | wc -l)
i=1
for f in ./*.intohylo; do
    echo "$i / $((n)): $f"
    a=$(($(gdate +%s%N)/1000000))
    echo `./../../tools/kcnf-converter/dist/build/kcnf_converter/kcnf_converter "$f"` > ../kcnf-output/"$f";
    b=$(($(gdate +%s%N)/1000000))
    echo "$a $b $f" | awk '{printf ""$3"; %.3f secs\n", ($2-$1)/1000}' >> ../general_info/to_kcnf.txt
    i=$((i+1))
done
