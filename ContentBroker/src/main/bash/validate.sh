#!/bin/bash

TODOS=`find src/*/java/de/ -name "*java" -exec grep "TODO" {} \; | wc -l`
XXXS=`find src/*/java/de/ -name "*java" -exec grep "XXX" {} \; | wc -l`

ALLOWED_TODOS=52
if [ "$TODOS" -gt "$ALLOWED_TODOS" ]
then
	echo "Number of TODO entries in java files must not exceed $ALLOWED_TODOS (actually is $TODOS)."
	exit 1
fi

ALLOWED_XXXS=10
if [ "$XXXS" -gt "$ALLOWED_XXXS" ]
then
	echo "Number of XXX entries in java files must not exceed $ALLOWED_XXXS (actually is $XXXS)."
	exit 1
fi

if [ "$1" = "ci" ]
then
	REVISION_NUMBER=`git rev-parse HEAD`
	if [ -d "/ci/BuildRepository/installation.$REVISION_NUMBER" ]
	then
		echo "The directory at /ci/BuildRepository/installation.$REVISION_NUMBER already exists which means"
		echo "you already have a valid build for the current revision. Will exit now."
		exit 1
	fi
fi
