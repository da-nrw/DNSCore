<html>
	<head>
		<title>Statistiken</title>
		<meta name="layout" content="main">
	</head>
	<body>
		<div class="page-body">
			<div class="blue-box"></div>
			<h2 id="page-header">Statistik über die eingelieferten Objekte</h2> 
			
			<g:if test="${msg}">
				<div class="message" role="status">${msg}</div>
			</g:if>
			<g:form>
				<fieldset class="abstand-oben-imp buttons ">
					<g:actionSubmit class="pdf" action="pdfCreate"
						value="${message(code: 'default.button.pdf.label', default: 'generate pdf')}" />
				</fieldset>
			</g:form>
			<g:form controller="statistics" >
			  <h3>Speicherbelegung</h3>
			
				<ul class="property-list object property-list-position">
					<li class="fieldcontain">
						<span class="property-label">AIP-Size aller bisher eingelieferten Pakete</span> 
						<span class="property-value" >${aipSizeGesamt}  GigaByte</span>
					</li>
					<li class="fieldcontain">
						<span class="property-label">bisher belegter Speicher</span>
						<span class="property-value">XXX  GigaByte</span>
					</li>					
				</ul>
				
				<h3>Dateien</h3>
				<ul class="property-list object property-list-position">
					<li class="fieldcontain">
						<span class="property-label">Anzahl der bisher archivierten Dateien</span>
						 <span class="property-value">${archived}</span>
					</li>
				</ul>
				
				<h3> Qualitative Situation des Archivgutes </h3>
				<ul class="property-list object property-list-position">
					<li class="fieldcontain">
						<span class="property-label">Qualität der bisher archivierten Dateien</span>
						<span class="property-value" style="overflow:auto; width: 300px;">
				             <table>
								 <thead class="thead-line">							
									<tr>
				  					   <g:sortableColumn property="quality-flag" title="${message(code: 'object.quality_flag.label', default: 'Qualitätslevel')}" />
									   <g:sortableColumn property="anzahl" title="${message(code: 'object.anzahl.label', default: 'Anzahl')}"  />	
									</tr>
								 </thead>
								 <tbody>
				        		    <g:each in="${qualityLevels}" var="qualityLevel" status="i">
				        		       <tr class="${ ((i % 2) == 0 ? 'odd' : 'even') }">
					        		       <td>${qualityLevel.last()}</td>
					        		       <td>${qualityLevel.first()}</td>
				        		      </tr>
				        		    </g:each>
				        		    <tr><td>${sqlLeer}</td></tr>
				             	</tbody>
				             </table>
						 </span>
					</li>
				</ul>
				<h3>Auswertung über PUID</h3>
				
				<ul class="property-list object property-list-position">
					<li class="fieldcontain">
						<span class="property-label">Dateiformate im SIP</span>
						<span class="property-value" style="overflow:auto; width: 600px;">
				             <table>
								 <thead class="thead-line">							
									<tr>
				  					   <g:sortableColumn property="puid"  title="Eingelieferte Dateiformate" />
									   <g:sortableColumn property="count"  title="Anzahl pro PUID"  />	
									</tr>
								 </thead>
								 <tbody>
				        		    <g:each in="${formatsSIP.keySet()}" var="counterPuidSIP" status="j">
				        		       <tr class="${ ((j % 2) == 0 ? 'odd' : 'even') }">
				        		           <td>${counterPuidSIP}</td>
					        		       <td>${formatsSIP.getAt(counterPuidSIP)}</td>
				        		      </tr>
				        		    </g:each>
				        		    <tr><td>${sqlLeer}</td></tr>
				             	</tbody>
				             </table>
						 </span>
					</li><li class="fieldcontain">
						<span class="property-label">Dateiformate im DIP</span>
						<span class="property-value" style="overflow:auto; width: 600px;">
				             <table>
								 <thead class="thead-line">							
									<tr>
				  					   <g:sortableColumn property="puid"  title="Eingelieferte Dateiformate" />
									   <g:sortableColumn property="count"  title="Anzahl pro PUID"  />	
									</tr>
								 </thead>
								 <tbody>
				        		    <g:each in="${formatsDIP.keySet()}" var="counterPuidDIP" status="j">
				        		       <tr class="${ ((j % 2) == 0 ? 'odd' : 'even') }">
				        		           <td>${counterPuidDIP}</td>
					        		       <td>${formatsDIP.getAt(counterPuidDIP)}</td>
				        		      </tr>
				        		    </g:each>
				        		    <tr><td>${sqlLeer}</td></tr>
				             	</tbody>
				             </table>
						 </span>
					</li>
				</ul>
			</g:form>
		</div>
	</body>
</html>