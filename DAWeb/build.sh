#!/bin/bash
# author: Jens Peters 

VERSION=`cat ../VERSION.txt`
ENVIRONMENT=$1

echo "Building WAR"
grails $ENVIRONMENT war

cp target/daweb3-$VERSION.war ../installation/daweb3.war

