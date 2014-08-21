<%@ page import="daweb3.User" %>



<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'email_contact', 'error')} required">
	<label for="email_contact">
		<g:message code="user.email_contact.label" default="Emailcontact" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="email_contact" required="" value="${userInstance?.email_contact}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'shortName', 'error')} required">
	<label for="shortName">
		<g:message code="user.shortName.label" default="Short Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="shortName" required="" value="${userInstance?.shortName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'username', 'error')} required">
	<label for="username">
		<g:message code="user.username.label" default="Username" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="username" required="" value="${userInstance?.username}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'password', 'error')} required">
	<label for="password">
		<g:message code="user.password.label" default="Password" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="password" required="" value="${userInstance?.password}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="user.description.label" default="Description" />
		
	</label>
	<g:textField name="description" value="${userInstance?.description}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'forbidden_nodes', 'error')} ">
	<label for="forbidden_nodes">
		<g:message code="user.forbidden_nodes.label" default="Forbiddennodes" />
		
	</label>
	<g:textField name="forbidden_nodes" value="${userInstance?.forbidden_nodes}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'accountExpired', 'error')} ">
	<label for="accountExpired">
		<g:message code="user.accountExpired.label" default="Account Expired" />
		
	</label>
	<g:checkBox name="accountExpired" value="${userInstance?.accountExpired}" />
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'accountLocked', 'error')} ">
	<label for="accountLocked">
		<g:message code="user.accountLocked.label" default="Account Locked" />
		
	</label>
	<g:checkBox name="accountLocked" value="${userInstance?.accountLocked}" />
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'enabled', 'error')} ">
	<label for="enabled">
		<g:message code="user.enabled.label" default="Enabled" />
		
	</label>
	<g:checkBox name="enabled" value="${userInstance?.enabled}" />
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'passwordExpired', 'error')} ">
	<label for="passwordExpired">
		<g:message code="user.passwordExpired.label" default="Password Expired" />
		
	</label>
	<g:checkBox name="passwordExpired" value="${userInstance?.passwordExpired}" />
</div>

