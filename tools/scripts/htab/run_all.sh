#!/bin/bash
TO_ML=/home/ezequiel/.Phd/code/qbf2ml/tests/scripts/to_ml.sh
SEARCH_SYMMS=/home/ezequiel/.Phd/code/scripts/formulas/search-symms.sh
RUN_HTAB=/home/ezequiel/.Phd/code/scripts/htab/run_tests.sh

ML_FOLDER=/home/ezequiel/.Phd/benchmarks/ml/qbf
QBF_FOLDER=/home/ezequiel/.Phd/benchmarks/qbf

#mkdir $ML_FOLDER/Letz
#$TO_ML $QBF_FOLDER/Letz $ML_FOLDER/Letz/Letz 1 4 300 6
#
#mkdir $ML_FOLDER/Ling
#$TO_ML $QBF_FOLDER/Ling $ML_FOLDER/Ling/Ling 1 4 300 6
#
#mkdir $ML_FOLDER/Qshifter
#$TO_ML $QBF_FOLDER/Qshifter $ML_FOLDER/Qshifter/Qshifter 1 4 300 6
#
#mkdir $ML_FOLDER/Scholl-Becker
#$TO_ML $QBF_FOLDER/Scholl-Becker $ML_FOLDER/Scholl-Becker/Scholl-Becker 1 4 300 6
#
#mkdir $ML_FOLDER/Wintersteiger
#$TO_ML $QBF_FOLDER/Wintersteiger $ML_FOLDER/Wintersteiger/Wintersteiger 1 4 300 6


# $SEARCH_SYMMS -s 3 -f $ML_FOLDER/Letz/Letz -o $ML_FOLDER/Letz/ -m $ML_FOLDER/Letz/Letz_bliss_o -t 300 -c 5

# $SEARCH_SYMMS -s 3 -f $ML_FOLDER/Ling/Ling -o $ML_FOLDER/Ling/ -m $ML_FOLDER/Ling/Ling_bliss_o -t 300 -c 5

# $SEARCH_SYMMS -s 3 -f $ML_FOLDER/Qshifter/Qshifter -o $ML_FOLDER/Qshifter/ -m $ML_FOLDER/Qshifter/Qshifter_bliss_o -t 300 -c 5

# $SEARCH_SYMMS -s 3 -f $ML_FOLDER/Scholl-Becker/Scholl-Becker -o $ML_FOLDER/Scholl-Becker/ -m $ML_FOLDER/Scholl-Becker/Scholl-Becker_bliss_o -t 300 -c 5

# $SEARCH_SYMMS -s 3 -f $ML_FOLDER/Wintersteiger/Wintersteiger -o $ML_FOLDER/Wintersteiger/ -m $ML_FOLDER/Wintersteiger/Wintersteiger_bliss_o -t 300 -c 5

# $SEARCH_SYMMS -s 3 -f $ML_FOLDER/2QBF/2QBF -o $ML_FOLDER/2QBF/ -m $ML_FOLDER/2QBF/2QBF_bliss_o -t 300 -c 5

# $SEARCH_SYMMS -s 3 -f $ML_FOLDER/Palacios/Palacios -o $ML_FOLDER/Palacios/ -m $ML_FOLDER/Palacios/Palacios_bliss_o -t 300 -c 5

# $SEARCH_SYMMS -s 3 -f $ML_FOLDER/Faber-Leone-Maratea-Ricca/Faber-Leone-Maratea-Ricca -o $ML_FOLDER/Faber-Leone-Maratea-Ricca/ -m $ML_FOLDER/Faber-Leone-Maratea-Ricca/Faber-Leone-Maratea-Ricca_bliss_o -t 300 -c 5

# $SEARCH_SYMMS -s 3 -f $ML_FOLDER/SMALLHARD/SMALLHARD -o $ML_FOLDER/SMALLHARD/ -m $ML_FOLDER/SMALLHARD/SMALLHARD_bliss_o -t 300 -c 5

#$RUN_HTAB $ML_FOLDER/Ling/Ling_typed_symms 600 7
#$RUN_HTAB $ML_FOLDER/Letz/Letz_typed_symms 600 7
#$RUN_HTAB /home/ezequiel/.Phd/benchmarks/ml/lwb_k_typed_symms 600 7
#$RUN_HTAB $ML_FOLDER/Palacios/Palacios_typed_symms 600 7
#$RUN_HTAB $ML_FOLDER/Qshifter/Qshifter_typed_symms 600 7
$RUN_HTAB $ML_FOLDER/Castellini/Castellini_typed_symms 600 7
$RUN_HTAB $ML_FOLDER/Rintanen/Rintanen_typed_symms 600 7
$RUN_HTAB $ML_FOLDER/Scholl-Becker/Scholl-Becker_typed_symms 600 7
$RUN_HTAB $ML_FOLDER/Wintersteiger/Wintersteiger_typed_symms 600 7
$RUN_HTAB $ML_FOLDER/2QBF/2QBF_typed_symms 600 7
$RUN_HTAB $ML_FOLDER/Faber-Leone-Maratea-Ricca/Faber-Leone-Maratea-Ricca_typed_symms 600 7
$RUN_HTAB $ML_FOLDER/SMALLHARD/SMALLHARD_typed_symms 600 7
$RUN_HTAB $ML_FOLDER/Basler/Basler_typed_symms 600 7
$RUN_HTAB $ML_FOLDER/Gent-Rowley/Gent-Rowley_typed_symms 600 7
