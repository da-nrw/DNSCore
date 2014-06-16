
<%@ page import="daweb3.QueueEntry" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'queueEntry.label', default: 'QueueEntry')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-queueEntry" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		<div id="show-queueEntry" class="content scaffold-show" role="main">
			<h1>Statusdetail</h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list queueEntry">
				<g:if test="${queueEntryInstance?.id}">
				<li class="fieldcontain">
					<span id="urn-label" class="property-label"><g:message code="queueEntry.id.label" default="Job-Id" /></span>
					
						<span class="property-value" aria-labelledby="urn-label">${queueEntryInstance?.getIdAsString() }</span>
					
				</li>
				</g:if>
			<g:if test="${queueEntryInstance?.obj.identifier}">
				<li class="fieldcontain">
					<span id="urn-label" class="property-label"><g:message code="queueEntry.id.label" default="Identifier" /></span>
					
						<span class="property-value" aria-labelledby="urn-label"><g:fieldValue bean="${queueEntryInstance.obj}" field="identifier"/></span>
					
				</li>
				</g:if>
					<g:if test="${queueEntryInstance?.obj.getIdAsString()}">
				<li class="fieldcontain">
					<span id="urn-label" class="property-label"><g:message code="object.id.label" default="Object-Id" /></span>
					
						<span class="property-value" aria-labelledby="urn-label">${queueEntryInstance?.obj.getIdAsString() }</span>
				</li>
				</g:if>
				<g:if test="${queueEntryInstance?.status}">
				<li class="fieldcontain">
					<span id="status-label" class="property-label"><g:message code="queueEntry.status.label" default="Status" /></span>
					
						<span class="property-value" aria-labelledby="status-label"><g:fieldValue bean="${queueEntryInstance}" field="status"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${queueEntryInstance?.obj.urn}">
				<li class="fieldcontain">
					<span id="urn-label" class="property-label"><g:message code="queueEntry.urn.label" default="Urn" /></span>
					
						<span class="property-value" aria-labelledby="urn-label"><g:fieldValue bean="${queueEntryInstance.obj}" field="urn"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${queueEntryInstance?.obj.getContractor().getShortName()}">
				<li class="fieldcontain">
					<span id="contractorShortName-label" class="property-label"><g:message code="queueEntry.contractorShortName.label" default="Contractor Short Name" /></span>
					
						<span class="property-value" aria-labelledby="contractorShortName-label">${queueEntryInstance?.obj.getContractor().getShortName()}</span>
					
				</li>
				</g:if>
			
				<g:if test="${queueEntryInstance?.created}">
				<li class="fieldcontain">
					<span id="created-label" class="property-label"><g:message code="queueEntry.created.label" default="Created" /></span>
					
						<span class="property-value" aria-labelledby="created-label"><g:fieldValue bean="${queueEntryInstance}" field="created"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${queueEntryInstance?.initialNode}">
				<li class="fieldcontain">
					<span id="initialNode-label" class="property-label"><g:message code="queueEntry.initialNode.label" default="Initial Node" /></span>
					
						<span class="property-value" aria-labelledby="initialNode-label"><g:fieldValue bean="${queueEntryInstance}" field="initialNode"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${queueEntryInstance?.modified}">
				<li class="fieldcontain">
					<span id="modified-label" class="property-label"><g:message code="queueEntry.modified.label" default="Modified" /></span>
					
						<span class="property-value" aria-labelledby="modified-label"><g:fieldValue bean="${queueEntryInstance}" field="modified"/></span>
					
				</li>
				</g:if>
				<g:if test="${queueEntryInstance?.obj.origName}">
				<li class="fieldcontain">
					<span id="origName-label" class="property-label"><g:message code="queueEntry.obj.origName.label" default="Orig Name" /></span>
					
						<span class="property-value" aria-labelledby="origName-label"><g:fieldValue bean="${queueEntryInstance.obj}" field="origName"/></span>
					
				</li>
				</g:if>
			
				
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${queueEntryInstance?.id}" />
					%{--<g:link class="edit" action="edit" id="${queueEntryInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />--}%
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
