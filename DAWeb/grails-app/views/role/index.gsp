
<%@ page import="daweb3.Role" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'role.label', default: 'Rolle')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="page-body">
			<a href="#list-role" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
			
			<div id="list-role" class="content scaffold-list" role="main">
				<div class="blue-box"></div>
				<h2><g:message code="default.list.label" args="[entityName]" /></h2>
				<div class="nav" role="navigation">
				<ul>
					<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
				</ul>
				</div>
				<g:if test="${flash.message}">
					<div class="message" role="status">${flash.message}</div>
				</g:if>
				<div class="table-style">
					<table>
						<thead class="thead-line">
							<tr>
							
								<g:sortableColumn property="authority" title="${message(code: 'role.authority.label', default: 'Authority')}" />
							
							</tr>
						</thead>
						<tbody>
						<g:each in="${roleInstanceList}" status="i" var="roleInstance">
							<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
							
								<td><g:link action="show" id="${roleInstance.id}">${fieldValue(bean: roleInstance, field: "authority")}</g:link></td>
							
							</tr>
						</g:each>
						</tbody>
					</table>
				</div>
				<div class="pagination">
					<g:paginate total="${roleInstanceCount ?: 0}" />
				</div>
			</div>
		</div>
	</body>
</html>
