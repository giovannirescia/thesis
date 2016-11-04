#!/bin/bash

if [ -f ~/.bash_profile ]
then
    source ~/.bash_profile
fi


# run this from parent dir
mkdir -p output/bliss-output
cd output/syncl-output
echo "Doing the bliss thing..."
cp *.stats ../bliss-output

for f in ./*.bliss; do
    echo "---------------------------------------------------------------------------------------------------------------------";
    filename=$(echo `basename "$f" .bliss`);
    ext=$(echo ".stats");
    echo "$filename";
    a=$(($(gdate +%s%N)/1000000))
    ./../../tools/bliss-0.73/bliss "$f" >> ../bliss-output/$filename$ext;
    b=$(($(gdate +%s%N)/1000000))
    echo "$a $b" | awk '{printf "\n\n\t%.3f secs\n\n", ($2-$1)/1000}'
    echo "---------------------------------------------------------------------------------------------------------------------";
done
