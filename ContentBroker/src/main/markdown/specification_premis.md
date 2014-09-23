# PREMIS Spezifikationen

### Inhalt einer vom SIP-Builder gebauten Premis-Datei

* Ein Object-Element für das gebaute Paket
  * objectIdentifierType: PACKAGE_NAME
  * objectIdentifierValue: \[DAN:Name des SIPs, d.h. Name der tgz-Datei ohne Dateiendung\]
* Ein Event-Element für die SIP-Erstellung
  * eventIdentifierType: SIP_CREATION_ID
  * eventIdentifierValue: Sip_Creation_\[DAN:Datum der Premis-Erstellung\]
  * EventType: SIP_CREATION
  * eventDateTime: \[DAN:Datum der Premis-Erstellung\]
  * Link auf den SIP-Builder-Agent
  * Link auf das Paket-Object
* Ein Agent-Element für den SIP-Builder, mit dem das SIP erstellt wurde
  * agentIdentifierType: APPLICATION_NAME
  * agentIdentifierValue: DA NRW SIP-Builder \[DAN:Version\]
  * agentType: APPLICATION
* Ein Rights-Element für die festgelegten Contract Rights

### Vom System erwartete Eigenschaften einer eingehenden PREMIS-Datei

* Ein Event der SIP-Erstellung
* Rechte

### Im Falle von Erstanlieferungen werden folgende PREMIS-Elemente angelegt:

###### einmalig ein PREMIS-Object

* Identifier-Typ URN angelegt.
* Entspricht einer Entität vom Typ model.Object

###### für das Package in jedem Fall ein PREMIS-Object

* Identifier vom Identifier-Typ PACKAGE_NAME angelegt.
* Der Package-Name entspricht dem Namen der am Ende des Ingestvorgangs erzeugten tar-Datei

###### für alle Files, die direkt im SIP mitangeliefert werden jeweils ein Premis-Object

* File-Object

###### für jedes konvertierte File jeweils ein PREMIS-Object

* siehe&nbsp;File-Object


###### für jedes konvertierte File jeweils ein PREMIS-Event

* siehe Konversionsevent

###### einmalig ein PREMIS-Event zur Repräsention des Ingest des jeweiligen Paketes

* siehe IngestEvent

###### für jeden an der Verarbeitung des Paketes beteiligten Knoten ein PREMIS-Agent

* Name des Knotens

###### je Paket eine SIP-Erstellungs-Event

* siehe SIPCreationEvent

###### für den Contractor ein PREMIS-Agent

* Contractor Short Name

### Im Falle von Deltas enthalten die PREMIS-Dateien folgende Elemente:


###### einmalig ein PREMIS-Object

* Identifier-Typ URN angelegt.
* Entspricht einer Entität vom Typ model.Object

###### für jedes Paket inklusive des neuesten Delta-Paketes ein PREMIS-Object

* Identifier vom Identifier-Typ PACKAGE_NAME angelegt.
* Der Package-Name entspricht dem Namen der am Ende des Ingestvorgangs erzeugten tar-Datei

###### für alle Files, die in allen (Delta)-SIPs mitangeliefert werden jeweils ein PREMIS-Object

* siehe&nbsp;PREMIS-Objekt, das ein File beschreibt

###### für jedes konvertierte File jeden Paketes jeweils ein PREMIS-Objekt

* siehe&nbsp;PREMIS-Objekt, das ein File beschreibt

###### für jedes konvertierte File jeden Paketes jeweils 1 PREMIS-Event&nbsp;

* siehe Konversionsevent

###### je Paket eine SIP-Erstellungs-Event

* siehe SIPCreationEvent

###### für jedes Paket ein PREMIS-Event

* siehe IngestEvent


###### für jeden an einer Konversion beteiligten Knoten ein PREMIS-Agent

* Name des Knotens

###### im Falle der Nutzung des SIP-Builders ein PREMIS-Agent

* Name bzw. Version des SIP-Builders

###### für den Contractor ein PREMIS-Agent

* Contractor Short Name

### Spezifikationen einzelner Elemente

##### FileObject

* Dateipfad relativ zum data-Verzeichnis
* CompositionLevel (momentan immer 0)
* MD5-Checksumme
* Dateigröße
* PRONOM-ID des Dateiformats
* JHOVE-Daten
* Ursprünglicher Dateiname (vor einer evtl. stattgefundenen Konversion)
* Name des Pakets, in dem die Datei enthalten ist
* Linking zu Package

##### KonversionsEvent

* Event-Typ CONVERT
* Identifier vom Typ TARGET_FILE_PATH
* Zeitpunkt der Konversion
* tatsächlicher Kommandozeilenaufruf
* kanonischer Name des Knotens, auf dem die Konversion stattgefunden hat
* Pfad (relativ zum data-Ordner) zur Datei in der a-Repräsentation, die konvertiert wurde
* Pfad (relativ zum data-Ordner) zur Datei in der b-Repräsentation, die das Ergebnis der Konversion ist

##### IngestEvent

* Event-Typ INGEST_EVENT
* Identifier vom Typ INGEST_ID
* Zeitpunkt des Ingests (= Zeitpunkt der Premis-Erstellung)
* Contractor Short Name des Contractors, der das Paket eingeliefert hat
* Name des Pakets (= Name der am Ende des Ingestvorgangs erzeugten tar-Datei)

##### SIPCreationEvent

* Event-Typ SIP_CREATION
* Identifier vom Typ SIP_CREATION_ID&nbsp;
* Zeitpunkt der SIP-Erstellung
* Version des Erstellungsprogrammes (z.B. SIP-Builder), mit dem das SIP erstellt wurde
* Name des Pakets (= Name der am Ende des Ingestvorgangs erzeugten tar-Datei)


Notizen:
Datenmodell Skizze zum besseren Verständnis hier
Zielstellung: Vollständige Objekthistorie nur aus Daten ableiten können. Das bedeutet auch, dass die Datenbank wiederhergestellt werden kann.
