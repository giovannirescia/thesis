#!/bin/bash

if [ -f ~/.bash_profile ]
then
    source ~/.bash_profile
fi

# run this from parent dir
mkdir -p output/syncl-output
cd output/kcnf-output
for f in ./*intohylo; do
    echo "---------------------------------------------------------------------------------------------------------------------";
    echo $f;
    a=$(($(gdate +%s%N)/1000000))
    ./../../tools/sy4ncl/dist/build/sy4ncl/sy4ncl -f "$f" -t 0;
    b=$(($(gdate +%s%N)/1000000))
    echo "$a $b" | awk '{printf "\n\n\t%.3f secs\n\n", ($2-$1)/1000}'
    echo "---------------------------------------------------------------------------------------------------------------------";

    mv *.bliss *.map *.stats ../syncl-output;
done
