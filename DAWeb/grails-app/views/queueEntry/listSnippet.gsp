<script type="text/javascript"><g:if test="${ periodical }">
			// comment out next line to stop periodical updater on page load.		
			$( document ).ready(
					function() {
		//			startUpdater()
					});
			</g:if>
</script>
<table>
	<thead>
		<tr>
			
			<th class="sortable field-id">
				<a href="#" onClick="return sortQueue('obj.identifier');">${message(code: 'obj.identifier.label', default: 'Identifier')}</a>
			</th>
			<th class="sortable field-status">
				<a href="#" onClick="return sortQueue('status');">${message(code: 'queueEntry.status.label', default: 'Information (Status)')}</a>
			</th>
			
			<th class="sortable field-urn">
				<a href="#" onClick="return sortQueue('urn');">${message(code: 'queueEntry.obj.urn', default: 'URN')}</a>
			</th>
			
			<th class="sortable field-created">
				<a href="#" onClick="return sortQueue('created');">${message(code: 'queueEntry.created.label', default: 'Erstellt')}</a>
			</th>
			
			<th class="sortable field-modified">
				<a href="#" onClick="return sortQueue('modified');">${message(code: 'queueEntry.modified.label', default: 'Geändert')}</a>
			</th>
			
			<th class="sortable field-origName">
				<a href="#" onClick="return sortQueue('origName');">${message(code: 'queueEntry.obj.origName.label', default: 'Orig. Name')}</a>
			</th>

		</tr>
	</thead>
	<tbody>
		<g:each in="${queueEntryInstanceList}" status="i" var="queueEntryInstance">
			<g:set var="statusType" value="status-type-${queueEntryInstance.status[-1]}" />
			<tr class="${ ((i % 2) == 0 ? 'odd' : 'even') + ' ' + statusType}">

				<td>
					<g:link action="show" id="${queueEntryInstance.id}"><g:if test="${queueEntryInstance.obj != null}">
						${fieldValue(bean: queueEntryInstance.obj, field: "identifier")}
					</g:if>
						</g:link>
				</td>
				<td>
						${queueEntryInstance.getInformation()}
				</td>
				<td>
					${fieldValue(bean: queueEntryInstance.obj, field: "urn")}
				</td>
				<td>
					${queueEntryInstance.getFormattedCreatedDate()}
				</td>

				<td>
					${queueEntryInstance.getFormattedModifiedDate()}
				</td>
				<td>
					${fieldValue(bean: queueEntryInstance.obj, field: "origName")}
				</td>

			</tr>
		</g:each>
		<g:if test="${queueEntryInstanceList == null || queueEntryInstanceList.isEmpty()}">
			<tr class="even">
				<td colspan="8"><i>No objects in queue ...</i></td>
			</tr>
		</g:if>
	</tbody>
</table>
(Standard view)