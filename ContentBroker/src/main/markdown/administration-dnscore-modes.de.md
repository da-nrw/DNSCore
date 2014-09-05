# DNSCore - Funktionsmodi

DNSCore kann grundsätzlich in drei verschiedenen Funktionsumfängen installiert werden. Der Installer (./install.sh) bietet diese drei Modi beid der Installation zur Auswahl an.

* (N)ode 
* (F)ull
* (P)res

## Node - Knotenmodus

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/system-modi1.jpg)

In diesem Modus hostet ein Knoten (Node) die beiden Komponenten DAWeb und ContentBroker. AIPs für die Langzeitarchivierung werden generiert und im Grid verteilt. PIPs für die Präsentation werden generiert und an den dafür vorgesehenen Präsentationsknoten (PresNode) gesendet.

##### beans.xml

Die ausgelieferte beans.xml enhält in diesem Fall folgende Imports:

    <import resource="classpath*:META-INF/beans-infrastructure.common.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.identifier.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.irods.xml"/>

Der Import der beans-infrastructure.irods.xml bedeutet, dass neben Basiskomponenten auch die iRODS-Komponente eingebunden ist. Deshalb muss der Block irods.* in der config.properties korrekt ausgefüllt sein.

Weiterhin importiert die beans.xml folgende Workflows:

    <import resource="classpath*:META-INF/beans-workflow.ingest.xml"/>
    <import resource="classpath*:META-INF/beans-workflow.retrieval.xml"/>
    <import resource="classpath*:META-INF/beans-workflow.pipgen.xml"/>
    <import resource="classpath*:META-INF/beans-workflow.other.xml"/>

Dies bedeutet, dass Der Knoten die normalen Knotenworkflows kennt und durchführen kann, jedoch keine Workflows zur PIP-Aufbereitung.

## Pres - Nur Presentation Repository

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/system-modi2.jpg)

Dieser Modus sollte bei der Installation von Knoten verwendet werden, die lediglich das PresentationRepository hosten. Auf solch einem Knoten laufen die Komponenten ContentBroker, Fedora und Elasticsearch. Es werden keine Einlieferungen von Usern an solch einem Knoten angenommen, sondern lediglich PIPs, die auf anderen Knoten erstellt wurden.

##### beans.xml

Die ausgelieferte beans.xml enhält in diesem Fall folgende Imports:

    <import resource="classpath*:META-INF/beans-infrastructure.common.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.fedora.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.irods.xml"/>

Es werden die Kompontenten irods und fedora importiert. Da sich hinter fedora Fedora und Elasticsearch verbergen, heisst dass, dass insgesamt die Blöcke irods.*, fedora.* als auch elasticsearch.* ausgefüllt sein müssen.

Weiterhin importiert die beans.xml folgende Workflows:

    <import resource="classpath*:META-INF/beans-workflow.presentation.xml"/>
    <import resource="classpath*:META-INF/beans-workflow.other.xml"/>
    
Es werden lediglich Präsentationsworkflows unterstützt. D.h. die Ingest und Retrieval-Workflows werden nicht unterstützt.

## Full - Knoten und Presentation Repository

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/system-modi3.jpg)

Dieser Modus erlaubt es, den Knoten sowohl als normalen Einlieferungsknoten fungieren zu lassen, der SIPs annimmt, AIPs generiert und diese mit anderen Knoten austauscht. Gleichzeitig aber kann er sowohl seine eigenen PIPs als auch die PIPs anderer Knoten zur Präsentation aufbereiten und vorhalten.

##### beans.xml

Die ausgelieferte beans.xml enhält in diesem Fall folgende Imports:

    <import resource="classpath*:META-INF/beans-infrastructure.common.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.identifier.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.fedora.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.irods.xml"/>

Es werden die Kompontenten irods und fedora importiert. Da sich hinter fedora Fedora und Elasticsearch verbergen, heisst dass, dass insgesamt die Blöcke irods.*, fedora.* als auch elasticsearch.* ausgefüllt sein müssen.

Weiterhin importiert die beans.xml folgende Workflows:

    <import resource="classpath*:META-INF/beans-workflow.presentation.xml"/>
    <import resource="classpath*:META-INF/beans-workflow.ingest.xml"/> 
    <import resource="classpath*:META-INF/beans-workflow.retrieval.xml"/>
    <import resource="classpath*:META-INF/beans-workflow.pipgen.xml"/>
    <import resource="classpath*:META-INF/beans-workflow.other.xml"/>

Es werden sowohl Präsentationsworkflows als auch normale Paketverarbeitungsworkflows unterstützt.
