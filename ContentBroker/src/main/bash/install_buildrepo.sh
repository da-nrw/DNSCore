#!/bin/bash

# author: Daniel M. de Oliveira

REVISION_NUMBER=`git rev-list HEAD | wc -l`
cp -r ../installation /ci/BuildRepository/installation.$REVISION_NUMBER
