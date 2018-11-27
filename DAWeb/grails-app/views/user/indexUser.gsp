<%@ page import="daweb3.User" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'user.label', default: 'Benutzer Daten')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="page-body">
			<div class="blue-box"></div>
			<h2><g:message code="default.show.label" args="[entityName]" /></h2>
			<div id="show-user" class="content scaffold-show" role="main">
				<g:if test="${flash.message}">
					<div class="message" role="status">${flash.message}</div>
				</g:if>
				<ol class="property-list user">
					<g:if test="${userInstance?.email_contact}"> 
					<li class="fieldcontain">
						<span id="email_contact-label" class="property-label"><g:message code="user.email_contact.label" default="Emailcontact" /></span>
						<span class="property-value" aria-labelledby="email_contact-label"><g:fieldValue bean="${userInstance}" field="email_contact"/></span>
					</li>
 					</g:if> 
				
					<g:if test="${userInstance?.contractorShortName}">
					<li class="fieldcontain">
						<span id="shortName-label" class="property-label"><g:message code="user.contractorshortName.label" default="Contractor Name" /></span>
						<span class="property-value" aria-labelledby="shortName-label"><g:fieldValue bean="${userInstance}" field="contractorShortName"/></span>
					</li>
					</g:if>
				
					<g:if test="${userInstance?.username}">
					<li class="fieldcontain">
						<span id="username-label" class="property-label"><g:message code="user.username.label" default="Username" /></span>
						<span class="property-value" aria-labelledby="username-label"><g:fieldValue bean="${userInstance}" field="username"/></span>
					</li>
					</g:if>

					<g:if test="${userInstance?.password}">
					<li class="fieldcontain">
						<span id="password-label" class="property-label"><g:message code="user.password.label" default="Password" /></span>
						<span class="property-value" aria-labelledby="password-label"><g:fieldValue bean="${userInstance}" field="password"/></span>
					</li>
					</g:if>
				
					<g:if test="${userInstance?.description}">
					<li class="fieldcontain">
						<span id="description-label" class="property-label"><g:message code="user.description.label" default="Description" /></span>
						<span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${userInstance}" field="description"/></span>
					</li>
					</g:if>
				
					<g:if test="${userInstance?.forbidden_nodes}">
						<li class="fieldcontain">
							<span id="forbidden_nodes-label" class="property-label"><g:message code="user.forbidden_nodes.label" default="Forbiddennodes" /></span>
							<span class="property-value" aria-labelledby="forbidden_nodes-label"><g:fieldValue bean="${userInstance}" field="forbidden_nodes"/></span>
						</li>
					</g:if>		
					
					<g:if test="${userInstance?.provider_type}">
						<li class="fieldcontain">
							<span id="provider_type-label" class="property-label"><g:message code="user.provider_type.label" default="Provider Type" /></span>
							<span class="property-value" aria-labelledby="provider_type-label"><g:fieldValue bean="${userInstance}" field="provider_type"/></span>
						</li>
					</g:if>
				</ol>
			</div>		 
			<g:form url="[resource:userInstance, action:'editUser']" >
				<fieldset class="buttons">
					<g:actionSubmit class="edit" action="editUser" resource="${userInstance}" value="${message(code: 'default.button.edit.label', default: 'Edit')}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>