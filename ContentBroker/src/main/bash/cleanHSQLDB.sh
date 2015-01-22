#!/bin/bash
# author: Daniel M. de Oliveira

DATABASE_PROC_ID=`ps -aef | grep hsqldb.jar | grep -v grep | awk '{print $2}'`
if [ "$DATABASE_PROC_ID" != "" ]
then
        echo Killing hsql database process $DATABASE_PROC_ID.
        kill -9 $DATABASE_PROC_ID
fi

sleep 2

rm -r mydb.tmp 2> /dev/null
rm mydb.*      2> /dev/null

echo cleanHSQLDB done
