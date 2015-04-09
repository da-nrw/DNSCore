# Leistungsmerkmal: Statusabfrage

Drittsysteme sollen die Möglichkeit haben, Informationen zu Objekten abzufragen. Es handelt sich primär um eine technische Schnittstelle. Die Antworten des Systems erfolgen als maschinenlesbarer JSON Code.

# Szenario AT-ST-1: Statusabfrage eines fehlerfrei archivierten Pakets mit Originalname

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

## Szenario AT-ST-2: Statusabfrage eines fehlerfrei archivierten Pakets mit URN

#### Vorbedingungen:

wie vor

#### Durchführung:

wie vor, nun aber mit der URL&nbsp;

* [https://|https://**]**<QSERVERNAME>**/daweb3/status/index?urn=<URN> wird mit dem für*&nbsp;*ATUseCaseIngest1.tgz vergebenen&nbsp;*{*}URN abgefragt*

#### *Akzeptanzkriterien:*

wie vor

## Szenario AT-ST-3: Statusabfrage eines fehlerfrei archivierten Pakets mit Identifier

#### Vorbedingungen:

wie vor

#### Durchführung:

wie vor, nun aber mit der URL

* [https://|https://**]**<QSERVERNAME>**/daweb3/status/index?identifier=<IDENTIFIER> wird mit dem für*&nbsp;*ATUseCaseIngest1.tgz vergebenen&nbsp;*{*}Identifier abgefragt*

#### Akzeptanzkriterien:

wie vor

## Szenario AT-ST-4: Abfrage der URN eines fehlerfrei archivierten Pakets mittels OriginalNamen

#### Vorbedingungen:

wie vor

#### Durchführung:

wie vor, nun aber mit der URL

* Die URL 
** [https://|https://**]**<QSERVERNAME>**/daweb3/status/index?origName=<DER ORIGNAME> wird mit dem für*&nbsp;*ATUseCaseIngest1.tgz vergebenen&nbsp;*{*}Originalnamen abgefragt*

#### Akzeptanzkriterien:

Die JSON Response enthält den korrekten Wert für URN

## Szenario AT-ST-5: Statusabfrage eines Pakets im Fehlerstatus

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
