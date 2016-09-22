


#!/bin/bash

# Usage: get_property FILE KEY
function get_property
{
    grep "^$2=" "$1" | cut -d'=' -f2
}


indexName=$(get_property "conf/config.properties" "elasticsearch.index")

#Todo: if indexName undefined -> indexName=portal_ci#

indexName=$indexName"_test"
printf "Reset Index: $indexName \n"
export http_proxy=
echo rebuilding index
curl -XDELETE "localhost:9200/$indexName"
curl -XPUT "localhost:9200/$indexName"

sleep 5
curl -XPOST "localhost:9200/$indexName/_close"
curl -XPUT "localhost:9200/$indexName/_settings" -d "@conf/es_settings.json"
curl -XPUT  "localhost:9200/$indexName/ore:Aggregation/_mapping" -d "@conf/es_mapping.json"
curl -XPOST "localhost:9200/$indexName/_open"
echo "\nReset Index: $indexName done\n"
