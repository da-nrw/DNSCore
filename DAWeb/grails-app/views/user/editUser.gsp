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
			<g:form  url="[resource:userInstance, action:'updateUser']" method="PUT"> 
				<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'email_contact', 'error')} required">
					<label for="email_contact">
						<g:message code="user.email_contact.label" default="Emailcontact" />
						<span class="required-indicator">*</span>
					</label>
					<g:textField name="email_contact" required="" value="${userInstance?.email_contact}" class="input-hoehe"/>
				</div>
	
				<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'contractorShortName', 'error')} required">
					<label for="contractorShortName">
						<g:message code="user.contractorShortName.label" default="Contractor Name" />
						<span class="required-indicator">*</span>
					</label>
					<g:field readonly="readonly" name="contractorShortName" required="" value="${userInstance?.contractorShortName}" class="input-hoehe"/>
				</div>
				
				<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'username', 'error')} required">
					<label for="username">
						<g:message code="user.username.label" default="Username" />
						<span class="required-indicator">*</span>
					</label>
					<g:textField name="username" required="" value="${userInstance?.username}" class="input-hoehe"/>
				</div>
				
				<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'password', 'error')} required">
					<label for="password">
						<g:message code="user.password.label" default="Password" />
						<span class="required-indicator">*</span>
					</label>
					<g:textField name="password" required="" value="${userInstance?.password}" class="input-hoehe"/>
				</div>
				
				<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'provider_type', 'error')} required">
					<label for="provider_type">
						<g:message code="user.provider_type.label" default="Provider Type" />
						<span class="required-indicator">*</span>
					</label>
					<g:field readonly="readonly" name="providerType" required="" value="${userInstance?.provider_type}" class="input-hoehe" />
				</div>
			<br/>
<!-- 			</g:form> -->
<!-- 			<g:form url="[resource:userInstance, action:'updateUser']" method="PUT" > -->
				<fieldset class="buttons">
					<g:actionSubmit class="save" action="updateUser" value="${message(code: 'default.button.update.label', default: 'Update')}" />
					<g:actionSubmit class="cancel" action="cancelUser" value="${message(code: 'default.button.cancel.label', default: 'Cancel')}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>