# Ingest 

Ingest beschreibt den Prozess der Übergabe eines SIP an das System bzw. die Übernahme des SIP durch das System.

## Manueller Ingest

### Voraussetzungen

* Der Vertragspartner hat einen Rechner mit Internetverbindung.
* Der Vertragspartner ist mit seinen Zugangsdaten an der DAWeb Oberfläche eingeloggt. 
* Der Vertragspartner ist per FileShare Client (z.B. FileZilla) mit dem ihm zugeteilten Nutzungsbereich auf dem Server verbunden, der für die Datenübernahme an seinem Knoten vorgesehen ist. 
* Der Vertragspartner besitzt auf seinem lokalen Rechner ein fertiges SIP. 

### Schritte

1. Der Vertragspartner legt das SIP mit Hilfe des FileShare Client im Ordner "incoming" auf seinem Nutzungsbereich ab.
2. Der Vertragspartner wartet, bis die Übertragung abgeschlossen ist.
2. Der Vertragspartner öffnet die DAWeb-Maske "Startseite"->"Verarbeitung für abgelieferte SIP starten".
3. In dieser Maske sollte nun das im "incoming" Ordner abgelegte SIP namentlich aufgeführt sein.
4. Der Vertragspartner markiert dieses Paket mit einem Häkchen für die Verarbeitung und betätigt den "Starten" Button am unteren Ende der Seite.
5. Der Eintrag für das Paket verschwindet jetzt, was die gestartete Verarbeitung signalisiert.
6. Der Vertragspartner wartet auf den Email-Report, welcher über den Status der Einlieferung informiert.

### Der Email-Report


### Probleme bei der Einlieferung.

* Keine Zugangsdaten für die DAWeb Oberfläche. Der Vertragspartner sollte sich an den jeweils zuständigen Betreiber wenden. Die Zugangsdaten werden dem Vertragspartner durch den zuständigen Administrator übermittelt.
* Keine Zugangsdaten für den Nutzungsbereich. Der Vertragspartner sollte sich an den jeweils zuständigen Betreiber wenden. Die Zugangsdaten werden dem Vertragspartnern durch den zuständigen Administrator übermittelt.
* Ausbleiben des Email-Report. Sollte nach einem oder zwei Tagen der Email-Report ausbleiben, sollte sich der Vertragspartner an den jeweils zuständigen Betreiber wenden. Die Ursachen hierfür sind vom Betreiber zu klären.

### Weiterführende Links

* Erstellung von SIPs mit Hilfe des SIP-Builder
* SIP-Spezifikation





The packages are then transported via the transport protocol of choice to the 
[UserArea](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/processing_stages.md#userarea). While UserArea is the technical term for the incoming
storage space, contractors only have to think of it as their web share to the system.
The contents of their respective webshare can be seen by contractors directly when
they upload their content with a tool of choice (for example FileZilla for SFTP),
but also is presented by DAWeb, where the second step of manual ingest takes place.
Here users can mark packages as ready for ingest, which DAWeb then signals to the 
other components of the node, which then extract the package from the share for 
further processing.

##### Steps

![](https://raw2.github.com/da-nrw/DNSCore/master/DAWeb/doc/ingest_1.png)
<br><sub>Start the ingest after uploading</sub>

**Note** that users are able to monitor the package while beeing processed.
This is an advanced feature which is only optional and primarily targeted at 
administrators. It is described in this (TODO link) document.
