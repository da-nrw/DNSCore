## Leistungsmerkmal: Umgang mit fehlerhaften Tags bei TIFF

### Hintergrund

Bei einigen Bildern, die in der von einem komerziellen Unternehmen vorgeschlagenen Formaterweiterung des TIFF Standards geschickt werden (im Folgenden verkürzend "BigTiff" genannnt), können diese unter bestimmten Voraussetzungen als fehlerhaft erkannt werden. Alle problematischen Bilder werden mit fehlerhaften Tags im Rich-IPTC Bereich geliefert, die gemäß Spezifikation DNS (als auch der vorgeschlagenen Erweiterung "BigTIFF") korrekt als fehlerhaft ausgesteuert werden. Die eigentlichen Bilddaten sind hiervon nicht betroffen. 

Je nach Entscheidung des Contractors wird die Archivierung in DNS dennoch vorgenommen, falls der Contractor diese Entscheidung per Webmaske vorgenommen hat. Es bestand ausdrücklich der Wunsch, diese Fehler auch übergehen zu können. 

Dafür wird eine Rückfrage des Systems ausgelöst, die eine Rückbestätigung der Entscheidung durch den Einliefernden erzwingt. Diese wird in der PREMIS vermerkt, ebenso dass es einen Fehler beim Konvertieren gab. Auf mögliche Konsequenzen für die weitere Bestandserhaltung bei der Übergehung dieser Fehler wird ausdrücklich hingewiesen! 

Der einzig bekannte Fehler in den bisher vorliegenden Testdaten äußert sich durch die Fehlerausgabe von (Bsp.): 
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

Er gibt einen Hinweis auf "division by zero" in einem Tagfeld, welches mit einem gültigen Wert befüllt sein sollte. 

## Szenario AT-TIFFT-1 Problematische Pakete zunächst zurückweisen

#### Kontext:

* [ATInvalidTiffTagsInBigTiff](../../test/java/de/uzk/hki/da/at/ATInvalidTiffTagsInBigTiff.java)testInvalidTiffTagsDetectUserException

#### Testpaket:

[ATInvalidTiffTagsInBigTiff.tgz](../../../src/test/resources/at/ATInvalidTiffTagsInBigTiff.tgz) 

#### Vorbedingung:

* Der User hat einen Account und kann sich unter der Rolle "Contractor" in der DA-WEB&nbsp;eingeloggen.

#### Durchführung:

1. Das Testpaket wird im Incoming Order abgelegt und die Verarbeitung gestartet (Maske "Verarbeitung für abgelieferte SIP starten")
1. Warten auf Email-Report.

Das System weist Pakete, die Dateien mit problematischen Tags enthalten, zurück. Das Paket landet in einem 4er Fehlerstatus (UserException)

#### Akzeptanzkriterien:

* Der Email Report enthält einen Hinweis über die problematische Datei.
* Das Objekt taucht nicht in der Ansicht "eingelieferte Objekte" als "archiviert" auf.

## Szenario AT-TIFFT-2 Archivierung nach Rückfrage durchführen 

Der Contractor will die Archivierung trotz des Hinweises in der Email auf die Probleme der zuk. Bestandserhaltung durchführen.

#### Kontext:

* [ATInvalidTiffTagsInBigTiff](../../test/java/de/uzk/hki/da/at/ATInvalidTiffTagsInBigTiff.java)testInvalidTiffTagsPrunedByUser

#### Testpaket:

[ATInvalidTiffTagsInBigTiff.tgz](../../../src/test/resources/at/ATInvalidTiffTagsInBigTiff.tgz) 

#### Vorbedingungen:

* wie AT-TIFFT-1

#### Durchführung:

1. wie AT-TIFFT-1.
1. Nach Erhalt des Email-Report: Einsichtnahme in Maske "Entscheidungsübersicht" der DA-Web.
1. In der Maske "Entscheidungsübersicht" bestätigt der Vertragspartner die Rückfrage mit *"Ja - Ingest fortführen"*.

#### Akzeptanzkriterien:

* Der Email Report enthält einen Hinweis, dass für das Objekt mit den&nbsp;﻿"Identifier" eine Entscheidung zu treffen ist.
* Der Email Report enthält einen Hinweis über die problematische Datei.
* Der Email Report enthält den Hinweis, dass der weitere Ingest des Paketes in die Langzeitarchivierung nicht empfohlen wird.
* Der Email Report enthält den Hinweis, dass der Ingest trotzdem fortgeführt werden kann, und dass diese Entscheidung für spätere Nachvollziehbarkeit gespeichert wird.
* Der Vertragspartner bekommt einen weiteren Email-Report, der den Ingest bestätigt.
* Das Objekt taucht unter eingelieferte Objekte auf als "archiviert" auf.&nbsp;
* &nbsp;

## Szenario AT-TIFFT-3 Archivierung nach Rückfrage nicht durchführen 

* Der Contractor will die Archivierung auf Grund des Hinweises nicht durchführen.

#### Kontext:

* [ATInvalidTiffTagsInBigTiff](../../test/java/de/uzk/hki/da/at/ATInvalidTiffTagsInBigTiff.java)testInvalidTiffTagsPrunedByUser

#### Testpaket:

[ATInvalidTiffTagsInBigTiff.tgz](../../../src/test/resources/at/ATInvalidTiffTagsInBigTiff.tgz)

#### Vorbedingungen:

wie AT-TIFFT-1

#### Durchführung:

1. Siehe Hintergrund.
1. Nach Erhalt des Email-Report: Einsichtnahme in Maske "Entscheidungsübersicht" der DA-Web.
1. In der Maske "Entscheidungsübersicht" bestätigt der Vertragspartner die Rückfrage mit *"Nein - Ingest abbrechen"*.

#### Akzeptanzkriterien:

* Der Email Report enthält einen Hinweis,&nbsp;dass für das Objekt mit den&nbsp;﻿"Identifier" eine Entscheidung zu treffen ist.
* Der Email Report enthält einen Hinweis über die problematische Datei.
* Der Email Report enthält den Hinweis, dass der weitere Ingest des Paketes in die Langzeitarchivierung nicht empfohlen wird.
* Der Email Report enthält den Hinweis, dass der Ingest trotzdem fortgeführt werden kann, und dass diese Entscheidung für spätere Nachvollziehbarkeit gespeichert wird.
* Der Vertragspartner bekommt einen weiteren Email-Report.
* Der Email Report enthält einen Hinweis, dass die Löschung des Objektes durchgeführt werden wird.
* Das Objekt taucht nicht in der Ansicht "eingelieferte Objekte" als "archiviert" auf.
