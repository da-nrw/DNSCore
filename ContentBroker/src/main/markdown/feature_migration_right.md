# Leistungsmerkmal: Migrationsbedingungen

Beschreibung

#### Kontext:

## Hintergrund

gilt für alle Szenarien\!

#### Vorbedingung:

* Der User hat einen Account und ist unter der Rolle "Contractor" eingeloggt in der DA-WEB.
* Der User hat einen Webshare mit Incoming Order, in den er Pakete legen kann. DA-WEB zeigt den Inhalt dieses Ordners in der Maske&nbsp;"Verarbeitung für abgelieferte SIP starten" an.

#### Durchführung:

1. Das Tespaket wird im Incoming Order abelegt und die Verarbeitung gestartet "Verarbeitung für abgelieferte SIP starten"
1. Warten auf Email, die besagt, dass eine Entscheidung hinsichtlich eines Paketes mit einem bestimmten Identifier getroffen werden muss.

## Szenario AT-MB-1 Keine Bedingung

Im Contract eines SIP ist eine mögliche Migrationsbedinung zu hinterlegen. Diese ist faktisch ein XML-Eintrag in der premis.xml, welche auch vom SIP-Builder generiert wird, je nach der Auswahl des Users. Lautet die hinterlegte Auswahl "keine Migrationsbedinung", so migriert das System, wenn möglich, aus den Dateiformaten des SIP in Dateiformate für die Langzeitarchivierung. 

#### Kontext:

* ATUseCaseIngestMigrationAllowed#test

#### Testpaket:

```
(GitHub) ATMigrationAllowed.tgz
 Inhalt
   data/image42.jpg
   data/premis.xml (MigrationRight: Migrationsbedingung: Keine)
```

#### Vorbedingungen

* Siehe Hintergrund.

#### Durchführung:

1. Siehe Hintergrund.
1. Technischen Identifier notieren.
1. In der Ansicht "Eingelieferte Objekte" das Objekt mit dem entsprechenden Identifier recherieren.
1. Button "anfordern" für Objekt anwählen
1. Entnahme des DIP-Paketes aus dem Entnahmeordner des einliefernden Users.
1. Entpacken des DIP und Überprüfen der Inhalte.

#### Akkzeptanzkriterien:

1. Das DIP enthält die Bilddatei vom migrierten Typ TIFF: image42.jp2
1. Es enhält nicht die originale Bilddatei: image42.jpg.

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

Die Tests AT-MB-2 und AT-MB3 gehen davon aus, dass der User die Rückfrage ablehnt oder bestätigt. In diesem Szenario handelt der User für eine gegebene Zeitspanne gar nicht und in der Folge findet eine automatische Ablehnung durch das System statt.

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


