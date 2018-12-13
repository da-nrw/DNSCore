<%@ page import="daweb3.User" %>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'email_contact', 'error')} required">
	<label for="email_contact">
		<g:message code="user.email_contact.label" default="Emailcontact" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="email_contact" required="" value="${userInstance?.email_contact}" class="input-hoehe"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'contractorShortName', 'error')} required">
	<label for="shortName">
		<g:message code="user.contractorShortName.label" default="Contractor Name" />
		<span class="required-indicator">*</span>
	</label>
<!-- 	<g:textField name="contractorshortName" required="" value="${userInstance?.contractorShortName}" class="input-hoehe"/> -->
 	<g:select id="contractor" required="" name="contractorShortName" from="${User.findAll().unique { it.contractorShortName.trim() }}" optionKey="contractorShortName" value="${userInstance?.contractorShortName}" noSelection="['':'-Bitte wählen-']" /> 
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
	<g:select id="providerType" required="" name="provider_type" from="${['Archiv','Bibliothek','Museum']}" keys="${['Archiv','Bibliothek','Museum']}" value="${userInstance?.provider_type}" noSelection="['':'-Bitte wählen-']" />
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'friendly_file_exts', 'error')} ">
	<label for="friendly_file_exts">
		<g:message code="user.friendly_file_exts.label" default="Friendly extensions" />
		
	</label>
	<g:textField name="friendly_file_exts" value="${userInstance?.friendly_file_exts}" class="input-hoehe"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="user.description.label" default="Description" />
		
	</label>
	<g:textField name="description" value="${userInstance?.description}" class="input-hoehe"/>
<div class="message">Änderung an diesen Feldern kann signifikantes Systemverhalten ändern!</div>
<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'usePublicMets', 'error')} ">
<label for="usePublicMets">
	<g:message code="user.usePublicMets.label" default="Use Public Mets" />
	
</label>
<g:checkBox name="usePublicMets" value="${userInstance?.usePublicMets}" />
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'useVirusScan', 'error')} ">
	<label for="useVirusScan">
		<g:message code="user.useVirusScan.label" default="Use Virus Scan" />
		
	</label>
	<g:checkBox name="useVirusScan" value="${userInstance?.useVirusScan}" />
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'deltaOnUrn', 'error')} ">
<label for="deltaOnUrn">
	<g:message code="user.deltaOnUrn.label" default="Delta On URN" />
	
</label>
<g:checkBox name="deltaOnUrn" value="${userInstance?.deltaOnUrn}" />
</div>
<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'mailsPooled', 'error')} ">
	<label for="mailsPooled">
		<g:message code="user.mails_pooled.label" default="Emails täglich als gesammelten Report" />
		
	</label>
	<g:checkBox name="mailsPooled" value="${userInstance?.mailsPooled}" />
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'forbidden_nodes', 'error')} ">
	<label for="forbidden_nodes">
		<g:message code="user.forbidden_nodes.label" default="Forbiddennodes" />
		
	</label>
	<g:textField name="forbidden_nodes" value="${userInstance?.forbidden_nodes}" class="input-hoehe"/>
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

