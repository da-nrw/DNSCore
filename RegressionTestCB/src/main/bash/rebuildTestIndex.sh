#!/bin/bash

# Usage: get_property FILE KEY
function get_property
{
    grep "^$2=" "$1" | cut -d'=' -f2
}

indexHost=$(get_property "conf/config.properties" "elasticsearch.hosts")
#indexHost=${indexHost##:*}
if [ "$indexHost" == "" ] ; then
	echo "No indexhost found in properties!!!";
	exit 0;
fi

indexName=$(get_property "conf/config.properties" "elasticsearch.index")
if [ "$indexName" == "" ] ; then
	echo "No indexname found in properties!!!";
	exit 0;
fi
#Todo: if indexName undefined -> indexName=portal_ci#

indexName=$indexName"_test"
printf "Reset Index: $indexHost $indexName \n"
export http_proxy=
echo rebuilding index
curl -XDELETE "$indexHost:9200/$indexName"
curl -XPUT "$indexHost:9200/$indexName"

sleep 5
curl -XPOST "$indexHost:9200/$indexName/_close"
curl -XPUT "$indexHost:9200/$indexName/_settings" -d "@conf/es_settings.json"
curl -XPUT  "$indexHost:9200/$indexName/ore:Aggregation/_mapping" -d "@conf/es_mapping.json"
curl -XPOST "$indexHost:9200/$indexName/_open"
echo "\nReset Index: $indexHost $indexName done\n"
