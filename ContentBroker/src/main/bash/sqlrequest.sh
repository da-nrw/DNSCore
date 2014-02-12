#!/bin/bash

# author: Thomas Kleinke

java -jar ../../../../3rdParty/hsqldb/lib/sqltool.jar --sql="$1" --autocommit xdb
