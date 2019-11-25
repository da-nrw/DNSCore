# Leistungsmerkmal: Lizenzverarbeitung für METS-SIP

Ausgehend von einer NRW-Regelung (EGOV-G) müssen alle mit Publikationsabsicht eingeliefrten SIP's eine gültige Veröffentlichungslizenz beinhalten.

Die Lizenz kann in den METS-Datei(accessCondition-Element, type="use and reproduction") stehen, oder in der PREMIS-Datei(publicationLicense-Element) enthalten sein, jedoch nicht an beiden Stellen gleichzeitig. Lizenzangabe aus der PREMIS-Datei wird in die METS-Datei des PIP übernommen.
Anschließend wird die angegebene Lizenz ins EDM übernommen und bis zum Portal weitergereicht.


 
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

* Testpaket ins Incoming-Order ablegen und die Verarbeitung starten (Maske "Verarbeitung für abgelieferte SIP starten")
* Warten auf die Bestätigungsmail.


#### Akzeptanzkriterien:
* Überprüfen ob die Lizenz in den PIP-Metadaten (METS, EDM, DC) in Fedora enthalten ist.
* Überprüfen ob die Lizenz bei dem entsp. Objekt im Portal erscheint.
