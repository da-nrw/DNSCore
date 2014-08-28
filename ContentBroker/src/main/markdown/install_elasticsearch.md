# Elasticsearch

## Installation

_**Achtung**: Die eingesetzte Version von Elasticsearch muss der der Elasticsearch-Java-Library im ContentBroker entsprechen (momentan: 0.90.3)._

Die Installationsmethode hängt vom eingesetzten Betriebssystem ab. Empfohlen wird die Intallation unter Linux aus dem DEB- oder RPM-Repository (siehe http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/setup-repositories.html). Bei Installation einer dieser Pakete wird gleichzeitig ein Service eingerichtet, damit Elasticsearch nicht manuell gestartet werden muss. Weitere Informationen zur Installation finden sich unter: http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/setup.html

    rpm --import http://packages.elasticsearch.org/GPG-KEY-elasticsearch
    cd /etc/yum.repos.d/
    vi elasticsearch.repo

Füge folgenden Text ein

[elasticsearch-0.9]
name=Elasticsearch repository for 0.90.3 packages
baseurl=http://packages.elasticsearch.org/elasticsearch/0.90/centos
gpgcheck=1
gpgkey=http://packages.elasticsearch.org/GPG-KEY-elasticsearch
enabled=1

    yum install elasticsearch

Wenn die Installation erfolgreich war sollte folgender Befehl eine sinnvolle Ausgabe liefern:

    curl -XGET 'http://localhost:9200/_cluster/state'
	
## Konfiguration

In der zentralen Konfigurationsdatei (unter CentOS /etc/elasticsearch/elasticsearch.yml) sollte unbedingt der Clustername geändert werden. Dieser muss dem Wert von elasticsearch.cluster in der Contentbroker-Konfiguration (ContentBroker/conf/config.properties) entsprechen und für das DNS-Grid eindeutig sein, da sich Server des gleichen Clusters im selben Netzwerk automatisch synchronisieren.

Anschließend muss der in der ContentBroker-Konfiguration angegebene Index erstellt werden:

    curl -XPUT 'http://localhost:9200/portal_test'
    oder
    curl -XPUT 'http://localhost:9200/portal_ci'

und das Mapping für diesen Index festgelegt werden:

cd []/DNSCore/ContentBroker/src/main/conf/

    curl -XPUT 'http://localhost:9200/portal_test/ore:Aggregation/_mapping' -d @es_mapping.json
    
Achten Sie auf die Angabe des richtigen Indexes!

