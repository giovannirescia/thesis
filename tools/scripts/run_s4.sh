#!/bin/bash
SHTAB=../tests/shtab
HTAB=htab
DIR=$1
for f in $(ls $DIR/*.intohylo)
 do
#  echo $f
  echo $f > out;
  echo "--- con simetrias ---" >> out;
  $SHTAB -t 10 --alltransitive -f $f -s "${f/.intohylo}".symm  >> out  2>&1 ;
  if grep -q "SYM on" out;
    then
      echo "--- sin usar simetrias ---" >> out;
      $HTAB --alltransitive -t 10 -f $f >> out ;
      cat out;
    fi
 rm out;
 done
