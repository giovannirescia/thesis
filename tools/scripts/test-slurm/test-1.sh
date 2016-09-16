#!/bin/bash

#SBATCH -t 0-0:05
#SBATCH -ntasks=3

# run 3 copies of the same program in parallel
# each program puts its outputfiles in a separate directory

for (( i=0; i<3; i++)); do
(
  ./test-1-payload f$i.txt
)&
done

# wait until all background processes are ended:

wait 

cat *.tmp >> log.out
#rm  *.tmp
echo "DONE"
