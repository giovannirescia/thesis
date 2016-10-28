#!/bin/bash

#echo `python2.7 ../sy4ncl/scripts/proc_bliss.py`
mkdir -p output/bliss-proc-output
cd output/bliss-output/
for f in ./*; do
    raw=$(echo `basename "$f" .stats`);
    ext=$(echo ".map");
    echo `python2.7 ../../tools/sy4ncl/scripts/proc_bliss.py "$f" ../syncl-output/$raw$ext`;
    mv *.symm ../bliss-proc-output;
done
