#!/bin/bash

cd tools/kcnf-converter
if [ ! -f dist/build/kcnf_converter/kcnf_converter ]
then
    cabal configure; cabal build
else
    echo "KCNF build exists, skypping..."
fi

cd ../sy4ncl
if [ ! -f dist/build/sy4ncl/sy4ncl ]
then
    cabal configure; cabal build
else
    echo "sy4ncl build exists, skypping..."
fi

cd ../bliss-0.73
if [ ! -f bliss ]
then
    make
else
    echo "bliss found, skypping..."
fi
