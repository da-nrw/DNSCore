# Leistungsmerkmal: Publikation Delta

Es gilt die Regel, das bei mehreren aufeinander folgenden Delta-Einlieferungen die Rechte des Contracts des zuletzt eingelieferten SIPs immer für das gesamte Objekt gelten. Streng genommen kann der Delta-Mechanismus so auch genutzt werden, um nur eine Contract-Datei nachzuliefern (leeres SIP, abgesehen von der premis.xml).&nbsp;


## Szenario AT-PD-1

#### Kontext:

* Dokumentation?

#### Vorbedingungen:

* Der User hat einen Account und ist unter der Rolle "Contractor" eingeloggt in der DA-WEB.
* Der User hat einen Webshare mit Incoming Order, in den er Pakete legen kann. DA-WEB zeigt den Inhalt dieses Ordners in der Maske&nbsp;"[Verarbeitung für abgelieferte SIP starten|https://da-nrw-q.lvr.de/daweb3/incoming/index]" an.

#### Testpaket(e):

* ATContractRightDeltas1.tgz 
```
data/premis.xml (keine Publikationsbeschränkungen für public)
data/Wikimedia Commons - Achillea_millefolium,_Gemeine_Schafgarbe.jpg (Auflösung 640x853px)
``` 

* ATContractRightDeltas3.tgz
```
data/premis.xml (Publikationsbeschränkungen: Öffentlichkeit, mittlere Auflösung).
``` 

* ATContractRightDeltas3.tgz
```
data/premis.xml (Publikationsbeschränkungen: Publikation für Öffentlichkeit nicht erlaubt).
```

#### Ablauf

1. Das erste Tespaket wird im Incoming Order abelegt und die Verarbeitung gestartet (Maske "[Verarbeitung für abgelieferte SIP starten|https://da-nrw-q.lvr.de/daweb3/incoming/index]")
1. Warten auf die Bestätigungsmail.
1. Recherchieren des Objektes in der Ansicht "eingelieferte Objekte".
1. Folgen des Links in das Fedora. Prüfen der Ergebnisse.
1. Das zweite Tespaket wird im Incoming Order abelegt und die Verarbeitung gestartet (Maske "[Verarbeitung für abgelieferte SIP starten|https://da-nrw-q.lvr.de/daweb3/incoming/index]")
1. Warten auf die Bestätigungsmail.
1. Im Fedora. Erneutes Prüfen der Ergebnisse.
1. Das dritte Tespaket wird im Incoming Order abelegt und die Verarbeitung gestartet (Maske "[Verarbeitung für abgelieferte SIP starten|https://da-nrw-q.lvr.de/daweb3/incoming/index]")
1. Warten auf die Bestätigungsmail.
1. Im Fedora. Erneutes Prüfen der Ergebnisse.

h4. Akzeptanzkriterien

* Nach der ersten Einlieferung: Im Presentation Repository: JPEG mit voller Auflösung (ca. 640x853px) vorhanden.
* In der Objektansicht muss das Publikationsflag gesetzt sein.
* Nach der zweiten Einlieferung: Objekt im PR vorhanden, ein JPEG mit begrenzter Auflösung (ca. 270x360px) vorhanden.
* In der Objektansicht muss das Publikationsflag gestetzt sein.
* Nach der dritten Einlieferung: Objekt im PR nicht mehr vorhanden
* In der Objektansicht darf das Publikationsfloag nicht gesetzt sein.

## Status und offene Punkte:


####

* Status hinsichtlich der Automatisierung ermitteln.
* Dokumentiert wo?
* Wie kommt bei "Auflösung mittel" die Auflösung 270x360px zustande? Ist das in Ordnung?
* ATUseCaseIngestDeltaContract#test
* TODO überprüfung der Auflösungsbeschränkung noch nicht implementiert.
