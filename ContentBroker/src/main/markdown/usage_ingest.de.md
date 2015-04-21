# Ingest 

Ingest beschreibt den Prozess der Übergabe eines SIP an das System bzw. die Übernahme des SIP durch das System.

## Manueller Ingest

### Voraussetzungen

* Der Contractor hat einen Rechner mit Internetverbindung.
* Der Contractor ist mit seinen Zugangsdaten an der DAWeb Oberfläche eingeloggt. 
* Der Contractor ist per FileShare Client (z.B. FileZilla) mit dem ihm zugeteilten Nutzungsbereich auf dem Server verbunden, der für die Datenübernahme an seinem Knoten vorgesehen ist. 
* Der Contractor besitzt auf seinem lokalen Rechner ein fertiges SIP. 

### Schritte

1. Der Contractor legt das SIP mit Hilfe des FileShare Client im Ordner "incoming" auf seinem Nutzungsbereich ab.
2\. Der Contractor wartet, bis die Übertragung abgeschlossen ist.
3\. Der Contractor öffnet die DAWeb-Maske "Startseite"->"Verarbeitung für abgelieferte SIP starten".

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/queue.png)

4\. In dieser Maske sollte nun das im "incoming" Ordner abgelegte SIP namentlich aufgeführt sein.
5\. Der Contractor markiert dieses Paket mit einem Häkchen für die Verarbeitung und betätigt den "Starten" Button am unteren Ende der Seite.
6\. Der Eintrag für das Paket verschwindet jetzt, was die gestartete Verarbeitung signalisiert.
7\. Der Contractor wartet auf den Einlieferungsbeleg (Eingang per Email), welcher über den Status der Einlieferung informiert.

### Verarbeitungsübersicht

In der DAWeb Maske "Verarbeitungsübersicht" kann der Contractor eine Übersicht über die derzeit in Verarbeitung befindlichen Pakete bekommen.

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/queue.png)

Diese Übersicht dient der Information. Über alle relevanten Ereignisse hinsichtlich der Verarbeitung eines SIP informiert das System den Nutzer in jedem Fall per Email Report.

### Der Email-Report

Der Email-Report informiert über relevante Ereignisse zum Ingest.

#### Erfolgreiche Einlieferung

Der Einlieferungsbeleg für ein erfolgreich eingeliefertes Paket sieht aus wie in folgendem Beispiel:

```
Betreff: [System] Einlieferungsbeleg für 2-20150409419938

Inhalt:

Ihr eingeliefertes Paket mit dem Namen "FT-EAD-001_Diesdas_1Ebene_2015-04-08" 
wurde erfolgreich im [System] archiviert.

Identifier: 2-20150409419938
URN: urn:nbn:de:danrw-2-20150409419938
```

Ein Einlieferungsbeleg in dieser Form signalisiert die Erfolgreiche Übernahme des SIP durch das System und die Umformung
des Paketes zu einem AIP und dessen geographische Verteilung. Das Paket kann untersteht nun der Kontrolle des Systemes hinsichtlich aller notwendiger Maßnahmen seines langfristigen Erhaltes.

Der Einlieferungsbeleg enthält folgende Informationen:

* **Technischer Identifier.** Der technische Identifier wird während des Ingest automatisch vom System vergeben. Er gilt für die Lebenszeit des Objektes als eindeutiger und dauerhafter Identifier. 
* **URN** Die URN wird entweder vom System vergeben oder vom Benutzer mitgeliefert (Siehe [URN-Vergabe](specification_sip.de.md#urn-vergabe)).
* **Originalname** Der Originalname (im Beispiel FT-EAD-001_Diesdas_1Ebene_2015-04-08) ergibt sich aus dem Dateinamen des jeweils ersten SIP zu einem Objekt, abzüglich der Dateiendung. Er dient als Schlüssel, um Delta-Einlieferungen zu einem Objekt vornehmen zu können (siehe [Delta-Ingest](usage_ingest_delta.de.md)). 

#### Fehler bei der Verarbeitung

Beim Ingest eines SIP wird eine Validierung der Nutzerdaten vorgenommen. Entsprechen die Daten nicht der gültigen
[SIP-Spezifikation](specification_sip.de.md), wird der Ingest abgebrochen und der Contractor per Email informiert.

```
Betreff: [System] Fehlerreport für 2-20150409419813

Inhalt:

Beim Ingest des Objektes mit dem Identifier 2-20150409419813 ist ein Fehler aufgetreten. 
Mehr als eine Metadatendatei wurde gefunden.
```

Die Email enthält neben dem eindeutigen Objektidentifier immer auch den Grund der Ablehnung. In dem Beispiel lautet der Grund "Mehr als eine Metadatendatei wurde gefunden." Solche, mit einer Mail quittierten, abgebrochenen Ingestvorgänge können nicht gerettet werden. Der Fehler muss in den Eingangsdaten behoben werden. 

Das Paket kann dann zu einem späteren Zeitpunkt unter dem gleichen Originalnamen wieder eingeliefert werden.**Wichtig** dabei ist, dass in jedem Fall abgewartet wird, bis das Objekt (oder das jeweils letzte Paket im Falle von Deltas) vom Administrator
gelöscht wird. Die Löschung wird quittiert durch eine Meldung an den Contractor:

```
Betreff: [System] Entfernung von SIP aus Workflow für 2-20150316407205

Inhalt:

Ihr abgegebenes SIP Paket dem Namen "AT-V-002_ATDuplicateMetadataFiles_2015-03-16" wurde aus der 
Verabreitungswarteschlange entfernt. Die Datei kann so nicht vom DNS verarbeitet werden. 
Korrigieren Sie ggfs. das Paket und bitte versuchen Sie eine erneute Ablieferung. 
Das Paket wurde nicht archiviert. 
```

#### Entscheidung notwendig

In bestimmten Fällen, abhängig von den im Contract des SIP festgelegten Einstellungen, wird der Ingest an festgelegter Stelle unterbrochen. Diese Unterbrechung wird durch eine Email signalisiert:

```
Betreff: [System] Entscheidung erforderlich für 2-20150409419837

Inhalt:

Bitte treffen Sie eine Entscheidung in der DAWeb-Maske "Entscheidungsübersicht"2-20150409419837
```

In der Entscheidungsansicht kann der Contractor dann zwischen verschiedenen Optionen wählen, woraufhin die Verarbeitung unter Berücksichtigung der gewählten Option fortgesetzt wird.

### Probleme bei der Einlieferung.

* Keine Zugangsdaten für die DAWeb Oberfläche. Der Vertragspartner sollte sich an den jeweils zuständigen Betreiber wenden. Die Zugangsdaten werden dem Vertragspartner durch den zuständigen Administrator übermittelt.
* Keine Zugangsdaten für den Nutzungsbereich. Der Vertragspartner sollte sich an den jeweils zuständigen Betreiber wenden. Die Zugangsdaten werden dem Vertragspartnern durch den zuständigen Administrator übermittelt.
* Ausbleiben des Email-Report. Sollte nach einem oder zwei Tagen der Email-Report ausbleiben, sollte sich der Vertragspartner an den jeweils zuständigen Betreiber wenden. Die Ursachen hierfür sind vom Betreiber zu klären.

### Weiterführende Links

* Erstellung von SIPs mit Hilfe des SIP-Builder
* [SIP-Spezifikation](specification_sip.de.md)





![](https://raw2.github.com/da-nrw/DNSCore/master/DAWeb/doc/ingest_1.png)


**Note** that users are able to monitor the package while beeing processed.
This is an advanced feature which is only optional and primarily targeted at 
administrators. It is described in this (TODO link) document.
