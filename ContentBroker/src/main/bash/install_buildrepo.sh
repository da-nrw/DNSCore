#!/bin/bash

# author: Daniel M. de Oliveira
# Jens Peters

echo "PWD: " `pwd`
cd /ci/DNSCore/ContentBroker
echo "PWD: " `pwd`
REVISION_NUMBER=`git rev-parse HEAD`
TARGET=/ci/BuildRepository/installation.$REVISION_NUMBER

mkdir $TARGET
mkdir $TARGET/ContentBroker
mkdir $TARGET/DAWeb
mkdir $TARGET/SipBuilder
mkdir $TARGET/RegressionTestCB


cp -r /ci/DNSCore/RegressionTestCB/target/installation $TARGET/RegressionTestCB

cd /ci/DNSCore/ContentBroker
cp -r target/installation $TARGET/ContentBroker

cp /ci/DNSCore/LICENSE $TARGET/DAWeb/

cd /ci/DNSCore/DAWeb
cp ./build/libs/daweb3-0.1-SNAPSHOT.war $TARGET/DAWeb/daweb3.war
cp doc/daweb3_properties.groovy.dev $TARGET/DAWeb/daweb3_properties.groovy.template

cd /ci/DNSCore/SIP-Builder
cp -r target/installation $TARGET/SipBuilder

cd /ci/DNSCore
git log --pretty=format:"%h %s %b" PRODUCTION..HEAD | grep -v 'Update.*md' | grep -vi Merge > $TARGET/CHANGES_README.txt

