#!/bin/bash
# author: Daniel M. de Oliveira
# author: Jens Peters
# configures jhove

SED_BIN=sed
OS=`uname -s`
case "$OS" in
SunOS)
				SED_BIN=gsed
        ;;
esac


INSTALL_PATH=`pwd`

if [ ! -d "$INSTALL_PATH" ]; then
	echo Error: $INSTALL_PATH is not a directory.
	exit
fi
mv jhove/conf/jhove.conf  jhove/conf/jhove.conf.tmp
mv jhove/jhove jhove/jhove.tmp 
$SED_BIN "s@CONTENTBROKER_ROOT@$INSTALL_PATH@" jhove/conf/jhove.conf.tmp >> jhove/conf/jhove.conf
$SED_BIN "s@CONTENTBROKER_ROOT@$INSTALL_PATH@" jhove/jhove.tmp >> jhove/jhove
rm jhove/conf/jhove.conf.tmp
rm jhove/jhove.tmp

PYTHON_BIN=`cat conf/config.properties | grep cb.bin.python | sed "s@cb.bin.python=@@"`
if [ "$PYTHON_BIN" = "" ]
then
	echo "ERROR could not read python.bin from config.properties"
	echo "ERROR Make sure there is an appropriate entry for cb.bin.python=[path to a python version > 2.7]".
	echo "ERROR If not, add it and call this script again."
	exit 1
fi
mv fido.sh fido.sh.tmp
sed "s@PYTHON_BIN@$PYTHON_BIN@" fido.sh.tmp >> fido.sh
rm fido.sh.tmp
chmod u+x fido.sh
