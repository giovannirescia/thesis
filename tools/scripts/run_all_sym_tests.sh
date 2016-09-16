#!/bin/bash

cd lwb_k_kcnf_symm/
./run_k.sh > ../utiles_k
cd ..
cd lwk_kt_symm/
./run_kt.sh > ../utiles_kt
cd ..
cd lwk_s4_symm/
./run_s4.sh > ../utiles_s4
cd ..
