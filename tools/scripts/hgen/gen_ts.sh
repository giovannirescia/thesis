#!/bin/bash
MIN_LT=20 #min value of the L/T relation, where L=number of clauses, T = #Props*AVG(Clause Size) *Depth relation
MAX_LT=35
STEP_LT=5
INST=500
MIN_DEPTH=3
MAX_DEPTH=3
AVG_CLAUSE_SIZE=3
MAX_PROPS=100
MIN_PROPS=10
HGEN=~/.Phd/code/hgen/hgen

n=0
for i in $(seq $MIN_PROPS 20 $MAX_PROPS)
do
	for j in $(seq $MIN_DEPTH 1 $MAX_DEPTH)
	do
		for k in $(seq $MIN_LT $STEP_LT $MAX_LT)
		do
			let n+=1
			#L=$(($k * $i + $j + $AVG_CLAUSE_SIZE))
			L=$(echo "$k * $i * $j * $AVG_CLAUSE_SIZE" | bc)
			Li=${L/.*}
			folder=$Li-$i-$j-$k
			echo "generating:"$folder	
			mkdir $folder
			$HGEN --num-inst=$INST --num-clauses=${L/.*} --clause-size=[0,1,1] --mods=1 --global-depth=$j --prop-vars=$i --nom-vars=0 --at-depth=0 -H
			
			for f in $(ls *.cnf)
			do
			    mv $f $f.intohylo
			done
			mv *.intohylo $folder
		done
	done
done

