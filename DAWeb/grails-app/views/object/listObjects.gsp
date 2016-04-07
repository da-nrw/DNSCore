<%@ page import="daweb3.Object" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'object.label', default: 'Object')}" />
		<title>Liste der Objekte</title>
	</head>
	<body>
		<a href="#listFormat-queueEntry" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		 <div id="list-queueEntry" class="content scaffold-list" role="main">			
			<table>
			    <g:form controller="object" action="listObjects">
            		<tr>
            			<td style="width:10%">Format:</td>
            			<td style="width:10%"><g:textField name="most_recent_formats" size="30"/></td>
            			<td style="width:10%">Metadatenformat:</td>
            			<td style="width:30%"><g:textField name="most_recent_secondary_attributes"  size="15"/></td>
            		<tr>
            			<td><g:actionSubmit value="suchen" action="listObjects"/></td>
            		</tr>
            	</g:form>
             </table> 
		</div>
		<div id="list-object" class="content scaffold-list" role="main" >
		<g:formRemote name="myForm" on404="alert('not found!')" url="[controller: 'object', action:'listObjects']" 
              onLoaded="queuedFor(data)">
           <table>
				 <thead>
					<tr>
					   <g:sortableColumn property="origName" title="${message(code: 'object.origName.label', default: 'Orig Name 1')}" />
					</tr>
				 </thead>
				 <tbody>
        		    <g:each in="${objects}" var="object" status="i" >
        		    	 <td> ${objects.origName}</td>
        		    </g:each>
             	</tbody>
             </table>
		</g:formRemote>
		</div>
	</body>
</html>
