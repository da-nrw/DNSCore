# Leistungsmerkmal: Validierung der Einlieferung

Die vom Vertragspartner eingelieferten, zur Übernahme durch das System vorgesehenen, SIPs
werden zu Beginn ihrer Umwandlung in für die Langzeitarchivierung geeignete AIPs, in mehrerer Hinsicht
geprüft. 

Dazu gehört vor allem eine Prüfung hinsichtlich der Verwendung von eindeutigen Dokumentennamen. Die Eindeutigkeit von
Dokumentennamen ist eine wichtige Voraussetzung für die Tauglichkeit von SIPs für die Aufbereitung zur Langzeitarchivierung
ihrer Inhalte.

#### Kontext

* [Objekt-Modell](object_model.md) (Documents im Objekt-Modell)
* Informationen zu Dokumenten in der [SIP-Spezifikation](specification_sip.de.md)
* Beschreibung von Documents und Oberflächenansicht: [https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/the_delta_feature.md]

## Hintergrund:

Die hier aufgeführten Unterpunkte gelten für alle Szenarien gleichermaßen.

#### Vorbedingungen:

* Der Nutzer ist in der DAWeb in der Rolle "Vertragspartner" eingeloggt.
* Der Nutzer ist mit einem FileShare Client mit dem entsprechenden für ihn reservierten Nutzungsbereich auf dem Verarbeitungsknoten verbunden.

#### Durchführung:

1. Das Testpaket wird eingespielt.

## Szenario: AT-V-1 Doppelter Dokumentenname

Dokumentennamen bezeichnen Dateien innerhalb eines Objektes eindeutig. Eine Datei a.jpg und ihr durch Konversion erzeugter Nachfolger a.tif teilen den Dokumentennamen "a". Aber auch eine durch Delta hinzugefügte Datei a.pdf würde den Dokumentennamen teilen. Dokumentennamen müssen eindeutig innerhalb von Objekten sein. Das System weist daher doppelte Dokumentennnamen zurück und informiert den User per Email.

#### Kontext:

* ATUseCaseIngestValidationNotPassed#testDuplicateDocumentName

#### Testpaket(e):

```
ATDuplicateDocumentName.tgz
->
ATDuplicateDocumentName/data/a.tif // Dokumentenname "a"
ATDuplicateDocumentName/data/a.jpg // Dokumentenname "a"
ATDuplicateDocumentName/data/premis.xml
```

#### Durchführung:

1. Einsichtnahme in die "Bearbeitungsübersicht.".
1. Einsichtnahme in die Email.

#### Akkzeptanzkriterien:

* In der Bearbeitungsübersicht bleibt das Testpaket in einem auf 4 endenden Fehlerstatus hängen.
* Man bekommt eine Email.
* Die Email soll einen Aussagekräftigen Hinweis darauf enthalten, dass ein Dokumentname doppelt vergeben wurde.

## Szenario: AT-V-2 Mehr als eine Metadatendatei gefunden

Vielfältige Regeln definieren, in welcher Form Metadatendateien innerhalb eines SIPs abgelegt werden müssen, damit das System sie als eine der vier unterstützen Pakettypen für die Präsentation behandeln kann. Davon abweichende Metadatentypen können vom System für die LZA verwendet werden und werden von der Präsentationskomponente ignoriert. Bestimmte Metadatenkombinationen muss das System jedoch zurückweisen, da es aufgrund von Mehrdeutigkeiten nicht selbstständig entscheiden kann. Diese Test hier dient als Beispiel dafür. Ein Paket enthält zwei EAD-Dateien, während die Spezifikation eine einzige Datei von diesem Typ verlangt.&nbsp;

####


#### Kontext

* ATUseCaseIngestValidationNotPassed#testDuplicateMetadataFiles

#### Testpaket:

```
ATDuplicateMetadataFiles.tgz
->
ATDuplicateMetadataFiles/data/vda3_copy.XML // EAD Datei 1
ATDuplicateMetadataFiles/data/vda3.XML // EAD Datei 2
ATDuplicateMetadataFiles/data/abc.tif
ATDuplicateMetadataFiles/data/premis.xml
```


#### Durchführung:

1. Einsichtnahme in die Bearbeitungsübersicht.
1. Einsichtnahme in die Email.

#### Akkzeptanzkriterien:

* In der Bearbeitungsübersicht bleibt das Testpaket in einem auf 4 endenden Fehlerstatus hängen.
* Man bekommt eine Email.&nbsp;
* Die Email soll einen aussagekräftigen Hinweis darauf enthalten, dass das Problem ist, dass zwei EAD Dateien im Paket vorhanden sind.

## Status und offene Punkte:

* Ist die Mail aussagekräftig? Kann der User verstehen was gemeint ist?
* Wenn nicht, kann der User ableiten, wie er Hilfe bekommt?
* Gegebenenfalls muss nach Klärung dieser Fragen das Akzeptanzkriterium hinsichtlich des Mail-Inhaltes neu definiert werden.

* Automatisiert mit Ausnahme Email-Versand.
* die tatsächlichen Regeln in Akzeptanztests unterzubringen, wäre derzeit zu aufwändig, da es sehr viele Kombinationen abzudecken gilt. Diese sind dann in UnitTests untergebracht
