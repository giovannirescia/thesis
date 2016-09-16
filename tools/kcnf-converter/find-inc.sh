#!/bin/bash
#original formulas       
ODIR=$1
#kcnf formulas
KDIR=$2
KEXT=.kcnf.intohylo

rm -rf Inconsistent
rm -rf Models
rm -rf BadModels
mkdir Inconsistent
mkdir Models
mkdir BadModels

for OF in $( ls $ODIR/*.intohylo); do
    BOF=$(basename $OF)
    KF=$KDIR/${BOF%.*}$KEXT
    BKF=$(basename $KF)
    echo "Verifying:" $BOF "vs"  $BKF
    htab -t 10 -f $OF > resphtabOF -m Models/$BOF.htab.m
    htab -t 10 -f $KF > resphtabKF -m Models/$BKF.htab.m

    if grep -q "is unsatisfiable" resphtabOF ; then
	if grep -q "is satisfiable" resphtabKF ; then
            cp $OF Inconsistent
            echo $BOF " UNSAT, " $BKF " SAT"
	fi
    elif grep -q "is satisfiable" resphtabOF ; then
	    if grep -q "is unsatisfiable" resphtabKF ; then
            cp $OF Inconsistent
	    echo $BOF " SAT, " $BKF " UNSAT"
        #elif (mcheck Models/$BOF.htab.m $OF | grep -q "False"); then
        #    echo "HTab bad model for " $BOF
        #    cp Models/$BOF.htab.m BadModel
        fi
    fi
#    if grep -q "is satisfiable" resphtabKF ; then
#        if (mcheck Models/$BKF.htab.m $KF | grep -q "False"); then
#	    echo "HTab bad model for " $BKF
#            cp Models/$BKF.htab.m BadModel
#	fi
#    fi
done
exit 

