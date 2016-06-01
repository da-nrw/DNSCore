<%@page import="com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList"%>
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
			<h1>Premis zu Objekt ${params.objectIdentifier }</h1>
			
	
	
	<div id="list">
	
	<ul>
		<% 
		objectList?.packages.each {
			it.each {
				int pkgId = it.id
				print "<li class='file'>"
			
				print it.toString()
		
				print "<ul>"
				eventList.each {
					if(pkgId == it.pkg_id) {
						if(it.type == "SIP_CREATION") {
							print "Das Paket wurde mit " + it.agentName + " am " + it.date + " erstellt. <br/>"
						}	
						else if(it.type == "INGEST") {
							print "Das Paket wurde von " + it.agentName + " am " + it. date + " eingeliefert. <br/>" 
						}
					}
				}
				
				println "Folgende Dateien sind in dem Paket enthalten: "
		
				dafileList.each {
					def dafile = it
					if(dafile.pkg_id == pkgId) {
					
						boolean noEvent = true;
						eventList.each {
							def source = it.sourceFile
		 					def target = it.targetFile		
							if(source != null && target != null) {
								if(dafile.id == source.id) {
							
									print "<li class='file'>"
									print "Datei: " + dafile.relative_path
							
									print "<div id='detail'>"
		
									print "Die Datei wurde von " + source?.rep_name + " im Format " + source?.format_puid
									print " nach " + target?.rep_name + " im Format " + target?.format_puid + " konvertiert. <br/>"
							
									%>
									<g:link controller="Object" action="premisAnzeigen" params="[xmldocument: objectList.xml]" target="_blank">mehr Informationen in XML anzeigen</g:link>
							
		
									</div>
									</li>
									<%
									noEvent = false;
								}
								else if (dafile.id == target.id) {
									noEvent = false;
								}
							}
					
						}
						if(noEvent) {
							print "<li class='file'>"
							print "Datei: " + dafile.relative_path
					
							print "<div id='detail'>"
		
							print "Die Datei ist nicht konvertiert und liegt in " + dafile.rep_name + " im Format " + dafile.format_puid + ". <br/>"
								
							%>
							<g:link controller="Object" action="premisAnzeigen" params="[xmldocument: objectList.xml]" target="_blank">mehr Informationen in XML anzeigen</g:link>
		
							</div>
							</li>
							<%
						}
					}
				}	
				print "</ul></li>"
			}
		} 
		%>

		
	</ul>
	
	</div>
	
	</body>
</html>