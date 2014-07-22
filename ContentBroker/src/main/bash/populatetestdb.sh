#!/bin/bash

# author: Daniel M. de Oliveira

HIER=`pwd`

if [ "$1" = "populate" ]
then
	sqls=(
	"DELETE FROM second_stage_scans;"
		"DELETE FROM queue;"
		"DELETE FROM events;"
		"DELETE FROM dafiles;"
		"DELETE FROM objects_packages;"
		"DELETE FROM packages;"
		"DELETE FROM objects;"
		"DELETE FROM conversion_routines_nodes;"
		"DELETE FROM nodes;"
		"DELETE FROM conversion_policies;"
		"DELETE FROM conversion_routines;"
		"DELETE FROM conversion_queue;"
		"DELETE FROM contractors;"
		"INSERT INTO contractors (id,short_name,admin) values (1,'TEST',0);"
        "INSERT INTO contractors (id,short_name,admin) values (2,'DEFAULT',0);"
        "INSERT INTO contractors (id,short_name,admin) values (3,'PRESENTER',0);"
        "INSERT INTO contractors (id,short_name,admin) values (4,'rods',1);"
        "INSERT INTO nodes (id,urn_index,name) values (1,0,'localnode');"
        "INSERT INTO conversion_routines (id,name,target_suffix,type) VALUES (1,'TIFF',null,'de.uzk.hki.da.format.TiffConversionStrategy');"
        "INSERT INTO conversion_routines (id,name,target_suffix,type) VALUES (2,'PIMG','jpg','de.uzk.hki.da.format.PublishImageConversionStrategy');"
        "INSERT INTO conversion_routines (id,name,target_suffix,type,params) VALUES (3,'CLITIF','tif','de.uzk.hki.da.format.CLIConversionStrategy','convert input output');"
        "INSERT INTO conversion_routines (id,name,target_suffix,type,params) VALUES (4,'CLICOPY','*','de.uzk.hki.da.format.PublishCLIConversionStrategy','cp input output');"
        "INSERT INTO conversion_routines_nodes (conversion_routines_id,nodes_id) VALUES (1,1);"
        "INSERT INTO conversion_routines_nodes (conversion_routines_id,nodes_id) VALUES (2,1);"
        "INSERT INTO conversion_routines_nodes (conversion_routines_id,nodes_id) VALUES (3,1);"
        "INSERT INTO conversion_policies (id,contractor_id,source_format,conversion_routine_id) VALUES (1,2,'fmt/353',1);"
        "INSERT INTO conversion_policies (id,contractor_id,source_format,conversion_routine_id) VALUES (2,3,'fmt/353',2);"
        "INSERT INTO conversion_policies (id,contractor_id,source_format,conversion_routine_id) VALUES (3,2,'fmt/116',3);"
        "INSERT INTO conversion_policies (id,contractor_id,source_format,conversion_routine_id) VALUES (4,3,'x-fmt/392',2);"
        "INSERT INTO conversion_policies (id,contractor_id,source_format,conversion_routine_id) VALUES (5,3,'fmt/4',2);"
        "INSERT INTO conversion_policies (id,contractor_id,source_format,conversion_routine_id) VALUES (6,3,'fmt/16',4);"
        "INSERT INTO conversion_policies (id,contractor_id,source_format,conversion_routine_id) VALUES (7,3,'fmt/354',4);"
        "INSERT INTO second_stage_scans (id,puid,allowed_values,format_identifier_script_name) VALUES (1,'x-fmt/384','svq1','ffmpeg.sh');"
        "INSERT INTO second_stage_scans (id,puid,allowed_values,format_identifier_script_name) VALUES (2,'fmt/200','dvvideo','ffmpeg.sh');"
        "INSERT INTO second_stage_scans (id,puid,allowed_values,format_identifier_script_name) VALUES (3,'fmt/5','cinepak','ffmpeg.sh');"
	)
fi

for i in "${sqls[@]}"
do
	echo "$i"
	cd $HIER
	
	if [ "$2" = "ci" ]
	then
	    psql -U cb_usr -d CB -c "$i"
	fi
	if [ "$2" = "dev" ]
	then
	    java -jar ../3rdParty/hsqldb/lib/sqltool.jar --autoCommit --sql "$i" xdb 
	fi
done




