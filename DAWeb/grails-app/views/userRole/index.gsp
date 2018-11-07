
<%@ page import="daweb3.UserRole" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'userRole.label', default: 'Benutzer-Rollen')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="page-body">
			<a href="#list-userRole" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
			
			<div id="list-userRole" class="content scaffold-list" role="main">
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
						<thead>
							<tr>
							
								<th><g:message code="userRole.role.label" default="Role" /></th>
								<th><g:message code="userRole.user.label" default="User" /></th>
							
							</tr>
						</thead>
						<tbody>
						<g:each in="${userRoleInstanceList}" status="i" var="userRoleInstance">
							<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
							
								<td><g:link action="show" params="${userRoleInstance.getPKId()}">${fieldValue(bean: userRoleInstance, field: "role")}</g:link></td>
							
								<td>${fieldValue(bean: userRoleInstance, field: "user")}</td>
							
							</tr>
						</g:each>
						</tbody>
					</table>
				</div>
				<div class="pagination">
					<g:paginate total="${userRoleInstanceCount ?: 0}" />
				</div>
			</div>
		</div>
	</body>
</html>
