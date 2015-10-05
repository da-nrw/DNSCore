#!/bin/bash

# author: Daniel M. de Oliveira
# Jens Peters

REVISION_NUMBER=`git rev-parse HEAD`
TARGET=/ci/BuildRepository/installation.$REVISION_NUMBER

mkdir $TARGET
mkdir $TARGET/ContentBroker
mkdir $TARGET/DAWeb
mkdir $TARGET/SipBuilder

cd /ci/DNSCore/ContentBroker
cp -r target/installation $TARGET/ContentBroker

cd /ci/DNSCore/DAWeb
cp target/daweb3-SNAPSHOT.war $TARGET/DAWeb/daweb3.war
cp doc/daweb3_properties.groovy.dev $TARGET/DAWeb/daweb3_properties.groovy.template

cd /ci/DNSCore/SIP-Builder
cp -r target/installation $TARGET/SipBuilder
cd /ci/DNScore
git tag b$BUILD_NUMBER
git log --pretty=format:%s PRODUCTION..b$BUILD_NUMBER >> $TARGET/CHANGES_README.txt
git push --tags
