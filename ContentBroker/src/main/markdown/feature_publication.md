# Leistungsmerkmal: Publikation

## Szenario AT-P-1

#### Kontext:

* [ATUseCaseTimeBasedPublication  testPublishInstOnly](../../test/java/de/uzk/hki/da/at/ATTimeBasedPublication.java#testPublishInstOnly#testPublishInstOnly)
* [ATUseCaseTimeBasedPublication  testNoPubWithLawSetForAudiencePublic](../../test/java/de/uzk/hki/da/at/ATTimeBasedPublication.java#testNoPubWithLawSetForAudiencePublic)
* [ATUseCaseTimeBasedPublication testNoPubWithStartDateSet](../../test/java/de/uzk/hki/da/at/ATTimeBasedPublication.java#testNoPubWithStartDateSet)
* [ATUseCaseTimeBasedPublication testPublishNothing](../../test/java/de/uzk/hki/da/at/ATTimeBasedPublication.java#testPublishNothing)
* [ATUseCaseTimeBasedPublication testPublishAll](../../test/java/de/uzk/hki/da/at/ATTimeBasedPublication.java#testPublishAll)

#### Vorbedingungen:

* Der User hat einen Account und ist unter der Rolle "Contractor" eingeloggt in der DA-WEB.
* Der User hat einen Webshare mit Incoming Order, in den er Pakete legen kann. DA-WEB zeigt den Inhalt dieses Ordners in der Maske&nbsp;"Verarbeitung für abgelieferte SIP starten" an.

#### Testpaket(e):

[ATUCTimeBasedPublInstOnly.tgz](https://cdn.rawgit.com/da-nrw/DNSCore/master/ContentBroker/src/test/resources/at/ATUCTimeBasedPublInstOnly.tgz) 

[ATUCTimeBasedPublNoPubWithLawSet.tgz](https://cdn.rawgit.com/da-nrw/DNSCore/master/ContentBroker/src/test/resources/at/ATUCTimeBasedPublNoPubWithLawSet.tgz) 

[ATUCTimeBasedPublNoPubWithStartDateSet.tgz](https://cdn.rawgit.com/da-nrw/DNSCore/master/ContentBroker/src/test/resources/at/ATUCTimeBasedPublNoPubWithStartDateSet.tgz) 

[ATUCTimeBasedPublPublishNothing.tgz](https://cdn.rawgit.com/da-nrw/DNSCore/master/ContentBroker/src/test/resources/at/ATUCTimeBasedPublNoPubWithStartDateSet.tgz) 

[ATUCTimeBasedPublAllPublic.tgz](https://cdn.rawgit.com/da-nrw/DNSCore/master/ContentBroker/src/test/resources/at/ATUCTimeBasedPublAllPublic.tgz) 

#### Ablauf

1. Alle Tespakete werden im Incoming Order unter *jeweils eindeutigen* Namen abgelegt und die Verarbeitung gestartet (Maske "Verarbeitung für abgelieferte SIP starten")
1. Warten auf die Bestätigungsmail für alle Objekte.
1. Einsichtnahme in die DA-WEB

#### Akzeptanzkriterien



| Paketname | Publikations Icon *vorhanden.* | Link "öffentliche Derivate" vorhanden |
|----|---------------------|--------------------------|
| ATUCTimeBasedPublInstOnly | Nein. | Nein. |
| ATUCTimeBasedPublNoPubWithLawSet| Nein. | Nein. |
| ATUCTimeBasedPublNoPubWithStartDateSet | Nein. | Nein. |
| ATUCTimeBasedPublPublishNothing | Nein. | Nein. |
| ATUCTimeBasedPublAllPublic | Ja. | Ja. Bei Klick auf das Icon landet man im Fedora  |

## Status und offene Punkte:

