# Mapping von LIDO zu EDM

Da auch Elternelemente in den XML-basierten Metadatenschemata relevant sein können, werden die Mappings werden in der Punktnotation bzw. in Form von jQuery/CSS-Selektoren dargestellt

Die Spalte "Quelle" gibt an, aus welchem Namensraum und Feldern aus dem LIDO die relevante Daten für das Mapping bezogen werden können.
$1 bis $n können als Platzhalter für die Merging-Regeln verwendet werden.

Die Spalte "Mapping zu EDM" gibt an, wohin die aus der Quelle bezogenen Daten in das EDM geschrieben werden sollen.

Die Spalte "Bemerkung" beschreibt, wie mehrere Felder aus der Quelle in einem oder mehreren EDM-Feldern kombiniert werden.

Die Spalte "Portal" beschreibt, in welchem Feld die inhalte im Portal eingesehen werden können. Nicht alle verarbeiteten Felder werden in der Portal-Ansicht angezeigt.

Die Spalte "Umgesetzt" beschreibt den Umsetzungssstatus mögliche Ausprägungen ""(Leer), "Vorschlag", "Review durch ULB", "In Umsetzung", "Build XYZ".


### Mapping für Titel im Portal:

<table><thead><tr>
<th><sub>Quelle LIDO</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt</sub></th>
</tr></thead><tbody>
<tr>
<td><sub> titleWrap/titleSet/appellationValue $1<br> </sub></td>
<td><sub>dc.title</sub></td>
<td><sub> dc:title = $1  <br></sub></td>
<td><sub>Titel</sub></td>
<td><sub>Build 1856</sub></td>
</tr>
<tr>
<td><sub> eventSet/event/eventDate $1<br> </sub></td>
<td><sub>dc:date</sub></td>
<td><sub> dc:date = $1  <br></sub></td>
<td><sub>Jahr</sub></td>
<td><sub>Build 1856</sub></td>
</tr>
<tr>
<td><sub> -event/eventPlace/displayPlace $1<br>
-event/eventPlace/place/namePlaceSet $2</sub></td>
<td><sub>dcterms:spatial</sub></td>
<td><sub> kombiniert mit Jahr <br>  <br></sub></td>
<td><sub>Ort/Verlag</sub></td>
<td><sub></sub></td>
</tr>
</tbody></table>



### Mapping für Objektbeziehnungen
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt</sub></th>
</tr></thead><tbody><tr>
<td><sub>
</sub></td>
<td><sub>dcterms:hasPart
</sub></td>
<td><sub>Zeigt ggf. auf die untergeordneten Objekte</sub></td>
<td><sub>Abhängige Objekte</sub></td>
<td><sub>Build: 1856</sub></td>
</tr>
<tr>
<td><sub>
</sub></td>
<td><sub>dcterms: isPartOf
</sub></td>
<td><sub>Zeigt ggf. auf das übergeordnete Objekt</sub></td>
<td><sub>Verweis</sub></td>
<td><sub>Build: 1856</sub></td>
</tr>
</tbody></table>



### Mapping für Thumbnail
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt</sub></th>
</tr></thead><tbody><tr>
<td><sub>FLocat href
</sub></td>
<td><sub>edm:isShownBy  <br>
edm:object  <br>
edm:hasView
</sub></td>
<td><sub>Wird nur befüllt, wenn das Objekt mehrere Referenzen auf Digitalisate enthält.</sub></td>
<td><sub>Thumbnail</sub></td>
<td><sub>Build: 1856</sub></td>
</tr>

</tbody></table>

### Mapping der Rechte/Lizenzen
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt</sub></th>
</tr></thead><tbody>
<tr>
<td>
<sub>
resourceWrap/ResourceSet/rightsResource/rightsHolder/legalBodyName/AppellationValue </sub></td>
<td><sub>edm.dataProvider </sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
</tr>
<tr>
<td><sub>
resourceWrap/ResourseSet/rightsResource/rightsType/conceptID </sub></td>
<td><sub>edm.ProvidedCHO.dc.rights  </sub></td>
<td><sub></sub></td>
<td><sub>Nutzungsrechte</sub></td>
<td><sub>Build 1954</sub></td>
</tr>

</tbody></table>
