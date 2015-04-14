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
6. Der Vertragspartner wartet auf den Einlieferungsbeleg (Eingang per Email), welcher über den Status der Einlieferung informiert.

### Der Email-Report

#### Erfolgreiche Einlieferung

Der Einlieferungsbeleg für ein erfolgreich eingeliefertes Paket sieht aus wie in folgendem Beispiel:

```
Betreff: [DA-NRW] Einlieferungsbeleg für 2-20150409419938

Inhalt:

Ihr eingeliefertes Paket mit dem Namen "FT-EAD-001_Diesdas_1Ebene_2015-04-08" 
wurde erfolgreich im [DA-NRW] archiviert.

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

#### Entscheidung notwendig



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
