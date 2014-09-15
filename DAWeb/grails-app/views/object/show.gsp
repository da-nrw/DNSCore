
<%@ page import="daweb3.Object" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'object.label', default: 'Object')}" />
		<title>DA-NRW Objekt</title>
	</head>
	<r:script>
function toggle(source) {
	  checkboxes = document.getElementsByName('currentPackages');
	  for(var i in checkboxes) {
	    checkboxes[i].checked = source.checked;
		}
	}</r:script>
	<body>
		<a href="#show-object" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><a class="list" href="<g:createLink controller="object" action="list" />">Liste der AIP</a></li>
			</ul>
		</div>
		<div id="show-object" class="content scaffold-show" role="main">
			<h1>Objektdetail</h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list object">
			
				<g:if test="${objectInstance?.user}">
				<li class="fieldcontain">
					<span id="contractor-label" class="property-label"><g:message code="object.user.label" default="Contractor" /></span>
					
						<span class="property-value" aria-labelledby="contractor-label">${objectInstance?.user?.encodeAsHTML()}</span>
					
				</li>
				</g:if>
			
				<g:if test="${objectInstance?.origName}">
				<li class="fieldcontain">
					<span id="origName-label" class="property-label"><g:message code="object.origName.label" default="Orig Name" /></span>
					
						<span class="property-value" aria-labelledby="origName-label"><g:fieldValue bean="${objectInstance}" field="origName"/></span>
					
				</li>
				</g:if><li class="fieldcontain">
				<span id="packages-label" class="property-label"><g:message code="object.packages.label" default="Packages" /></span>
				<g:form controller="package" action="retrievePackages">
				<g:hiddenField name="oid" value="${objectInstance?.id}" />
				<span class="property-value" ><input type="checkbox"  onClick="toggle(this)"/>Alle an-/abwählen</span><br>
				<g:if test="${objectInstance?.packages}">
							<g:each in="${objectInstance.packages}" var="p">
							<span class="property-value" ><g:checkBox name="currentPackages" value="${p.getId()}" checked="false" />${p?.encodeAsHTML()}</span>
						</g:each>	
				<span class="property-value" ><g:actionSubmit value="Versioniertes Retrieval starten" controller="package" action="retrievePackages"/></span>
				</g:if>
				</g:form>
				</li>
				<g:if test="${objectInstance?.object_state}">
				<g:set var="statusCode" value="${objectInstance.getStatusCode()}" />
				<li class="fieldcontain">
					<span id="packages-label" class="property-label">Status:</span>
					<g:if test="${statusCode == 1}">
									<g:img style="width:16px; height:16px" uri="/images/icons/warning32.png"/>
								</g:if>
								<g:elseif test="${statusCode == 2}">
									<g:img style="width:16px; height:16px" uri="/images/icons/clock32.png"/>
								</g:elseif>
								<g:elseif test="${statusCode == 0}">
									<g:img style="width:16px; height:16px" uri="/images/icons/check32.png"/>
								</g:elseif>
							
					
				</li>
				</g:if>			
				<g:if test="${objectInstance?.urn}">
				<li class="fieldcontain">
					<span id="urn-label" class="property-label"><g:message code="object.urn.label" default="Urn" /></span>
					<span class="property-value" aria-labelledby="urn-label"> ${objectInstance?.urn}</span>
					<g:if test="${objectInstance.published_flag==1}">
						<g:link url="${objectInstance.getPublicPresLink() }" target="_blank"><span class="property-value" aria-labelledby="urn-label">Öffentliche Derivate (PIP)</span></g:link>
					</g:if>
					<g:if test="${objectInstance.published_flag==2}">
						<g:link url="${objectInstance.getInstPresLink() }" target="_blank"><span class="property-value" aria-labelledby="urn-label">Institutionelle Derivate (PIP)</span></g:link>
					</g:if>
					<g:if test="${objectInstance.published_flag==3}">
						<g:link url="${objectInstance.getPublicPresLink() }" target="_blank"><span class="property-value" aria-labelledby="urn-label">Öffentliche Derivate (PIP)</span></g:link>
						<g:link url="${objectInstance.getInstPresLink() }" target="_blank"><span class="property-value" aria-labelledby="urn-label">Institutionelle Derivate (PIP)</span></g:link>
					</g:if>
					</li>
				<li class="fieldcontain">
					<span id="urn-label" class="property-label">Technischer Name:</span>
					<span class="property-value" aria-labelledby="urn-label">${objectInstance?.identifier}</span>
					
				</li>
				
				</g:if>
				
				
				<g:if test="${objectInstance?.created}">
					<li class="fieldcontain">
					<span id="origName-label" class="property-label"><g:message code="object.created.label" default="Datum erstellt" /></span>
					
						<span class="property-value" aria-labelledby="origName-label">${objectInstance.getFormattedCreatedDate()}</span>
				</li>
			
				</g:if>
					<g:if test="${objectInstance?.modified}">
					<li class="fieldcontain">
					<span id="origName-label" class="property-label"><g:message code="object.modified.label" default="Datum geändert" /></span>
					
						<span class="property-value" aria-labelledby="origName-label">${objectInstance.getFormattedModifiedDate()}</span>
				</li>
				</g:if>
					<g:if test="${objectInstance?.static_nondisclosure_limit}">
					<li class="fieldcontain">
					<span id="origName-label" class="property-label">Startdatum einer Veröffentlichung</span>
					
						<span class="property-value" aria-labelledby="origName-label">${objectInstance.static_nondisclosure_limit}</span>
				</li>
				</g:if>
					<g:if test="${objectInstance?.dynamic_nondisclosure_limit}">
					<li class="fieldcontain">
					<span id="origName-label" class="property-label">Startdatum nach Gesetz"</span>
					
						<span class="property-value" aria-labelledby="origName-label">${objectInstance.static_nondisclosure_limit}</span>
				</li>
				</g:if>
					<g:if test="${objectInstance?.last_checked}">
					<li class="fieldcontain">
					<span id="origName-label" class="property-label">Letzte Überprüfung (Audit)</span>
					
						<span class="property-value" aria-labelledby="origName-label">${objectInstance.last_checked}</span>
				</li>
				</g:if>
					<g:if test="${objectInstance?.original_formats}">
					<li class="fieldcontain">
					<span id="origName-label" class="property-label">Enthaltene Formate aller zu diesem Objekt eingelieferten SIP</span>
						  <g:each in="${objectInstance.original_formats?.split(",")}">
						  	 <g:if test="${!it.startsWith("danrw")}">
						  	 <g:link url="http://www.nationalarchives.gov.uk/PRONOM/${it}" target="_blank"><span class="property-value" aria-labelledby="urn-label">${it}</span></g:link>
						   </g:if><g:else><span class="property-value" aria-labelledby="urn-label">${it}</span></g:else></g:each>
					</li>
				</g:if>
				
					<g:if test="${objectInstance?.most_recent_formats}">
					<li class="fieldcontain">
					<span id="origName-label" class="property-label">Formate der aktuellsten Repräsentation (DIP)</span>
					
						  <g:each in="${objectInstance.most_recent_formats?.split(",")}">
						  	 <g:if test="${!it.startsWith("danrw")}">
						  	 <g:link url="http://www.nationalarchives.gov.uk/PRONOM/${it}" target="pronom"><span class="property-value" aria-labelledby="urn-label">${it}</span></g:link>
						   </g:if><g:else><span class="property-value" aria-labelledby="origName-label">${it}</span></g:else></g:each>
				</li>
				</g:if>
					<g:if test="${objectInstance?.most_recent_secondary_attributes}">
					<li class="fieldcontain">
					<span id="origName-label" class="property-label">Codecs der Oberflächenansicht</span>
						  <g:each in="${objectInstance.most_recent_secondary_attributes?.split(",")}">
						  	 <span class="property-value" aria-labelledby="origName-label">${it}</span>
						  </g:each>
				</li>
				</g:if>
				<g:if test="${objectInstance?.ddb_exclusion!=null}">
					
					<li class="fieldcontain">
					<span id="origName-label" class="property-label">Beschränkung Harvesting DDB</span>
						  	 <span class="property-value" aria-labelledby="origName-label">${objectInstance.ddb_exclusion}</span>
					</li>
				</g:if>
				
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${objectInstance?.id}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
