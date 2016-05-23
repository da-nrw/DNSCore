<%@ page import="daweb3.User" %>
<%@page import="daweb3.Object" %>
<%@ page import="daweb3.Event" %>

<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>Premis</title>
		<style type="text/css" media="screen">
			#list ul {
				list-style-position: inside;
			}

			#list li {
				line-height: 1.3;
				list-style-position: inside;
				margin: 0.5em;
			}
			#detail {
				background-color: #d0d0d0;
				padding: 0.5em;
				margin-left: 6em;
			}
			#list ul li ul {
				display: none;
			}
			#list * #detail {
				display: none;
			}

		</style>
		<r:script type="text/javascript">

			$(document).ready(function() {
   				$(".file").click(function(e) {
      				$(this).toggleClass("filed");
      				$(this).children().slideToggle("300");
      				e.stopPropagation();
   				});  
			});
			
			
			function xmlAnzeigen() {
				var i = window.open();
				i.document.write("/home/julia/Desktop/premis_neu.xml");
				i.focus();
			}

		</r:script>
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
			<h1>Premis zu ${params.objectIdentifier }</h1>
			Anzahl der Events ${size}
			
		<br/> Test Premis <br/>
	${params}
	
	<br/><br/><br/>
	
	<div id="list">
	
	<ul>
		<li class="file">Paket 1
		<ul>
		<% eventList.each{ 
			if(it.type=="CONVERT")	{
				$xml="/home/julia/Desktop/premis_neu.xml"
				print "<li class='file'>"
				print it.sourceFile?.relative_path
				print "<div id='detail'>"
				print it.sourceFile?.rep_name+"<br/>"
				print it.targetFile?.rep_name+"<br/>"
				%>
				<g:link controller="Object" action="premisAnzeigen" params="[xmldocument: xmldocument]" target="_blank">mehr Informationen in XML anzeigen</g:link>
				<%print "</div>"
				print "</li>"
			}
		} %>
		</ul>
		</li>
		<li class="file">Paket 2 </li>
		
	</ul>
	
	</div>

	
	<!-- 
	
	print "<a href="+xmldocument+" target='_blank' type='text/plain'>mehr Informationen in XML anzeigen</a>"
	
	
	<g:link url='"+xmldocument+"' target='_blank'></g:link>
	
	<a href="+xmldocument+" target='_blank'> 
	
	

	
	 -->
	</body>
</html>