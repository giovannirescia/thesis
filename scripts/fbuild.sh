#!/bin/bash
cd tools/kcnf-converter
cabal configure; cabal build
cd ../sy4ncl
cabal configure; cabal build
cd ../bliss-0.73
make
