#!/bin/bash

TODOS=`find src/*/java/de/ -name "*java" -exec grep "TODO" {} \; | wc -l`
XXXS=`find src/*/java/de/ -name "*java" -exec grep "XXX" {} \; | wc -l`

ALLOWED_TODOS=43
if [ "$TODOS" -gt "$ALLOWED_TODOS" ]
then
	echo "Number of TODO entries in java files must not exceed $ALLOWED_TODOS (actually is $TODOS)."
	exit 1
fi

ALLOWED_XXXS=13
if [ "$XXXS" -gt "$ALLOWED_XXXS" ]
then
	echo "Number of XXX entries in java files must not exceed $ALLOWED_XXXS (actually is $XXXS)."
	exit 1
fi