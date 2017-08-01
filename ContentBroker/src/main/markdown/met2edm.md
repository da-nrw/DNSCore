# Mapping Tabelle METS/Mods zu EDM

Diese Tabelle enthält die Spezifikationen für das Metadaten-Mapping


| METS/Mods | EDM/Index | Format | Bemerkung | Portal | Status |
| --------- | --------- | ------ | --------- | ------ | ------ |


Teste html


<table cellpadding="2" cellspacing="2">
	<tr>
		<th style="border: none; padding: 0cm">
			<p>MODS 
			</p>
		</th>
		<th style="border: none; padding: 0cm">
			<p>EDM / Index 
			</p>
		</th>
		<th style="border: none; padding: 0cm">
			<p>Format 
			</p>
		</th>
		<th style="border: none; padding: 0cm">
			<p>Bemerkung 
			</p>
		</th>
		<th style="border: none; padding: 0cm">
			<p>Portal 
			</p>
		</th>
		<th style="border: none; padding: 0cm">
			<p>Status 
			</p>
		</th>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>titleInfo <br/>
displayLabel <br/>
subTitle 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:title 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Titel 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>Alle name-Elemente: <br/>
name/role/roleTerm[@type='code'][text()
			= 'cre' or text()='aut'] 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:creator 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>roleTerm/@type=code darf nicht in EDM übernommen werden!
			<br/>
'Wenn roleTerm/@type=text existiert, Inhalt vor den Namen in
			die EDM schreiben. <br/>
&lt;dc:creator&gt;[roleTerm/@type=text]:Johann
			Wolfgang Goethe&lt;/dc:creator&gt; 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Person 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1888</font></p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>Alle name-Elemente 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:contributor 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>roleTerm type=code darf nicht in EDM übernommen werden! <br/>
Wenn
			roleTerm/@type=text existiert, Inhalt vor den Namen in die EDM
			schreiben. <br/>
&lt;dc:contributor&gt;[roleTerm/@type=text]:
			Johann Wolfgang Goethe&lt;/dc:contributor&gt; 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Person 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1888</font></p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>originInfo/publisher 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:publisher 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Verlag 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>place/placeTerm text oder publisher 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:publisher 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Verlag 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>dateIssued (wenn Inhalt von&nbsp;&lt;mods:edition&gt; ungleich
			&quot;[Electronic&nbsp;ed.]&quot;) 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&lt;dcterms:issued&gt; 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>EDM ermöglicht Refinements für spezielle Felder. Hier sollten
			sie anstelle des allgemeinen &lt;dc:date&gt; verwendet werden. 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1888</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>dateIssued (wenn Inhalt von&nbsp;&lt;mods:edition&gt; gleich
			&quot;[Electronic&nbsp;ed.]&quot;) 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&lt;dcterms:created&gt; 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1888.</font>
			<font color="#ff6600">Dabei ist neuer weiterer
			Spezifikationsbedarf aufgefallen (vgl. nächstes Feld).</font></p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>originInfo/publisher und wenn Inhalt von mods:edition unterhalb
			des originInfo 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:publisher Literal des Publishers mit Zusatz <span lang="hi-IN">﻿﻿</span>[elektr.
			Ed.] 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Hiermit soll die Zuordnung eines Publishers zu einer Ausgabe
			ermöglicht werden. EDM unterstützt eine solche Zuordnung über
			weitere Prädikate <b>nicht.</b> 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#ff0000">Vorschlag, muss von ULBs reviewed werden.</font>
						</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>rightsMD/mdWrap/xmlDate/rights/owner 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>edm:dataProvider 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>FLocat href 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>edm:isShownBy <br/>
edm:object <br/>
edm:hasView 
			</p>
		</td>
		<td style="border: none; padding: 0cm"><table cellpadding="2" cellspacing="2">
	<tr>
		<th style="border: none; padding: 0cm">
			<p>MODS 
			</p>
		</th>
		<th style="border: none; padding: 0cm">
			<p>EDM / Index 
			</p>
		</th>
		<th style="border: none; padding: 0cm">
			<p>Format 
			</p>
		</th>
		<th style="border: none; padding: 0cm">
			<p>Bemerkung 
			</p>
		</th>
		<th style="border: none; padding: 0cm">
			<p>Portal 
			</p>
		</th>
		<th style="border: none; padding: 0cm">
			<p>Status 
			</p>
		</th>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>titleInfo <br/>
displayLabel <br/>
subTitle 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:title 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Titel 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>Alle name-Elemente: <br/>
name/role/roleTerm[@type='code'][text()
			= 'cre' or text()='aut'] 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:creator 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>roleTerm/@type=code darf nicht in EDM übernommen werden!
			<br/>
'Wenn roleTerm/@type=text existiert, Inhalt vor den Namen in<table cellpadding="2" cellspacing="2">
	<tr>
		<th style="border: none; padding: 0cm">
			<p>MODS 
			</p>
		</th>
		<th style="border: none; padding: 0cm">
			<p>EDM / Index 
			</p>
		</th>
		<th style="border: none; padding: 0cm">
			<p>Format 
			</p>
		</th>
		<th style="border: none; padding: 0cm">
			<p>Bemerkung 
			</p>
		</th>
		<th style="border: none; padding: 0cm">
			<p>Portal 
			</p>
		</th>
		<th style="border: none; padding: 0cm">
			<p>Status 
			</p>
		</th>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>titleInfo <br/>
displayLabel <br/>
subTitle 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:title 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Titel 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>Alle name-Elemente: <br/>
name/role/roleTerm[@type='code'][text()
			= 'cre' or text()='aut'] 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:creator 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>roleTerm/@type=code darf nicht in EDM übernommen werden!
			<br/>
'Wenn roleTerm/@type=text existiert, Inhalt vor den Namen in
			die EDM schreiben. <br/>
&lt;dc:creator&gt;[roleTerm/@type=text]:Johann
			Wolfgang Goethe&lt;/dc:creator&gt; 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Person 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1888</font></p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>Alle name-Elemente 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:contributor 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>roleTerm type=code darf nicht in EDM übernommen werden! <br/>
Wenn
			roleTerm/@type=text existiert, Inhalt vor den Namen in die EDM
			schreiben. <br/>
&lt;dc:contributor&gt;[roleTerm/@type=text]:
			Johann Wolfgang Goethe&lt;/dc:contributor&gt; 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Person 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1888</font></p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>originInfo/publisher 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:publisher 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Verlag 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>place/placeTerm text oder publisher 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:publisher 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Verlag 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>dateIssued (wenn Inhalt von&nbsp;&lt;mods:edition&gt; ungleich
			&quot;[Electronic&nbsp;ed.]&quot;) 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&lt;dcterms:issued&gt; 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>EDM ermöglicht Refinements für spezielle Felder. Hier sollten
			sie anstelle des allgemeinen &lt;dc:date&gt; verwendet werden. 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1888</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>dateIssued (wenn Inhalt von&nbsp;&lt;mods:edition&gt; gleich
			&quot;[Electronic&nbsp;ed.]&quot;) 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&lt;dcterms:created&gt; 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1888.</font>
			<font color="#ff6600">Dabei ist neuer weiterer
			Spezifikationsbedarf aufgefallen (vgl. nächstes Feld).</font></p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>originInfo/publisher und wenn Inhalt von mods:edition unterhalb
			des originInfo 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:publisher Literal des Publishers mit Zusatz <span lang="hi-IN">﻿﻿</span>[elektr.
			Ed.] 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Hiermit soll die Zuordnung eines Publishers zu einer Ausgabe
			ermöglicht werden. EDM unterstützt eine solche Zuordnung über
			weitere Prädikate <b>nicht.</b> 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#ff0000">Vorschlag, muss von ULBs reviewed werden.</font>
						</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>rightsMD/mdWrap/xmlDate/rights/owner 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>edm:dataProvider 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>FLocat href 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>edm:isShownBy <br/>
edm:object <br/>
edm:hasView 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String <br/>
String <br/>
String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><br/>
<br/>
Wird nur befüllt, wenn das Objekt mehrere
			Referenzen auf Digitalisate enthält. 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><br/>
<br/>
Thumbnail 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dcterms: hasPart 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Zeigt ggf. auf die untergeordneten Objekte 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Abhängige Objekte 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dcterms: isPartOf 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Zeigt ggf. auf das übergeordnete Objekt 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Verweis 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>physicalDescription/extent 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dcterms:extent 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Umfang 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1888</font></p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>originInfo/edition 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:publisher ? 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>eine eigene EDM-Kategorie für edition scheint es nicht zu
			geben 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Ort / Verlag, Jahr 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#ff6600">In Planung</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>mods/accessCondition(type=use and reproduction)xlink:href 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>edm:ProvidedCHO/dc:rights 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String(URL) 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Nur die URL aus dem mods lesen! 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Nutzungsrechte 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#ff0000">In Planung: Wichtig für nächsten Build</font></p>
		</td>
	</tr>
</table>
			die EDM schreiben. <br/>
&lt;dc:creator&gt;[roleTerm/@type=text]:Johann
			Wolfgang Goethe&lt;/dc:creator&gt; 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Person 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1888</font></p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>Alle name-Elemente 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:contributor 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>roleTerm type=code darf nicht in EDM übernommen werden! <br/>
Wenn
			roleTerm/@type=text existiert, Inhalt vor den Namen in die EDM
			schreiben. <br/>
&lt;dc:contributor&gt;[roleTerm/@type=text]:
			Johann Wolfgang Goethe&lt;/dc:contributor&gt; 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Person 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1888</font></p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>originInfo/publisher 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:publisher 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Verlag 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>place/placeTerm text oder publisher 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:publisher 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Verlag 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>dateIssued (wenn Inhalt von&nbsp;&lt;mods:edition&gt; ungleich
			&quot;[Electronic&nbsp;ed.]&quot;) 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&lt;dcterms:issued&gt; 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>EDM ermöglicht Refinements für spezielle Felder. Hier sollten
			sie anstelle des allgemeinen &lt;dc:date&gt; verwendet werden. 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1888</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>dateIssued (wenn Inhalt von&nbsp;&lt;mods:edition&gt; gleich
			&quot;[Electronic&nbsp;ed.]&quot;) 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&lt;dcterms:created&gt; 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1888.</font>
			<font color="#ff6600">Dabei ist neuer weiterer
			Spezifikationsbedarf aufgefallen (vgl. nächstes Feld).</font></p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>originInfo/publisher und wenn Inhalt von mods:edition unterhalb
			des originInfo 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:publisher Literal des Publishers mit Zusatz <span lang="hi-IN">﻿﻿</span>[elektr.
			Ed.] 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Hiermit soll die Zuordnung eines Publishers zu einer Ausgabe
			ermöglicht werden. EDM unterstützt eine solche Zuordnung über
			weitere Prädikate <b>nicht.</b> 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#ff0000">Vorschlag, muss von ULBs reviewed werden.</font>
						</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>rightsMD/mdWrap/xmlDate/rights/owner 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>edm:dataProvider 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>FLocat href 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>edm:isShownBy <br/>
edm:object <br/>
edm:hasView 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String <br/>
String <br/>
String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><br/>
<br/>
Wird nur befüllt, wenn das Objekt mehrere
			Referenzen auf Digitalisate enthält. 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><br/>
<br/>
Thumbnail 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dcterms: hasPart 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Zeigt ggf. auf die untergeordneten Objekte 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Abhängige Objekte 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dcterms: isPartOf 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Zeigt ggf. auf das übergeordnete Objekt 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Verweis 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>physicalDescription/extent 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dcterms:extent 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Umfang 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1888</font></p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>originInfo/edition 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:publisher ? 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>eine eigene EDM-Kategorie für edition scheint es nicht zu
			geben 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Ort / Verlag, Jahr 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#ff6600">In Planung</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>mods/accessCondition(type=use and reproduction)xlink:href 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>edm:ProvidedCHO/dc:rights 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String(URL) 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Nur die URL aus dem mods lesen! 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Nutzungsrechte 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#ff0000">In Planung: Wichtig für nächsten Build</font></p>
		</td>
	</tr>
</table>
			<p>String <br/>
String <br/>
String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><br/>
<br/>
Wird nur befüllt, wenn das Objekt mehrere
			Referenzen auf Digitalisate enthält. 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><br/>
<br/>
Thumbnail 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dcterms: hasPart 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Zeigt ggf. auf die untergeordneten Objekte 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Abhängige Objekte 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dcterms: isPartOf 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Zeigt ggf. auf das übergeordnete Objekt 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Verweis 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1856</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>physicalDescription/extent 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dcterms:extent 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>&nbsp;</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Umfang 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#008000">Umgesetzt für Build&nbsp;1888</font></p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>originInfo/edition 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>dc:publisher ? 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>eine eigene EDM-Kategorie für edition scheint es nicht zu
			geben 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Ort / Verlag, Jahr 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#ff6600">In Planung</font> 
			</p>
		</td>
	</tr>
	<tr>
		<td style="border: none; padding: 0cm">
			<p>mods/accessCondition(type=use and reproduction)xlink:href 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>edm:ProvidedCHO/dc:rights 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>String(URL) 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Nur die URL aus dem mods lesen! 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p>Nutzungsrechte 
			</p>
		</td>
		<td style="border: none; padding: 0cm">
			<p><font color="#ff0000">In Planung: Wichtig für nächsten Build</font></p>
		</td>
	</tr>
</table>
