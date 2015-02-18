## Referenzdokumentation: config.properties

config.properties ist der Dateiname der Hauptkonfigurationsdatei des ContentBroker. Diese Datei befindet sich immer unter

    ${CB_HOME}/conf/config.properties
    
abzulegen bzw. zu finden. Sie ist notwendiger Bestandteil jeder DNSCore Installation.

Die Datei ist in verschiedene Blöcke unterteilt, die je nach gewünschter Gesamtkonfiguration (zusammen mit der beans.xml) vorhanden sein müssen. Insbesondere die mit **cb.implementation** startenden Konfigurationsparameter bestimmten, ob evenutell weitere Blöcke, wie z.B. der irods Block, ausgefüllt werden müssen oder auch nicht. Im folgenden werden alle Blöcke im Einzelnen behandelt.

Anmerkung: Wenn ein den folgenden Beschreibungen ein Parameter mit DEFAULT gekennzeichnet ist, bedeutet dass, dass, wenn der Parameter weggelassen wird, er automatisch vom ContentBroker mit dem dem gekennzeichneten Wert initialisiert wird.

## localNode

Der localNode.*-Block beinhaltet den Knoten selbst betreffende Konfigurationen und ist obligatorisch in jeder config.properties. Hier sehen wir ein Beispiel:  [config.properties.ci](../conf/config.properties.ci)

    localNode.userAreaRootPath=/ci/storage/UserArea
    localNode.ingestAreaRootPath=/ci/storage/IngestArea 
    localNode.workAreaRootPath=/ci/storage/WorkArea
    localNode.gridCacheAreaRootPath=/ci/storage/GridCacheArea
    localNode.workingResource=ciWorkingResource 
    localNode.replDestinations=ciArchiveResourceGroup
    localNode.id=

Der localNode entspricht dabei dem [Knoten](object_model.de.md#node---der-knoten)-Konzept der Applikation. Die einzurichtenden Pfade

#### localNode.userAreaRootPath

#### localNode.ingestAreaRootPath

#### localNode.workAreaRootPath

#### localNode.gridCacheAreaRootPath

entsprechen dabei den [Areas](processing_stages.md), also den unterschiedlichen Speicher-Bereichen, auf denen der ContentBroker seine Arbeit verrichtet. Es sollten immer absolute Pfade eingetragen sein. Es spielt keine Rolle, ob ein abschließendes Slash gesetzt ist oder nicht. 

Auf Knoten, auf denen nur die Präsenationskomponenten laufen, ist lediglich der Eintrag für **localNode.workAreaRootPath=** vonnöten, da die Komponenten für die Annahme und Herausgabe von Paketen und Speicherung von Paketen auf LZA-Medien hier keine Rolle spielen.

#### localNode.replDestinations
   
Unter ***replDestinations*** sind diejenigen Knoten angegeben, zu denen die Applikation Sekundärkopien der AIPs zur Sicherung der Langzeiterhaltung repliziert. Sind mehrere Knoten als Ziel angegeben, sind diese durch Kommata zu trennen.

Prinzipiell hängt es von der konkret eingesetzten ***GridFacade*** ab, worauf die Namen verweisen, im Falle von iRODS basierten ***GridFacade***s ("irodsGridFacade", "irodsFederatedGridFacade") entsprechen die Werte Namen entsprechender iRODS-Resourcen-(!)Gruppen. 

#### localNode.workingResource

Dieser Eintrag muss mit der Verwendung von iRODS basierten Speicheradaptern als Implementation des [DistributedConversionAdapter](#cbimplementationdistributedconversionadapter) (siehe  unten) ausgefüllt werden und bezeichnet eine dedizierte Resource, 
die als Pendant zur WorkingArea dient. Dass heisst, dass diese Resource immer (!) den VaultPath haben muss, der auch bei [localNode.workAreaRootPath](#localnodeworkarearootpath) angegeben ist.

#### localNode.id
    
Die hier einzutragende Zahl muss genau der id des Eintrages des jeweiligen Knoten (Node) in der Objektdatenbank entsprechen. Die hier eingetragene id ist dabei eindeutig innerhalb eines Gesamtsystems, d.h. es dürfen nicht zwei Maschinen mit derselben id konfiguriert sein.

## cb

Beispiel aus [config.properties.ci](../conf/config.properties.ci)

    cb.serverSocketNumber=4455
    cb.implementation.grid=irodsGridFacade
    cb.implementation.distributedConversion=irodsDistributedConversionAdapter
    cb.implementation.repository=fedoraRepositoryFacade
    cb.implementation.metadataExtractor=jhoveMetadataExtractor
    cb.bin.python=/ci/python/python

Die hierunter zusammengefassten Einträge hängen, genau wie die localNode Einträge, mit den Knoten (Node) Konzept zusammen, sind jedoch im Gegensatz zu diesen nicht fachlicher, sondern technischer Natur.

#### cb.serverSocketNumber

Hier ist eine freie Portnummer angegeben, die zur serverinternen Kommunikation zwischen ContentBroker und DAWeb bzw. ContentBroker und cbTalk.sh via einer MessageQueue reserviert wird.

#### cb.implementation.grid

Wenn ein Knoten nicht allein zur Verwaltung von PIPs genutzt wird, sondern in vollem Umfang als Knoten im Sinne der Langzeiterhaltung dient, muss dieser unter diesem Knoten der Name der Implementation angegeben werden, die die Anbindung
an die jeweils genutzte Speichertechnologie ermöglicht.

Derzeit verfügbare Implementationen sind 

* **irodsGridFacade** - iRODS im Zonenbetrieb
* **irodsFederatedGridFacade** - iRODS im Federatedbetrieb
* **fakeGridFacade** - Minimalimplementation zu Testzwecken. DEFAULT

#### cb.implementation.distributedConversionAdapter

Um Workflows gestalten so zu können, dass Jobs von mehreren Knoten des Systemes kooperativ bearbeitet werden können, ist eine Technologie vonnöten, um die Synchronisierung der Daten auf der WorkArea zu erreichen. Hierfür stehen derzeit drei Implementationen zur Verfügung:

* **irodsDistributedConversionAdapter** - Aufbauend auf einer Lösung basierend auf iRODS im Zonenbetrieb
* **irodsFederatedDistributedConversionAdapter** - Aufbauend auf einer Lösung basierend auf iRODS im Federated-Betrieb
* **fakeDistributedConversionAdapter** - Minimalimplementation zu Testzwecken. DEFAULT 

Ist entweder *irodsDistributedConversionAdapter* sowie *irodsFederatedDistributedConversionAdapter* gewählt, so muss auch der Eintrag [localNode.workingResource](#localnodeworkingresource) (siehe oben) gesetzt sein.

#### cb.implementation.repository

Die jeweils ausgewählte Implementation kapselt die Technologie, mit der das PresentationRepository umgesetzt ist.

Mögliche Werte sind

* **fedoraRepositoryFacade**
* **fakeRepositoryFacade** DEFAULT

Ist *fedoraRepositoryFacade* gewählt, so müssen sämtliche Parameter aus den [fedora](#fedora)- UND [elasticsearch](#elasticsearch)-Blöcken ausgefüllt sein.

#### cb.implementation.fileFormatFacade

Mögliche Werte sind

* **cb.implementation.fileFormatFacader=configurableFileFormatFacade** DEFAULT
* **cb.implementation.fileFormatFacade=fakeFileFormatFacade** 

#### cb.bin.python

Hier muss, wenn *cb.implementation.fileFormatFacade* nicht explizit auf *fakeFileFormatFacade* gesetzt ist, der Pfad zu einer Python Binary (Version >= 2.7) angegeben sein. Wenn sichergestellt werden kann, dass die Binary im Pfad der Laufzeitumgebung des ContentBroker ist, kann hier einfach *python* angegeben werden, ansonsten empiehlt sich hier die Angabe eine absoluten Pfades.

## irods

Ein vollständiges Beispiel für den Block ist [config.properties.ci](../conf/config.properties.ci)

    irods.user=rods
    irods.password=WpXlLLg3a4/S/iYrs6UhtQ==
    irods.server=cihost
    irods.zone=c-i
    irods.default_resc=ciWorkingResource
    irods.pam=false
    irods.keyStorePassword=
    irods.keyStorePath=
    irods.trustStorePath=

Wenn mindestens eines der Subsysteme *gridFacade* bzw. *distributedConversionAdapter* auf die Nutzung einer iRODS-basierten Implementation (irodsGridFacade,irodsFederatedGridFacade,irodsDistributedConversionAdapter,irodsFederatedDistributedConversionAdapter) konfiguriert ist, dann muss der ensprechende **irods**-Block in der config.properties ebenfalls ausgefüllt sein. Bei der Auswahl anderer Implementationen ist der Block überflüssig.

**BEANS** Der irods-Block an dieser Stelle korrespondiert mit dem Import-Eintrag ```<import resource="classpath*:META-INF/beans-infrastructure.irods.xml"/>``` aus der conf/beans.xml. Es muss sichergestellt werden, dass bei Nutzung von iRODS der entsprechende Eintrag in der Beans vorhanden ist.

#### iRODS-Basisdaten

    irods.user=
    irods.server=
    irods.zone=
    irods.default_resc=
    
#### Verschlüsselung der Authentifizierung
   
    irods.pam=
    irods.keyStorePassword=
    irods.keyStorePath=
    irods.trustStorePath=

#### irods.password

Das hier einzutragende Passwort muss vorab verschlüsselt sein mit Hilfe des DNSCore-eigenen [PasswordEncryptor](../../../../../../tree/master/PasswordEncryptor).

## fedora

Beispiel aus [config.properties.ci](../conf/config.properties.ci)

    fedora.url=http://localhost:8080/fedora
    fedora.user=fedoraAdmin
    fedora.password=BYi/MFjKDFd5Dpe52PSUoA==

When the application has been installed in one of wither pres or full mode, the presentation module is activated via its respective import in the import section of the beans.xml (see down below). Es muss sichergestellt werden, dass die eingetragenen Werte denen ensprechen, die bei der Installation von Fedora angegeben wurden. Mehr zur Installatioin von Fedora findet sich [hier](install_fedora.de.md).

**BEANS** der fedora-Block hier korrespondiert mit dem Eintrag ```<import resource="classpath*:META-INF/beans-infrastructure.fedora.xml"/>``` aus der conf/beans.xml. Es muss sichergestellt werden, dass bei Nutzung von Fedora/Elasticsearch der entsprechende Eintrag in der Beans vorhanden ist.

#### fedora.url

Die URL des Fedora Server

#### fedora.user

Der Fedora User, mit dem der ContentBroker auf dem Fedora arbeitet.

#### fedora.password
    
Das hier einzutragende Passwort muss vorab verschlüsselt sein mit Hilfe des DNSCore-eigenen [PasswordEncryptor](../../../../../../tree/master/PasswordEncryptor).

## elasticsearch

Beispiel [config.properties.ci](../conf/config.properties.ci)

    elasticsearch.index=portal_ci
    elasticsearch.hosts=localhost
    elasticsearch.cluster=cluster_ci

Wenn [cb.implementation.repository](#cbimplementationrepository) auf *fedoraRepositoryFacade* gesetzt ist, ist es notwendig, die Parameter aus diesem Block einzutragen. Es muss sichergestellt werden, dass die eingetragenen Werte der Elasticsearch-Installation entsprechen. Mehr zur Installation von Elasticsearch findet sich [hier](install_elasticsearch.de.md).

**BEANS** der elasticsearch-Block hier korrespondiert mit dem Eintrag ```<import resource="classpath*:META-INF/beans-infrastructure.fedora.xml"/>``` aus der conf/beans.xml. Es muss sichergestellt werden, dass bei Nutzung von Fedora/Elasticsearch der entsprechende Eintrag in der Beans vorhanden ist.

#### elasticsearch.index

Der Elasticsearch Indexname

#### elasticsearch.hosts

Der Elasticsearch hostname

#### elasticsearch.cluster
    
Der Elasticsearch clustername


