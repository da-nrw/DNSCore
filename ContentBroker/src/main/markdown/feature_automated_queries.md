# Leistungsmerkmal: Automatisierte Abfragen (Status & Retrieval)

Für Informationen zu der Verabreitung von Paketen steht im Normalfall dem Anwender die Webschnittstelle DA-WEB zur Verfügung. 

Da es für massenhafte Abfragen (druch Drittsysteme, durch den Anwender zur Überwachung einer Charge) auch möglich sein soll generisch Abfragen an die DNS zu stellen, stehen zwei Wege zur Verfügung: 

Es gibt eine technische Webschnittstelle, die Anfragen im JSON Format via HTTP verarbeiten kann. Die Antworten des Systems erfolgen als maschinenlesbarer JSON Code. Dieses Teilfeature eignet sich für Drittsysteme und arbeiten durch technische Anmeldung am System. Direkter Internetzugriff auf DNS und dem Drittsystem sind erforderlich.

Parallel dazu gibt es die Möglichkeit, Abfragen mittels einer CSV Datei durchzuführen.

Beide technischen Abfragen arbeiten auf der Statusabfrage (ist mein Objekt fertig archiviert? In welchem Arbeitsschritt ist mein SIP jetzt?) UND auf dem Leistungsmerkmal Retrieval. Es ist also möglich, via dieser Abfragen auch Retrievalanfragen zu erstellen. 

Im folgenden sind die Statusabfrageszenarien als AT-ST-JSON-1 bis AT-ST-JSON-5 gelistet, bzw. AT-R-JSON-1 für das Retrieval. 

Die Statusabfrageb mittels CSV Datei haben die Bezeichnung AT-ST-CSV-1 bis AT-ST-CSV-5, bzw. AT-R-CSV-1 für das Retrieval. 

## Szenario AT-ST-CSV-1: Statusabfrage mittels vorbereiteter CSV Datei eines fehlerfrei archivierten Pakets

##### Vorbedingungen:

Eine semikolongetrennte Datei mit den Spalten:

    identifier;origName;statuscode;erfolg;bemerkung

Zeichensatz ist CP1252 (Windows-Standard). Es wird angenommen, dass die Datei mittels EXCEL erstellt wurde. 

#### Testpaket(e):

#### Vorbedingungen:

#### Durchführung:

#### Akzeptanzkriterien:

#### Status und offene Punkte


## Szenario AT-ST-JSON-1: Statusabfrage eines fehlerfrei archivierten Pakets mit Originalname mittels JSON

##### Vorbedingungen:

* Der Nutzer hat einen Account und ist unter der Rolle "Vertragspartner" eingeloggt in der DA-WEB.
* Der User hat mindestens ein beliebiges Paket eingespielt, welches fertig archiviert ist.
* Dem User liegen URN, Identifier und originalName vor.
* Der User hat die Möglichkeit die Links vorher in richtiger Art und Weise zu konstruieren.

#### Durchführung:

1. Das Paket ATUseCaseIngest1.tgz wird eingeliefert
1. *Die URL&nbsp;**[https://|https://**]**<QSERVERNAME>**/daweb3/status/index?origName=<DER ORIGNAME> wird mit dem für* *ATUseCaseIngest1.tgz vergebenen&nbsp;*{*}Originalnamen abgefragt*
1. Es kommt einmalig eine neue Abfrage des Benutzernamens, Passwort des Contractors.

#### Akzeptanzkriterien:

* Im IE muss ggfs. "Datei öffnen mit .. Texteditor" angegeben werden, um die JSON Antwort zu sehen.
* Das fertig archivierte Objekt gibt den Status "archived" an.

```json
https://da-nrw-q.lvr.de/daweb3/status/index?origName=EAD_Testdatensatz_2015-03-16
{"result":[
{"type":"Object","status":"archived","urn":"urn:nbn:de:danrw-2-20150316406969","contractor":"LVR-Test","origName":"EAD_Testdatensatz_2015-03-16","identifier":"2-20150316406969","packages":\["1"\]}
]}
```

## Szenario AT-ST-JSON-2: Statusabfrage eines fehlerfrei archivierten Pakets mit URN mit JSON

#### Vorbedingungen:

wie vor

#### Durchführung:

wie vor, nun aber mit der URL&nbsp;

* [https://|https://**]**<QSERVERNAME>**/daweb3/status/index?urn=<URN> wird mit dem für*&nbsp;*ATUseCaseIngest1.tgz vergebenen&nbsp;*{*}URN abgefragt*

#### *Akzeptanzkriterien:*

wie vor

## Szenario AT-ST-JSON-3: Statusabfrage eines fehlerfrei archivierten Pakets mit Identifier mit JSON

#### Vorbedingungen:

wie vor

#### Durchführung:

wie vor, nun aber mit der URL

* [https://|https://**]**<QSERVERNAME>**/daweb3/status/index?identifier=<IDENTIFIER> wird mit dem für*&nbsp;*ATUseCaseIngest1.tgz vergebenen&nbsp;*{*}Identifier abgefragt*

#### Akzeptanzkriterien:

wie vor

## Szenario AT-ST-JSON-4: Abfrage der URN eines fehlerfrei archivierten Pakets mittels OriginalNamen mit JSON

#### Vorbedingungen:

wie vor

#### Durchführung:

wie vor, nun aber mit der URL

* Die URL 
** [https://|https://**]**<QSERVERNAME>**/daweb3/status/index?origName=<DER ORIGNAME> wird mit dem für*&nbsp;*ATUseCaseIngest1.tgz vergebenen&nbsp;*{*}Originalnamen abgefragt*

#### Akzeptanzkriterien:

Die JSON Response enthält den korrekten Wert für URN

## Szenario AT-ST-JSON-5: Statusabfrage eines Pakets im Fehlerstatus mit JSON

#### Kontext:

wie vor

#### Vorbedingungen:

wie vor

#### Durchführung:

1. Eine fehlerhaftes Paket wird eingeliefert (TODO welches)
1. *Die URL&nbsp;**[https://|https://**]**<QSERVERNAME>**/daweb3/status/index?origName=<DER ORIGNAME> wird mit dem für*&nbsp;das fehlerhafte Paket*&nbsp;vergebenen&nbsp;*{*}Originalnamen abgefragt\*

#### Akzeptanzkriterien:

Die Response bezeichnet das Objekt als "transient" und den Queue Entry als "package in progress error", 
das Object als "Object in transient state"


```json
{"result":[{"type":"QueueEntry","urn":null,"contractor":"LVR-Test","origName":"AT-V-001_ATDuplicateDocumentName_2015-03-16","identifier":"2-20150316407197","status":"package in progress error : (114)"},
{"type":"Object","status":"Object is in transient state","urn":null,"contractor":"LVR-Test","origName":"AT-V-001_ATDuplicateDocumentName_2015-03-16","identifier":"2-20150316407197","packages":["1"]}]}
```


## Szenario: AT-R-JSON-1

Automatisierte Drittsysteme haben die Möglichkeit, Retrievalanfragen auch mittels JSON Request zu erstellen. 

#### Testpaket(e):

#### Vorbedingungen:

#### Durchführung:

#### Akzeptanzkriterien:

#### Status und offene Punkte:

## Szenario: AT-R-CSV-1

Der Anwender hat die Möglichkeit, Retrievalanfragen mittels einer vorbereiteten CSV Datei mit den Spaltenköpfen

    identifier;origName;statuscode;erfolg;bemerkung

Im Zeichensatz CP1252 (Windows Standard), semikolongetrennt zu erstellen. 

#### Testpaket(e):

#### Vorbedingungen:

#### Durchführung:

#### Akzeptanzkriterien:

#### Status und offene Punkte:
```
