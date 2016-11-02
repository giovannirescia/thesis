#!/bin/bash

if [ -f ~/.bash_profile ]
then
    source ~/.bash_profile
fi
source ~/.bash_profile
# run this from parent dir
mkdir -p output/kcnf-output
cd output/translations
echo "Formating formulas into CNF format..."
echo ""
echo "Times:"
echo ""
for f in ./*.intohylo; do
    a=$(($(gdate +%s%N)/1000000))
    echo `./../../tools/kcnf-converter/dist/build/kcnf_converter/kcnf_converter "$f"` > ../kcnf-output/"$f";
    b=$(($(gdate +%s%N)/1000000))
    echo "$a $b $f" | awk '{printf ""$3":\n\n\t%.3f secs\n\n", ($2-$1)/1000}'
done
