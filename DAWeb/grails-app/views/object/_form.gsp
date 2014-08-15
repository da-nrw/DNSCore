<%@ page import="daweb3.Object" %>



<div class="fieldcontain ${hasErrors(bean: objectInstance, field: 'contractor', 'error')} required">
	<label for="contractor">
		<g:message code="object.contractor.label" default="Contractor" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="contractor" name="contractor.id" from="${daweb3.User.list()}" optionKey="id" required="" value="${objectInstance?.contractor?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: objectInstance, field: 'origName', 'error')} ">
	<label for="origName">
		<g:message code="object.origName.label" default="Orig Name" />
		
	</label>
	<g:textField name="origName" value="${objectInstance?.origName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: objectInstance, field: 'packages', 'error')} ">
	<label for="packages">
		<g:message code="object.packages.label" default="Packages" />
		
	</label>
	<g:select name="packages" from="${daweb3.Package.list()}" multiple="multiple" optionKey="id" size="5" value="${objectInstance?.packages*.id}" class="many-to-many"/>
</div>

<div class="fieldcontain ${hasErrors(bean: objectInstance, field: 'urn', 'error')} ">
	<label for="urn">
		<g:message code="object.urn.label" default="Urn" />
		
	</label>
	<g:textField name="urn" value="${objectInstance?.urn}"/>
</div>

