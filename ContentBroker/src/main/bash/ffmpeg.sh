#!/bin/bash
# AUTHOR: Daniel M. de Oliveira
#
TESTFILEPATH="$1"

NUMBEROFSTREAMS=`ffmpeg -i "$TESTFILEPATH" 2>&1 >/dev/null | grep 'Stream.*Video' |  wc -l`

# Currently only files with one videostream is supported
if [[ $NUMBEROFSTREAMS -ne "1" ]]
then
	printf "1"
	exit 1
fi

# TODO: Audio Streams

CODEC=`ffmpeg -i "$TESTFILEPATH" 2>&1 >/dev/null | grep 'Stream.*Video' |  awk '{print $4}' | sed 's/,//'`

LENGTH=${#CODEC}
if [[ $LENGTH -eq "" ]] 
then
	printf "1"
	exit 1
fi

printf $CODEC
