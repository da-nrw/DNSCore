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
			<!--  ${params.pkg}
			<br/> <br/>
			${events}
			<br/> <br/>
			${events.each{ 
				it.eventIdentifier.eventIdentifierType 
				} }
			<br/><br/>
			${events[0].eventIdentifier.eventIdentifierType }-->
			<!-- 
			<table>
				<thead>
					<tr>
						<th>eventType</th>

						<th>eventDateTime</th>
						<th>eventDetail</th>
						<th>linkingAgentIdentifierType</th>
						<th>linkingAgentIdentifierValue</th>
						<th>linkingObjectIdentifierType</th>
						<th>linkingObjectIdentifierValue</th>
						<th>linkingObjectRole</th>
						<th>linkingObjectIdentifierType</th>
						<th>linkingObjectIdentifierValue</th>
						<th>linkingObjectRole</th>
					</tr>
				</thead>
				
				<!-- 
				
					<th>eventIdentifierType</th>
					<th>eventIdentifierValue</th>
				
					print "<td>"+it.eventIdentifier.eventIdentifierType+"</td>"
					print "<td>"+it.eventIdentifier.eventIdentifierValue+"</td>"
				
					
				 -- >
				
				<tbody>
				<% events.each{ 
					print "<tr>"
					print "<td>"+it.eventType+"</td>"

					print "<td>"+it.eventDateTime+"</td>"
					print "<td>"+it.eventDetail+"</td>"
					print "<td>"+it.linkingAgentIdentifier.linkingAgentIdentifierType+"</td>"
					print "<td>"+it.linkingAgentIdentifier.linkingAgentIdentifierValue+"</td>"
					print "<td>"+it.linkingObjectIdentifier[0].linkingObjectIdentifierType+"</td>"
					print "<td>"+it.linkingObjectIdentifier[0].linkingObjectIdentifierValue+"</td>"
					print "<td>"+it.linkingObjectIdentifier[0].linkingObjectRole+"</td>"
					print "<td>"+it.linkingObjectIdentifier[1]?.linkingObjectIdentifierType+"</td>"
					print "<td>"+it.linkingObjectIdentifier[1]?.linkingObjectIdentifierValue+"</td>"
					print "<td>"+it.linkingObjectIdentifier[1]?.linkingObjectRole+"</td>"
					print "</tr>"
				} %>
				</tbody>
			</table>
			-->
			
			<table>
				<thead>
					<tr>
						<th>eventType</th>
						
						<th>eventDateTime</th>
						<th>eventDetail</th>
						<th>linkingAgentIdentifierType</th>
						<th>linkingAgentIdentifierValue</th>
						
						<th>linkingObjectIdentifierValue / Source</th>

						<th>linkingObjectIdentifierValue / Target</th>

					</tr>
				</thead>
				<tbody>
				<!-- 
					
					<th>eventIdentifierType</th>
					<th>eventIdentifierValue</th>
					
					<th>linkingObjectIdentifierType</th>
					
					<th>linkingObjectRole</th>
					<th>linkingObjectIdentifierType</th>
				
					<th>linkingObjectRole</th>
				
				
					print "<td>"+it.eventIdentifier.eventIdentifierType+"</td>"
					print "<td>"+it.eventIdentifier.eventIdentifierValue+"</td>"
					
					print "<td>"+it.linkingObjectIdentifier[0]?.linkingObjectIdentifierValue+"</td>"
					print "<td>"+it.linkingObjectIdentifier[0]?.linkingObjectRole+"</td>"
					
					print "<td>"+it.linkingObjectIdentifier[1]?.linkingObjectIdentifierValue+"</td>"
					print "<td>"+it.linkingObjectIdentifier[1]?.linkingObjectRole+"</td>"
				 -->
				<% eventList.each{ 
					print "<tr>"
					print "<td>"+(it.type==null?"":it.type)+"</td>"
				
					print "<td>"+(it.date==null?"":it.date)+"</td>"
					print "<td>"+(it.detail==null?"":it.detail)+"</td>"
					print "<td>"+(it.agentType==null?"":it.agentType)+"</td>"
					print "<td>"+(it.agentName==null?"":it.agentName)+"</td>"
					print "<td>"+(it.sourceFile?.rep_name==null?"":it.sourceFile.rep_name)+"</td>"

					print "<td>"+(it.targetFile?.rep_name==null?"":it.targetFile.rep_name)+"</td>"

					print "</tr>"
				} %>
				</tbody>
			</table>
		</div>
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
				print "<a href="+xmldocument+" target='_blank' type='text/plain'>mehr Informationen in XML anzeigen</a>"
				print "</div>"
				print "</li>"
			}
		} %>
		</ul>
		</li>
		<li class="file">Paket 2 </li>
	</ul>
	
	</div>

	
	<!-- 
	
	<g:link url='"+xmldocument+"' target='_blank'></g:link>
	
	<a href="+xmldocument+" target='_blank'>  -->
	</body>
</html>