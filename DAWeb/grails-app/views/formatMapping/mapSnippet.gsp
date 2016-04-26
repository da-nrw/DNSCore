<div class="list" id="entry-list"> <br>
			<g:formRemote name="myForm" url="[controller: 'formatMapping', action:'deleteAndFill']"  update="refreshView" >
					<g:actionSubmit value="Tabelle leeren und neu laden" action="deleteAndFill"  onclick="return confirm('Tabelle wirklich aktualisieren?')"/>
			</g:formRemote>
		</div> <br>
<table>
	 <thead>							
			<tr>
				<g:sortableColumn property="puid" title="${message(code: 'formatMapping.puid', default: 'PUID')}" />
				<g:sortableColumn property="extension" title="${message(code: 'formatMapping.extension', default: 'Erweiterung')}" />
				<g:sortableColumn property="mimeType" title="${message(code: 'formatMapping.mimeType', default: 'MIME-Type')}" />
				<g:sortableColumn property="formatName" title="${message(code: 'formatMapping.formatName', default: 'Bezeichnung')}" />
			</tr>
	</thead>
	<tbody>	
		<g:each in="${formatMappingSnFind}" var="formatMappingSn" status="i">
       		<tr class="${ ((i % 2) == 0 ? 'odd' : 'even') }">
       			<td>${formatMappingSn.puid}</td>
       			<td>${formatMappingSn.extension}</td>
       			<td>${formatMappingSn.mimeType}</td>
       			<td>${formatMappingSn.formatName}</td>
			</tr>
		</g:each>
	</tbody>
</table>
