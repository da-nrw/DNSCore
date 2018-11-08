
<%@ page import="daweb3.User" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'user.label', default: 'Benutzer')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="page-body">
			
			<div id="list-user" class="content scaffold-list" role="main">
				<div class="blue-box"></div>
				<h2><g:message code="default.list.label" args="[entityName]" /></h2>
				<a href="#list-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
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
							
								<g:sortableColumn property="email_contact" title="${message(code: 'user.email_contact.label', default: 'Emailcontact')}" />
							
								<g:sortableColumn property="shortName" title="${message(code: 'user.shortName.label', default: 'Short Name')}" />
							
								<g:sortableColumn property="username" title="${message(code: 'user.username.label', default: 'Username')}" />
								
								<g:sortableColumn property="provider_type" title="${message(code: 'user.provider_type.label', default: 'Provider Type')}" />
							
								<g:sortableColumn property="password" title="${message(code: 'user.password.label', default: 'Password')}" />
							
								<g:sortableColumn property="description" title="${message(code: 'user.description.label', default: 'Description')}" />
							
								<g:sortableColumn property="forbidden_nodes" title="${message(code: 'user.forbidden_nodes.label', default: 'Forbiddennodes')}" />
							
							</tr>
						</thead>
						<tbody>
						<g:each in="${userInstanceList}" status="i" var="userInstance">
							<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
							
								<td><g:link action="show" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "email_contact")}</g:link></td>
							
								<td>${fieldValue(bean: userInstance, field: "shortName")}</td>
							
								<td>${fieldValue(bean: userInstance, field: "username")}</td>
								
								<td>${fieldValue(bean: userInstance, field: "provider_type")}</td>
							
								<td>${fieldValue(bean: userInstance, field: "password")}</td>
								
								<td>${fieldValue(bean: userInstance, field: "description")}</td>
							
								<td>${fieldValue(bean: userInstance, field: "forbidden_nodes")}</td>
							
							</tr>
						</g:each>
						</tbody>
					</table>
				</div>
				<g:if test="${userInstanceList.size() > 9}">
					<div class="pagination">
						<g:paginate total="${userInstanceCount ?: 0}" />
					</div>
				</g:if>
			</div>
		</div>
	</body>
</html>