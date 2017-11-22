# Leistungsmerkmal: Lizenzverarbeitung für METS-SIP

Ausgehend von einer NRW-Regelung müssen alle mit Publikationsabsicht eingeliefrten SIP's eine gültige Veröffentlichungslizenz beinhalten.

Die Lizenz kann in den Mets-Metadaten stehen, oder in der Premis-Datei enthalten sein, jedoch nicht an beiden stellen gleichzeitig. Lizenzangabe in der Premis-Datei wird in die METS-Datei des PIP übernommen.
Anschlisend wird die angegebene Lizenz ins EDM übernommen und bis zum Portal weitergereicht.

 
#### Kontext:

* [ATIngestLicensedMetsSip](../../test/java/de/uzk/hki/da/at/ATIngestLicensedMetsSip.java)


#### Testpakete:

```
  (GitHub) ../../src/test/resources/at/LicenseInMets.tgz
  (GitHub) ../../src/test/resources/at/LicenseInPremis.tgz
```

#### Vorbedingungen:

* Der Tester ist unter der Rolle "Contractor" angemeldet/eingeloggt in der "DAWeb"

#### Durchführung:

* Testpacket ins Incoming-Order ablegen und die Verarbeitung starten (Maske "Verarbeitung für abgelieferte SIP starten")
* Warten auf die Bestätigungsmail.
* Überprüfen ob die Lizenz in den PIP-Metadaten (METS, EDM, DC) in Fedora enthalten ist.
* Überprüfen ob die Lizenz bei dem entsp. Objekt im Portal erscheint.

#### Akzeptanzkriterien:
* Die Lizenz ist in den PIP-Metadaten und im Portal bei dem Objekt enthalten
