# Mapping von LIDO zu EDM

Da auch Elternelemente in den XML-basierten Metadatenschemata relevant sein können, werden die Mappings werden in der Punktnotation bzw. in Form von jQuery/CSS-Selektoren dargestellt

Die Spalte "Quelle" gibt an, aus welchem Namensraum und Feldern aus dem LIDO die relevante Daten für das Mapping bezogen werden können.
$1 bis $n können, sofern dies erforderlich ist, als Platzhalter für die Merging-Regeln verwendet werden

Die Spalte "Mapping zu EDM" gibt an, wohin die aus der Quelle bezogenen Daten in das EDM geschrieben werden sollen.

Die Spalte "Bemerkung" beschreibt, wie mehrere Felder aus der Quelle in einem oder mehreren EDM-Feldern kombiniert werden.

Die Spalte "Portal" beschreibt, in welchem Feld die Inhalte im Portal eingesehen werden können. Nicht alle verarbeiteten Felder werden in der Portal-Ansicht angezeigt.

Die Spalte "Umgesetzt" beschreibt den Umsetzungssstatus mögliche Ausprägungen ""(Leer), "Vorschlag", "Review durch ULB", "In Umsetzung", "Build XYZ".

Zur besseren Lesbarkeit werden in der folgenden Tabelle die untersten vier Hierarchie-Ebenen der LIDO-Spezifikation wie folgt behandelt:

<ul>
   <li><b>lidoWrap</b> - wird weggelassen, da Root-Element des gesamten Dokuments</li>
   <ul>
      <li><b>lido</b> - wird weggelassen, da Record-Element</li>
      <br />
      <ul>
         <li><b>descriptiveMetadata</b> - wird mit <b>dM</b> abgekürzt</li>
         <ul>
            <li><b>objectClassificationWrap</b> - wird mit <b>oC-W</b> abgekürzt</li>
            <li><b>objectIdentificationWrap</b> - wird mit <b>oI-W</b> abgekürzt</li>
            <li><b>eventWrap</b> - wird mit <b>e-W</b> abgekürzt</li>
            <li><b>objectRelationWrap</b> - wird mit <b>oR-W</b> abgekürzt</li>
            <br />
         </ul>
         <li><b>administrativeMetadata</b> - wird mit <b>aD</b> abgekürzt</li>
         <ul>
            <li><b>rightsWorkWrap</b> - wird mit <b>rW-W</b> abgekürzt</li>
            <li><b>recordWrap</b> - wird mit <b>r-W</b> abgekürzt</li>
            <li><b>resourceWrap</b> - wird mit <b>rsrc-W</b> abgekürzt</li>
         </ul>
      </ul>
   </ul>
</ul>


### Mapping für Titel und Beschreibung im Portal:

<table><thead><tr>
<th><sub>Quelle LIDO</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt</sub></th>
</tr></thead><tbody>
<tr>
<td><sub> dM.oI-W.titleWrap.titleSet.appellationValue </br></sub></td>
<td><sub> edm:ProvidedCHO.dc.title</sub></td>
<td><sub> EDM-Pflichtfeld, wenn keine Beschreibung vorhanden, Pflichtfeld DA-NRW Portal </br></sub></td>
<td><sub>Titel</sub></td>
<td><sub>Build 1856</sub></td>
</tr>
<tr>
<td><sub> dM.oI-W.objectDescriptionWrap.objectDescriptionSet.descriptiveNoteValue </sub></td>
<td><sub>edm.ProvidedCHO.dc.description</sub></td>
<td><sub> EDM-Pflichtfeld, wenn kein Titel vorhanden <br></sub></td>
<td><sub>Beschreibung</sub></td>
<td><sub>Build: 2086</sub></td>
</tr>
</tbody></table>

### Mapping auf Person(en) / Institution(en) im Portal
<table><thead><tr>
<th><sub>Quelle LIDO</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt</sub></th>
</tr></thead><tbody>
<tr>
 <td><sub>
 e-W.eventSet.event.eventActor.displayActorInRole $1 <br>
  </sub></td>
<td><sub> edm:ProvidedCHO.dc.creator</sub></td>
<td><sub> EDM: Empfohlenes Feld. <br />Die Rolle eines Beitragenden existiert in der aktuellen LIDO-Spezifikation bisher nicht. Sollten Personen nicht als Creator definiert werden, müssten dafür noch Erkennungsmerkmale definiert werden. </sub></td>
<td><sub>Person</sub></td>
<td><sub>Build: 2086</sub></td>
</tr>

</tbody></table>


### Mapping für beschreibende Metadaten im Portal:
<table><thead><tr>
<th><sub>Quelle LIDO</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt</sub></th>
</tr></thead><tbody>
<tr>
<td><sub> e-W.eventSet.event.eventDate.displayDate</sub></td>
<td><sub> edm:ProvidedCHO.dc.date</sub></td>
<td><sub> Wenn keine Spezifikation des Datums in den LIDO-Daten, dann dc:date </sub></td>
<td><sub> Zeitangabe <br></sub></td>
<td><sub>Build: 2086</sub></td>
</tr>
   
 
  <tr>
<td><sub> -event/eventPlace/displayPlace<br>
-event/eventPlace/place/namePlaceSet/appellationValue</sub></td>
<td><sub> edm:ProvidedCHO.dcterms.spatial</sub></td>
<td><sub> </sub></td>
<td><sub> </sub></td>
<td><sub>Build: 2086</sub></td>
</tr>
 <tr>
<td><sub>
objectMeasurementsWrap/objectMeasurementsSet<br>/displayObjectMeasurement</sub></td>
<td><sub> edm:ProvidedCHO.dcterms.extend</sub></td>
<td><sub><br> </sub></td>
<td><sub>Umfang</sub></td>
<td><sub>Build: 2086</sub></td>
    </tr>
    <tr>
  <td><sub> mD.e-W.eventSet.event/Wrap/eventSet/event<br>/eventDescriptionSet/descriptiveNoteValue</sub></td>
<td><sub>dcterms:provenance</sub></td>
<td><sub> </sub></td>
<td><sub>Herkunft/Provenienz ???</sub></td>
<td><sub>Build: 1954</sub></td>
  </tr>
  
  <tr>
  <td><sub>aD.rsrc-Wrap.resourceSet.resourceType.term</sub></td>
<td><sub>edm:type</sub></td>
<td><sub> Pflichtfeld </sub></td>
<td><sub>Inhalt wird großgeschrieben: <br>'image'->'IMAGE'</sub></td>
<td><sub>Build: 2086</sub></td>
    </tr>
<td><sub> $1=descriptiveMetadata/objectIdentificationWrap/repositoryWrap/repositorySet/workID

  mit lido:type="inventory number"
<br>
$2=administrativeMetadata/resourceWrap/resourceSet/resourceSource/legalBodyID

  mit lido:source="ISIL"</sub></td>
<td><sub>edm.ProvidedCHO.dc.identifier</sub></td>
<td><sub> der lokale Identifier zumeist mit Isil kombiniert </sub></td>
<td><sub>Identifier</sub></td>
<td><sub>Build: 2086</sub></td>
</tr>

 <tr>
<td><sub>
objectMeasurementsWrap/objectMeasurementsSet<br>/displayObjectMeasurement</sub></td>
<td><sub> edm:ProvidedCHO.dcterms.extent</sub></td>
<td><sub><br> </sub></td>
<td><sub>Umfang</sub></td>
<td><sub><b>Build: 2086</b></sub></td>
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
aM.r-W.recordSource.legalBodyName/appellationValue $1<br>kombiniert mit ISIL<br> administrativeMetadata/recordWrap/recordSource<br />/legalBodyID $2</sub></td>
<td><sub>edm.dataProvider 	</sub></td>
<td><sub>edm.dataProvider=$1 + $2  </sub></td>
<td><sub>Besitzende Institution</sub></td>
<td><sub>Build: 2086</sub></td>
</tr>
  

 <tr><td>
<sub>
</sub>"Digitales Archiv NRW"</td>
<td><sub>edm.provider </sub></td>
<td><sub>Pflichtfeld, Der Repository-Betreiber wird hier als datenprovider aufgefasst</sub></td>
<td><sub></sub></td>
<td><sub><b>Nächstes Build</b></sub></td>
</tr>

<tr>
<td><sub>
aM/r-W/resourseSet<br>/rightsResource/rightsType/conceptID $1 </sub></td>
<td><sub>-edm.ProvidedCHO.dc.rights=$1 <br> -ore:Aggregation/edm:aggregatedCHO<br>/edm.rights[rdf:resource=$1] </sub></td>
<td><sub>Lizenz-URL</sub></td>
<td><sub>Nutzungsrechte</sub></td>
<td><sub>Build 1954</sub></td>
</tr>


</tbody></table>
