#!/bin/bash

# author: Daniel M. de Oliveira
# author: Jens Peters
echo clean iRODS Stuff

if [ "$1" = "ci" ]
then
	irm -rf /c-i/aip/TEST                2>/dev/null
	irm -rf /c-i/pips/institution/TEST   2>/dev/null
	irm -rf /c-i/pips/public/TEST        2>/dev/null
	rm -r /ci/storage/GridCacheArea/aip/TEST 
	mkdir /ci/storage/GridCacheArea/aip/TEST
	src/main/bash/rebuildIndex.sh
fi

