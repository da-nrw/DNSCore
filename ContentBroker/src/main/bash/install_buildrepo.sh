#!/bin/bash

# author: Daniel M. de Oliveira

cd /ci/DNSCore/ContentBroker
REVISION_NUMBER=`git rev-list HEAD | wc -l`
cp -r target/installation /ci/BuildRepository/installation.$REVISION_NUMBER
