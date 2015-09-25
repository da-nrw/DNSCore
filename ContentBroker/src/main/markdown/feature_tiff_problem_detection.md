## Leistungsmerkmal: Umgang mit fehlerhaften Tags bei TIFF

Die momentane Version der DNS steuert TIFF mit fehlerhaften Tags im Rich-IPTC Bereich als fehlerhaft aus.

Bsp:

```
74134.tif TIFF 3474x2141 3474x2141+0+0 8-bit Grayscale DirectClass 7.094mb 
identify: 374134.tif: wrong data type 2 for "RichTIFFIPTC"; tag ignored. `TIFFReadDirectory' @ tiff.c/TIFFWarnings/546.
identify: 374134.tif: unknown field with tag 33434 (0x829a) encountered. `TIFFReadDirectory' @ tiff.c/TIFFWarnings/546.
identify: 374134.tif: unknown field with tag 33437 (0x829d) encountered. `TIFFReadDirectory' @ tiff.c/TIFFWarnings/546.
identify: 374134.tif: unknown field with tag 34852 (0x8824) encountered. `TIFFReadDirectory' @ tiff.c/TIFFWarnings/546.
identify: 374134.tif: unknown field with tag 36867 (0x9003) encountered. `TIFFReadDirectory' @ tiff.c/TIFFWarnings/546.
identify: 374134.tif: unknown field with tag 37395 (0x9213) encountered. `TIFFReadDirectory' @ tiff.c/TIFFWarnings/546.
identify: Tag 33434: Rational with zero denominator (num = 0). `374134.tif' @ tiff.c/TIFFErrors/336.
identify: Tag 33437: Rational with zero denominator (num = 0). `374134.tif' @ tiff.c/TIFFErrors/336.
```

#### Kontext:

## Hintergrund

gilt für alle Szenarien!

#### Testpaket(e):

```
(GitHub) NameDesTestPakets.tgz
  data/premis.xml
  data/ok.tif
  data/failure_in_tag.tif (enthält nicht standardkonforme Tiff-Tags)
```

#### Vorbedingung:

* Der User hat einen Account und kann sich unter der Rolle "Contractor" in der DA-WEB&nbsp;eingeloggen.

#### Durchführung:

1. Das Testpaket wird im Incoming Order abelegt und die Verarbeitung gestartet (Maske "Verarbeitung für abgelieferte SIP starten")
1. Warten auf Email-Report.

## Szenario AT-TIFFT-1 Problematische Pakete zurückweisen

Das System weist Pakete, die Dateien mit problematischen Tags enthalten, zurück.

#### Kontext:

* Name des automatisierten Akzeptanztests


#### Testpaket(e):

* Siehe Hintergrund

#### Vorbedingungen:

* Siehe Hintergrund.

#### Durchführung:

* Siehe Hintergrund.

#### Akzeptanzkriterien:

* Der Email Report enthält einen Hinweis, dass die Löschung des Objektes durchgeführt werden wird.
* Der Email Report enthält einen Hinweis über die Problematische Datei "failure_in_tag.tif".
* Das Objekt taucht nicht in der Ansicht "eingelieferte Objekte" als "archiviert" auf.



## Vorschlag: Szenario AT-TIFFT-2 Archivierung nach Rückfrage durchführen (nicht impl.)

Der Contractor will die Archivierung trotz des Hinweises in der Email auf die Probleme der zuk. Bestandserhaltung durchführen.

#### Kontext:

* Name des automatisierten Akzeptanztests

#### Testpaket(e):

* Siehe Hintergrund

#### Vorbedingungen:

* Siehe Hintergrund.

#### Durchführung:

1. Siehe Hintergrund.
1. Nach Erhalt des Email-Report: Einsichtnahme in Maske "Entscheidungsübersicht" der DA-Web.
1. In der Maske "Entscheidungsübersicht" bestätigt der Vertragspartner die Rückfrage mit *"Ja - Ingest fortführen"*.

#### Akzeptanzkriterien:

* Der Email Report enthält einen Hinweis, dass für das Objekt mit den&nbsp;﻿"Identifier" eine Entscheidung zu treffen ist.
* Der Email Report enthält einen Hinweis über die Problematische Datei "failure_in_tag.tif".
* Der Email Report enthält den Hinweis, dass der weitere Ingest des Paketes in die Langzeitarchivierung nicht empfohlen wird.
* Der Email Report enthält den Hinweis, dass der Ingest trotzdem fortgeführt werden kann, und dass diese Entscheidung für spätere Nachvollziehbarkeit gespeichert wird.
* Der Vertragspartner bekommt einen weiteren Email-Report, der den Ingest bestätigt.
* Das Objekt taucht unter eingelieferte Objekte auf als "archiviert" auf.&nbsp;
* &nbsp;

## Vorschlag: Szenario AT-TIFFT-3 Archivierung nach Rückfrage nicht durchführen (nicht impl.)

* Der Contractor &nbsp;will die Archivierung auf Grund des Hinweises nicht durchführen.

#### Kontext:

* Name des automatisierten Akzeptanztests

#### Testpaket(e):

* Siehe Hintergrund.

#### Vorbedingungen:

* Siehe Hintergrund.

#### Durchführung:

1. Siehe Hintergrund.
1. Nach Erhalt des Email-Report: Einsichtnahme in Maske "Entscheidungsübersicht" der DA-Web.
1. In der Maske "Entscheidungsübersicht" bestätigt der Vertragspartner die Rückfrage mit *"Nein - Ingest abbrechen"*.

#### Akzeptanzkriterien:

* Der Email Report enthält einen Hinweis,&nbsp;dass für das Objekt mit den&nbsp;﻿"Identifier" eine Entscheidung zu treffen ist.
* Der Email Report enthält einen Hinweis über die Problematische Datei "failure_in_tag.tif".
* Der Email Report enthält den Hinweis, dass der weitere Ingest des Paketes in die Langzeitarchivierung nicht empfohlen wird.
* Der Email Report enthält den Hinweis, dass der Ingest trotzdem fortgeführt werden kann, und dass diese Entscheidung für spätere Nachvollziehbarkeit gespeichert wird.
* Der Vertragspartner bekommt einen weiteren Email-Report.
* Der Email Report enthält einen Hinweis, dass die Löschung des Objektes durchgeführt werden wird.
* Das Objekt taucht nicht in der Ansicht "eingelieferte Objekte" als "archiviert" auf.
