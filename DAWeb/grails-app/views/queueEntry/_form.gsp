<%@ page import="daweb3.QueueEntry" %>



<div class="fieldcontain ${hasErrors(bean: queueEntryInstance, field: 'status', 'error')} ">
	<label for="status">
		<g:message code="queueEntry.status.label" default="Status" />
		
	</label>
	<g:textField name="status" value="${queueEntryInstance?.status}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: queueEntryInstance, field: 'urn', 'error')} ">
	<label for="urn">
		<g:message code="queueEntry.urn.label" default="Urn" />
		
	</label>
	<g:textField name="urn" value="${queueEntryInstance?.urn}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: queueEntryInstance, field: 'contractorShortName', 'error')} ">
	<label for="contractorShortName">
		<g:message code="queueEntry.contractorShortName.label" default="Contractor Short Name" />
		
	</label>
	<g:textField name="contractorShortName" value="${queueEntryInstance?.contractorShortName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: queueEntryInstance, field: 'created', 'error')} ">
	<label for="created">
		<g:message code="queueEntry.created.label" default="Created" />
		
	</label>
	<g:textField name="created" value="${queueEntryInstance?.created}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: queueEntryInstance, field: 'initialNode', 'error')} ">
	<label for="initialNode">
		<g:message code="queueEntry.initialNode.label" default="Initial Node" />
		
	</label>
	<g:textField name="initialNode" value="${queueEntryInstance?.initialNode}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: queueEntryInstance, field: 'modified', 'error')} ">
	<label for="modified">
		<g:message code="queueEntry.modified.label" default="Modified" />
		
	</label>
	<g:textField name="modified" value="${queueEntryInstance?.modified}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: queueEntryInstance, field: 'nodeName', 'error')} ">
	<label for="nodeName">
		<g:message code="queueEntry.nodeName.label" default="Node Name" />
		
	</label>
	<g:textField name="nodeName" value="${queueEntryInstance?.nodeName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: queueEntryInstance.obj, field: 'origName', 'error')} ">
	<label for="origName">
		<g:message code="queueEntry.obj.origName.label" default="Orig Name" />
		
	</label>
	<g:textField name="origName" value="${queueEntryInstance?.obj.origName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: queueEntryInstance, field: 'replDestinations', 'error')} ">
	<label for="replDestinations">
		<g:message code="queueEntry.replDestinations.label" default="Repl Destinations" />
		
	</label>
	<g:textField name="replDestinations" value="${queueEntryInstance?.replDestinations}"/>
</div>

