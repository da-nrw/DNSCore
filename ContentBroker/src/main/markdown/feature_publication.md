# Leistungsmerkmal: Publikation

## Szenario AT-P-1

#### Kontext:

* [ATUseCaseTimeBasedPublication](../../src/test/java/de/uzk/hki/da/at/ATUseCaseTimeBasedPublication.java#testPublishInstOnly)
* [ATUseCaseTimeBasedPublication](../../src/test/java/de/uzk/hki/da/at/ATUseCaseTimeBasedPublication.java#testNoPubWithLawSetForAudiencePublic)
* [ATUseCaseTimeBasedPublication](../../src/test/java/de/uzk/hki/da/at/ATUseCaseTimeBasedPublication.java#testNoPubWithStartDateSet)
* [ATUseCaseTimeBasedPublication](../../src/test/java/de/uzk/hki/da/at/ATUseCaseTimeBasedPublication.java#testPublishNothing)
* [ATUseCaseTimeBasedPublication](../../src/test/java/de/uzk/hki/da/at/ATUseCaseTimeBasedPublication.java#testPublishAll)

#### Vorbedingungen:

* Der User hat einen Account und ist unter der Rolle "Contractor" eingeloggt in der DA-WEB.
* Der User hat einen Webshare mit Incoming Order, in den er Pakete legen kann. DA-WEB zeigt den Inhalt dieses Ordners in der Maske&nbsp;"[Verarbeitung für abgelieferte SIP starten|https://da-nrw-q.lvr.de/daweb3/incoming/index]" an.

#### Testpaket(e):

* ATUCTimeBasedPublInstOnly
* ATUCTimeBasedPublNoPubWithLawSet
* ATUCTimeBasedPublNoPubWithStartDateSet
* ATUCTimeBasedPublPublishNothing
* ATUCTimeBasedPublAllPublic

#### Ablauf

1. Alle Tespakete werden im Incoming Order unter *jeweils eindeutigen* Namen abgelegt und die Verarbeitung gestartet (Maske "[Verarbeitung für abgelieferte SIP starten|https://da-nrw-q.lvr.de/daweb3/incoming/index]")
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

####

* &nbsp;

##
