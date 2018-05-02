<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'systemEvent.label', default: 'SystemEvent')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="page-body">
			<a href="#create-systemEvent" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
			<div id="create-systemEvent" class="content scaffold-create" role="main">
				<h1><g:message code="default.create.label" args="[entityName]" /></h1>
				<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
				</g:if>
				<g:hasErrors bean="${systemEventInstance}">
				<ul class="errors" role="alert">
					<g:eachError bean="${systemEventInstance}" var="error">
					<li> <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
					</g:eachError>
				</ul>
				</g:hasErrors>
				<g:form url="[resource:systemEventInstance, action:'save']" >
					<fieldset class="form">
						<g:render template="form"/>
					</fieldset>
					<fieldset class="buttons">
						<g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
						<g:actionSubmit class="cancel" action="cancelCreate" value="${message(code: 'default.button.cancel.label', default: 'Cancel')}" />
					</fieldset>
				</g:form>
			</div>
		</div>
	</body>
</html>
