#!/bin/bash

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
    echo "---------------------------------------------------------------------------------------------------------------------";
    ./../../tools/bliss-0.73/bliss "$f" >> ../bliss-output/$filename$ext;
done
