#!/bin/bash

# Author: Daniel M. de Oliveira

TARGET=$1

mkdir $1/conf
cp src/main/resources/healthCheck.avi $1/conf
cp src/main/resources/healthCheck.tif $1/conf
cp -r ../3rdParty/fido $1/
cp -r ../3rdParty/jhove $1/
cp src/main/sh/jhove $1/jhove
cp src/main/conf/jhove.conf $1/jhove/conf
cp src/test/resources/at/ATUseCaseIngest1.tgz $1/conf/basic_test_package.tgz
cp src/main/bash/fido.sh $1/
cp src/main/bash/ffmpeg.sh $1/
cp src/main/bash/configure.sh $1/
