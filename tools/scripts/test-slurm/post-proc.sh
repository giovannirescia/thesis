#!/bin/bash
echo "doing post-processing!"
cat *.tmp >> results.out
rm *.tmp
