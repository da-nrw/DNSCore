	/*
	  DA-NRW Software Suite | ContentBroker
	  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
	  Universität zu Köln
	
	  This program is free software: you can redistribute it and/or modify
	  it under the terms of the GNU General Public License as published by
	  the Free Software Foundation, either version 3 of the License, or
	  (at your option) any later version.
	
	  This program is distributed in the hope that it will be useful,
	  but WITHOUT ANY WARRANTY; without even the implied warranty of
	  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	  GNU General Public License for more details.
	
	  You should have received a copy of the GNU General Public License
	  along with this program.  If not, see <http://www.gnu.org/licenses/>.
	*/
	
# Preparing Fedora for DNSCore

This document is intended not to describe the fedora installation itself, but 
the configuration needed in order to let DNSCore work with it.

(To be translated and formatted)

## Presentation Repository

The so called "Presentation Repository" stores derivates (proxies) of your AIP. Although in OAIS terms, they are DIP too, in DNSCore they are called PIP (Presentation IP). The stored files to be considered to be browser readable. The presenation repository. DNSCore uses Fedora Commons 3.5 

### Prerequsites 

1. tomcat 
2. Postgres

### Installation of Fedora Commons for DNSCore

Fedora commons 3.5 Setup:

Create Database and user for fedora.

    sudo java -jar fcrepo-installer-3.5.jar
    
### Installation of Resource Index

    Installation type: custom
    Authentication requirement for API-A: false
    SSL availability: false
    Servlet engine: existingTomcat
    Tomcat home directory
    Tomcat HTTP port: 8080
    Tomcat shutdown port: 8005
    Database: postgresql
    Postgresql JDBC driver: included
    Database username: fedora
    JDBC URL: jdbc:postgresql://localhost/fedora
    JDBC DriverClass: org.postgresql.Driver
    Enable FeSL AuthN: true
    Enable FeSL AuthZ: true
    Policy enforcement enabled: true
    Low Level Storage: akubra-fs
    Enable Resource Index: true
    Enable Messaging: false
    Deploy local services and demos: false

Ensure tomcat owner can access the Fedora home dir.

Open 
    /opt/fedora/server/config/fedora.fcfg öffnen

and change adminEmailList and repositoryName depending to your needs.
     
    repositoryDomainName = danrw.de
    repositoryName = DA-NRW Presentation Repository

alter module 
    
    org.fcrepo.server.resourceIndex.ResourceIndex den Paramter datastore auf localPostgresMPTTriplestore

to your database properties.

<pre>
<module role="org.fcrepo.server.security.Authorization" class="org.fcrepo.server.security.DefaultAuthorization">
	<comment>Builds and manages Fedora's authorization structure.</comment>
	<param name="REPOSITORY-POLICY-GUITOOL-POLICIES-DIRECTORY" value="data/fedora-xacml-policies/repository-policies-generated-by-policyguitool" isFilePath="true">
  	<comment>This parameter is for future use.</comment>
	</param>
	<param name="POLICY-SCHEMA-PATH" value="xsd/cs-xacml-schema-policy-01.xsd"/>
	<param name="ENFORCE-MODE" value="permit-all-requests"/>
	<param name="VALIDATE-OBJECT-POLICIES-FROM-DATASTREAM" value="false"/>
	<param name="XACML-COMBINING-ALGORITHM" value="com.sun.xacml.combine.OrderedDenyOverridesPolicyAlg"/>
	<param name="VALIDATE-OBJECT-POLICIES-FROM-FILE" value="false"/>
	<param name="VALIDATE-REPOSITORY-POLICIES" value="false"/>
	<param name="REPOSITORY-POLICIES-DIRECTORY" value="data/fedora-xacml-policies/repository-policies" isFilePath="true"/>
  </module>
  </pre>
### Rebuild of ResourceIndex: 
run as root: 
    export $FEDORA_WEBAPP_HOME=/var/lib/tomcat7/webapps/fedora
    fedora/server/bin/fedora-rebuild.sh


/opt/tomcat/webapps/fedora/WEB-INF/applicationContext.xml öffnen
Den Parameter fedoraServerHost ändern

<bean class="org.fcrepo.server.config.Parameter">
  <constructor-arg type="java.lang.String" value="fedoraServerHost">
    <!-- Defines the host name for the Fedora server, as seen from
         the  outside world. -->
  </constructor-arg>
  <property name="value" value="<hostname>" />
</bean>

Tomcat neustarten

### Policies

Um den Zugriff auf Objekte zu verhindern, die nicht öffentlich zugänglich sein sollen, müssen die entsprechenden XACML-Policies wie folgt installiert werden:
Default Bootstrap-Policies löschen (sonst werden diese bei jedem Neustart geladen)
sudo rm -f /opt/fedora/pdp/policies/*

DA-NRW Policies laden
Skripte und Policy-Dateien aus dem bazaar-Repository laden

    bzr checkout sftp://[login]@repositories.hki.uni-koeln.de/repositories/bzr/danrw/Fedora/trunk

Die Policy-Objekte liegen unter trunk/policies und können mit dem Skript scripts/setup-policies.py geladen werden. Das Package python-httplib2 muss dazu installiert sein.
Vor der Ausführung sollte die FedoraClient-URL in den Skriptdateien angepasst werden.
Skript ausführen
    
    python scripts/setup-policies.py

AttributeFinder anpassen
Damit die Policy für die Beschränkung des Zugriffs auf nicht-öffentliche Objekte richtig funktioniert muss folgende Änderung in der Datei /opt/fedora/pdp/conf/config-attribute-finder.xml vorgenommen werden:
Die Zeile
<attribute designator="resource" 
    name="info:fedora/fedora-system:def/model#ownerId"/>
muss ersetzt werden durch:
<attribute designator="resource" 
      name="info:fedora/fedora-system:def/model#ownerId">
    <config name="target" value="object"/>
</attribute>
Dadurch wird sichergestellt, dass die in der Policy fedora-policy_access-closed-collection.xml referenzierte Eigenschaft ownerId sich bei Datastreams auf das übergeordnete Objekt bezieht. Erst dadurch ist es möglich, dass dem Besitzer eines Objekts auch die zugehörigen Datastreams angezeigt werden.
Tomcat neustarten
	User anlegen
Die Verwaltung der Fedora-User erfolgt derzeit über den Standard- Authentifizierungsmechanismus von Fedora. Neue User müssen in der Datei /opt/fedora/server/config/fedora-users.xml eingetragen werden.

Neben der Rolle administrator, welche Vollzugriff auf das Repository erlaubt, kann die Rolle harvester vergeben werden um Zugriff auf weitere lesende Funktionen der API (wie z.B. die Suche) zu erlauben.
OAI Provider
Installation

Der OAI Provider ist ein separates Servlet, dass die in Fedora bereits vorhandene OAI-PMH Schnittstelle durch eine stabilere und besser konfigurierbare Version ersetzt. Er wird wie folgt installiert:
oaiprovider.war herunterladen
wget http://downloads.sourceforge.net/fedora-commons/oaiprovider-1.2.2.zip
entpacken
unzip oaiprovider-1.2.2.zip
war in Tomcat deployen
sudo -u tomcat cp oaiprovider-1.2.2/oaiprovider.war /opt/tomcat/webapps/
Datenbank und Datenbankuser proai mit Passwort proai anlegen
adduser proai
passwd proai
psql -U postgres template1

CREATE DATABASE proai;
GRANT ALL PRIVILEGES ON DATABASE proai TO proai;

### Konfiguration

Objekt zur Identifikation der OAI-PMH-Schnittstelle in Fedora anlegen.

<pre>
python scripts/ingest.py -f config/danrw_PresentationRepository.xml
Objekt zur Beschreibung des DDB-Sets in Fedora anlegen:
python scripts/ingest.py -f config/set_ddb.xml
Konfigurationsdatei /opt/tomcat/webapps/oaiprovider/WEB-INF/classes/proai.properties bearbeiten.
Folgende Einstellungen vornehmen:
proai.cacheBaseDir = /data/danrw/proai/cache
proai.sessionBaseDir = /data/danrw/proai/sessions
proai.schemaDir = /data/danrw/proai/schemas
driver.fedora.queryFactory = fedora.services.oaiprovider.MPTQueryFactory
driver.fedora.pass = Passwort des Fedora Admins
driver.fedora.identify = http://localhost:8080/fedora/objects/\
 danrw:PresentationRepository/datastreams/Identify.xml/content
driver.fedora.itemID = http://www.openarchives.org/OAI/2.0/identifier
driver.fedora.md.formats = oai_dc mets ead epicur
driver.fedora.md.format.mets.loc = http://www.loc.gov/standards/\
 mets/version18/mets.xsd
driver.fedora.md.format.ead.loc = http://www.landesarchiv-bw.de/sixcms/media.php/\
 120/55633/EAD_DDB_1.1_Findbuch_XSD1.1.xsd
driver.fedora.md.format.epicur.loc = http://www.persistent-identifier.de/xepicur/version1.0/xepicur.xsd
driver.fedora.md.format.mets.uri = http://www.loc.gov/METS/
driver.fedora.md.format.ead.uri = urn:isbn:1-931666-22-9
driver.fedora.md.format.epicur.uri = urn:nbn:de:1111-2004033116
driver.fedora.md.format.oai_dc.dissType = info:fedora/*/DC
driver.fedora.md.format.mets.dissType = info:fedora/*/METS
driver.fedora.md.format.ead.dissType = info:fedora/*/EAD
driver.fedora.md.format.epicur.dissType = info:fedora/*/epicur.xml
driver.fedora.mpt.jdbc.user = fedora
driver.fedora.mpt.jdbc.password = Password des DB-Users für die Tabelle riTriples
</pre>

Die Datei mit den entsprechenden Einstellungen befindet sich auch im Bazaar-Repository unter Fedora/trunk/config/proai.properties
Tomcat neustarten

Der OAI Provider loggt standardmäßig zu stdout, d.h. in die Datei /opt/tomcat/log/catalina.out. Das Loggingverhalten kann in  /opt/tomcat/webapps/oaiprovider/WEB-INF/classes/log4j.xml angepasst werden.
	Elasticsearch
Installation

Achtung: Die eingesetzte Version von Elasticsearch muss der der Elasticsearch-Java-Library im ContentBroker entsprechen (momentan: 0.90.3).

Die Installationsmethode hängt vom eingesetzten Betriebssystem ab. Empfohlen wird die Intallation unter Linux aus dem DEB- oder RPM-Repository (siehe http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/setup-repositories.html). Bei Installation einer dieser Pakete wird gleichzeitig ein Service eingerichtet, damit Elasticsearch nicht manuell gestartet werden muss. Weitere Informationen zur Installation finden sich unter: http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/setup.html

Wenn die Installation erfolgreich war sollte folgender Befehl eine sinnvolle Ausgabe liefern:
	curl -XGET 'http://localhost:9200/_cluster/state'
Konfiguration
In der zentralen Konfigurationsdatei (unter CentOS /etc/elasticsearch/elasticsearch.yml) sollte unbedingt der Clustername geändert werden. Dieser muss dem Wert von elasticsearch.cluster in der Contentbroker-Konfiguration (ContentBroker/conf/config.properties) entsprechen.

Anschließend muss der in der ContentBroker-Konfiguration angegebene Index erstellt werden:
	curl -XPUT 'http://localhost:9200/portal_test'

und das Mapping für diesen Index festgelegt werden:
<pre>
	curl -XPUT 'http://localhost:9200/portal_test/aggregation/_mapping' -d '
	{
        "aggregation": {
            "properties": {
                "@id": {
                    "type": "string"
                },
                "@type": {
                    "type": "string"
                },
                "edm:aggregatedCHO": {
                    "dynamic": "true",
                    "path": "just_name",
                    "properties": {
                        "@id": {
                            "type": "string"
                        },
                        "@type": {
                            "type": "string"
                        },
                        "dc:contributor": {
                            "type": "string",
                            "index_name": "danrw_contributor"
                        },
                        "dc:creator": {
                            "type": "string",
                            "index_name": "danrw_contributor"
                        },
                        "dc:date": {
                            "type": "date",
                            "index_name": "danrw_date",
                            "format": "dateOptionalTime"
                        },
                        "dc:description": {
                            "type": "string",
                            "index_name": "danrw_title"
                        },
                        "dc:format": {
                            "type": "string"
                        },
                        "dc:identifier": {
                            "type": "multi_field",
                            "fields": {
                                "dc:identifier": {
                                    "type": "string",
                                    "index_name": "danrw_institution"
                                },
                                "identifier_facet": {
                                    "type": "string",
                                    "index": "not_analyzed",
                                    "omit_norms": true,
                                    "index_options": "docs",
                                    "include_in_all": false
                                }
                            }
                        },
                        "dc:language": {
                            "type": "string"
                        },
                        "dc:publisher": {
                            "type": "string",
                            "index_name": "danrw_institution"
                        },
                        "dc:relation": {
                            "type": "string"
                        },
                        "dc:rights": {
                            "type": "string"
                        },
                        "dc:source": {
                            "type": "string",
                            "index_name": "danrw_institution"
                        },
                        "dc:subject": {
                            "type": "string",
                            "index_name": "danrw_subject"
                        },
                        "dc:title": {
                            "type": "string",
                            "index_name": "danrw_title"
                        },
                        "dc:type": {
                            "type": "string"
                        },
                        "dcterms:alternative": {
                            "type": "string",
                            "index_name": "danrw_title"
                        },
                        "dcterms:created": {
                            "type": "date",
                            "format": "dateOptionalTime"
                        },
                        "dcterms:date": {
                            "type": "string"
                        },
                        "dcterms:extent": {
                            "type": "string"
                        },
                        "dcterms:isPartOf": {
                            "type": "multi_field",
                            "fields": {
                                "dcterms:isPartOf": {
                                    "type": "string",
                                    "index_name": "danrw_collection"
                                },
                                "isPartOf_facet": {
                                    "type": "string",
                                    "index": "not_analyzed",
                                    "omit_norms": true,
                                    "index_options": "docs",
                                    "include_in_all": false
                                }
                            }
                        },
                        "dcterms:issued": {
                            "type": "string"
                        },
                        "dcterms:provenance": {
                            "type": "string",
                            "index_name": "danrw_institution"
                        },
                        "dcterms:references": {
                            "type": "string"
                        },
                        "dcterms:spatial": {
                            "type": "string",
                            "index_name": "danrw_place"
                        },
                        "dcterms:temporal": {
                            "type": "string"
                        },
                        "edm:isNextInSequence": {
                            "dynamic": "true",
                            "path": "just_name",
                            "properties": {
                                "@id": {
                                    "type": "string"
                                }
                            }
                        },
                        "edm:type": {
                            "type": "string"
                        },
                        "owl:sameAs": {
                            "type": "string"
                        }
                    }
                },
                "edm:dataProvider": {
                    "type": "multi_field",
                    "fields": {
                        "edm:dataProvider": {
                            "type": "string",
                            "index_name": "danrw_institution"
                        },
                        "data_provider_facet": {
                            "type": "string",
                            "index": "not_analyzed",
                            "omit_norms": true,
                            "index_options": "docs",
                            "include_in_all": false
                        }
                    }
                },
                "edm:hasView": {
                    "dynamic": "true",
                    "path": "just_name",
                    "properties": {
                        "@id": {
                            "type": "string"
                        },
                        "dc:format": {
                            "type": "string",
                            "index_name": "danrw_mediaType",
                            "index": "not_analyzed",
                            "omit_norms": true,
                            "index_options": "docs"
                        }
                    }
                },
                "edm:isShownAt": {
                    "properties": {
                        "@id": {
                            "type": "string"
                        }
                    }
                },
                "edm:object": {
                    "dynamic": "true",
                    "path": "just_name",
                    "properties": {
                        "@id": {
                            "type": "string"
                        },
                        "dc:format": {
                            "type": "string"
                        }
                    }
                },
                "edm:provider": {
                    "type": "string"
                },
                "edm:rights": {
                    "properties": {
                        "@id": {
                            "type": "string"
                        }
                    }
                },
                "id": {
                    "type": "string"
                },
                "type": {
                    "type": "string"
                }
            }
        }
    }'
    
</pre>
