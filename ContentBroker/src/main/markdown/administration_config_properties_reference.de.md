## Die Konfigurationsdatei "config.properties" - Referenzdokumentation

Die Datei ist in einer ContentBroker immer unter 

    ${CB_HOME}/conf/config.properties
    
abzulegen bzw. zu finden. Sie ist notwendiger Bestandteil jeder DNSCore Installation.

Die Datei ist in verschiedene Blöcke unterteilt, die je nach gewünschter Gesamtkonfiguration (zusammen mit der beans.xml) vorhanden sein müssen.

### localNode

Der localNode.*-Block beinhaltet den Knoten selbst betreffende Konfigurationen und ist obligatorisch in jeder config.properties. Hier sehen wir ein Beispiel:  [config.properties.ci](../conf/config.properties.ci)

    localNode.userAreaRootPath=/ci/storage/UserArea
    localNode.ingestAreaRootPath=/ci/storage/IngestArea 
    localNode.workAreaRootPath=/ci/storage/WorkArea
    localNode.gridCacheAreaRootPath=/ci/storage/GridCacheArea
    localNode.workingResource=ciWorkingResource 
    localNode.replDestinations=ciArchiveResourceGroup
    localNode.id=

Der localNode entspricht dabei dem [Knoten](object_model.de.md#node---der-knoten)-Konzept der Applikation. Die einzurichtenden Pfade

    localNode.userAreaRootPath=
    localNode.ingestAreaRootPath=
    localNode.workAreaRootPath=
    localNode.gridCacheAreaRootPath=

entsprechen dabei den [Areas](processing_stages.md), also den unterschiedlichen Speicher-Bereichen, auf denen der ContentBroker seine Arbeit verrichtet. Es sollten immer absolute Pfade eingetragen sein. Es spielt keine Rolle, ob ein abschließendes Slash gesetzt ist oder nicht. 

Auf Knoten, auf denen nur die Präsenationskomponenten laufen, ist lediglich der Eintrag für **localNode.workAreaRootPath=** vonnöten, da die Komponenten für die Annahme und Herausgabe von Paketen und Speicherung von Paketen auf LZA-Medien hier keine Rolle spielen.

##### localNode.replDestinations
   
Unter ***replDestinations*** sind diejenigen Knoten angegeben, zu denen die Applikation Sekundärkopien der AIPs zur Sicherung der Langzeiterhaltung repliziert. Sind mehrere Knoten als Ziel angegeben, sind diese durch Kommata zu trennen.

Prinzipiell hängt es von der konkret eingesetzten ***GridFacade*** ab, worauf die Namen verweisen, im Falle von iRODS basierten ***GridFacade***s ("irodsGridFacade", "irodsFederatedGridFacade") entsprechen die Werte Namen entsprechender iRODS-Resourcen-(!)Gruppen. 

##### localNode.workingResource

Dieser Eintrag muss mit der Verwendung von iRODS basierten Speicheradaptern als Implementation des *DistributedConversionAdapter* (siehe [unten](#cbimplementationdistributedconversion)) ausgefüllt werden und bezeichnt eine dedizierte Resource, 
die als Pendant zur WorkingArea dient. Dass heisst, dass diese Resource immer (!) den VaultPath haben muss, der auch bei localNode.workingAreaRootPath angegeben ist.

##### localNode.id
    
Die hier einzutragende Zahl muss genau der id des Eintrages des jeweiligen Knoten (Node) in der Objektdatenbank entsprechen. Die hier eingetragene id ist dabei eindeutig innerhalb eines Gesamtsystems, d.h. es dürfen nicht zwei Maschinen mit derselben id konfiguriert sein.

### cb

Beispiel aus [config.properties.ci](../conf/config.properties.ci)

    cb.serverSocketNumber=4455
    cb.implementation.grid=irodsGridFacade
    cb.implementation.distributedConversion=irodsDistributedConversionAdapter
    cb.implementation.repository=fedoraRepositoryFacade
    cb.implementation.metadataExtractor=jhoveMetadataExtractor
    cb.bin.python=/ci/python/python

Die hierunter zusammengefassten Einträge hängen, genau wie die localNode Einträge, mit den Knoten (Node) Konzept zusammen, sind jedoch im Gegensatz zu diesen nicht fachlicher, sondern technischer Natur.

##### cb.serverSocketNumber

Hier ist eine freie Portnummer angegeben, die zur serverinternen Kommunikation zwischen ContentBroker und DAWeb bzw. ContentBroker und cbTalk.sh via einer MessageQueue reserviert wird.

##### cb.implementation.grid=

Wenn ein Knoten nicht allein zur Verwaltung von PIPs genutzt wird, sondern in vollem Umfang als Knoten im Sinne der Langzeiterhaltung dient, muss dieser unter diesem Knoten der Name der Implementation angegeben werden, die die Anbindung
an die jeweils genutzte Speichertechnologie ermöglicht.

Derzeit verfügbare Implementationen sind 

* ***irodsGridFacade*** - iRODS im Zonenbetrieb
* ***irodsFederatedGridFacade*** - iRODS im Federatedbetrieb
* ***fakeGridFacade*** - Minimalimplementation zu Testzwecken. DEFAULT


##### cb.implementation.distributedConversion

Um Workflows gestalten so zu können, dass Jobs von mehreren Knoten des Systemes kooperativ bearbeitet werden können, ist eine Technologie vonnöten, um die Synchronisierung der Daten auf der WorkArea zu erreichen. Hierfür stehen derzeit drei Implementationen zur Verfügung:

* ***irodsDistributedConversionAdapter*** - Aufbauend auf einer Lösung basierend auf iRODS im Zonenbetrieb
* ***irodsFederatedDistributedConversionAdapter*** - Aufbauend auf einer Lösung basierend auf iRODS im Federated-Betrieb
* ***fakeDistributedConversionAdapter*** - Minimalimplementation zu Testzwecken. DEFAULT 

Ist entweder **irodsDistributedConversionAdapter** sowie **irodsFederatedDistributedConversionAdapter** gewählt, so muss auch der Eintrag localNode.workingResource gesetzt sein.

##### cb.bin.python

Here you have to insert the command to run an instance of python (at the moment >= 2.7 is required). If you are sure the required command is globally visible in the environment (the shell or process) in which the ContentBroker.jar is intended to run, you simple can insert something as simple as "python" as a value. If this is not the case, for example if the packaging system of your distro has only python in a version < 2.7 and you have a self compiled version at another path
on your file system, you should insert the full path to the python binary as a value.

	cb.implementation.metadataExtractor=
	cb.implementation.metadataExtractor=jhoveMetadataExtractor (defaul)
	cb.implementation.metadataExtractor=fakeMetadataExtractor
	
	
### irods

Wenn mindestens eines der Subsysteme "gridFacade" bzw. "distributedConversionAdapter", konfigurierbar per

    cb.implementation.grid=
    cb.implementation.distributedConversionAdapter=
    
auf die Verwendung von iRODS hin konfiguriert sind, siehe

    cb.implementation.grid=irodsGridFacade
    cb.implementation.distributedConversionAdapter=irodsDistributedConversionAdapter
    
so ist es erforderlich, dass der optionale "irods.*"-Block auch innerhalb der config.properties vorhanden ist.

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


 

These settings are optional and must be set only if cb.implementation.grid or cb.implementaion.districutedConversion
are set to use the corresponding irods specific implementations. Nodes not using irods dont need these parameters.

    irods.user=
    irods.server=
    irods.zone=
    irods.default_resc=
    irods.pam=
    irods.keyStorePassword=
    irods.keyStorePath=
    irods.trustStorePath=

asdf

    irods.password= 

The password has to be encrypted with the password encryptor/decryptor which is part of the DNSCore project itself (if you haven't already, you can see the sub project [here](https://github.com/da-nrw/DNSCore/tree/master/PasswordEncryptor).

### fedora

Beispiel aus [config.properties.ci](../conf/config.properties.ci)

    fedora.url=http://localhost:8080/fedora
    fedora.user=fedoraAdmin
    fedora.password=BYi/MFjKDFd5Dpe52PSUoA==

When the application has been installed in one of wither pres or full mode, the presentation module is activated via its respective import in the import section of the beans.xml (see down below).

    fedora.url=
    fedora.user=

In pres or full mode the ContentBroker and the presentation repository are hosted on one and the same machine. Fedora runs on a tomcat and fedora.url points to the http://... adress of Fedora while fedora.user is a fedora user prepared
for usage by the ContentBroker.

    fedora.password=
    
The passwort has to be encrypted/decrypted with the PasswordEncryptor of DNSCore.

###elasticsearch

Beispiel [config.properties.ci](../conf/config.properties.ci)

    elasticsearch.index=portal_ci
    elasticsearch.hosts=localhost
    elasticsearch.cluster=cluster_ci

The elasticsearch settings only are necessary on nodes which provide presentation repository functionality, which is enabled by choosing either the full or pres setting during installation.

    elasticsearch.index=
    elasticsearch.hosts=
    elasticsearch.cluster=
    
Make sure you insert the same settings you have used during your elasticsearch installation.
