
<%@ page import="daweb3.Package" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'package.label', default: 'Package')}" />
		<title>DA-NRW Package</title>
	</head>
	<body>
		<a href="#show-object" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
					</ul>
		</div>
		<div id="show-package" class="content scaffold-show" role="main">
			<h1>Package</h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list object">
			
				<g:if test="${packageInstance?.id}">
				<li class="fieldcontain">
					<span id="contractor-label" class="property-label"><g:message code="package.id.label" default="Id" /></span>
					
						<span class="property-value" aria-labelledby="contractor-label">${packageInstance?.id}</span>
					
				</li>
				</g:if>
				<g:if test="${packageInstance?.name}">
				<li class="fieldcontain">
					<span id="contractor-label" class="property-label"><g:message code="package.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="contractor-label">${packageInstance?.name}</span>
					
				</li>
				</g:if>
				<g:if test="${packageInstance?.checksum}">
				<li class="fieldcontain">
					<span id="contractor-label" class="property-label"><g:message code="package.checksum.label" default="Checksum" /></span>
					
						<span class="property-value" aria-labelledby="contractor-label">${packageInstance?.checksum}</span>
					
				</li>
				</g:if>
				<g:if test="${packageInstance?.status}">
				<li class="fieldcontain">
					<span id="contractor-label" class="property-label"><g:message code="package.status.label" default="Status" /></span>
					
						<span class="property-value" aria-labelledby="contractor-label">${packageInstance?.status}</span>
					
				</li>
				</g:if>
				<g:if test="${packageInstance?.last_checked}">
				<li class="fieldcontain">
					<span id="contractor-label" class="property-label"><g:message code="package.last_checked.label" default="Letzte Überprüfung" /></span>
					
						<span class="property-value" aria-labelledby="contractor-label">${packageInstance?.last_checked}</span>
					
				</li>
				</g:if>
				
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${packageInstance?.id}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
