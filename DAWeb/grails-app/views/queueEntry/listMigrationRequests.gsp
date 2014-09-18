<%@ page import="daweb3.QueueEntry" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'queueEntry.label', default: 'QueueEntry')}" />
		<title>Anfragen</title>
	</head>
	<body>
		
		<a href="#list-queueEntry" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		
		<div id="list-queueEntry" class="content scaffold-list" role="main">			
			<h1>Anfragenübersicht</h1>			
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>			
			<!-- This div is updated through the periodical updater -->
			<div class="list" id="entry-list">
<table>
	<thead>
		<tr>
			
			<th class="sortable field-id">
				<a href="#" onClick="return sortQueue('queueEntry.obj.identifier');">${message(code: 'queueEntry.obj.identifier.label', default: 'Identifier')}</a>
			</th>
				<th class="sortable field-id">
				<a href="#" onClick="return sortQueue('queueEntry.question');">${message(code: 'queueEntry.question.label', default: 'Anfrage lautet')}</a>
			</th>
			<th class="sortable field-origName">
				Aktion
			</th>
			<th class="sortable field-urn">
				<a href="#" onClick="return sortQueue('urn');">${message(code: 'queueEntry.obj.urn', default: 'URN')}</a>
			</th>
			
			<th class="sortable field-contractorShortName">
				<a href="#" onClick="return sortQueue('contractorShortName');">${message(code: 'queueEntry.obj.user.shortName.label', default: 'Contractor')}</a>
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
				<g:if test="${queueEntryInstance.obj != null}">
						${fieldValue(bean: queueEntryInstance.obj, field: "identifier")}
				</g:if>
				</td>
				<td>
					${fieldValue(bean: queueEntryInstance, field: "question")}
				</td>
				<td>
				<g:link onclick="return confirm('Antwort Nein - Sind Sie sicher?');" action="performMigrationRequestNo" id="${queueEntryInstance.id}">
						<g:img style="width:16px; height:16px" uri="/images/icons/list_remove.png" 
									title="${message(code: 'default.workflow.icon.no', default: 'Antwort Nein')}" 
									alt="${message(code: 'default.workflow.icon.no', default: 'Antwort Nein')}"/>
					</g:link>
				<g:link onclick="return confirm('Antwort Ja - Sind Sie sicher?');" action="performMigrationRequestYes" id="${queueEntryInstance.id}">
						<g:img style="width:16px; height:16px" uri="/images/icons/check32.png" 
									title="${message(code: 'default.workflow.icon.yes', default: 'Antwort Ja')}" 
									alt="${message(code: 'default.workflow.icon.yes', default: 'Antwort Ja')}"/>
					</g:link>
					
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
			</tr>
		</g:each>
		<g:if test="${queueEntryInstanceList == null || queueEntryInstanceList.isEmpty()}">
			<tr class="even">
				<td colspan="8"><i>Es warten keine Anfragen des Systems auf Ihre Entscheidung ...</i></td>
			</tr>
		</g:if>
	</tbody>
</table>
	</div>				
		</div>
	</body>
</html>
