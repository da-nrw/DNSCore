#!/bin/bash

export http_proxy=
echo rebuilding index
curl -XDELETE 'localhost:9200/portal_ci_test'
curl -XPUT 'localhost:9200/portal_ci_test'
cd src/main/conf
curl -XPUT    'localhost:9200/portal_ci_test/ore:Aggregation/_mapping' -d "@es_mapping.json"


