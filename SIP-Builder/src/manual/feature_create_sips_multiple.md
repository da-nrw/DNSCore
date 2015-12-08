# Leistungsmerkmal: SIPs aus Unterordnern des Quellverzeichnisses erstellen

#Beschreibung

## Hintergrund

Der Kunde hat mehrere Datensätze, die er als einzelne SIPs ins DNS einliefern möchte. Alle diese Datensätze, die SIP-Kandidaten, liegen auf der obersten Ebene eines gemeinsamen Verzeichnisses. Er möchte aber nicht jede SIP-Bildung einizeln anstoßen, sondern übergibt dem SIP-Builder stattdessen das übergeordnete Verzeichnis und erwartet als Ausgabe die SIPs aus allen unterordnern dieses Verzeichnisses. 

Für jedes einzelne SIP überprüft der SIP-Builder die Struktur des Pakets sowie die darin enthaltenen Metadaten. Bei identifizierten Abweichungen sieht der User entsprechende Fehlermeldungen.

Zur Zeit gibt es zwei Kategorien von Fehlermeldungen:
* Abweichungen, die zwangsläufig zu Problemen im ContentBroker führen werden.
* Abweichungen, die unter bestimmten Bedingungen zu einem Fehler im ContentBroker führen werden. Der Fehler wird dem Kunden per Email mitgeteilt und ist in der DAWEB ebenfalls vermerkt.

#### Vorbedingung:

* Der User hat den SIP-Builder mit der Build-Nr. >= 1497.

## Szenario AT-BMS-METS: Bilden mehrerer SIPs mit jeweils einer Metadatendatei des Typs METS

#### Kontext:

* [ATBuildMultipleMetsSips](../test/java/de/uzk/hki/da/at/ATBuildMultipleMetsSips.java).test()

#### Testpaket:   

* [ATBuildMultipleMetsSips](../test/resources/at/ATBuildMultipleMetsSips)

#### Vorbedingungen

* Siehe Hintergrund.

#### Durchführung:

1. Download des Testpakets
1. Starten des SIP-Builders
1. Auswahl der Option "SIPs aus Unterordnern des Quellverzeichnisses erstellen"
1. Auswahl des Pakets ATBuildMultipleMetsSips als Quellordner
1. Festlegung des Zielordners
1. Erstellung der SIPs mittels GUI

#### Akzeptanzkriterien:

1. Der ausgewählte Zielordner enthält insgesamt drei SIPs:
```
  ATBuildMultipleMetsSip1.tgz,
  ATBuildMultipleMetsSip2.tgz,
  ATBuildMultipleMetsSip3.tgz
``` 

1. Jedes dieser drei SIPs hat folgenden Inhalt
```
  ATBuildMultipleMetsSip1/data/premis.xml
  ATBuildMultipleMetsSip1/data/export_mets.xml 
  ATBuildMultipleMetsSip1/data/image
``` 
Der Ordner image enthält 29 Bilder im BMP-Dateiformat.

