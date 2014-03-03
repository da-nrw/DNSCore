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
Das Presentation Repository des DA-NRW zur Bereitstellung der konvertierten browserfähigen Objekte und zum Abruf durch Portale läuft nur an einem Knoten des Grids und zwar an der Universität zu Köln. Momentan wird als Repository-Software Fedora 3.5 eingesetzt.

Im folgenden wird davon ausgegangen dass die Installationspfade wie folgt lauten (diese müssen beim Abarbeiten der Dokumentation ggf. entsprechend angepasst werden):
Fedora: /opt/fedora
Tomcat: /opt/tomcat

Im Source-Code-Repository befindet sich ein Projekt mit Skripten und Config-Dateien zu Fedora. Die aktuelle Adresse ist 
sftp://[login]@repositories.hki.uni-koeln.de/repositories/bzr/danrw/Fedora/trunk
Fedora
Installation
Tomcat und PostgreSQL müssen installiert sein
Fedora 3.5 läuft sowohl unter Tomcat 6, als auch unter Tomcat 7. Da folgende Versionen von Fedora Tomcat 7 benötigen wird dieser jedoch empfohlen.
User 'fedora' und Datenbank ‘fedora’ in PostgreSQL anlegen
adduser fedora
passwd fedora
psql -U postgres template1
CREATE USER fedora WITH PASSWORD 's3cr3t';
CREATE DATABASE fedora;
GRANT ALL PRIVILEGES ON DATABASE fedora TO fedora;
Datenbank "riTriples" für den Resource Index in PostgreSQL anlegen (Achtung: casesensitive name -> quotes verwenden!)
CREATE DATABASE "riTriples";
GRANT ALL PRIVILEGES ON DATABASE "riTriples" TO fedora;
Fedora Installer in der Version 3.5 downloaden:
wget http://downloads.sourceforge.net/fedora-commons/fcrepo-installer-3.5.jar
Installer ausführen:
sudo java -jar fcrepo-installer-3.5.jar

Während der Installation folgende Optionen wählen:
Installation type: custom
Fedora home directory: Pfad in dem Fedora installiert werden soll,
z.B.: /opt/fedora
Fedora administrator password: Passwort
Fedora server host: DNS-Name des Presentation Repository Servers
Fedora application server context: fedora
Authentication requirement for API-A: false
SSL availability: false
Servlet engine: existingTomcat
Tomcat home directory: Pfad zu Tomcat (z.B. /opt/tomcat)
Tomcat HTTP port: 8080
Tomcat shutdown port: 8005
Database: postgresql
Postgresql JDBC driver: included
Database username: fedora
Database password: In Schritt 2 gewähltes Passwort
JDBC URL: jdbc:postgresql://localhost/fedora
JDBC DriverClass: org.postgresql.Driver
Enable FeSL AuthN: true
Enable FeSL AuthZ: true
Policy enforcement enabled: true
Low Level Storage: akubra-fs
Enable Resource Index: true
Enable Messaging: false
Deploy local services and demos: false

Die Datei fedora.fcfg mit den entsprechenden Einstellungen befindet sich auch im Repository unter Fedora/trunk/config/fedora.fcfg

Ggf. die vom Installer erstellte server.xml in den Tomcat-Ordner conf kopieren
Eventuell müssen die Berechtigungen so angepasst werden, dass der User, unter dem Tomcat läuft, Schreibzugriff auf den Home-Ordner von Fedora hat
/opt/fedora/server/config/fedora.fcfg öffnen
Ggf. adminEmailList und repositoryName anpassen
Im Modul org.fcrepo.oai.OAIProvider ggf. adminEmails setzen, außerdem:
repositoryDomainName = danrw.de
repositoryName = DA-NRW Presentation Repository
Im Modul org.fcrepo.server.resourceIndex.ResourceIndex den Paramter datastore auf localPostgresMPTTriplestore
Ggf. die Datenbankeinstellungen (weiter unten in fedora.fcfg) im datastore localPostgresMPTTriplestore anpassen
Tomcat neustarten. 
Hinweise:
 Wenn Zugriff auf Fedora danach nicht möglich, muss idR das http-basic Login erneuert werden (Browser neu starten, oder in FF: Chronik->NeuesteChronikLöschen->AktiveLogins)
Wenn Nach Umstellung auf "localPostgresMPTTriplestore" von localMulgaraTriplestore Fedora nicht startet, muss ggf. der ResourceIndex neu aufgebaut werden, zusätzlich kann es zu Problemen mit der Validation von db-Schemata kommen. Siehe hierzu: 
http://comments.gmane.org/gmane.comp.cms.fedora-commons.user/6444
Die genannte Datei existiert nicht mehr in fcrepo 3.7, stattdessen:
fedora/server/config/fedora.fcfg line 147
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
Rebuild des ResourceIndex: 
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
Fedora kann jetzt unter folgenden URLs erreicht werden:
Generelle Informationen: http://<servername>:8080/fedora
Suchinterface: http://<servername>:8080/fedora/objects
Admininterface: http://<servername>:8080/fedora/admin
Policies
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
CREATE USER proai WITH PASSWORD 'proai';
CREATE DATABASE proai;
GRANT ALL PRIVILEGES ON DATABASE proai TO proai;

Konfiguration

Objekt zur Identifikation der OAI-PMH-Schnittstelle in Fedora anlegen.
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
