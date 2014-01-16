#!/bin/bash

# author: Jens Peters
# author: Daniel M. de Oliveira

TARGET=$1
if [[ "${TARGET:${#TARGET}-1}" == "/" ]]; then
	TARGET="${TARGET%?}"
fi	
if [ ! -d "$TARGET" ]; then
	echo Error: $TARGET is not a directory.
		exit
fi
HOME=`pwd`
if [ $TARGET = $TARGET ]; then 
	echo Error target environment $TARGET and current src tree are identical!
	exit
fi


echo Installing to $TARGET
tar xf ContentBroker.tar -C $TARGET
if [ -e config.properties.cb ]
then
	cp config.properties.cb $TARGET/conf/config.properties
fi
cp hibernateCentralDB.cfg.xml $TARGET/conf/
cp jhove $TARGET/jhove/
cp jhove.conf $TARGET/jhove/conf/
cp beans.xml $TARGET/conf
cp logback.xml $TARGET/conf
cp configure.sh $TARGET/
if [ -e ffmpeg.sh ] 
then
	cp -f ffmpeg.sh $TARGET/
fi

cd $TARGET
./configure.sh
