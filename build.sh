#!/bin/bash

#################################
# Please do not edit this file! #
#################################
set -e
JAVA_HOME='/usr/java/default'
NXJ_HOME='/local/java/leJOS_NXJ'

echo "Compiling $1"
JAVA_HOME=$JAVA_HOME NXJ_HOME=$NXJ_HOME $NXJ_HOME/bin/nxjc $1

# Get the file without the extension
filename=$(basename -- "$1")
extension="${filename##*.}"
filename="${filename%.*}"

# Deploy it to the NXT (if it's connected)
#$NXJ_HOME/bin/nxj $filename


# linking
$NXJ_HOME/bin/nxjlink -o $filename.nxj $filename

# Upload to the nxt
$NXJ_HOME/bin/nxjupload -r $filename.nxj
