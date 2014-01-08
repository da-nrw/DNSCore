#!/bin/bash
# author: Daniel M. de Oliveira
# author: Jens Peters
# configures jhove

INSTALL_PATH=`pwd`

if [ ! -d "$INSTALL_PATH" ]; then
	echo Error: $INSTALL_PATH is not a directory.
	exit
fi
mv jhove/conf/jhove.conf  jhove/conf/jhove.conf.tmp
mv jhove/jhove jhove/jhove.tmp 
sed "s@CONTENTBROKER_ROOT@$INSTALL_PATH@" jhove/conf/jhove.conf.tmp >> jhove/conf/jhove.conf
sed "s@CONTENTBROKER_ROOT@$INSTALL_PATH@" jhove/jhove.tmp >> jhove/jhove
rm jhove/conf/jhove.conf.tmp
rm jhove/jhove.tmp

PYTHON_BIN=`cat conf/config.properties | grep python.bin | sed "s@python.bin=@@"`
if [ "$PYTHON_BIN" = "" ]
then
	echo ERROR could not read python.path from config.properties
	exit
fi
mv fido.sh fido.sh.tmp
sed "s@PYTHON_BIN@$PYTHON_BIN@" fido.sh.tmp >> fido.sh
rm fido.sh.tmp
chmod u+x fido.sh
