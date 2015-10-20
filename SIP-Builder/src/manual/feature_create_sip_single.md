# Leistungsmerkmal: Ein SIP aus dem Quellverzeichnis erstellen

#Beschreibung

## Hintergrund

Der Kunde hat seine Daten in einem Verzeichnis zusammengetragen und möchte sie in dieser Form in DNS langzeitarchivieren.
Dafür muss er zunächst mit dem SIP-Builder ein SIP erstellen.
Der SIP-Builder überprüft vor der Bildung des SIP die Struktur des Pakets sowie die darin enthaltenen Metadaten. Bei identifizierten Abweichungen sieht der User entsprechende Fehlermeldungen.

Zur Zeit gibt es zwei Kategorien von Fehlermeldungen:
* Abweichungen, die zwangsläufig zu Problemen im ContentBroker führen werden.
* Abweichungen, die unter bestimmten Bedingungen zu einer fehlerfreien Verarbeitung im ContentBroker führen werden.

#### Vorbedingung:

* Der User hat den SIP-Builder mit der Build-Nr. >= 1411.

## Szenario AT-BSS-EAD-1: Bilden eines einzelnen (-single) SIPs mit einer Metadatendatei des Typs EAD

#### Kontext:

* [ATSipBuilderCliEad](../test/java/de/uzk/hki/da/at/ATSipBuilderCliEad.java).testBuildSingleSipCorrectReferences()

#### Testpaket:   

* [ATBuildSingleEadSip](../test/resources/at/ATBuildSingleEadSip)

#### Durchführung:

1. Download des Testpakets
1. Starten des SIP-Builders
1. Auswahl der Option "Ein SIP aus dem Quellverzeichnis erstellen"
1. Auswahl des Pakets ATBuildSingleEadSip als Quellordner
1. Festlegung des Zielordners
1. Erstellung des SIPs mittels GUI

#### Akkzeptanzkriterien:

1. In dem ausgewählten Zielordner befindet sich ein SIP mit dem Namen ATBuildSingleEadSip.tgz

## Szenario AT-MB-2 Migrationsrückfrage ablehnen

Szenario AT-MD-2 Migrationsrückfrage ablehnen
Siehe auch Test AT-R1a.

Hier wird vom einem Hinterlegten Recht "Migrationsbedinung - Nach Rückfrage" ausgegangen, so wie es auch vom SIP-Builder nach entsprechender Wahl in der premis.xml hinterlegt wird. In diesem Fall migriert das System Daten des entsprechenden Objektes niemals ungefragt, sondern fordert vom User eine Entscheidung ein, ob das Objekt migriert werden soll oder nicht.
Dieses Szenario beschreibt den Fall, dass die Migration abgelehnt wird.

#### Kontext:

* ATUseCaseIngestMigrationNotAllowed#test

#### Testpaket

```
(GitHub) ATMigrationNotAllowed.tgz
  Inhalt:
  data/image42.jpg
  data/premis.xml (MigrationRight: Migrationsbedingung: Zustimmung für Migration einholen).
```


####


#### Vorbedingungen

* Siehe Hintergrund.

#### Durchführung

1. Siehe Hintergrund.
1. In der DA-WEB-Maske "Entscheidungsübersicht" für das entsprechende Objekt Migration&nbsp;*ablehnen*.
1. Warten auf Email, die die Einlieferung in die LZA bestätigt.&nbsp;
1. Identifier notieren.
1. In der Ansicht "Eingelieferte Objekte" das Objekt mit dem entsprechenden Identifier recherieren.
1. Button "anfordern" für Objekt anwählen
1. Entnahme des DIP-Paketes aus dem Entnahmeordner des einliefernden Users.
1. Entpacken des DIP und Überprüfen der Inhalte.

#### Akzeptanzkriterien

* Das DIP enthält die Bilddatei vom originalen Typ JPEG: image42.jpg.
* Es enhält nicht die migrierte Version der originalen Bilddatei Bilddatei: image42.jp2.


## Szenario AT-MB-3 Migrationsrückfrage bestätigen.

Während beim letzten Test die Migration letztendlich abgelehnt wurde, wird bei diesem Test bestätigt.

#### Kontext:

* ATUseCaseIngestMigrationNotAllowed#test

#### Testpaket:

```
  (GitHub) ATMigrationNotAllowed.tgz
  Inhalt:
    data/image42.jpg
    data/premis.xml (MigrationRight: Migrationsbedingung: Zustimmung für Migration einholen).
``` 

#### Vorbedingung:

* Siehe Hintergrund.

#### Durchführung:

1. Siehe Hintergrund.
1. In der DA-WEB Entscheidungsübersicht für das entsprechende Objekt Migration positiv&nbsp;*bestätigen*.
1. Warten auf Email, die die Einlieferung in die LZA bestätigt.&nbsp;
1. Identifier notieren.
1. In der Ansicht "Eingelieferte Objekte" das Objekt mit dem entsprechenden Identifier recherieren.
1. Button "anfordern" für Objekt anwählen
1. Entnahme des DIP-Paketes aus dem Entnahmeordner des einliefernden Users.
1. Entpacken des DIP und Überprüfen der Inhalte.

#### Akzeptanzkriterien:

* Das DIP enthält die Bilddatei vom migrierten Typ TIFF image42.jp2.
* Es enhält nicht die originale Bilddatei: image42.jpg.




## Szenario AT-MB-5 Informieren über Migration

Bei der Wahl der Einstellung "Über Migration informieren wird der Nutzer bei jeder Migration seiner Daten informiert"

#### Testpaket:

``` 
  (GitHub) TODO
  Inhalt:
    data/image42.jpg
    data/premis.xml ((MigrationRight: Migrationsbedingung: Über Migration informieren).
```

#### Durchführung:

1. Warten auf Email, die über die Migration informiert

#### Akzeptanzkriterien:

* TODO Inhalt der Email

## Szenario AT-MB-4 Migration nach Verstreichen der Entscheidungsfrist.

Die Tests AT-MBD-2 und AT-MB3 gehen davon aus, dass der User die Rückfrage ablehnt oder bestätigt. In diesem Szenario handelt der User für eine gegebene Zeitspanne gar nicht und in der Folge findet eine automatische Ablehung durch das System statt.

#### Kontext:

* ATUseCaseIngestMigrationNotAllowed#test

#### Testpaket:

```
(GitHub) ATMigrationNotAllowed.tgz
  Inhalt:
  data/image42.jpg
  data/premis.xml (&nbsp;(MigrationRight: Migrationsbedingung: Zustimmung für Migration einholen).
``` 


#### Vorbedingungen

* Siehe Hintergrund

#### Durchführung:

1. Siehe Hintergrund
1. 30 Tage warten.
1. Warten auf Email, die die Einlieferung in die LZA bestätigt.&nbsp;
1. Identifier notieren.
1. In der Ansicht "Eingelieferte Objekte" das Objekt mit dem entsprechenden Identifier recherieren.
1. Button "anfordern" für Objekt anwählen
1. Entnahme des DIP-Paketes aus dem Entnahmeordner des einliefernden Users.
1. Entpacken des DIP und Überprüfen der Inhalte.

#### Akkzeptanzkriterien:

* Das DIP enthält die Bilddatei vom originalen Typ JPEG: image42.jpg.
* Es enhält nicht die migrierte Version der originalen Bilddatei Bilddatei: image42.jp2.

## Status und offene Punkte:

implementiert mit Build > #1320

