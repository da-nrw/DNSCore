<%@ page import="daweb3.Object" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'object.label', default: 'Object')}" />
		<title>Auswahl Objekte Formate</title>
	</head>
	<body>
		<div class="page-body">
		<div class="blue-box"></div>
		<h2>Auswahl Objekte nach Formaten</h2>
			<a href="#listFormat-queueEntry" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
			 <div id="list-queueEntry" class="content scaffold-list" role="main">			
				<table>
				    <g:form controller="object" action="listObjects">
	            		<tr>
	            			<td style="width:5%; padding-left: 0px;">Format:</td>
	            			<td style="width:10%"><g:textField name="most_recent_formats" value="${params.most_recent_formats}" size="30" class="input-hoehe"/></td>
	            			<td style="width:10%">Metadatenformat:</td>
	            			<td style="width:20%"><g:textField name="most_recent_secondary_attributes" value="${params.most_recent_secondary_attributes}" size="30" class="input-hoehe"/></td>
	            			<td><g:actionSubmit value="suchen" action="listObjectsSearch" class="style-buttons"/> </td>
	            			<td> ${suLeer}</td>
	            		</tr>
	            	</g:form>
	             </table> 
			  </div>
			<div id="list-object" class="content scaffold-list" role="main" >
			   <g:formRemote name="myForm" on404="alert('not found!')" url="[controller: 'object', action:'listObjects']"  onLoaded="queuedFor(data)">
			    <div style="overflow:auto; height: 600px">
	             <table>
					 <thead class="thead-line">							
						<tr>
	  					   <g:sortableColumn property="identifier" title="${message(code: 'object.identifier', default: 'Identifier')}" />
						   <g:sortableColumn property="urn" title="${message(code: 'object.urn.label', default: 'Urn')}"  />	
						   <g:sortableColumn property="origName" title="${message(code: 'object.origName.label', default: 'Orig Name')}" />
						   <g:sortableColumn property="most_recent_formats" title="${message(code: 'object.most_recent_formats.label', default: 'Format')}"  />
						   <g:sortableColumn property="mapping-format" title="${message(code: 'formatMapping.extension.label', default: 'Mapping')}"  />
						   <g:sortableColumn property="mapping-name" title="${message(code: 'formatMapping.name.label', default: 'Name')}"  />
						   <g:sortableColumn property="most_recent_secondary_attributes" title="${message(code: 'object.most_recent_secondary_attributes.label', default: 'Meta-Format')}"  />
						</tr>
					 </thead>
					 <tbody>
	        		    <g:each in="${objects}" var="object" status="i">
	        		       <tr class="${ ((i % 2) == 0 ? 'odd' : 'even') }">
	        		        	<td>${object.identifier}</td>
								<td>${object.getFormattedUrn()}</td>
	        		    	    <td>${object.origName}</td>
	        		    	    <td>${object.most_recent_formats}</td>
		        		    	<td>${extension[i].toString().replace('[', ' ').replace(']','')}</td>
				        		<td>${name[i].toString().replace('[', ' ').replace(']','')}</td>
	        		    	    <td>${object.most_recent_secondary_attributes}</td>
	        		      </tr>
	        		    </g:each>
	        		    <tr><td>${sqlLeer}</td></tr>
	             	</tbody>
	             </table>
			   </div>
			   </g:formRemote>
			</div>
		</div>
	</body>
</html>
