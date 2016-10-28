#!/bin/bash

# run this from parent dir
mkdir -p output/syncl-output
cd output/kcnf-output
for f in ./*intohylo; do
    ./../../tools/sy4ncl/dist/build/sy4ncl/sy4ncl -f "$f" -t 0;
    mv *.bliss *.map *.stats ../syncl-output;
done
