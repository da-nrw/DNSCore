# Leistungsmerkmal: Archivierung von Multipage-Tiffs  

## Senario: AT-MPT-1: Multipage-Tiffs werden vollständig archiviert, die erste Seite publiziert  

## Hintergrund:

Ein sogenanntes Multipage-Tiff enthält mehrere Bilder, von denen aber nur das erste zur Publikation  aufbereitet wird. 

#### Testpakete:

* ATMultipageTiff.tgz

#### Vorbedingungen:

* User ist unter der Rolle "Contractor" angemeldet/eingeloggt in der "DAWeb"
 
#### Durchführung:

1. Das Paket ATMultipageTiff.tgz wird in den User-Eingangsordner abgelegt.
1. Die Paketverarbeitung wird gestartet über die Maske "Verarbeitung für abgelieferte SIP starten".
1. Warten auf die Email mit dem Einlieferungsbeleg.
1. Der Einlieferungsbeleg muss eine erfolgreiche Archivierung melden. 
1. Folgen des Links in das Fedora. Prüfen, ob zu dem Multipage-Tiff die erste Seite angezeigt wird. 

#### Akzeptanzkriterien:

* Der Einlieferungsbeleg enthält die Information, dass die Archivierung des Objektes erfolgreich war.
* Zu dem Multipage-Tiff wird die erste Seite angezeigt.
