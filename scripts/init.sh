#!/bin/bash

echo "Creating directories..."

mkdir -p output/kcnf-output
mkdir -p output/syncl-output
mkdir -p output/bliss-output
mkdir -p output/bliss-proc-output
mkdir -p output/final-output

echo "Removing old files..."
rm output/kcnf-output/*
rm output/syncl-output/*
rm output/bliss-output/*
rm output/bliss-proc-output/*
rm output/final-output/*
