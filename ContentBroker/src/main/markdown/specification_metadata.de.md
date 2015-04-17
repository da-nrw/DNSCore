

## Allgemeine Definition

„Metadaten sind strukturierte Daten zur einheitlichen Beschreibung von Ressourcen jeglicher Art (z. B. Daten, Dokumente, Personen, Gemälde, Orte, Gebäude, Konzepte)“ .  Sie unterstützen die Recherche der beschriebenen Daten auf dem Dateisystem, in Datenbanken sowie im World Wide Web. Aufgrund der Heterogenität der Primärdaten an sich sowie der Vielfältigkeit der verschiedenen Kontexte, in denen diese Daten verwendet werden, existieren unterschiedliche Arten, Formate und Standards der Metadaten.
Unabhängig vom jeweiligen Metadatenformat werden inhaltlich gesehen folgende Arten von Metadaten unterschieden:
* Deskriptive Metadaten dienen der Beschreibung von allen für die Recherche relevanten Informationen wie beispielsweise Titel, Autor und Format einer Ressource.
* Strukturelle Metadaten bilden die Dokumentenstruktur ab und zeigen Beziehungen zwischen Ressourcen auf.
* Administrative Metadaten liefern Informationen über die Herkunft sowie die verschiedenen Stationen der Verarbeitung der Ressource wie etwa Archivierung, Konvertierung etc.
* Technische Metadaten enthalten Informationen über die technischen Parameter der Ressource wie etwa Dateityp, Dateigröße und Auflösung.

## Akzeptierte Metadatenformate
Im Rahmen des DA-NRW werden derzeit vier Metadatenformate akzeptiert:

* EAD
* METS
* LIDO
* XMP

Die genannten Formate enthalten jeweils alle vier Arten von Metadaten. 
Lebensweg der Metadaten im DNSCore
Abhängig von der Verarbeitung der Primärdaten ändern sich mit diesen auch die entsprechenden Metadaten. Dabei werden in DNS der strukturelle sowie der technische Teil der jeweiligen Metadaten aktualisiert. Der von den Archivaren, Bibliothekaren etc. erfasste deskriptive Teil der Metadaten bleibt unverändert. Die Administrativen Metadaten werden für das gesamte Paket in der PREMIS.xml zusammengetragen.

### Langzeitarchivierung

Für die Metadaten gilt im DNSCore dasselbe Prinzip wie für die Primärobjekte: Jede einzelne Datei wird zunächst auf Byte-Ebene gesichert. Darüber hinaus wird der strukturelle sowie der technische Teil der Metadaten ggf. an die Veränderungen der beschriebenen Primärdatei bzw. Primärdateien angepasst.  Mit anderen Worten: Bei Migration der Primärdatei in ein langzeitsicheres Format wird in der entsprechenden Metadatendatei der Referenzpfand sowie die Angabe des Formats aktualisiert, sodass  die Metadatendatei stets eine gültige Beschreibung der Primärdatei bleibt.

### Präsentation

Für die Präsentation im Portal werden die Primärdaten aus dem langzeitsicheren Dateiformat in das dafür jeweils festgelegte Präsentationsformat konvertiert. Sowohl die Primärdateien als auch die entsprechenden Metadaten erhalten eine DA-NRW interne URL. Daher ist eine erneute Anpassung der Metadaten unerlässlich. Im strukturellen Teil der Metadatendateien wird also der relative Pfad auf dem Dateisystem durch die generierte URL ersetzt. Dabei enthält die URL selbstverständlich die aktualisierte – dem Zielformat für die Repräsentation entsprechende – Dateiendung. 

## Anforderungen an die Metadaten im DNSCore
Im Kontext des DA-NRW sowie der Langzeitarchivierung im Allgemeinen gibt es eine zentrale Regel, die stets eingehalten werden muss:    

**SIP-Pakete müssen in sich konsistent sein.**

Für die Struktur der Metadaten hat diese Regel wenige einfache Konsequenzen: 

1.	Metadaten dürfen ausschließlich die im SIP mitgelieferten Primärdaten referenzieren.
Diese Forderung ist in keinster Weise DNSCore-spezifisch, sondern ist Bestandteil der oben angeführten allgemeinen Definition des Begriffs Metadaten. Mit anderen Worten bedeutet der Terminus Metadaten, auch Daten über Daten genannt, nichts anderes, als dass die Metadaten die referenzierten Primärdaten lediglich begleiten und beschreiben. Liefert man nun nur die Beschreibung ohne das beschriebene Digitalisat, enthält das Paket Informationen, die nicht zugeordnet werden können. Damit ist ein solches Paket nicht konsistent und wird aus diesem Grund von DNSCore abgelehnt.
2. Alle in der Metadatendatei enthaltenen Referenzen auf die Primärdaten müssen relativ ab der Metadatendatei angegeben werden.
Die Forderung der Konsistenz der Metadaten beinhaltet die genaue und vor allem eindeutige Referenzierung der Primärdaten. Dies kann leicht erreicht werden, indem der Speicherort der Primärdaten im SIP stets relativ von der Metadatendatei angegeben wird. 
3.	Die Metadatendatei muss auf der obersten Dateiebene  des SIP liegen.
Auf diese Weise kann sichergestellt werden, dass die Metadatendatei von DNS als solche erkannt wird. Darüber hinaus wird so die Angabe relativer Pfade in der Metadatendatei sehr übersichtlich. 
Nur die SIP-Pakete, die alle oben genannten Forderungen erfüllen, können im DNS ordnungsgemäß verarbeitet werden. 

### Verbreitete Abweichungen:

Oft werden Metadaten eingeliefert, in denen die Primärdaten mittels URLs (http://...) referenziert werden. Unabhängig davon, ob die eigentlichen Primärdaten ganz ausgelassen oder doch mitgeliefert werden, sind die entsprechenden SIP-Pakete nicht konsistent und werden von DNS abgelehnt. Im ersten Fall enthält das SIP-Paket Beschreibungen von nicht vorhandenen Daten. Dies macht per Definition keinen Sinn. Im zweiten Fall werden zwar sowohl Primärdateien als auch Metadaten eingeliefert, aber nur die Primärdaten können verarbeitet werden. Die mitgelieferten Metadaten können aufgrund fehlender Referenzen auf „echte“, sich im Paket unter angegebenem Pfad befindlichen Primärdaten nicht aktualisiert werden. 

### Validierung der Metadaten in DNS 

Jedes SIP, das in das DNS eingeliefert wird, durchläuft eine Validierung seiner Metadaten. Dabei wird in der Metadatendatei  jede einzelne Referenz auf eine Primärdatei  auf die tatsächliche Existenz der jeweils referenzierten Primärdatei geprüft. Sollte auch nur eine einzige Datei unter dem angegebenen Pfad nicht zu finden sein, wird das gesamte SIP als inkonsistent abgelehnt.




