
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
			</ul>
		</div>
		<div id="show-conversionPolicies" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list conversionPolicies">
			
				<g:if test="${conversionPoliciesInstance?.contractor}">
				<li class="fieldcontain">
					<span id="contractor-label" class="property-label"><g:message code="conversionPolicies.contractor.label" default="Contractor" /></span>
					
						<span class="property-value" aria-labelledby="contractor-label">${conversionPoliciesInstance?.contractor?.encodeAsHTML()}</span>
					
				</li>
				</g:if>
			
				<g:if test="${conversionPoliciesInstance?.conversion_routine}">
				<li class="fieldcontain">
					<span id="conversion_routine-label" class="property-label"><g:message code="conversionPolicies.conversion_routine.label" default="Conversionroutine" /></span>
					
						<span class="property-value" aria-labelledby="conversion_routine-label">${conversionPoliciesInstance?.conversion_routine?.encodeAsHTML()}</span>
					
				</li>
				</g:if>
			
				<g:if test="${conversionPoliciesInstance?.source_format}">
				<li class="fieldcontain">
					<span id="source_format-label" class="property-label"><g:message code="conversionPolicies.source_format.label" default="Sourceformat" /></span>
					
						<span class="property-value" aria-labelledby="source_format-label"><g:fieldValue bean="${conversionPoliciesInstance}" field="source_format"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${conversionPoliciesInstance?.id}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
