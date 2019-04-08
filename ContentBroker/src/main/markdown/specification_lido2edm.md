# Mapping von LIDO zu EDM

Da auch Elternelemente in den XML-basierten Metadatenschemata relevant sein können, werden die Mappings werden in der Punktnotation bzw. in Form von jQuery/CSS-Selektoren dargestellt

Die Spalte "Quelle" gibt an, aus welchem Namensraum und Feldern aus dem LIDO die relevante Daten für das Mapping bezogen werden können.
$1 bis $n können, sofern dies erforderlich ist, als Platzhalter für die Merging-Regeln verwendet werden

Die Spalte "Mapping zu EDM" gibt an, wohin die aus der Quelle bezogenen Daten in das EDM geschrieben werden sollen.

Die Spalte "Bemerkung" beschreibt, wie mehrere Felder aus der Quelle in einem oder mehreren EDM-Feldern kombiniert werden.

Die Spalte "Portal" beschreibt, in welchem Feld die inhalte im Portal eingesehen werden können. Nicht alle verarbeiteten Felder werden in der Portal-Ansicht angezeigt.

Die Spalte "Umgesetzt" beschreibt den Umsetzungssstatus mögliche Ausprägungen ""(Leer), "Vorschlag", "Review durch ULB", "In Umsetzung", "Build XYZ".


### Mapping der Grobbeschreibung im Portal:

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
<td><sub> descriptiveMetadata/objectIdentificationWrap<br>/objectDescriptionWrap<br>/objectDescriptionSet/descriptiveNoteValue $1<br> </sub></td>
<td><sub>dc:description</sub></td>
<td><sub> dc:description = $1  <br></sub></td>
<td><sub>Beschreibung</sub></td>
<td><sub><b>Nächstes Build</b></sub></td>
</tr>
<tr>
<td><sub>repositoryWrap/repositorySet/workID[type="inventory number"]  $1<br> </sub></td>
<td><sub>dc.identifier</sub></td>
<td><sub> dc.identifier = $1  <br></sub></td>
<td><sub> Inventarnummer ???</sub></td>
<td><sub><b>Nächstes Build</b></sub></td>
</tr>
<tr>
<td><sub> eventSet/event/eventDate $1<br> </sub></td>
<td><sub>dc:date</sub></td>
<td><sub> dc:date = $1  <br></sub></td>
<td><sub></sub></td>
<td><sub><b>Nächstes Build</b></sub></td>
</tr>
 <tr>
<td><sub> eventSet/event/eventDate<br> </sub></td>
<td><sub>dcterms:issued</sub></td>
<td><sub>Momentan werden alle Datumsangaben zu dcterms:issued gemappt.</sub></td>
<td><sub>Jahr</sub></td>
<td><sub>Build 1856</sub></td>
</tr>
 <tr>
<td><sub> eventSet/event/eventDate<br> </sub></td>
<td><sub>dcterms:iscreated</sub></td>
<td><sub> 
 Es sollte identifiziert werden welche der Datumsangaben, richtig zu created und issued eingeordnet werden können. <br>Eventl. mithilfe von &lteventType/&gt lösbar. </sub></td>
<td><sub>Jahr</sub></td>
<td><sub>Voschlag</sub></td>
</tr>
<tr>
<td><sub> -event/eventPlace/displayPlace <br>
-event/eventPlace/place/namePlaceSet </sub></td>
<td><sub> dc:publisher</sub></td>
<td><sub>-Portal liest Ort/Verlag aus dem Publisher-Feld aus<br>Jira-Ticket:1587 <br> dc:publisher->DA-NRW-Portal(Erschienen)</sub></td>
<td><sub>Erschienen</sub></td>
<td><sub>Build: 1856</sub></td>
</tr>
  <tr>
<td><sub> -event/eventPlace/displayPlace<br>
-event/eventPlace/place/namePlaceSet</sub></td>
<td><sub>dcterms:spatial <br> (edm:currentLocation)</sub></td>
<td><sub> </sub></td>
<td><sub> </sub></td>
<td><sub><b>Nächstes Build</b></sub></td>
</tr>
 
 
 <tr>
<td><sub>
objectMeasurementsWrap/objectMeasurementsSet<br>/displayObjectMeasurement</sub></td>
<td><sub>dcterms.extend</sub></td>
<td><sub><br> </sub></td>
<td><sub>Umfang</sub></td>
<td><sub><b>Nächstes Build</b></sub></td>
    </tr>
  <tr>
  <td><sub>
administrativeMetadata/resourceWrap/resourceSet<br>/resourceType/term/addedSearchTerm</sub></td>
<td><sub>dc:type<br>edm:type</sub></td>
<td><sub> </sub></td>
<td><sub>Inhalt wird großgeschrieben: <br>'image'->'IMAGE'</sub></td>
<td><sub><b>Nächstes Build</b></sub></td>
    </tr>
  <tr>
  <td><sub>
descriptiveMetadata/eventWrap/eventSet/event<br>/materialsTech/eventMaterialsTech/displayMaterialsTech</sub></td>
<td><sub>dcterms.medium <br></sub></td>
<td><sub> </sub></td>
<td><sub>Material/Technik ???</sub></td>
<td><sub>Vorschlag</sub></td>
  </tr>
  <tr>
  <td><sub>
descriptiveMetadata/eventWrap/eventSet<br>/event/Wrap/eventSet/event<br>/eventDescriptionSet/descriptiveNoteValue</sub></td>
<td><sub>???<br>dcterms:provenance<br> dc:description</sub></td>
<td><sub> </sub></td>
<td><sub>Herkunft/Provenienz ???</sub></td>
<td><sub>Vorschlag</sub></td>
  </tr>
  <tr>
  <td><sub>
objectRelationWrap/subjectSet<br>/subject[type=Stichwort]/subjectConcept</sub></td>
<td><sub>dc:subject<br>skos:prefLabel</sub></td>
<td><sub> </sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
  </tr>
  <tr>
  <td><sub>
objectRelationWrap/subjectSet<br>/subject[type=Schlagwort]/subjectConcept</sub></td>
<td><sub>??? <br>dc:type</sub></td>
<td><sub> </sub></td>
<td><sub></sub></td>
<td><sub></sub></td>

</tr>


</tbody></table>



### Mapping für Objektbeziehungen
<table><thead><tr>
<th><sub>Quelle LIDO</sub></th>
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


### Mapping auf Person(en) / Institution(en) im Portal
<table><thead><tr>
<th><sub>Quelle LIDO</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt</sub></th>
</tr></thead><tbody><tr>
<td><sub>
 eventWrap/eventSet/event/eventActor/displayActorInRole $1 <br>
  </sub></td>
<td><sub>dc:creator</sub></td>
<td><sub>dc:creator = $1 <br>
  Anzeige DA NRW Portal: bisher mit Institution kombiniert
  </sub></td>
<td><sub>Person</sub></td>
<td><sub><b>Nächstes Build</b></sub></td>
  </tr>
  <tr>
 <td><sub>
 eventWrap/eventSet/event/eventActor/actorInRole/actor/nameActorSet/appelationValue $1 <br>
  </sub></td>
<td><sub>dc:contributer</sub></td>
<td><sub>dc:contributer = $1 </sub></td>
<td><sub>Person</sub></td>
<td><sub>Vorschlag </sub></td>
</tr>

</tbody></table>



### Mapping für Institutionstyp
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt</sub></th>
</tr></thead><tbody><tr>
<td><sub>
</sub></td>
<td><sub>@InstitutionType
</sub></td>
<td><sub>-Sofern der Contractor in der 'users'-Tabelle in der  
'provider_type'-Spalte einen nicht leeren String-Wert enthält, wird dieser in den Index als Wert der @institutionType-Variable übernommen.<br>
@InstitutionType=Archiv|Museum|Bibliothek</sub></td>
<td><sub>Für die Einschränkung der Suche auf einen Institutionstyp (Archiv, Museum, Bibliothek ...)</sub></td>
<td><sub>Build: 1954</sub></td>
</tr>
</tbody></table>

### Mapping für Thumbnail
<table><thead><tr>
<th><sub>Quelle LIDO</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt</sub></th>
</tr></thead><tbody><tr>
<td><sub>linkResource
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
<th><sub>Quelle LIDO</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt</sub></th>
</tr></thead><tbody>
<tr>
<td>
<sub>
administrativeMetadata/recordWrap/recordSource<br>/legalBodyName/appellationValue $1<br>kombiniert mit ISIL<br> administrativeMetadata/recordWrap/recordSource<br>/legalBodyID $2</sub></td>
<td><sub>edm.dataProvider 	</sub></td>
<td><sub>Wie kombiniert man?<br>edm.dataProvider=$1 + $2  </sub></td>
<td><sub>Institution ???</sub></td>
<td><sub><b>Nächstes Build</b></sub></td>
</tr>
  
 <tr><td>
<sub>
administrativeMetadata/resourceWrap<br>/resourceSet/rightsResource<br>/rightsHolder/legalBodyName/appellationValue </sub></td>
<td><sub>edm.intermediateProvider </sub></td>
<td><sub></sub></td>
<td><sub>Inhaber Nutzungsrecht ???</sub></td>
<td><sub><b>Nächstes Build</b></sub></td>
</tr>

 <tr><td>
<sub>
</sub>"Digitales Archiv NRW"</td>
<td><sub>edm.provider </sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
<td><sub><b>Nächstes Build</b></sub></td>
</tr>

<tr>
<td><sub>
administrativeMetadata/resourceWrap/resourseSet<br>/rightsResource/rightsType/conceptID $1 </sub></td>
<td><sub>edm.ProvidedCHO.dc.rights=$1 <br> ore:Aggregation/edm:aggregatedCHO<br>/edm.rights[rdf:resource=$1] </sub></td>
<td><sub>Lizenz-URL</sub></td>
<td><sub>Nutzungsrechte</sub></td>
<td><sub>Build 1954</sub></td>
</tr>


</tbody></table>
