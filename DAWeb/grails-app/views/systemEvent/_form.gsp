<%@ page import="daweb3.SystemEvent" %>

<div class="fieldcontain ${hasErrors(bean: systemEventInstance, field: 'type', 'error')} ">
	<label for="type">
		<g:message code="systemEvent.type.label" default="Type" />	
	</label>
	<g:hiddenField name="user.id" value="${ systemEventInstance?.user?.id} " />
	<g:hiddenField name="node.id" value="${ systemEventInstance?.node?.id} " />
	
	<g:select id="node" name="type" from="${['AutomaticCheckIPTCErrorEvent']}" required="" value="${{systemEventInstance?.type}}" class="many-to-one"/>
</div>


