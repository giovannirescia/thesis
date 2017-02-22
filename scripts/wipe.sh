#!/bin/bash

echo "Removing old files..."
#rm output/*-output/*
#rm output/mappings/*
#rm output/translations/*

if find output/*-output/ -mindepth 1 | read; then
    rm output/*-output/*
fi
if find output/mappings/ -mindepth 1 | read; then
    rm output/mappings/*
fi
if find output/translations/ -mindepth 1 | read; then
    rm output/translations/*
fi
