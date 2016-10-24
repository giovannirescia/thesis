#!/bin/bash
# restore.sh

echo "Removing old .intohylo files..."
rm intohylo-proc/*intohylo
cd intohylo-raw
rm *intohylo
echo "Copying new .inthoylo files..."
cp ../../../output/translations/*Box*intohylo .
# restore.sh
echo "Formating formulas into CNF format..."
for f in ./*; do
    echo `./../tools/kcnf_converter "$f"` > ../intohylo-proc/"$f";
done
echo "Finding symmetries..."
cd ../intohylo-proc
rm output-stats/*
rm bliss-output/*
for f in ./*intohylo; do
    ./../tools/sy4ncl -f "$f" -t 0;
done

mv *.bliss *.map *.stats output-stats

cd output-stats
echo "Doing the bliss thing..."
for f in ./*.bliss; do
    echo "---------------------------------------------------------------------------------------------------------------------";
    echo "$f";
    echo "---------------------------------------------------------------------------------------------------------------------";
    echo `./../../tools/bliss "$f"` > ../bliss-output/"$f";
done
