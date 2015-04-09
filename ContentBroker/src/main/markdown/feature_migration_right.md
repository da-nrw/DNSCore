# Leistungsmerkmal: Migrationsbedingungen

Beschreibung

#### Kontext:

* [https://jira.lvrintern.lvr.de/browse/DANRW-73|https://jira.lvrintern.lvr.de/browse/DANRW-73?jql=text%20~%20%22migration%22]

## Hintergrund&nbsp;

gilt für alle Szenarien\!

#### Vorbedingung:

* Der User hat einen Account und ist unter der Rolle "Contractor" eingeloggt in der DA-WEB.
* Der User hat einen Webshare mit Incoming Order, in den er Pakete legen kann. DA-WEB zeigt den Inhalt dieses Ordners in der Maske&nbsp;"[Verarbeitung für abgelieferte SIP starten|https://da-nrw-q.lvr.de/daweb3/incoming/index]" an.

#### Durchführung:

# Das Tespaket wird im Incoming Order abelegt und die Verarbeitung gestartet (Maske "[Verarbeitung für abgelieferte SIP starten|https://da-nrw-q.lvr.de/daweb3/incoming/index]")
# Warten auf Email, die besagt, dass eine Entscheidung hinsichtlich eines Paketes mit einem bestimmten Identifier getroffen werden muss.



## {color:#000000}Szenario{color}&nbsp;{color:#00ff00}AT-MB{color}{color:#00ff00}\-1{color}&nbsp;{color:#000000}Keine Bedingung{color}

Im Contract eines SIP ist eine mögliche Migrationsbedinung zu hinterlegen. Diese ist faktisch ein XML-Eintrag in der premis.xml, welche auch vom SIP-Builder generiert wird, je nach der Auswahl des Users. Lautet die hinterlegte Auswahl "*keine Migrationsbedinung"*, so migriert das System, wenn möglich, aus den Dateiformaten des SIP in Dateiformate für die Langzeitarchivierung. &nbsp;&nbsp;

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

1. Das DIP&nbsp;*enthält*&nbsp;die Bilddatei vom migrierten Typ TIFF:&nbsp;*image42.jp2*
1. Es enhält nicht die originale Bilddatei: image42.jpg.

## Szenario AT-MD-2 Migrationsrückfrage ablehnen

Siehe auch Test AT-R1a. Hier wird vom einem Hinterlegten Recht&nbsp;*"Migrationsbedinung - Nach Rückfrage"*&nbsp;ausgegangen, so wie es auch vom SIP-Builder nach entsprechender Wahl in der premis.xml hinterlegt wird. In diesem Fall migriert das System Daten des entsprechenden Objektes niemals ungefragt, sondern fordert vom User eine Entscheidung ein, ob das Objekt migriert werden soll oder nicht.

#### Kontext:

* ATUseCaseIngestMigrationNotAllowed#test

#### Testpaket

```
(GitHub) ATMigrationNotAllowed.tgz
  Inhalt:
  data/image42.jpg
  data/premis.xml (MigrationRight: Migrationsbedingung: Zustimmung für Migration einholen).



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

#### Akkzeptanzkriterien

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

h4. Durchführung:

1. Siehe Hintergrund.
1. In der DA-WEB Entscheidungsübersicht für das entsprechende Objekt Migration positiv&nbsp;*bestätigen*.
1. Warten auf Email, die die Einlieferung in die LZA bestätigt.&nbsp;
1. Identifier notieren.
1. In der Ansicht "Eingelieferte Objekte" das Objekt mit dem entsprechenden Identifier recherieren.
1. Button "anfordern" für Objekt anwählen
1. Entnahme des DIP-Paketes aus dem Entnahmeordner des einliefernden Users.
1. Entpacken des DIP und Überprüfen der Inhalte.

#### Akkzeptanzkriterien:

* Das DIP enthält die Bilddatei vom migrierten Typ TIFF image42.jp2.
* Es enhält nicht die originale Bilddatei: image42.jpg.

h2. Szenario AT-MB-5 Informieren über Migration

Bei der Wahl der Einstellung "Über Migration informieren wird der Nutzer bei jeder Migration seiner Daten informiert"

h4. Testpaket:

``` 
  (GitHub) TODO
  Inhalt:
    data/image42.jpg
    data/premis.xml ((MigrationRight: Migrationsbedingung: Über Migration informieren).
```

#### Durchführung:

1. Warten auf Email, die über die Migration informiert

#### Akkzeptanzkriterien:

* TODO Inhalt der Email

## Szenario AT-MB-4 Migration nach Verstreichen der Frist.

Die Tests AT-MD-2 und AT-MD3 gehen davon aus, dass der User die Rückfrage ablehnt oder bestätigt. In diesem Szenario handelt der User für eine gegebene Zeitspanne gar nicht und in der Folge findet eine automatische Ablehung durch das System statt.

#### Kontext:

* ATUseCaseIngestMigrationNotAllowed#test

#### Testpaket:

```
(GitHub) ATMigrationNotAllowed.tgz
  Inhalt:
  data/image42.jpg
  data/premis.xml (&nbsp;(MigrationRight: Migrationsbedingung: Zustimmung für Migration einholen).



#### Vorbedingungen

* Siehe Hintergrund

#### Durchführung:

1. {color:#000000}Siehe Hintergrund{color}
1. {color:#ff0000}48 Stunden warten.{color}
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


####

* Nicht implementiert.
* Wie lange soll gewartet werden?
* Soll das konfigurierbar oder fix sein?
* Was soll dann geschehen? Ablehnung oder Durchführung der Migration?
