<table>
	<thead>
		<tr>
			
			<th class="sortable field-id">
				<a href="#" onClick="return sortQueue('queueEntry.obj.identifier');">${message(code: 'queueEntry.obj.identifier.label', default: 'Identifier')}</a>
			</th>
			
			<th class="sortable field-status">
				<a href="#" onClick="return sortQueue('status');">${message(code: 'queueEntry.status.label', default: 'Status')}</a>
			</th>
			
			<th class="sortable field-urn">
				<a href="#" onClick="return sortQueue('urn');">${message(code: 'queueEntry.obj.urn', default: 'URN')}</a>
			</th>
			
			<th class="sortable field-contractorShortName">
				<a href="#" onClick="return sortQueue('contractorShortName');">${message(code: 'queueEntry.obj.contractor.shortName.label', default: 'Contractor')}</a>
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
			
			<th class="sortable field-initialNode">
				<a href="#" onClick="return sortQueue('initialNode');">${message(code: 'queueEntry.initialNode.label', default: 'Initialer Knoten')}</a>
			</th>

		</tr>
	</thead>
	<tbody>
		<g:each in="${queueEntryInstanceList}" status="i" var="queueEntryInstance">
			<g:set var="statusType" value="status-type-${queueEntryInstance.status[-1]}" />
			<tr class="${ ((i % 2) == 0 ? 'odd' : 'even') + ' ' + statusType}">

				<td>
					<g:if test="${queueEntryInstance.obj != null}">
						${fieldValue(bean: queueEntryInstance.obj, field: "identifier")}
					</g:if>
				</td>

				<td>
					<g:link action="show" id="${queueEntryInstance.id}">
						${fieldValue(bean: queueEntryInstance, field: "status")}
					</g:link>
					<g:set var="statusType" value="${queueEntryInstance.status[-1]}" />
					<g:if test="${statusType == "1" && admin }">
								<g:link action="queueRetry" id="${queueEntryInstance.id}">
									<g:img style="width:16px; height:16px" uri="/images/icons/exchange32.png"/>
								</g:link>
					</g:if>
					<g:if test="${ statusType == "1" && admin }">
					<g:link action="queueRecover" id="${queueEntryInstance.id}">
									<g:img style="width:16px; height:16px" uri="/images/icons/recover.jpg"/>
					</g:link>
				</g:if>
				</td>

				<td>
					${fieldValue(bean: queueEntryInstance.obj, field: "urn")}
				</td>
				<td>
				<g:if test="${queueEntryInstance.obj != null}">
					${fieldValue(bean: queueEntryInstance.obj.contractor, field: "shortName")}
				</g:if>
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

				<td>
					${fieldValue(bean: queueEntryInstance, field: "initialNode")}
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
