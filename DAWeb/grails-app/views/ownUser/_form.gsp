<%@ page import="daweb3.User" %>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'email_contact', 'error')} required">
	<label for="email_contact">
		<g:message code="user.email_contact.label" default="Emailcontact" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="email_contact" required="" value="${userInstance?.email_contact}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'password', 'error')} required">
	<label for="password">
		<g:message code="user.password.label" default="Password" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="password" required="" value="${userInstance?.password}"/>
</div>
