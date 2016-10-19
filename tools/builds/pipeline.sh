#!/bin/bash
# restore.sh
rm intohylo-proc/*intohylo
cd intohylo-raw
rm *intohylo
cp ../../../output/translations/*Box*intohylo .
# restore.sh

for f in ./*; do
    echo `./../tools/kcnf_converter "$f"` > ../intohylo-proc/"$f";
done

cd ../intohylo-proc
for f in ./*intohylo; do
    ./../tools/sy4ncl -f "$f" -t 0;
done

mv *.bliss *.map *.stats output-stats

cd output-stats

for f in ./*.bliss; do
    echo "---------------------------------------------------------------------------------------------------------------------";
    echo "$f";
    echo "---------------------------------------------------------------------------------------------------------------------";
    ./../../tools/bliss "$f";
done
