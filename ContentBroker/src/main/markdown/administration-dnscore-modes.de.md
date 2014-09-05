# DNSCore - Funktionsmodi

DNSCore kann grundsätzlich in drei verschiedenen Funktionsumfängen installiert werden. Der Installer (./install.sh) bietet diese drei Modi beid der Installation zur Auswahl an.

* (N)ode 
* (F)ull
* (P)res

### Node - Knotenmodus

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/system-modi1.jpg)

In diesem Modus hostet ein Knoten (Node) die beiden Komponenten DAWeb und ContentBroker. AIPs für die Langzeitarchivierung werden generiert und im Grid verteilt. PIPs für die Präsentation werden generiert und an den dafür vorgesehenen Präsentationsknoten (PresNode) gesendet.

### Pres - Nur Presentation Repository

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/system-modi2.jpg)

Dieser Modus sollte bei der Installation von Knoten verwendet werden, die lediglich das PresentationRepository hosten. Auf solch einem Knoten laufen die Komponenten ContentBroker, Fedora und Elasticsearch. Es werden keine Einlieferungen von Usern an solch einem Knoten angenommen, sondern lediglich PIPs, die auf anderen Knoten erstellt wurden.

### Full - Knoten und Presentation Repository

# Organisation der Beans
