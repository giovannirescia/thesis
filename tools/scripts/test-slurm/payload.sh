#!/bin/bash
PID=$BASHPID
echo "proccessing file: "$1 "(" $2 "/" $3")"
echo "proccess: "$PID " - file: " $1  >> $PID.tmp
sleep 1
echo >&3
