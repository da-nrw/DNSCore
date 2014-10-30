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
	rm -r /ci/storage/WorkArea/work
	mkdir /ci/storage/WorkArea/work/TEST
	mkdir /ci/storage/WorkArea/pips/TEST
	rm -r /ci/storage/IngestArea/TEST
	mkdir /ci/storage/IngestArea/TEST
	src/main/bash/rebuildIndex.sh
fi

