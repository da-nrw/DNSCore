#!/bin/bash

# author: Thomas Kleinke

java -jar src/main/hsqldb/lib/sqltool.jar --sql="$1" xdb
