# Delta Retrieval

Wenn ein oder mehr Delta SIPs zu einem Objekt hinzugeführt wurden, besteht zum "normalen" 
Retrieval zusätzlich die Möglichkeit eines Delta-Retrieval.

## Manuelles Delta Retrieval

### Voraussetzungen

* Der Contractor hat einen Rechner mit Internetverbindung.
* Der Contractor ist mit seinen Zugangsdaten in der DAWeb Oberfläche eingeloggt.
* Der Contractor hat zu einem früheren Zeitpunkt ein SIP eingespielt. Er hat den Identifier notiert.

### Schritte

1\. In der DAWeb Maske "Eingelieferte Objekte (AIP)" gibt es die Möglichkeit, die aufgelisteten Objekte per Filter einzugrenzen.

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/retrieval1.png)

2\. Das so ausgewählte Objekt wird dann gelistet. Per Klick auf die URN öffnet sich 
die "Objektdetailansicht" für das jeweilige Objekt.

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/retrievaldelta.png)

In der Objektdetailansicht sind dann die einzelnen AIPs aufgelistet, die zu einem jeweiligen Objekt erstellt wurden.
Zu jedem eingelieferten SIP für das Objekt gibt es dort einen Eintrag für das jeweilige AIP. Das jeweilige AIP enthält 
sowohl den Original Inhalt des SIP als auch eventuell migrierte Versionen der enthaltenen Dateien. 

Ein oder mehrere AIPs können per Häkchen für das Retrieval markiert werden. Per Klick auf die Schaltfläche 
"Versioniertes Retrieval starten" wird das Retrieval angestoßen.

Wie auch beim "normalen" Retrieval informiert das System anschließend den Contractor per Mail, dass das DIP / die DIPs 
erstellt wurden und nun zum Download bereit sind.

* TODO per Entnahme
* TODO per FTP
* 

## Links

* AIP Spezifikation

