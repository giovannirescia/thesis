#!/bin/bash

if [ -f ~/.bash_profile ]
then
    source ~/.bash_profile
fi


# run this from parent dir
mkdir -p output/bliss-output
cd output/syncl-output
echo ""
echo "----------------------------"
echo ""
echo "Doing the Bliss thing..."
echo ""

echo "" > ../general_info/bliss.txt

cp *.stats ../bliss-output
n=$(ls -1 | grep .bliss | wc -l)
i=1

for f in ./*.bliss; do
    echo "$i / $((n)): $f"
    filename=$(echo `basename "$f" .bliss`);
    ext=$(echo ".stats");
    a=$(($(gdate +%s%N)/1000000));
    ./../../tools/bliss-0.73/bliss "$f" >> ../bliss-output/$filename$ext;
    b=$(($(gdate +%s%N)/1000000));
    echo "$a $b $filename" | awk '{printf ""$3"; %.3f secs\n", ($2-$1)/1000}' >> ../general_info/bliss.txt;
    i=$((i+1));
done
