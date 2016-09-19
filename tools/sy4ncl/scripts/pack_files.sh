#!/bin/bash
FOLDER=$1
BLISS_FOLDER_0=$FOLDER"_0_bliss"
BLISS_FOLDER_1=$FOLDER"_1_bliss"
STATS_FOLDER_0=$FOLDER"_0_stats"
STATS_FOLDER_1=$FOLDER"_1_stats"

#find $BLISS_FOLDER_0 -type f -name *.stats -print0 | xargs -0I{} cp {} $STATS_FOLDER_0
#find $BLISS_FOLDER_0 -type f -name *.map -print0 | xargs -0I{} cp {} $STATS_FOLDER_0
#find $BLISS_FOLDER_1 -type f -name *.stats -print0 | xargs -0I{} cp {} $STATS_FOLDER_1
#find $BLISS_FOLDER_1 -type f -name *.map -print0 | xargs -0I{} cp {} $STATS_FOLDER_1

tar -zcf $FOLDER.tar.gz $FOLDER $BLISS_FOLDER_0 $BLISS_FOLDER_1

rm -rf $FOLDER $BLISS_FOLDER_0 $BLISS_FOLDER_1