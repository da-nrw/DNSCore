# Leistungsmerkmal: Integritätsprüfung

Dieser Test kann nicht nur durch eine Person mit vollen Administrationsrechten auf das Gesamtsystem durchgeführt werden.

#### Kontext:

## Hintergrund:

Gilt für alle nachfolgenden Szenarien\!

#### Testpakete:

```
  (GitHub) ATUseCaseIngest1.tgz
  (GitHub) verändertes_tar.pack_1.tar
```

#### Vorbedingungen:

* Der Tester ist unter der Rolle "Contractor" angemeldet/eingeloggt in der "DAWeb"
* Der Tester hat volle Administrationsrechte: Rolle: "DA-Admin,Knotenadmin"

#### Durchführung:

* Als "Contractor": Das Paket wird in den Vertragspartner-Eingangsordner abgelegt.
* Als "Contractor": Die Paketverarbeitung wird gestartet über die Maske "Verarbeitung für abgelieferte SIP starten".
* Als "Contractor": Warten auf die Email mit dem Einlieferungsbeleg.

## Szenario AT-IP-1 Eine fremde Kopie wird beschädigt

#### Durchführung:

* Siehe Hintergrund.
* Als "Administrator": Beschädigung der Kopie: Die auf einem der fremden Knoten liegende Kopie wird ausgetauscht durch verändertes_tar.pack_1.tar (Nach Impl entfernen: Im AT Die Checksumme der fremden Kopie wird manipuliert, um einen geänderten Stand anzuzeigen.)
* Als "Contractor": Ca. 30 Sekunden warten. Dann Einsichtnahme in die DA-Web "eingelieferte Objekte".

#### Akzeptanzkriterien:

* Das Objekt wird als invalide gemeldet (konkret : button Achtung in der Spalte X)

## Szenario AT-IP-2 Eine eigene Kopie wird beschädigt

#### Durchführung:

* Siehe Hintergrund.
* Als "Administrator": Beschädigung der lokalen Kopie: Die auf einem der lokealen Knoten liegende Kopie wird ausgetauscht durch verändertes_tar.pack_1.tar
* Als "Contractor": Ca. 30 Sekunden warten. Dann Einsichtnahme in die DA-Web "eingelieferte Objekte".

h4. Akzeptanzkriterien:

* Das Objekt wird als *invalide* gemeldet (konkret : button Achtung in der Spalte X)


h2. Szenario&nbsp;{color:#00ff00}AT-IP-3{color}&nbsp;Alle Kopien werden beschädigt

* Siehe Hintergrund.
* Als "Administrator": Beschädigung der lokalen Kopie: Die auf einem der lokealen Knoten liegende Kopie wird ausgetauscht durch verändertes_tar.pack_1.tar
* Als "Administrator": Beschädigung der Kopie: Die auf einem der fremden Knoten liegende Kopie wird ausgetauscht durch verändertes_tar.pack_1.tar (Nach Impl entfernen: Im AT Die Checksumme der fremden Kopie wird manipuliert, um einen geänderten Stand anzuzeigen.)
* Als "Contractor": Ca. 30 Sekunden warten. Dann Einsichtnahme in die DA-Web "eingelieferte Objekte".

h4. Akzeptanzkriterien:

* Das Objekt wird als&nbsp;*invalide&nbsp;*gemeldet (konkret : button Achtung in der Spalte X)


h2. Szenario&nbsp;{color:#00ff00}AT-IP-4{color}&nbsp;Alle Kopien bleiben intakt

* Siehe Hintergrund.
* Als "Contractor": Ca. 30 Sekunden warten. Dann Einsichtnahme in die DA-Web "eingelieferte Objekte".

h4. Akzeptanzkriterien:

* Das Objekt wird als&nbsp;*valide&nbsp;*gemeldet (konkret : kein button Achtung in der Spalte X)

h2. Szenario&nbsp;{color:#00ff00}AT-IP-5{color} {color:#000000}Die Checksummen der fremden Knoten sind veraltet{color}
