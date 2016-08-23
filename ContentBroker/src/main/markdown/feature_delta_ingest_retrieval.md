# Leistungsmerkmal: Delta Ingest und Retrieval

Dieser Test fokussiert die für den User offensichtlichsten Aspekte des Delta-Mechanismus. Zum einen bezieht sich das auf die Erkennung eines Paketes als zu einem Objekt zugehörig (z. B. durch die Namenswahl per&nbsp;*OriginalName*) und zum anderen auf die Regeln der Erstellung des DIPs (Stichwort{*}Oberflächenansicht*).&nbsp;


#### Kontext:

* [SIP-Spezifikation](specification_sip.de.md)
* [DIP-Spezifikation](specification_dip.md)
* [Delta](the_delta_feature.md)


## Szenario: AT-DIR-1 Ingest und Retrieval

#### Testpakete:

* ATUseCaseIngestDelta1.tgz
```
  Inhalt:
  data/CCITT_1.TIF
  data/CCITT_1_UNCOMPRESSED.TIF
  data/CCITT_2.TIF
```

* ATUseCaseIngestDelta2.tgz
```
  Inhalt:
  data/CCITT_2.TIF
  data/CCITT_3.TIF
```

#### Vorbedingungen:

* User ist unter der Rolle "Contractor" angemeldet/eingeloggt in der "DAWeb"

#### Durchführung:

1. Das erste Paket wird in den User-Eingangsordner unter einem *für das Szenario* *jeweils eindeutigen Namen* abgelegt.
1. Die Paketverarbeitung wird gestartet über die Maske "Verarbeitung für abgelieferte SIP starten".
1. Warten auf die Email mit dem Einlieferungsbeleg.
1. Sobald der Einlieferungsbeleg eingetroffen ist, wird das zweite Paket eingeliefert, und zwar mit dem selben OriginalName (für das entsprechende Szenario) wie das erste Paket eingeliefert wurde.
1. Erneut warten auf den Einlieferungsbeleg.
1. In der Maske "Eingelieferte Objekte" das Objekt per technischem Identifier recherchieren.
1. Das Retrieval des Paketes per Button "Anfordern" für das entsprechende Paket anstoßen.
1. Der User entnimmt das Paket dem User-Entnahmeordner.
1. Der User entpackt das Paket und überprüft den Inhalt.

#### Akzeptanzkriterien:

* Der Einlieferungsbeleg enthält einen klaren Hinweis darauf, dass es sich um eine Delta-Lieferung zu einem bekannten Objekt handelt.
* Der in der Mail erwähnte technische Identifier entspricht dem der ersten Einlieferung.
* Das über die Ansicht "eingelieferte Objekte" zurückgeholte Paket muss nach dem Entpacken&nbsp;*exakt*&nbsp;die folgende Inhalte aufweisen:

``` 
  Identifier/data/CCITT_1.TIF
  Identifier/data/CCITT_1_UNCOMPRESSED.TIF
  Identifier/data/CCITT_2.TIF
  Identifier/data/CCITT_3.TIF
```

## Szenario: AT-DIR-2 Versioniertes Retrieval. Alle Packages


#### Testpakete:

* ATUseCaseIngestDelta1.tgz
```
  Inhalt:
  data/CCITT_1.TIF
  data/CCITT_1_UNCOMPRESSED.TIF
  data/CCITT_2.TIF
```

* ATUseCaseIngestDelta2.tgz
```
  Inhalt:
  data/CCITT_2.TIF
  data/CCITT_3.TIF
```


#### Vorbedingungen:

* User ist unter der Rolle "Contractor" angemeldet/eingeloggt in der "DAWeb"

#### Durchführung:

1. Das erste Paket wird in den User-Eingangsordner unter einem *für das Szenario* *jeweils eindeutigen Namen* abgelegt.
1. Die Paketverarbeitung wird gestartet über die Maske "Verarbeitung für abgelieferte SIP starten".
1. Warten auf die Email mit dem Einlieferungsbeleg.
1. Sobald der Einlieferungsbeleg eingetroffen ist, wird das zweite Paket eingeliefert, und zwar mit dem selben OriginalName (für das entsprechende Szenario) wie das erste Paket eingeliefert wurde.
1. Erneut warten auf den Einlieferungsbeleg.
1. In der Maske "Eingelieferte Objekte" das Objekt per technischem Identifier recherchieren.
1. Der User geht in die Objektdetailansicht und wählt das versionierte Retrieval aus: erstes Paket | zweites Paket | alle Pakete
1. Der User entnimmt das Paket dem User-Entnahmeordner und entpackt es.
1. Er entnimmt und entpackt das Package.
1. Er überprüft die Inhalte.

#### Akzeptanzkriterien:

* Das über das versionierte Retrieval zurückgeholte Paket weist unterhalb des Ordners identifier/data, abgesehen von den premis.xml-Dateien,&nbsp;*exakt*&nbsp;folgende Inhalte auf



|  | erstes Paket |  zweites Paket | alle Pakete |
|-----------|---------------------|-----------------|--------------|
| repname1+a/CCITT_1.TIF | vorhanden ||  vorhanden |
| repname1+a/CCITT_1_UNCOMPRESSED.TIF | vorhanden ||  vorhanden |
| repname1+a/CCITT_2.TIF | vorhanden || vorhanden |
| repname1+b/CCITT_1.TIF | vorhanden || vorhanden |
| repname1+b/CCITT_2.TIF | vorhanden || vorhanden |
| repname2+a/CCITT_2.TIF || vorhanden |  vorhanden |
| repname2+a/CCITT_3.TIF || vorhanden | vorhanden |
| repname2+b/CCITT_2.TIF || vorhanden | vorhanden |
| repname2+b/CCITT_3.TIF || vorhanden | vorhanden |



## Status und offene Punkte:


####

* Automatisierte Aspekte:
** Die PREMIS-Datei wird automatisch geprüft in&nbsp;ATUseCaseIngestDeltaPREMISCheck#testProperPREMISCreation
* Nicht automatisierte Aspekte:
** &nbsp;Paketstruktur des DIP

## Senario: AT-DIR-3 Erkennen eines Deltapaketes anhand gleicher URN  

Ein sogenanntes Delta kann sowohl durch gleichen Einlieferungsnamen als auch durch identische URN erzeugt werden. Dieser Test bezieht sich auf das Erzeugen eines Deltas durch gleiche URN in den Metadaten.  
&nbsp;

#### Testpakete:

* ATDeltaOnURN_1.tgz
* ATDeltaOnURN_2.tgz
* ATDeltaOnURN_3.tgz

#### Vorbedingungen:

* User ist unter der Rolle "Contractor" angemeldet/eingeloggt in der "DAWeb"
* Der angemeldete User muss delta_on_urn auf true gesetzt haben
 
#### Durchführung:

1. Das Paket ATDeltaOnURN_1.tgz wird in den User-Eingangsordner abgelegt.
1. Die Paketverarbeitung wird gestartet über die Maske "Verarbeitung für abgelieferte SIP starten".
1. Warten auf die Email mit dem Einlieferungsbeleg.
1. Sobald der Einlieferungsbeleg eingetroffen ist, wird das zweite Paket ATDeltaOnURN_2.tgz eingeliefert, und zwar unter einem anderen Namen als das erste Paket.
1. Erneut warten auf den Einlieferungsbeleg.
1. Sodann kann das dritte Paket ATDeltaOnURN_3.tgz eingeliefert werden, und zwar unter einem anderen Namen als das erste und das zweite Paket.
1. Erneut warten auf den Einlieferungsbeleg.

#### Akzeptanzkriterien:

* Der zweite und der dritte Einlieferungsbeleg tragen dieselbe Identifikationsnummer wie der erste Beleg.
* Im Einlieferungsordner befindet sich nur ein Paket mit dem Namen des ersten Pakets.
