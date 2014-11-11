#!/bin/bash

# author: Daniel M. de Oliveira

rm cbTalk.sh
rm README.txt
rm ContentBroker_start.sh.template
rm ContentBroker_stop.sh.template
rm -r conf
rm target/installation/config.properties
rm target/installation/hibernateCentralDB.cfg.xml


sqls=(
	"DELETE FROM events;"
	"DELETE FROM conversion_queue;"
	"DELETE FROM dafiles;"
	"DELETE FROM queue;"
	"DELETE FROM objects_packages;"
	"DELETE FROM packages;"
	"DELETE FROM objects;"
)

for i in "${sqls[@]}"
do
	echo "$i"

	if [ "$1" = "ci" ]
	then
	    psql -U cb_usr -d CB -c "$i"
	fi
done

