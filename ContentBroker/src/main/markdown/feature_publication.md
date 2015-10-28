# Leistungsmerkmal: Publikation

## Szenario AT-P-1

#### Kontext:

* [ATUseCaseTimeBasedPublication  testPublishInstOnly](../../test/java/de/uzk/hki/da/at/ATTimeBasedPublication.java#testPublishInstOnly#testPublishInstOnly)
* [ATUseCaseTimeBasedPublication  testNoPubWithLawSetForAudiencePublic](../../test/java/de/uzk/hki/da/at/ATTimeBasedPublication.java#testNoPubWithLawSetForAudiencePublic)
* [ATUseCaseTimeBasedPublication testNoPubWithStartDateSet](../../test/java/de/uzk/hki/da/at/ATTimeBasedPublication.java#testNoPubWithStartDateSet)
* [ATUseCaseTimeBasedPublication testPublishNothing](../../test/java/de/uzk/hki/da/at/ATTimeBasedPublication.java#testPublishNothing)
* [ATUseCaseTimeBasedPublication testPublishAll](../../test/java/de/uzk/hki/da/at/ATTimeBasedPublication.java#testPublishAll)

#### Testpakete für automatisierte Tests:

* [ATTimeBasedPublInstOnly_id.pack_1.tar](https://cdn.rawgit.com/da-nrw/DNSCore/master/ContentBroker/src/test/resources/at/ATTimeBasedPublInstOnly_id.pack_1.tar) 
* [ATTimeBasedPublNoPubWithLawSet_id.pack_1.tar](https://cdn.rawgit.com/da-nrw/DNSCore/master/ContentBroker/src/test/resources/at/ATTimeBasedPublNoPubWithLawSet_id.pack_1.tar) 
* [ATTimeBasedPublNoPubWithStartDateSet_id.pack_1.tar](https://cdn.rawgit.com/da-nrw/DNSCore/master/ContentBroker/src/test/resources/at/ATTimeBasedPublNoPubWithStartDateSet_id.pack_1.tar) 
* [ATTimeBasedPublPublishNothing_id.pack_1.tar](https://cdn.rawgit.com/da-nrw/DNSCore/master/ContentBroker/src/test/resources/at/ATTimeBasedPublPublishNothing_id.pack_1.tar) 
* [ATTimeBasedPublAllPublic_id.pack_1.tar](https://cdn.rawgit.com/da-nrw/DNSCore/master/ContentBroker/src/test/resources/at/ATTimeBasedPublAllPublic_id.pack_1.tar) 

## Darüberhinaus sollten mittels händischer Tests die zeitgesteuerte Veröffentlichung überprüft werden: 

#### Vorbedingungen:

* Der User verfügt über SIPBuilder.
* Der User hat einen Account und ist unter der Rolle "Contractor" eingeloggt in der DA-WEB.
* Der User hat einen Webshare mit Incoming Order, in den er Pakete legen kann. DA-WEB zeigt den Inhalt dieses Ordners in der Maske&nbsp;"Verarbeitung für abgelieferte SIP starten" an.

#### Ablauf

1. Die Datei [ATTimeBasedPublAllPublicManuell.bmp](https://cdn.rawgit.com/da-nrw/DNSCore/master/ContentBroker/src/test/resources/at/ATTimeBasedPublAllPublicManuell.bmp) herunterladen.
2. Mittels SIPBuilder mehrere Test-Pakete aufbauen. Dabei jeweils unterschiedliche Veröffentlichungsdaten definieren.
3. Die Tespakete im Incoming Ordner unter *jeweils eindeutigen* Namen ablegen und die Verarbeitung starten (Maske "Verarbeitung für abgelieferte SIP starten")
4. Warten auf die Bestätigungsmail für die Objekte.
5. Einsichtnahme in die DA-WEB

#### Akzeptanzkriterien:
1. Die Testpakete mit Veröffentlichungsdatum nicht in der Zukunft sollten veröffenlicht worden sein.
Ein Publikations Icon ist vorhanden und nach anklicken landedt man in Fedora;
    
2. Die Testpakete mit Veröffentlichungsdatum in der Zukunft sollten nicht veröffenlicht worden sein.

3. Einsichtnahme in die DA-WEB nachdem das Veröffentlichungsdatum erreicht wurde (z.B. ein Tag nach Einlieferung) zeigt eine erfolgreiche Veröffentlichung an.





