
<%@ page import="daweb3.ConversionPolicies" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'conversionPolicies.label', default: 'ConversionPolicies')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-conversionPolicies" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				
			</ul>
		</div>
		<div id="list-conversionPolicies" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<th><g:message code="conversionPolicies.contractor.label" default="ID" /></th>
					
						<th><g:message code="conversionPolicies.conversion_routine.label" default="Konversionsroutine" /></th>
					
						<g:sortableColumn property="source_format" title="${message(code: 'conversionPolicies.source_format.label', default: 'Quellformat')}" />
					
						<th><g:message code="conversionPolicies.conversion_routine.label" default="Zielformat" /></th>
					
						<th><g:message code="conversionPolicies.presentation.label" default="Presentation Repository Policy" /></th>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${conversionPoliciesInstanceList}" status="i" var="conversionPoliciesInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${conversionPoliciesInstance.id}">${conversionPoliciesInstance.id}</g:link></td>
					
						<td>${fieldValue(bean: conversionPoliciesInstance, field: "conversion_routine")}</td>
					
						<td><g:if test="${!fieldValue(bean: conversionPoliciesInstance, field: "source_format").startsWith("danrw")}">
						  	 <g:link target='pronom' url="http://www.nationalarchives.gov.uk/PRONOM/${fieldValue(bean: conversionPoliciesInstance, field: "source_format")}">${fieldValue(bean: conversionPoliciesInstance, field: "source_format")}</g:link>
						   </g:if><g:else>${fieldValue(bean: conversionPoliciesInstance, field: "source_format")}</g:else></td>
						<td>${fieldValue(bean: conversionPoliciesInstance.conversion_routine, field: "target_suffix")}</td>
						<td>${fieldValue(bean: conversionPoliciesInstance, field: "presentation")}</td>
					
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<a href="https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/format_module.md">Mehr dazu</a>
			<div class="pagination">
				<g:paginate total="${conversionPoliciesInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
