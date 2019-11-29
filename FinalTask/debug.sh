#/bin/bash

SRCNAME=$1
ERRCODE=$2

/local/java/leJOS_NXJ/bin/nxjc $SRCNAME.java
/local/java/leJOS_NXJ/bin/nxjlink -o $SRCNAME.nxj -od $SRCNAME.nxd $SRCNAME
/local/java/leJOS_NXJ/bin/nxjdebugtool -di $SRCNAME.nxd -c -m $ERRCODE
