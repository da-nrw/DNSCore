
<%@ page import="daweb3.CbNode" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'cbNode.label', default: 'CbNode')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-cbNode" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-cbNode" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="name" title="${message(code: 'cbNode.name.label', default: 'Name')}" />
					
						<g:sortableColumn property="urn_index" title="${message(code: 'cbNode.urn_index.label', default: 'Urnindex')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${cbNodeInstanceList}" status="i" var="cbNodeInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${cbNodeInstance.id}">${fieldValue(bean: cbNodeInstance, field: "name")}</g:link></td>
					
						<td>${fieldValue(bean: cbNodeInstance, field: "urn_index")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${cbNodeInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
