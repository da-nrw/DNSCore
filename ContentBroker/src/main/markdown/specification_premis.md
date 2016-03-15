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

    ```xml
    <rights>
        <rightsStatement>
            <rightsStatementIdentifier>
                <rightsStatementIdentifierType>rightsid</rightsStatementIdentifierType>
                <rightsStatementIdentifierValue></rightsStatementIdentifierValue>
            </rightsStatementIdentifier>
            <rightsBasis>license</rightsBasis>
    ```
  * rightsGranted-Block je identischer Aufbau für PUBLICATION_PUBLIC | PUBLICATION_INSTITUTION | MIGRATION

    ```xml
            <rightsGranted>
                <act>PUBLICATION_PUBLIC</act>
                <restriction>see rightsExtension</restriction>
                <termOfGrant>
                    <startDate>2016-03-14T00:00:00.000+01:00</startDate>
                </termOfGrant>
            </rightsGranted>
    ```

    ```xml
        </rightsStatement>
    ```
* Ein Rights-Extention Element für genauere Spezifikation der Publikationsrechte

    ```xml
        <rightsExtension>
            <rightsGranted xmlns="http://www.danrw.de/contract/v1" xmlns:xsi="http://www.danrw.de/contract/v1 http://www.danrw.de/schemas/contract/v1/danrw-contract-1.xsd">
    ```
  * Migrationsbedingung: NONE | NOTIFY | CONFIRM 

    ```xml
                <migrationRight>
                    <condition>NONE</condition>
                </migrationRight>
    ```
  * DDB-Harvesting nicht erlauben (falls das Element fehlt, gilt es als erlaubt)

    ```xml
                <DDBexclusion/>
    ```
  * publicationRight-Block je für PUBLIC | INSTITUTION

    ```xml 
                <publicationRight>
                    <audience>PUBLIC</audience>
                    <startDate>2016-03-10T00:00:00.000+01:00</startDate>
    ```
  * Begrenzung der Publikation durch Sperrgesetz: EPFLICHT, URHG_DE, PUBLICDOMAIN_DE

    ```xml
                    <lawID>URHG_DE</lawID>
    ```
  * Block für Vorschaurestriktionen für die Öffentlichkeit

    ```xml
                    <restrictions>
    ```
    * Audio-Restriktionen

     ```xml
                        <restrictAudio>
                            <duration>60</duration>
                        </restrictAudio>
     ```
    * Text-Restriktionen

     ```xml
                        <restrictText>
                            <certainPages>1 2 3 4 5 6 7 8 9 10</certainPages>
                        </restrictText>
     ```
    * Video-Restriktionen

     ```xml
                        <restrictVideo>
                            <height>720</height>
                            <duration>120</duration>
                        </restrictVideo>
     ```
    * Bild-Restriktionen

     ```xml
                        <restrictImage>
                            <width>25%</width>
                            <height>25%</height>
                            <watermark>
                                <watermarkString>WasserzeichenText</watermarkString>
                                <pointSize>20</pointSize>
                                <position>south</position>
                                <opacity>25</opacity>
                            </watermark>
                        </restrictImage>
     ```
    ```xml
                    </restrictions>                 
    ```
  
    ```xml
                </publicationRight>            
    ```
    ```xml
            </rightsGranted>
        </rightsExtension>
    ```
   ```xml
    </rights>
   ```


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
