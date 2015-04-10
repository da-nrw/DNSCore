# Leistungsmerkmal: Ingest und Retrieval

Ingest und Retrieval sind die basalen Workflows der DNSCore aus der Sicht der Endnutzer (Rolle Vertragspartner).
Beim Ingest übergibt der Nutzer dem System ein SIP zur Übernahme. Beim Retrieval wird ein DIP angefordert und entnommen.

* [SIP-Spezifikation](specification_sip.de.md)
* [DIP-Spezifikation](specification_dip.md)
* Endnutzer-Dokumentation [Einlieferung und Entnahme](https://github.com/da-nrw/DNSCore/blob/master/DAWeb/doc/manual_ingest_and_retrieval.md)

Gilt für alle Szenarien:

## Szenario AT-UCI-1 Basistest - Ingest und Retrieval

#### Testpaket(e):

* ../../src/test/resources/at/ATUseCaseIngest1.tgz

#### Vorbedingungen:

* Der Nutzer ist unter der Rolle "Vertragspartner" angemeldet in der "DAWeb"

#### Durchführung:

1. Das Paket wird in den User-Eingangsordner abgelegt.
1. Die Paketverarbeitung wird gestartet über die Maske "Verarbeitung für abgelieferte SIP starten".
1. Warten auf die Email mit dem Einlieferungsbeleg.
1. In der Maske "Eingelieferte Objekte" das Objekt per technischem Identifier recherchieren.
1. Das Retrieval des Paketes per Button "Anfordern" für das entsprechende Paket anstoßen.
1. Der User entnimmt das Paket dem User-Entnahmeordner.
1. Der User entpackt das Paket und überprüft die Inhalten

#### Akkzeptanzkriterien:

* Der User erhält innerhalb einer Minute einen Einlieferungsbeleg in seinem Email-Postfach.
* Der Einlieferungsbeleg enthält die Information, dass die Archivierung des Objektes erfolgreich war.
* Der Einlieferungsbeleg enthält den technischen Identifier für das Objekt.
* Der User erhält innerhalb einer Minute eine Benachrichtigung über die Fertigstellung des Retrievalpaketes.
* Das Paket weist folgende Inhalte auf
```
  Identifier/data/CCITT_1.TIF
  Identifier/data/CCITT_1_UNCOMPRESSED.TIF
  Identifier/data/CCITT_2.TIF
``` 

#### Status und offene Punkte:

* Ist die Email für den Einlieferungsbeleg aussagekräftig?
* Ist die Email für die Fertigstellung des SIP aussagekräftig? Weiss der User, was zu tun ist? Evtl. Dokumentation?
* Falls sich hier konkrete Anforderungen ergeben, müssen die Akzeptanzkriterien dementsprechend angepasst werden.
* Ist die angegebene Dokumentation ausreichendv und verständlich?
* Automatisierte Aspekte:
** Die PREMIS-Datei wird automatisch geprüft in&nbsp;ATUseCaseIngestPREMISCheck#testProperPREMISCreation

## Szenario: AT-UCI-2: Generierung der PREMIS-Datei

#### Testpaket(e):

* Vorbedingungen:

#### Durchführung:

#### Akkzeptanzkriterien:

#### Status und offene Punkte:

####
