<script type="text/javascript"><g:if test="${ periodical }">
			// comment out next line to stop periodical updater on page load.		
			$( document ).ready(
					function() {
		//			startUpdater()
					});
			</g:if>
</script>
<script language="JavaScript">
function toggle(source) {
	  checkboxes = document.getElementsByName('modifyIds');
	  for(var i in checkboxes)
	    checkboxes[i].checked = source.checked;
}
function subForm(text, action) {
    if (!confirm(text)) return false;
    document.getElementById('mod').action = action;
    document.getElementById('mod').submit();
    return true;
}
</script>
<div style="overflow:auto; height: 600px">
<table>
	<thead>
		<tr><g:if test="${params.search?.status != null && params.search?.status.length()==3}">
			<th>
			Auswahl
			</th></g:if>
			<th class="sortable field-id">
				<a href="#" onClick="return sortQueue('obj.identifier');">${message(code: 'queueEntry.obj.identifier.label', default: 'Identifier')}</a>
			</th>
			<th class="sortable field-status">
				<a href="#" onClick="return sortQueue('status');">${message(code: 'queueEntry.status.label', default: 'Status')}</a>
			</th>
			
			<th class="sortable field-urn">
				${message(code: 'queueEntry.obj.urn', default: 'URN')}
			</th>
			
			<th class="sortable field-contractorShortName">
				<a href="#" onClick="return sortQueue('user.shortName');">${message(code: 'queueEntry.obj.user.shortName.label', default: 'Contractor')}</a>
			</th>
			
			<th class="sortable field-created">
				<a href="#" onClick="return sortQueue('created');">${message(code: 'queueEntry.created.label', default: 'Erstellt')}</a>
			</th>
			
			<th class="sortable field-modified">
				<a href="#" onClick="return sortQueue('modified');">${message(code: 'queueEntry.modified.label', default: 'Geändert')}</a>
			</th>
			
			<th class="sortable field-origName">
				<a href="#" onClick="return sortQueue('obj.origName');">${message(code: 'queueEntry.obj.origName.label', default: 'Orig. Name')}</a>
			</th>
			
			<th class="sortable field-initialNode">
				<a href="#" onClick="return sortQueue('initialNode');">${message(code: 'queueEntry.initialNode.label', default: 'Zuständiger Knoten')}</a>
			</th>

		</tr>
	</thead>
	
	<tbody>
	<form method="POST" id="mod">
		<g:set var="showDeleteAll" value="false" />
		<g:set var="showRecoverAll" value="false" />
		<g:set var="showRetryAll" value="false" />
		<g:each in="${queueEntryInstanceList}" status="i" var="queueEntryInstance">
			<g:set var="statusType" value="status-type-${queueEntryInstance.status[-1]}" />
			<tr class="${ ((i % 2) == 0 ? 'odd' : 'even') + ' ' + statusType}">
				<g:if test="${params.search?.status != null && params.search?.status.length()==3}"><td>
				<g:checkBox name="modifyIds" value="${queueEntryInstance.getId()}" checked="false" />
				</td></g:if>
				<td>
					<g:if test="${queueEntryInstance.obj != null}">
						${fieldValue(bean: queueEntryInstance.obj, field: "identifier")}
					</g:if>
				</td>

				<td>
					<g:link action="show" id="${queueEntryInstance.id}">
						${fieldValue(bean: queueEntryInstance, field: "status")}
					</g:link>
					<g:if test="${queueEntryInstance.showRetryButton()}">
								<g:set var="showRetryAll" value="true" />
								<g:link action="queueRetry" id="${queueEntryInstance.id}">
									<g:img style="width:16px; height:16px" uri="/images/icons/exchange32.png" title="Workflow für Paket neu starten" alt="Workflow für Objekt neu starten"/>
								</g:link>
					</g:if>
					<g:if test="${queueEntryInstance.showRecoverButton() || queueEntryInstance.showRecoverButtonAfterSomeTime() }">
						<g:set var="showRecoverAll" value="true" />
						<g:link action="queueRecover" id="${queueEntryInstance.id}">
							<g:img style="width:16px; height:16px" uri="/images/icons/back-icon.png"
									title="${message(code: 'default.workflow.icon.restart', default: 'Gesamten Workflow für Paket neu starten')}" 
									alt="${message(code: 'default.workflow.icon.restart', default: 'Gesamten Workflow für Paket neu starten')}"/>
					</g:link>
					</g:if> 
					<g:if test="${ queueEntryInstance.showDeletionButton()}">
					<g:set var="showDeleteAll" value="true" />
					<g:link onclick="return confirm('Eintrag löschen. Sind Sie sicher?');" action="queueDelete" id="${queueEntryInstance.id}">
						<g:img style="width:16px; height:16px" uri="/images/icons/list_remove.png" 
									title="${message(code: 'default.workflow.icon.delete', default: 'Paket löschen')}" 
									alt="${message(code: 'default.workflow.icon.delete', default: 'Paket löschen')}"/>
					</g:link>
					</g:if>
				</td>

				<td>
					${fieldValue(bean: queueEntryInstance.obj, field: "urn")}
				</td>
				<td>
				<g:if test="${queueEntryInstance.obj != null}">
					${fieldValue(bean: queueEntryInstance.obj.user, field: "shortName")}
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
		<tr>
		<td colspan="100">
		<g:if test="${params.search?.status != null && params.search?.status.length()==3}">
		<input type="checkbox" onClick="toggle(this)"/>Alle an-/abwählen, für alle Pakete im Status ${params.search?.status}:
			<g:if test="${ showRetryAll == "true"}">
				<a onClick="return subForm('Nur den letzten Arbeitsschritt für alle Pakete wiederholen. Sind Sie sicher?','../queueRetryAll');"><img src="${resource(dir: 'images/icons', file: 'exchange32.png')}" style="width:16px; height:16px" alt="${message(code: 'default.workflow.icon.restart', default: 'Gesamten Workflow für Paket neu starten')}" title="${message(code: 'default.workflow.icon.retry', default: 'Workflow für Paket neu starten')}"/></a>
			</g:if>
			<g:if test="${ showRecoverAll == "true" }">
				<a onClick="return subForm('Den gesamten Workflow wiederholen? Sind Sie sicher?','../queueRecoverAll');"><img src="${resource(dir: 'images/icons', file: 'back-icon.png')}" style="width:16px; height:16px" title="${message(code: 'default.workflow.icon.restart', default: 'Gesamten Workflow für Paket neu starten')}" 
									alt="${message(code: 'default.workflow.icon.restart', default: 'Gesamten Workflow für Paket neu starten')}"/></a>
			</g:if>
			<g:if test="${ showDeleteAll == "true" }">
				<a onClick="return subForm('Alle Pakete löschen. Sind Sie sicher?','../queueDeleteAll');" ><img src="${resource(dir: 'images/icons', file: 'list_remove.png')}" style="width:16px; height:16px"/ title="${message(code: 'default.workflow.icon.delete', default: 'Paket löschen')}" 
									alt="${message(code: 'default.workflow.icon.delete', default: 'Paket löschen')}"></a>
			</g:if>
		</g:if>
		</td>
		</tr>
		</form>
		<g:if test="${queueEntryInstanceList == null || queueEntryInstanceList.isEmpty()}">
			<tr class="even">
				<td colspan="8"><i>No objects in queue ...</i></td>
			</tr>
		</g:if>
	</tbody>
	
</table>
</div>
(Administrator view)