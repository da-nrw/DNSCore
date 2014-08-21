
<%@ page import="daweb3.ConversionPolicies" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'conversionPolicies.label', default: 'ConversionPolicies')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-conversionPolicies" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<g:if test="${admin}">
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
				</g:if>
			</ul>
		</div>
		<div id="show-conversionPolicies" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list conversionPolicies">
			
				<g:if test="${conversionPoliciesInstance?.id}">
				<li class="fieldcontain">
					<span id="contractor-label" class="property-label"><g:message code="conversionPolicies.contractor.label" default="id" /></span>
					
						<span class="property-value" aria-labelledby="contractor-label">${conversionPoliciesInstance?.id?.encodeAsHTML()}</span>
					
				</li>
				</g:if>
			
				<g:if test="${conversionPoliciesInstance?.conversion_routine}">
				<li class="fieldcontain">
					<span id="conversion_routine-label" class="property-label"><g:message code="conversionPolicies.conversion_routine.label" default="konversionroutine" /></span>
					
						<span class="property-value" aria-labelledby="conversion_routine-label">${conversionPoliciesInstance?.conversion_routine?.encodeAsHTML()}</span>
					
				</li>
				</g:if>
			
				<g:if test="${conversionPoliciesInstance?.source_format}">
				<li class="fieldcontain">
					<span id="source_format-label" class="property-label"><g:message code="conversionPolicies.source_format.label" default="Quellformat" /></span>
					
						<span class="property-value" aria-labelledby="source_format-label"><g:fieldValue bean="${conversionPoliciesInstance}" field="source_format"/></span>
					
				</li>
				</g:if>
					<g:if test="${conversionPoliciesInstance?.conversion_routine?.target_suffix}">
				<li class="fieldcontain">
					<span id="source_format-label" class="property-label"><g:message code="conversionPolicies.conversion_routine.target_suffix.label" default="Zielformat" /></span>
					
						<span class="property-value" aria-labelledby="source_format-label"><g:fieldValue bean="${conversionRoutine}" field="target_suffix"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
			
				<fieldset class="buttons">
					<g:if test="${admin}">
					<g:link class="edit" action="edit" resource="${conversionPoliciesInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
					</g:if>
					<g:hiddenField name="id" value="${conversionPoliciesInstance?.id}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
