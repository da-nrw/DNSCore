<script type="text/javascript">
	<g:if test="${ periodical }">
			// comment out next line to stop periodical updater on page load.		
			$( document ).ready(
					function() {
		//			startUpdater()
					});
	</g:if>
</script>
<div style="overflow:auto; height: float; margin-top: 10px">
	<g:if test="${flash.message}">
		<div class="message" role="status">${flash.message}</div>
	</g:if><br>
<table>
	<thead class="thead-line">
		<tr>
			
			<th class="sortable field-id">
				<a href="#" onClick="return sortQueue('obj.identifier');">${message(code: 'obj.identifier.label', default: 'Identifier')}</a>
			</th>
			<th class="sortable field-status">
				<a href="#" onClick="return sortQueue('status');">${message(code: 'queueEntry.status.label', default: 'Information (Status)')}</a>
			</th>
			
			<th class="sortable field-urn">
				<a href="#" onClick="return sortQueue('obj.urn');">${message(code: 'queueEntry.obj.urn', default: 'URN')}</a>
			</th>
			
			<th class="sortable field-created">
				<a href="#" onClick="return sortQueue('createdAt');">${message(code: 'queueEntry.created.label', default: 'Erstellt')}</a>
			</th>
			
			<th class="sortable field-modified">
				<a href="#" onClick="return sortQueue('modifiedAt');">${message(code: 'queueEntry.modified.label', default: 'Geändert')}</a>
			</th>
			
			<th class="sortable field-origName">
				<a href="#" onClick="return sortQueue('obj.origName');">${message(code: 'queueEntry.obj.origName.label', default: 'Orig. Name')}</a>
			</th>

		</tr>
	</thead>
	<tbody>
		<g:each in="${queueEntryInstanceList}" status="i" var="queueEntryInstance">
			<g:set var="statusType" value="status-type-${queueEntryInstance.status[-1]}" />
			<!-- <tr class="${ ((i % 2) == 0 ? 'odd' : 'even') + ' ' + statusType}"> -->
				<tr class="${ ((i % 2) == 0 ? 'odd' : 'even')}">
				<td>
					<g:link action="show" id="${queueEntryInstance.id}">
						<g:if test="${queueEntryInstance.obj != null}">
						${fieldValue(bean: queueEntryInstance.obj, field: "identifier")}
						</g:if>
					</g:link>
				</td>
				<td>
					<g:if test="${queueEntryInstance.showTrafficLightYellow()}">
						<asset:image style="width:16px; height:16px" src="/icons/yellow-gr.png" title="${queueEntryInstance.getInformation()}" alt="${queueEntryInstance.getInformation()}"/>
					</g:if>
					<g:elseif test="${queueEntryInstance.showTrafficLightRed()}">
						<asset:image style="width:16px; height:16px" src="/icons/red-gr.png" title="${queueEntryInstance.getInformation()}" alt="${queueEntryInstance.getInformation()}"/>
						${queueEntryInstance.status}
					</g:elseif>
					<g:if test="${queueEntryInstance.showTrafficLightGreen()}">
						<asset:image style="width:16px; height:16px" src="/icons/green-gr.png" title="${queueEntryInstance.getInformation()}" alt="${queueEntryInstance.getInformation()}"/>
					</g:if>
					<g:if test="${ queueEntryInstance.showDeletionButton()}">
						<g:set var="showDeleteAll" value="true" />
						<g:link onclick="return confirm('Eintrag mit ID ${fieldValue(bean: queueEntryInstance.obj, field: "identifier")} wirklich löschen?');" action="queueDelete" id="${queueEntryInstance.id}">
							<asset:image style="width:16px; height:16px" src="/icons/list_remove.png" 
										title="${message(code: 'default.workflow.icon.delete', default: 'Paket löschen')}" 
										alt="${message(code: 'default.workflow.icon.delete', default: 'Paket löschen')}"/>
						</g:link>
					</g:if>
					
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
</div>
(Standard view)