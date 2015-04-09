h2. Leistungsmerkmal: Ingest Delta

Dieser Test fokussiert die für den User offensichtlichsten Aspekte des Delta-Mechanismus. Zum einen bezieht sich das auf die Erkennung eines Paketes als zu einem Objekt zugehörig (durch die Nameswahl per&nbsp;*OriginalName*). Zum anderen bezieht sich das auf die Regeln der Erstellung des DIP (Stichwort{*}Oberflächenansicht*).&nbsp;


h4. Kontext:

* [SIP-Spezifikation](specification_sip.de.md)
* [DIP-Spezifikation:](specification_dip.md)
* Delta:&nbsp;[https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/the_delta_feature.md]

h2. Hintergrund:

Gilt für alle Szenarien!

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

# Das erste Paket wird in den User-Eingangsordner unter einem *für das Szenario* *jeweils eindeutigen Namen* abgelegt.
# Die Paketverarbeitung wird gestartet über die Maske "Verarbeitung für abgelieferte SIP starten".
# Warten auf die Email mit dem Einlieferungsbeleg.
# Sobald der Einlieferungsbeleg eingetroffen ist, wird das zweite Paket eingeliefert, und zwar mit dem selben OriginalName (für das entsprechende Szenario) wie das erste Paket eingeliefert wurde.
# Erneut warten auf den Einlieferungsbeleg.

## Szenario: AT-UCID-1 Ingest und Retrieval

#### Testpaket(e):

* siehe Hintergrund.

#### Durchführung:

# Siehe Hintergrund
# In der Maske "Eingelieferte Objekte" das Objekt per technischem Identifier recherchieren.
# Das Retrieval des Paketes per Button "Anfordern" für das entsprechende Paket anstoßen.
# Der User entnimmt das Paket dem User-Entnahmeordner.
# Der User entpackt das Paket und überprüft den Inhalt.

h4. Akkzeptanzkriterien:

* Der Einlieferungsbeleg enthält einen klaren Hinweis darauf, dass es sich um eine Delta-Lieferung zu einem bekannten Objekt handelt.
* Der in der Mail erwähnte technische Identifier entspricht dem in Test AT-1a erhaltenen.
* Das über die Ansicht "eingelieferte Objekte" zurückgeholte Paket muss nach dem Entpacken&nbsp;*exakt*&nbsp;die folgende Inhalte aufweisen:
** Identifier/data/CCITT_1.TIF
** Identifier/data/CCITT_1_UNCOMPRESSED.TIF
** Identifier/data/CCITT_2.TIF
** Identifier/data/CCITT_3.TIF

## Szenario: AT-UCID-2 Versioniertes Retrieval. Alle Packages


#### Testpaket(e):

* siehe Hintergrund.

#### Durchführung:

1. Siehe Hintergrund
1. In der Maske "Eingelieferte Objekte" das Objekt per technischem Identifier recherchieren.
1. Der User geht in die Objektdetailansicht und wählt das versionierte Retrieval aus: erstes Paket | zweites Paket | alle Pakete
1. Der User entnimmt das Paket dem User-Entnahmeordner und entpackt es.
1. Er entnimmt und entpackt das Package.
1. Er überprüft die Inhalte.

#### Akkzeptanzkriterien:

* Das über das versionierte Retrieval zurückgeholte Paket weist unterhalb des Ordners identifier/data, abgesehen von den premis.xml-Dateien,&nbsp;*exakt*&nbsp;folgende Inhalte auf



|  | erstes Paket |  zweites Paket | alle Pakete |
|-----------|---------------------|-----------------|--------------|
| repname1+a/CCITT_1.TIF | vorhanden ||  vorhanden |
| repname1+a/CCITT_1_UNCOMPRESSED.TIF | vorhanden ||  vorhanden |
| repname1+a/CCITT_2.TIF | vorhanden |  vorhanden ||
| repname1+b/CCITT_1.TIF | vorhanden |  vorhanden ||
| repname1+b/CCITT_2.TIF | vorhanden |  vorhanden ||
| repname2+a/CCITT_2.TIF || vorhanden |  vorhanden |
| repname2+a/CCITT_3.TIF || vorhanden | vorhanden |
| repname2+b/CCITT_2.TIF || vorhanden | vorhanden |
| repname2+b/CCITT_3.TIF || vorhanden | vorhanden |



## Status und offene Punkte:


####

* Ist die Sache mit der Nameserkennung per OriginalName transparent? Fehlt Dokumentation oder kann bestehende Dokumentation verbessert werden?{color}
* Automatisierte Aspekte:
** Die PREMIS-Datei wird automatisch geprüft in&nbsp;ATUseCaseIngestDeltaPREMISCheck#testProperPREMISCreation
* Nicht automatisierte Aspekte:
** &nbsp;Paketstruktur des DIP
