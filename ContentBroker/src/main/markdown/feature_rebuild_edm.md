# Leistungsmerkmal: Starten der Neuanlage der EDM-Daten und des ElasticSearch-Suchindexes mithilfe eines Systemevents


Für den Fall, dass die Mets->EDM Mapping-Regeln sich versionsübergreifend ändern, sollte eine Möglichkeit bestehen, den bereits publizierten Datenbestand neu zu publizieren. 
Dafür ist ein RecreateEDMAndReindexEvent-Systemevent vorhanden, dieser versetzt alle regulär verarbeiteten und publizierten Objekte eines Contractors in den 560-Status, 
wodurch eine neue EDM erzeugt wird (570-Status) und in den ES-Index republiziert (580-Status).  

#### Kontext:

* [ATRecreateEDM](../../test/java/de/uzk/hki/da/at/ATRecreateEDM.java)

## Hintergrund:

Gilt für alle nachfolgenden Szenarien!

#### Testpakete:

```
  (GitHub) ../../src/test/resources/at/ATMetsEDMMapingDateIssued.tgz
```

#### Vorbedingungen:

* Der Tester ist unter der Rolle "Contractor" angemeldet/eingeloggt in der "DAWeb"

#### Durchführung:

* Testpacket ins Incoming-Order ablegen und die Verarbeitung starten (Maske "Verarbeitung für abgelieferte SIP starten")
* Warten auf die Bestätigungsmail.
* RecreateEDMAndReindexEvent-Systemevent anlegen
* In die *Bearbeitungsübersicht* wechseln
* Eventuell Warten (max. 30 sec)
* Alle publizierten Objekte des Test-CSN sollten republiziert werden. D.h. auf die Objekte wird kurzzeitig die 560-,570-,580-Action angewendet.

#### Akzeptanzkriterien:
* Mindestens das Testpacket, aber auch alle anderen publizierten Test-Packete werden automatisiert in die Verarbeitung mithilfe der 560-,570-,580-Actions versetzt.
* Nach der (Re-)Publication des Datenbestandes, ist das RecreateEDMAndReindexEvent-Systemevent in der Systemevent-Ansicht nicht mehr vorhanden.
