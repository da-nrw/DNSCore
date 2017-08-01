# Mapping von METS / Mods zu EDM

Da auch Elternelemente in den XML-basierten Metadatenschemata relevant sein können, werden die Mappings werden in der Punktnotation dargestellt


## Mapping für Titel im Portal:

### Mods

* mods.titleInfo.title [1]
* mods.titleInfo.subTitle [2]
* mods.titleInfo.nonSort [3]
* mods.titleInfo.displayLabel


### Mapping zu EDM / Index

* dc.title

### Bemerkungen:  
Regeln für das Mergen der Felder

* dc.title = [1] + [2]  ; wenn beide vorhanden.
* dc.titel = [3] + [1]  ; wenn beide vorhanden.


### Status
Umgesetzt in Build 1856

## Mapping auf Person(en) / Institution(en) im Portal

### Mods

* mods.name.namePart wenn role.roleTerm(type=code) == aut oder cre [1]
* mods.name.namePart.role.roleTerm(type=text) [2]


### Mapping zu EDM / Index

* dc.creator

### Bemerkung:  

Regeln für das Mergen der Felder

* dc.creator = [2] + ", " + [1]  ; wenn beide vorhanden.

### Status
Umgesetzt in Build 1856


## Mapping auf Person(en) / Institution(en) im Portal

### Mods

* mods.name.namePart wenn role.roleTerm(type=code) != aut oder cre [1]
* mods.name.namePart.role.roleTerm(type=text) [2]


### Mapping zu EDM / Index

* dc.contributor

### Bemerkung:  

Regeln für das Mergen der Felder

* dc.contributor = [2] + ", " + [1]  ; wenn beide vorhanden.

### Status
Umgesetzt in Build 1856

## Mapping auf *Erschienen* im Portal

### Mods

Wenn
* mods.originInfo.edition != "[Electronic ed.]"

dann

* mods.originInfo.publisher [1]
* mods.originInfo.place.placeTerm(type=text) [2]
* mods.originInfo.dateIssued [3]


### Mapping zu EDM / Index

* dc.publisher [1] und [2]
* dcterms.issued [3]

### Bemerkung:  

Regeln für das Mergen der Felder

* dc.publisher = [1] + " (" + [2] + ")"
* dcterms.issued = [3]


### Status
Umgesetzt in Build 1856

## Mapping auf *Elektronische Edition* im Portal

### Mods

Wenn
* mods.originInfo.edition == "[Electronic ed.]"

dann

* mods.originInfo.publisher [1]
* mods.originInfo.place.placeTerm(type=text) [2]
* mods.originInfo.dateIssued [3]


### Mapping zu EDM / Index

* dc.publisher [1] und [2]
* dcterms.created [3]

### Bemerkung:  

Regeln für das Mergen der Felder

* dc.publisher = [1] + " (" + [2] + ")" + ", [Elektr. Ed.]"
* dcterms.created = [3]

Zusatz ", [Elektr. Ed.]" wird benötigt, um Publisher zuordnen zu können. Soll im Portal nicht angezeigt werden.

### Status
[ ] Zusatz offen

## Mapping für edmDataProvider
Wird aktuell im Portal nicht angezeigt. Ggf. für Suche wichtig

### METS / DV

* dv.rights.owner


### Mapping zu EDM / Index

* edm.dataProvider

### Bemerkung:  


### Status
[x] Umgesetzt

## Mapping für Nutzungslizenz im Portal

### Mods

* mods.accessCondition(type=use and reproduction) Inhalt des Attributs xlink:href


### Mapping zu EDM / Index

* edm.ProvidedCHO.dc.rights

### Bemerkung:  
Nutr die URL der Lizenz soll übernommen werden!

### Status



