#!/bin/bash

# author: Polina Gubaidullina

cd target/installation/

./SipBuilder-Unix.sh -source=$1 -destination=$2 $3 $4
