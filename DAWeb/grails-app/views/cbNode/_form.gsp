<%@ page import="daweb3.CbNode" %>



<div class="fieldcontain ${hasErrors(bean: cbNodeInstance, field: 'contractors', 'error')} ">
	<label for="contractors">
		<g:message code="cbNode.contractors.label" default="Contractors" />
		
	</label>
	<g:select name="contractors" from="${daweb3.User.list()}" multiple="multiple" optionKey="id" size="5" value="${cbNodeInstance?.contractors*.id}" class="many-to-many"/>
</div>

<div class="fieldcontain ${hasErrors(bean: cbNodeInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="cbNode.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${cbNodeInstance?.name}"/>
</div>
