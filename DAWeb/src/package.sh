#!/bin/bash

VERSION="v`cat ../VERSION.txt` (build: `echo $BUILD_NUMBER` rev: `git rev-parse HEAD` created on `date`)"
REVISION_NUMBER=`git rev-parse HEAD`
TARGET=/ci/BuildRepository/installation.$REVISION_NUMBER/DAWeb

echo "copying Version and Build Info to DaWeb"
cp -f application.properties.template application.properties
mv application.properties application.properties.tmp
sed "s@app\.version=.*@app\.version=$VERSION@" application.properties.tmp >> application.properties
rm application.properties.tmp

mkdir $TARGET
cp target/daweb3-0.8.war $TARGET
cp doc/daweb3_properties.groovy.dev $TARGET/daweb3_properties.groovy.template
