<%@ page import="daweb3.ConversionPolicies" %>



<div class="fieldcontain ${hasErrors(bean: conversionPoliciesInstance, field: 'contractor', 'error')} required">
	<label for="contractor">
		<g:message code="conversionPolicies.contractor.label" default="Contractor" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="contractor" name="contractor.id" from="${daweb3.Contractor.list()}" optionKey="id" required="" value="${conversionPoliciesInstance?.contractor?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: conversionPoliciesInstance, field: 'conversion_routine', 'error')} required">
	<label for="conversion_routine">
		<g:message code="conversionPolicies.conversion_routine.label" default="Conversionroutine" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="conversion_routine" name="conversion_routine.id" from="${daweb3.ConversionRoutine.list()}" optionKey="id" required="" value="${conversionPoliciesInstance?.conversion_routine?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: conversionPoliciesInstance, field: 'source_format', 'error')} ">
	<label for="source_format">
		<g:message code="conversionPolicies.source_format.label" default="Sourceformat" />
		
	</label>
	<g:textField name="source_format" value="${conversionPoliciesInstance?.source_format}"/>
</div>

