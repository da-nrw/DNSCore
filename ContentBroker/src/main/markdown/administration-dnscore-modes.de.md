# DNSCore - Funktionsmodi

DNSCore kann grundsätzlich in drei verschiedenen Funktionsumfängen installiert werden. Der Installer (./install.sh) bietet diese drei Modi beid der Installation zur Auswahl an.

* (N)ode 
* (F)ull
* (P)res
 
Je nach Auswahl liefert der Installer den ContentBroker mit einer bestimmten Konfiguration aus (beans.xml), die die gewünschten Komponenten und Funktionalitäten des ContentBroker.jar aktiviert. Wenn diese Komponenten aktiviert sind, müssen auch die entsprechenden Blöcke in den config.properties korrekt ausgefüllt sein (siehe jeweils Abschnitte beans.xml), damit die Komponenten die notwendigen Konfigurationsinformationen enthalten.

## Node - Knotenmodus

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/system-modi1.jpg)

In diesem Modus hostet ein Knoten (Node) die Komponenten DAWeb, ContentBroker und iRODS. AIPs für die Langzeitarchivierung werden generiert und im Grid verteilt. PIPs für die Präsentation werden generiert und an den dafür vorgesehenen Präsentationsknoten (PresNode) gesendet. 

##### beans.xml

Die ausgelieferte beans.xml enhält in diesem Fall folgende Imports:

     <import resource="classpath*:META-INF/beans-infrastructure.common.xml"/>
     <import resource="classpath*:META-INF/beans-infrastructure.identifier.xml"/>
     <import resource="classpath*:META-INF/beans-infrastructure.irods.xml"/>
     <import resource="classpath*:META-INF/beans-infrastructure.irodsgridfacade.xml"/>
     <import resource="classpath*:META-INF/beans-infrastructure.irodsdistributedconversionadapter.xml"/>

     <import resource="classpath*:META-INF/beans-workflow.ingest.xml"/>
     <import resource="classpath*:META-INF/beans-workflow.retrieval.xml"/>
     <import resource="classpath*:META-INF/beans-workflow.pipgen.xml"/>
     <import resource="classpath*:META-INF/beans-workflow.other.xml"/>

Die Module IrodsGridFacade und IrodsDistributedConversionAdapter werden eingebunden. 
Der Knoten kennt die normalen Knotenworkflows kennt und kann sie durchführen. Jedoch kennt er keine Workflows zur PIP-Aufbereitung.

##### config.properties

Folgende Paremeter müssen aufgrund der Bean Configuration vorhanden sein.

    irods.user=rods
    irods.password=
    irods.server=
    irods.zone=
    irods.default_resc=
    irods.pam=
    irods.keyStorePassword=
    irods.keyStorePath=
    irods.trustStorePath=

    localNode.userAreaRootPath=
    localNode.ingestAreaRootPath=
    localNode.workAreaRootPath=
    localNode.gridCacheAreaRootPath=
    localNode.workingResource=
    localNode.replDestinations=ciArchiveResourceGroup
    localNode.id=1

    cb.serverSocketNumber=
    cb.implementation.grid=irodsGridFacade
    cb.implementation.distributedConversion=irodsDistributedConversionAdapter
    cb.implementation.repository=fedoraRepositoryFacade
    cb.bin.python=/ci/python/python
 
## Pres - Nur Presentation Repository

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/system-modi2.jpg)

Dieser Modus sollte bei der Installation von Knoten verwendet werden, die lediglich das PresentationRepository hosten. Auf solch einem Knoten laufen die Komponenten ContentBroker, iRODS, Fedora und Elasticsearch. Es werden keine Einlieferungen von Usern an solch einem Knoten angenommen, sondern lediglich PIPs, die auf anderen Knoten erstellt wurden.

##### beans.xml

Die ausgelieferte beans.xml enhält in diesem Fall folgende Imports:

    <import resource="classpath*:META-INF/beans-infrastructure.common.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.fedora.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.irods.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.irodsdistributedconversionadapter.xml"/>

    <import resource="classpath*:META-INF/beans-workflow.presentation.xml"/>
    <import resource="classpath*:META-INF/beans-workflow.other.xml"/>

Es werden die Komponenten IrodsDistributedConversionAdapter und Fedora3RepositoryFacade eingebunden.
Nur Präsentationsworkflows werden unterstützt. D.h. die Ingest und Retrieval-Workflows werden nicht unterstützt.

##### config.properties

Die oben gezeigte Beankonfiguration macht folgende Einträge in der config.properties nötig.

    localNode.id=
    localNode.workAreaRootPath=
    
    fedora.url=
    fedora.user=
    fedora.password=

    irods.user=
    irods.password=
    irods.server=
    irods.zone=
    irods.default_resc=
    irods.pam=
    irods.keyStorePassword=
    irods.keyStorePath=
    irods.trustStorePath=

    elasticsearch.index=portal_ci
    elasticsearch.hosts=localhost
    elasticsearch.cluster=cluster_ci

    cb.serverSocketNumber=
    cb.implementation.distributedConversion=irodsDistributedConversionAdapter
    cb.implementation.repository=fedoraRepositoryFacade
    cb.bin.python=/ci/python/python

## Full - Knoten und Presentation Repository

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/system-modi3.jpg)

Dieser Modus erlaubt es, den Knoten sowohl als normalen Einlieferungsknoten fungieren zu lassen, der SIPs annimmt, AIPs generiert und diese mit anderen Knoten austauscht. Gleichzeitig aber kann er sowohl seine eigenen PIPs als auch die PIPs anderer Knoten zur Präsentation aufbereiten und vorhalten.

##### beans.xml

Die ausgelieferte beans.xml enhält in diesem Fall folgende Imports:

    <import resource="classpath*:META-INF/beans-infrastructure.common.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.identifier.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.fedora.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.irods.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.irodsgridfacade.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.irodsdistributedconversionadapter.xml"/>

    <import resource="classpath*:META-INF/beans-workflow.presentation.xml"/>
    <import resource="classpath*:META-INF/beans-workflow.ingest.xml"/> 
    <import resource="classpath*:META-INF/beans-workflow.retrieval.xml"/>
    <import resource="classpath*:META-INF/beans-workflow.pipgen.xml"/>
    <import resource="classpath*:META-INF/beans-workflow.other.xml"/>

Es werden sowohl Präsentationsworkflows als auch normale Paketverarbeitungsworkflows unterstützt. Die Komponenten iRODSDistributedConversionAdapter, iRODSGridFacade, sowie Fedora3RepositoryFacade sind eingebunden.

##### config.properties

Solch eine Konfiguration erfordert die folgenden Parameter.

    fedora.url=
    fedora.user=
    fedora.password=

    irods.user=rods
    irods.password=
    irods.server=
    irods.zone=
    irods.default_resc=
    irods.pam=
    irods.keyStorePassword=
    irods.keyStorePath=
    irods.trustStorePath=

    localNode.userAreaRootPath=
    localNode.ingestAreaRootPath=
    localNode.workAreaRootPath=
    localNode.gridCacheAreaRootPath=
    localNode.workingResource=
    localNode.replDestinations=ciArchiveResourceGroup
    localNode.id=1

    elasticsearch.index=
    elasticsearch.hosts=
    elasticsearch.cluster=

    cb.serverSocketNumber=
    cb.implementation.grid=irodsGridFacade
    cb.implementation.distributedConversion=irodsDistributedConversionAdapter
    cb.implementation.repository=fedoraRepositoryFacade
    cb.bin.python=/ci/python/python
