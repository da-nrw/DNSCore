<%@ page import="daweb3.Package" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'package.label', default: 'Package')}" />
		<title>DA-NRW Package</title>
	</head>
	<body>
		<div class="page-body">
			<div class="blue-box"></div>
			<h2>Package</h2>
			<a href="#show-object" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
			<div id="show-package" class="content scaffold-show" role="main">
				
				<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
				</g:if>
			</div>
		</div>
	</body>
</html>
