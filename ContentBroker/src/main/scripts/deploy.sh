#!/bin/bash

# author: Daniel M. de Oliveira

REVISION_NUMBER=`git rev-list HEAD --count`

rm -r /data/danrw/buildRepository/installation.$REVISION_NUMBER
cp -r ../installation /data/danrw/buildRepository/installation.$REVISION_NUMBER