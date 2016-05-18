# Leistungsmerkmal: Erkennen eines Deltapaketes anhand gleicher URN  

Ein sogenanntes Delta kann sowohl durch gleichen Einlieferungsnamen als auch durch identische URN erzeugt werden. Dieser Test bezieht sich auf das Erzeugen eines Deltas durch gleiche URN in den Metadaten.  
&nbsp;

#### Kontext:

* [Delta](the_delta_feature.md)

## Hintergrund:

Gilt für alle Szenarien!

#### Testpakete:

* ATDeltaOnURN_1.tgz
* ATDeltaOnURN_2.tgz
* ATDeltaOnURN_3.tgz

#### Vorbedingungen:

* User ist unter der Rolle "Contractor" angemeldet/eingeloggt in der "DAWeb"

#### Durchführung:

1. Das Paket ATDeltaOnURN_1.tgz wird in den User-Eingangsordner abgelegt.
1. Die Paketverarbeitung wird gestartet über die Maske "Verarbeitung für abgelieferte SIP starten".
1. Warten auf die Email mit dem Einlieferungsbeleg.
1. Sobald der Einlieferungsbeleg eingetroffen ist, wird das zweite Paket ATDeltaOnURN_2.tgz eingeliefert, und zwar unter einem anderen Namen als das erste Paket eingeliefert wurde.
1. Erneut warten auf den Einlieferungsbeleg.
1. Überprüfen ob dieser Einlieferungsbeleg die gleiche Identifikationsnummer trägt als der erste.
1. Sodann kann das dritte Paket ATDeltaOnURN_3.tgz eingeliefert, und zwar unter einem anderen Namen als das erste und das zweite Paket eingeliefert wurden.
1. Erneut warten auf den Einlieferungsbeleg.
1. Auch dieser Einlieferungsbeleg muss die gleiche Identifikationsnummer tragen.
