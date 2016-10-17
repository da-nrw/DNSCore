<%@page import="com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList"%>
<%@ page import="daweb3.User" %>
<%@page import="daweb3.Object" %>
<%@ page import="daweb3.Event" %>
<%@ page import="daweb3.DAFile" %>

<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>Premisanfrage</title>
		
		<style type="text/css" media="screen">
			.list{
				font: normal 14px/16px ;
				color: #333;
				padding-left: 5px;
				margin-left: 20px;
			}
		</style>
	</head>
	<body>
		<a href="#list-object" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><a class="back" href="${createLink(uri: '/object/list')}">Back</a></li>
			</ul>
		</div>
		<div id="list-object" class="content scaffold-list" role="main">
			<h1 style="font-weight: bold;">Premis zu Objekt ${params.objName } (${params.objectIdentifier }) angefordert</h1>
			<div class="list">		
				${ result.msg}
			</div>
			<!-- ${result} -->
		</div>
	</body>
</html>