#!/bin/bash

echo "Creating directories..."

sh scripts/unzip.sh

mkdir -p output/kcnf-output
mkdir -p output/syncl-output
mkdir -p output/bliss-output
mkdir -p output/bliss-proc-output
mkdir -p output/final-output

sh scripts/wipe.sh

sh scripts/build.sh
