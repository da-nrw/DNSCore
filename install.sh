#!/bin/bash

VERSION=$1
TARGET=$2
INSTALLATION=../installation

echo Installing to $TARGET
tar xf $INSTALLATION/binary-repository/ContentBroker-binaries-$VERSION.tar -C $TARGET
if [ -e $INSTALLATION/config.properties.cb ]
then
	cp $INSTALLATION/config.properties.cb $TARGET/conf/config.properties
fi
cp $INSTALLATION/hibernateCentralDB.cfg.xml $TARGET/conf/
cp $INSTALLATION/jhove $TARGET/jhove/
cp $INSTALLATION/jhove.conf $TARGET/jhove/conf/
cp $INSTALLATION/beans.xml $TARGET/conf
cp $INSTALLATION/logback.xml $TARGET/conf
cp $INSTALLATION/configure.sh $TARGET/
if [ -e $INSTALLATION/ffmpeg.sh ] 
then
	cp -f $INSTALLATION/ffmpeg.sh $TARGET/
fi
