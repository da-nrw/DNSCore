# Mapping von METS / Mods zu EDM

Da auch Elternelemente in den XML-basierten Metadatenschemata relevant sein können, werden die Mappings werden in der Punktnotation bzw. in Form von jQuery/CSS-Selektoren dargestellt


Die Mappings werden so gelesen, dass die Abschnittsüberschrift jeweils sagt, welches Zielfeld im Portal oder für OAI-PMH befüllt werden soll. Der Unterabschnitt **"Quelle"** gibt an, ais welchem Namensraum aus dem Mets und welchen Feldern relevante Daten für das Mapping bezogen werden **können**. 

$1 bis $n sind Platzhalter für die unten folgenden Merging-Regeln.

Der Unterabschnitt **"Mapping zu EDM / Index"** gibt an, wohin die aus der Quelle bezogenen Daten in das EDM geschrieben werden sollen.

Der Unterabschnitt **"Regeln für das Mergen der Felder"** beschreibt, wie mehrere Felder aus der Quelle in einem oder mehreren EDM-Feldern kombiniert werden. 


## Mapping für Titel im Portal:

### Quelle Mods

* mods.titleInfo.title = $1
* mods.titleInfo.subTitle = $2
* mods.titleInfo.nonSort = $3
* mods.titleInfo.displayLabel


### Mapping zu EDM / Index

* dc.title

### Regeln für das Mergen der Felder

* dc.title = $1 + " " + $2  ; wenn beide vorhanden.
* dc.titel = $3 + " " + $1  ; wenn beide vorhanden.
* dc.titel = $3 + " " + $1 + " " + $2  ; wenn drei vorhanden.

### Status
Umgesetzt in Build 1856

### Tabellarische Übersicht

<table>
<thead style="font-weight: 600;">
<tr>
<td>Quelle</td>
<td>Ziel</td>
<td>Regeln</td>
<td>Status</td>
</tr>
</thead>
<tr>
<td>
<ul>
<li>mods.titleInfo.title = $1</li>
<li>mods.titleInfo.subTitle = $2</li>
<li>mods.titleInfo.nonSort = $3</li>
<li>mods.titleInfo.displayLabel</li>
</ul>
</td>
<td>
<ul>
<li>dc.title</li>
</ul>
</td>
<td>
<ul>
<li>dc.title = $1 + " " + $2  ; wenn beide vorhanden.</li>
<li>dc.titel = $3 + " " + $1  ; wenn beide vorhanden.</li>
<li>dc.titel = $3 + " " + $1 + " " + $2  ; wenn drei vorhanden.</li>
</ul>
</td>
<td>Umgesetzt in Build 1856</td>
</tr>
</table>


## Mapping auf Person(en) / Institution(en) im Portal

### Quelle Mods

*Bedingung*

* mods.name.role.roleTerm[type=code] == aut oder cre

*Wenn erfüllt*

* mods.name.namePart = $1
* mods.name.role.roleTerm[type=text] = $2


### Mapping zu EDM / Index

* dc.creator

### Regeln für das Mergen der Felder

* dc.creator = $2 + ", " + $1 ; wenn beide vorhanden.

### Status
Umgesetzt in Build 1856

### Tabellarische Übersicht

<table>
<thead style="font-weight: 600;">
<tr>
<td>Quelle</td>
<td>Ziel</td>
<td>Regeln</td>
<td>Status</td>
</tr>
</thead>
<tr>
<td>
<p><em>Bedingung</em></p>
<ul>
<li>mods.name.role.roleTerm[type=code] == aut oder cre</li>
</ul>
<ul>
<li>mods.name.namePart = $1</li>
<li>mods.name.role.roleTerm[type=text] = $2</li>
</ul>
</td>
<td>
<ul>
<li>dc.creator</li>
</ul>
</td>
<td>
<ul>
<li>dc.creator = $2 + ", " + $1 ; wenn beide vorhanden.</li>
</ul>
</td>
<td>Umgesetzt in Build 1856</td>
</tr>
</table>

## Mapping auf Person(en) / Institution(en) im Portal

### Quelle Mods

*Bedingung*

* mods.name.role.roleTerm[type=code] != aut oder cre

*Wenn erfüllt*

* mods.name.namePart = $1
* mods.name.namePart.role.roleTerm[type=text] = $2


### Mapping zu EDM / Index

* dc.contributor

### Regeln für das Mergen der Felder

* dc.contributor = $2 + ", " + $1  ; wenn beide vorhanden.

### Status
Umgesetzt in Build 1856

### Tabellarische Übersicht

<table>
<thead style="font-weight: 600;">
<tr>
<td>Quelle</td>
<td>Ziel</td>
<td>Regeln</td>
<td>Status</td>
</tr>
</thead>
<tr>
<td>
<p><em>Bedingung</em></p>
<ul>
<li>mods.name.role.roleTerm[type=code] != aut oder cre</li>
</ul>
<ul>
<li>mods.name.namePart = $1</li>
<li>mods.name.role.roleTerm[type=text] = $2</li>
</ul>
</td>
<td>
<ul>
<li>dc.conrtributor</li>
</ul>
</td>
<td>
<ul>
<li>dc.contributor = $2 + ", " + $1 ; wenn beide vorhanden.</li>
</ul>
</td>
<td>Umgesetzt in Build 1856</td>
</tr>
</table>

## Mapping auf *Erschienen* im Portal

### Quelle Mods

*Bedingung*

* mods.originInfo.edition != "[Electronic ed.]"

*Wenn erfüllt*

* mods.originInfo.publisher = $1
* mods.originInfo.place.placeTerm[type=text] = $2
* mods.originInfo.dateIssued = $3


### Mapping zu EDM / Index

* dc.publisher $1 und $2
* dcterms.issued $3

### Regeln für das Mergen der Felder
### Bemerkung:  


* dc.publisher = $1 + " (" + $2 + ")"
* dcterms.issued = $3


### Status
Umgesetzt in Build 1856

### Tabellarische Übersicht

<table>
<thead style="font-weight: 600;">
<tr>
<td>Quelle</td>
<td>Ziel</td>
<td>Regeln</td>
<td>Status</td>
</tr>
</thead>
<tr>
<td>
<p><em>Bedingung</em></p>
<ul>
<li>mods.originInfo.edition != "[Electronic ed.]"</li>
</ul>
<ul>
<li>mods.originInfo.publisher = $1</li>
<li>mods.originInfo.place.placeTerm[type=text] = $2</li>
<li>mods.originInfo.dateIssued = $3</li>
</ul>
</td>
<td>
<ul>
<li>dc.publisher $1 und $2</li>
<li>dcterms.issued = $3</li>
</ul>
</td>
<td>
<ul>
<li>dc.publisher = $1 + " (" + $2 + ")"</li>
<li>dcterms.issued = $3</li>
</ul>
</td>
<td>Umgesetzt in Build 1856</td>
</tr>
</table>

## Mapping auf Elektronische Edition im Portal

### Quelle Mods

*Bedingung*

* mods.originInfo.edition == "[Electronic ed.]"

*Wenn erfüllt*

* mods.originInfo.publisher = $1
* mods.originInfo.place.placeTerm[type=text] = $2
* mods.originInfo.dateIssued = $3


### Mapping zu EDM / Index

* dc.publisher $1 und $2
* dcterms.created $2

### Regeln für das Mergen der Felder

* dc.publisher = $1 + " (" + $2 + ")" + ", [Elektr. Ed.]"
* dcterms.created = $3


### Bemerkung
Zusatz ", [Elektr. Ed.]" wird benötigt, um Publisher zuordnen zu können. Soll im Portal nicht angezeigt werden.

### Status
[ ] Zusatz offen

### Tabellarische Übersicht

<table>
<thead style="font-weight: 600;">
<tr>
<td>Quelle</td>
<td>Ziel</td>
<td>Regeln</td>
<td>Status</td>
</tr>
</thead>
<tr>
<td>
<p><em>Bedingung</em></p>
<ul>
<li>mods.originInfo.edition == "[Electronic ed.]"</li>
</ul>
<ul>
<li>mods.originInfo.publisher = $1</li>
<li>mods.originInfo.place.placeTerm[type=text] = $2</li>
<li>mods.originInfo.dateIssued = $3</li>
</ul>
</td>
<td>
<ul>
<li>dc.publisher $1 und $2</li>
<li>dcterms.issued = $3</li>
</ul>
</td>
<td>
<ul>
<li>dc.publisher = $1 + " (" + $2 + ")" + ", [Elektr. Ed.]"</li>
<li>dcterms.issued = $3</li>
</ul>
<ul>
<li>Zusatz ", [Elektr. Ed.]" wird benötigt, um Publisher zuordnen zu können. Soll im Portal nicht angezeigt werden.</li>
</ul>
</td>
<td>Umgesetzt in Build 1856, [ ] Zusatz offen?</td>
</tr>
</table>

## Mapping auf Umfang im Portal

### Quelle Mods

* mods.physicalDescription.extent = $1
* mods.physicalDescription.note = $2


### Mapping zu EDM / Index

* dcterms.extend

### Regeln für das Mergen der Felder

* dcterms.extend = $1 + ", " + $2


### Status
erledigt Build?

### Tabellarische Übersicht

<table>
<thead style="font-weight: 600;">
<tr>
<td>Quelle</td>
<td>Ziel</td>
<td>Regeln</td>
<td>Status</td>
</tr>
</thead>
<tr>
<td>
<ul>
<li>mods.physicalDescription.extent = $1</li>
<li>mods.physicalDescription.note = $2</li>
</ul>
</td>
<td>
<ul>
<li>dcterms.extend</li>
</ul>
</td>
<td>
<ul>
<li>dcterms.extend = $1 + ", " + $2</li>
</ul>
</td>
<td>Umgesetzt in Build ?</td>
</tr>
</table>




## Mapping für edmDataProvider in OAI-PMH
Wird aktuell im Portal nicht angezeigt. Ggf. für Suche wichtig

###  Quelle DV

* dv.rights.owner


### Mapping zu EDM / Index

* edm.dataProvider

### Status
[x] Umgesetzt

### Tabellarische Übersicht

<table>
<thead style="font-weight: 600;">
<tr>
<td>Quelle</td>
<td>Ziel</td>
<td>Regeln</td>
<td>Status</td>
</tr>
</thead>
<tr>
<td>
<ul>
<li>dv.rights.owner</li>
</ul>
</td>
<td>
<ul>
<li>edm.dataProvider</li>
</ul>
</td>
<td>
</td>
<td>Umgesetzt in Build ?</td>
</tr>
</table>

## Mapping für Nutzungslizenz im Portal

### Quelle Mods

* mods.accessCondition[type="use and reproduction"].attr('xlink:href')


### Mapping zu EDM / Index

* edm.ProvidedCHO.dc.rights
* ore.aggregation.edm.rights

### Bemerkung:  
Nur die URL der Lizenz soll übernommen werden!

### Status
Mapping auf edm.ProvidedCHO.dc.rights umgesetzt, Mapping auf ore.aggregation.edm.rights fehlt, ist verpflichtend für Europeana

### Tabellarische Übersicht

<table>
<thead style="font-weight: 600;">
<tr>
<td>Quelle</td>
<td>Ziel</td>
<td>Regeln</td>
<td>Status</td>
</tr>
</thead>
<tr>
<td>
<ul>
<li>mods.accessCondition[type="use and reproduction"].attr('xlink:href')</li>
</ul>
</td>
<td>
<ul>
<li>edm.ProvidedCHO.dc.rights</li>
<li>ore.aggregation.edm.rights</li>
</ul>
</td>
<td>
</td>
<td>Mapping auf edm.ProvidedCHO.dc.rights umgesetzt, Mapping auf ore.aggregation.edm.rights fehlt, ist verpflichtend für Europeana</td>
</tr>
</table>
