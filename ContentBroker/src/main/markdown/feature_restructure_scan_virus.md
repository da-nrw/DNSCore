# Leistungsmerkmal: Virenscan auf entpackte SIP-Datei


### Beschreibung:

Es muss sichergestellt werden, dass die eingehende, vom System zu verarbeitende SIP-Datei virenfrei ist. 
Hierzu wird der Virescanner clamAV verwendet. 
Der Virenscanner scannt das Verzeichnis des entpackten SIP (incl. der Unterverzeichnisse). 
Im Fehlerfall wird eine UserException (VIRUS_DETECTED) geworfen, wodurch eine EMail ausgelöst wird.
 

#### Kontext:

* [ATRestructureActionScanVirus.java](../../test/java/de/uzk/hki/da/at/ATRestructureActionScanVirus.java)

#### Vorbedingung (gilt für alle Szenarien): 

* Der User hat einen Account und ist unter der Rolle "Contractor" in DA-WEB eingeloggt.
* Der User hat einen Webshare mit Incoming Ordner, in den er Pakete legen kann. DA-WEB zeigt den Inhalt dieses Ordners in der Maske&nbsp;"Verarbeitung für abgelieferte SIP starten" an.

#### Durchführung:

1. Das Tespaket wird im Incoming Order ablegt
2. starten der Verarbeitung über "Verarbeitung für abgelieferte SIP starten"


## Szenario AT-RAVIRUS-1

#### Kontext:

ATRestructureActionScanVirus.testVirus

#### Testordner:

* ../../src/test/resources/at/ATRestructureActionScanVirus/virus  

#### Vorbedingungen

* Siehe Hintergrund.

#### Durchführung:

1. Einsichtnahme in die Bearbeitungsübersicht.
1. Einsichtnahme in die Email.

#### Akzeptanzkriterien:

* In der Bearbeitungsübersicht überprüfen, ob das Packet im Status 134 endet 
* Man bekommt eine EMail, die darauf hinweist, dass das Testpaket einen Virus enthält
* in der Maske 'System-Eventsteuerung' überprüfen, ob ein Eintrag vorhanden ist


## Status und offene Punkte:

* Ist die generierte Mail aussagekräfig? Kann der Anwender verstehen, was gemeint ist?

## Szenario AT-RAVIRUS-2

#### Kontext:

ATRestructureActionScanVirus.testNoVirus

#### Testordner:

* ../../src/test/resources/at/ATRestructureActionScanVirus/noVirus  

#### Vorbedingungen

* Siehe Hintergrund.

#### Durchführung:

* Siehe Hintergrund.

#### Akzeptanzkriterien:

1) überprüfen, ob das Paket normal verarbeitet wird


## Szenario AT-RAVIRUS-3

#### Kontext:

ATRestructureActionScanVirus.testPremisNoVirus

#### Testordner:

* ../../src/test/resources/at/ATRestructureActionScanVirusPremis.tar 

#### Vorbedingungen

* Siehe Hintergrund.

#### Durchführung:

* Siehe Hintergrund.

#### Akzeptanzkriterien:

1) überprüfen, ob in der premis.xml ein event-Tag mit eventIdentifierType = NO_VIRUS vorhanden ist.
   Des Weiteren muss in dem Tag eventDetail die Version des Virenscanners enthalten sein 
   (z.B. "KEIN Virus im Paket mit Identifier 1-20160912420 gefunden! Gescannt mit 'ClamAV 0.99.2/22199/Wed Sep  7 01:36:53 2016'")


## Status und offene Punkte:

implementiert ab Build XXXX 



