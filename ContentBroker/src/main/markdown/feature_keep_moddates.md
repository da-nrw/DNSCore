# Leistungsmerkmal: Beibehaltung des Dateiänderungsdatum

Das Dateiänderungsdatum einer jeden mittels SIP-Builder eingebundenen, im DNS archivierten und anschließend angeforderten Datei soll über alle diese Schritte erhalten bleiben.

Dies gilt nur für die nicht konvertierten Originaldatein.

#### Kontext:

* Kompletter Verarbeitungs-Workflow vom Packen des SIPs bis zum Retrieval aus dem Archiv.

## Hintergrund:

#### Vorbedingungen:

* Der Nutzer verfügt über den SIPBuilder ab Version 1606
* Der Nutzer kann Pakete ins Archiv einliefern und anfordern.

#### Durchführung:

1. Das Testpaket wird ausgpackt.
1. Die ausgepackte Verzeichnisstruktur wird mittels SIP-Builder in ein SIP gepackt.
1. Das SIP wird ins DNS eingeliefert.
1. Das Paket wird aus dem DNS angefordert (in der Maske Objektdetail "Versioniertes Retrieval starten").
1. Das angeforderte Paket wird entpackt.
1. Die Modifizierungszeitpunkte der Ein- und Ausgangsdateien werden verglichen (a-Representation).


#### Kontext

* [ATKeepModDates](../../test/java/de/uzk/hki/da/at/ATKeepModDates.java).test()

#### Testpaket(e):

```
(GitHub) KeepModDates.tgz

#### Vorbedingungen:

* siehe Hintergrund.

#### Durchführung:

1. siehe Hintergrund.

#### Akkzeptanzkriterien:

* die Dateiänderungsdaten der Originaldateien und der Dateien des Retrievals stimmen überein.


