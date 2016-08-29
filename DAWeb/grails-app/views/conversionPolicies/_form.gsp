<%@ page import="daweb3.ConversionPolicies" %>

<g:if test="${!fieldValue(bean: conversionPoliciesInstance, field: "id").equals("0")}">
<div class="fieldcontain ${hasErrors(bean: conversionPoliciesInstance, field: 'id', 'error')}">
	<label for="conversion_routine_id">
		<g:message code="conversionPolicies.contractor.label" default="id" />
	</label>
	<g:textField name="id" value="${conversionPoliciesInstance?.id?.encodeAsHTML()}" disabled="true"/>	
</div>
</g:if>

<div class="fieldcontain ${hasErrors(bean: conversionPoliciesInstance, field: 'conversion_routine', 'error')} required">
	<label for="conversion_routine">
		<g:message code="conversionPolicies.conversion_routine.label" default="Konversionsroutine" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="conversion_routine" name="conversion_routine.id" from="${daweb3.ConversionRoutine.list()}" 
				optionKey="id" required="" value="${conversionPoliciesInstance?.conversion_routine?.id}" 
				class="many-to-one" 	style="width:100%;max-width:500px"/>
</div>

<div class="fieldcontain ${hasErrors(bean: conversionPoliciesInstance, field: 'source_format', 'error')} required">
	<label for="source_format">
		<g:message code="conversionPolicies.source_format.label" default="Quellformat" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="source_format" value="${conversionPoliciesInstance?.source_format}"/></div>

<div class="fieldcontain ${hasErrors(bean: conversionPoliciesInstance, field: 'presentation', 'error')} ">
	<label for="accountExpired">
		<g:message code="conversionPolicies.presentation.label" default="Presentation Repository Policy" />
		
	</label>
	<g:checkBox name="presentation" value="${conversionPoliciesInstance?.presentation}" />
</div>
