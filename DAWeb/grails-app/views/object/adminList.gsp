<%@ page import="daweb3.Object" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'object.label', default: 'Object')}" />
		<asset:stylesheet src="accordion.css" />
		<title>DA-NRW Objekte</title>
		<r:require module="messagebox"/>
		<g:javascript>
			function queuedFor(result) {
				var type = "error";
				if (result.success) type = "info";
				var messageBox = $("<div class='message-box'></div>");
				$("#page-body").prepend(messageBox);
				messageBox.message({
					type: type, message: result.msg
				});
			}
		</g:javascript>
	</head>
	<body>
		<div id="page-body">
			<a href="#list-object" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
			<h1>eingelieferte AIP's</h1>
			<div class="nav" role="navigation">
				<ul>
					<g:if test="${objArt=='gesamten'}">
						<li id="aktuell"><g:link url="[action: 'listAll', controller: 'object']">alle Objekte</g:link></li>
					</g:if>
					<g:else>
						<li><g:link url="[action: 'listAll', controller: 'object']">alle Objekte</g:link></li>
					</g:else>
					<g:if test="${objArt=='verarbeiteten'}">
						<li id="aktuell"><g:link url="[controller: 'object', action: 'archived']">archivierte Objekte</g:link></li>
					</g:if>
					<g:else>
						<li><g:link url="[controller: 'object', action: 'archived']">archivierte Objekte</g:link></li>
					</g:else>
					<g:if test="${objArt=='sich in Bearbeitung befindlichen'}">
						<li id="aktuell"><g:link url="[controller: 'object', action: 'working']">Objekte in Verarbeitung</g:link></li>
					</g:if>
					<g:else>
						<li><g:link url="[controller: 'object', action: 'working']">Objekte in Verarbeitung</g:link></li>
					</g:else>
					<!--  	<li><a class="list" href="${createLink(controller: 'object', action: 'error')}">fehlerhafte Objekte</a></li> -->
				</ul>
			</div><br>
			<button class="accordion">Filter
				<g:if test="${params.search}"><br>
		    		<g:if test="${!params.search?.origName.isEmpty()}">
		    			<span style="margin-right: 25px"><i>Originalname: </i>${params.search?.origName}</span>
		    		</g:if> 
		    		<g:if test="${!params.search?.urn.isEmpty()}">
		    			 <span style="margin-right: 25px"><i>URN: </i>${params.search?.urn}</span>
		    		</g:if> 
		    		<g:if test="${!params.search?.identifier.isEmpty()}">
		    			<span style="margin-right: 25px"><i>Identifier: </i>${params.search?.identifier}</span>
		    		</g:if> 
		    		<g:if test="${params.searchContractorName != null}">
		    			<g:if test="${!params.searchContractorName.isEmpty()}">
		    				<span style="margin-right: 25px"><i>Contractor: </i>${params.searchContractorName}</span>
		    			</g:if>
		    		</g:if> 
					<div>
						<g:if test="${params.searchDateType != null } ">
	    					<g:if test="${params.searchDateType == 'createdAt'}">Datumsbereich erstellt</g:if>
	    					<g:if test="${params.searchDateType == 'modifiedAt'}">Datumsbereich geändert</g:if>
			    		</g:if>    
			    		<g:if test="${params.searchDateStart != null}">
			    			<g:if test="${params.searchDateStart != '0'}">
			    				<span style="margin-right: 25px"><i>Von Datum: </i>${params.searchDateStart}</span>
			    			</g:if>
			    		</g:if> 	
			    		<g:if test="${params.searchDateEnd != null}">
			    			<g:if test="${params.searchDateEnd != '0'}">
			    				<span style="margin-right: 25px"><i>Bis Datum: </i>${params.searchDateEnd}</span>
			    			</g:if>
			    		</g:if> 
			    	</div>
		    	</g:if> 
		    </button>
		    <div class="panel">
	            <g:form name="searchForm" action="list">
				 	<g:hiddenField name="filterOn" value="${filterOn}" />
	            	<table>
	            		<tr>
	            			<td>Original Name:</td>
	            			<td><g:textField name="search.origName" value="${params.search?.origName}" size="50"/></td>
	            		</tr>
	            		<tr>
	            			<td>URN:</td>
	            			<td><g:textField name="search.urn" value="${params.search?.urn}" size="50"/></td>
	            		</tr>
	            			<tr>
	            			<td>Identifier:</td>
	            			<td><g:textField name="search.identifier" value="${params.search?.identifier}" size="50"/></td>
	            		</tr>
	            		
	            		<tr>
	            			<td>Datumsbereich:	</td>	
		            		<td>
		            			<g:select id="datetype" name="searchDateType" from="${['Datum erstellt','Datum geändert']}" keys="${['createdAt','modifiedAt']}" value="${params.searchDateType}" noSelection="[null:'-Bitte wählen-']"/>
		            		</td>
						</tr>
	            		<tr>
	            			<td>Von Datum: </td>
	            			<td>
	            			<g:if test="${params.search?.searchDateStart != null}" >
	            					<g:datePicker name="searchDateStart" default="none" noSelection="['':'']"  value="${params.search?.searchDateStart.date.format('TT.MM.JJJJ HH:mm')}"/>
	            				</g:if>
	            				<g:else>
	            					<g:datePicker name="searchDateStart" default="none" noSelection="['':'']"  value="${params.search?.searchDateStart}"/>
	            				</g:else>
							</td>
	            		</tr>
	            		<tr>
	            			<td>Bis Datum: </td>
	            			<td>
	            				<g:if test="${params.search?.searchDateEnd != null}" >
	            					<g:datePicker name="searchDateEnd" default="none" noSelection="['':'']"  value="${params.search?.searchDateEnd.date.format('dd.MM.yyyy HH:mm')}"/>
	            				</g:if>
	            				<g:else>
	            					<g:datePicker name="searchDateEnd" default="none" noSelection="['':'']"  value="${params.search?.searchDateEnd}"/>
	            				</g:else>
	            				<% // fix for https://github.com/zoran119/grails-jquery-date-time-picker/issues/12 %>
		            			<script type="text/javascript">
			            			 $(document).ready(function(){
			            			$("#searchDateStart").val("${params.searchDateStart}")
			            			$("#searchDateEnd").val("${params.searchDateEnd}")
			            			 })
		            			</script>
	            			</td>
	            		</tr>
 	            		<g:if test="${admin}"> 
	            			<tr>	
		            			<td>Contractor:</td>
		            			<td>
		            				<g:if test="${params.searchContractorName  == null || params.searchContractorName.isEmpty()}" >
		            					<g:select id="user" name="searchContractorName" from="${contractorList}" optionKey="shortName" noSelection="['':'-Bitte wählen-']" value="${objectInstance?.contractorList?.shortName}" class="many-to-one"/>
		            				</g:if>
		            				<g:if test="${params.searchContractorName  != null && !params.searchContractorName.isEmpty()}" >
		            					<g:select id="user" name="searchContractorName" from="${contractorList}" optionKey="shortName" noSelection="['':'-Bitte wählen-']" value="${params.searchContractorName}" class="many-to-one"/>
		            				</g:if>
		            			</td>
	            			</tr>
 	            		</g:if> 
	            		<tr>
	            			<td></td>
	            			<td>
	            				<g:submitButton name="submit" value="Filter anwenden"/>
	           					<g:submitButton name="loeschen" type="submit" value="Filter löschen"/>
		           			</td>
		           			<g:javascript>
			           			$(document).ready(function(){
			           				 	$("#loeschen").click(function() {                				 
					            			$('#searchForm').find(':input').each(function() {
					            	            switch(this.type) {
					                            case 'text':
					                            	$(this).val('');
					                                break;                      
					                            case 'textarea':
					                                $(this).val('');
					                                break;
					            			 	case 'hidden':
					                                $(this).val('0');
					                                break;	
					            			 	case 'select-one':
						                            $(this).val(null);
						                            break;
						                        case 'datePicker':
					                        		$(this).val(null);
					                        		break;
					                            }
					            			});
			           				    });
			           			});
			           		</g:javascript>
	            		</tr>
	            	</table>     
	            </g:form>
	            <script>
					var acc = document.getElementsByClassName("accordion");
					var i;
					for (i = 0; i < acc.length; i++) {
					    acc[i].onclick = function(){
					        this.classList.toggle("active", true);
					        var panel = this.nextElementSibling;
					        if (panel.style.display == "block") {
					            panel.style.display = "none";
					        } else {
					            panel.style.display = "block";
					        }		
					    }
					}
				</script>
	        </div>
			<div id="list-object" class="content scaffold-list" role="main">
				<h1>Ihre ${objArt} DA-NRW Objekte (${objectInstanceList.size()} Treffer)</h1>
				<g:if test="${flash.message}">
					<div class="message" role="status">${flash.message}</div>
				</g:if>
				<g:formRemote name="myForm" on404="alert('not found!')" 
	              url="[controller: 'object', action:'queueAllForRetrieval']" 
	              onLoaded="queuedFor(data)">
	              <div style="overflow:auto; height: 600px">
					 <table>
						<thead>
							<tr>
								<th><g:message code="object.identifier.label" default="Ident" /></th>
								<g:sortableColumn property="urn" title="${message(code: 'object.urn.label', default: 'Urn')}" />
		     					<g:sortableColumn property="user" title="${message(code: 'object.user.label', default: 'Contractor')}" />
								<g:sortableColumn property="origName" title="${message(code: 'object.origName.label', default: 'Orig Name')}" />
								<g:sortableColumn property="createdAt" title="${message(code: 'object.created.label', default: 'Erstellt')}" />
								<g:sortableColumn property="modifiedAt" title="${message(code: 'object.modified.label', default: 'Geändert')}" />
								
								<g:if test="${admin}">
									<g:sortableColumn style="text-align: center" property="object_state" title="${message(code: 'object.object_state.label', default: 'Objekt Status')}" />
									<th style="text-align: center">Überprüfen</th>
									<th style="text-align: center">Pres. Derivate</th>
									<th style="text-align: center">Index</th>
								</g:if>
								<th style="text-align: center">Publ.</th>
								<th style="text-align: center">Anfordern				
									<g:if test="${!paginate}">
											<g:actionSubmitImage value="submit" action="submit"
	                     						src="${resource(dir: 'images/icons', file: 'boxdownload32.png')}" />
									</g:if>
								</th>
								<th style="text-align: center">Entnahme	</th>		
							</tr>
						</thead>
						<tbody>
						<g:each in="${objectInstanceList}" status="i" var="objectInstance">
							<g:set var="statusCode" value="100" />
							<g:if test="${admin}">				
								<g:set var="statusCode" value="${objectInstance.getStatusCode()}" />
							</g:if>
							<tr class="${ ((i % 2) == 0 ? 'odd' : 'even')}">
									<td>${fieldValue(bean: objectInstance, field: "identifier")}</td>
							
								<td><g:link action="show" id="${objectInstance.id}">${objectInstance.getFormattedUrn()}</g:link></td>
							
								<td>${fieldValue(bean: objectInstance, field: "user")}</td>
							
								<td>${fieldValue(bean: objectInstance, field: "origName")}</td>
								<td>${objectInstance.getFormattedCreatedDate()}</td>
								<td>${objectInstance.getFormattedModifiedDate()}</td>
								
								<td style="text-align: center">
									<g:if test="${statusCode == 1}">
										<asset:image style="width:16px; height:16px" src="/icons/warning32.png"/>
									</g:if>
									<g:elseif test="${statusCode == 2}">
										<asset:image style="width:16px; height:16px" src="/icons/clock32.png"/>
									</g:elseif>
									<g:elseif test="${statusCode == 0}">
										<asset:image style="width:16px; height:16px" src="/icons/check32.png"/>
									</g:elseif>
								</td>
								<g:if test="${!objectInstance.isInWorkflowButton()}">
								<td style="text-align: center">
									<g:remoteLink action="queueForInspect" onLoaded="queuedFor(data)" id="${objectInstance.id}">
										<asset:image style="width:16px; height:16px" src="/icons/search32.png"
										title="${message(code: 'default.ltp.icon.queueForInspect', default: 'Objekt zum manuellen Überprüfen anfordern')}" 
										alt="${message(code: 'default.ltp.icon.queueForInspect', default: 'Objekt zum manuellen Überprüfen anfordern')}"/>
									</g:remoteLink>
								</td>
								<td style="text-align: center">
									<g:remoteLink action="queueForRebuildPresentation" onLoaded="queuedFor(data)" id="${objectInstance.id}">
										<asset:image style="width:16px; height:16px" src="/icons/exchange32.png"
										title="${message(code: 'default.ltp.icon.rebuildPR', default: 'Objekt für Präsentation neu erzeugen')}" 
										alt="${message(code: 'default.ltp.icon.rebuildPR', default: 'Objekt für Präsentation neu erzeugen')}"/>
									</g:remoteLink>
								</td>
								<td style="text-align: center">
									<g:remoteLink action="queueForIndex" onLoaded="queuedFor(data)" id="${objectInstance.id}">
										<asset:image style="width:16px; height:16px" src="/icons/exchange32.png"
										title="${message(code: 'default.ltp.icon.rebuildIndex', default: 'Objekt neu indexieren')}" 
										alt="${message(code: 'default.ltp.icon.rebuildIndex', default: 'Objekt neu indexieren')}"/>
									</g:remoteLink>
								</td>
								</g:if>
								<g:else>
								<td colspan="3" text-align: center">Objekt in der Verarbeitung</td>
								</g:else>
								<td>	
									<g:if test="${objectInstance.getPublished_flag()==1}">
										<g:link url="${objectInstance.getPublicPresLink()}" target="_blank"><asset:image width="16px" height="16px" src="/icons/globe.png"/></g:link>
									</g:if>
									<g:if test="${objectInstance.getPublished_flag()==2}">
										<g:link url="${objectInstance.getInstPresLink()}" target="_blank"><asset:image width="16px" height="16px" src="/icons/globe.png"/></g:link>
									</g:if>
									<g:if test="${objectInstance.getPublished_flag()==3}">
										<asset:image width="16px" height="16px" src="/icons/globe.png"/>
									</g:if>
								</td>
								<g:if test="${!objectInstance.isInWorkflowButton()}">
									<td style="text-align: center">
										<g:remoteLink action="queueForRetrieval" onLoaded="queuedFor(data)" id="${objectInstance.id}">
											<asset:image width="16px" height="16px" src="/icons/boxdownload32.png"/>
										</g:remoteLink>
									</td>
								</g:if><g:else><td></td></g:else>
								<td style="text-align: center">
									<g:if test="${new File(baseFolder+ "/"+ objectInstance.identifier +".tar").exists()}">
										 <g:link controller="outgoing" action="download" params="['filename':objectInstance.identifier +'.tar']">
											<asset:image style="width:16px; height:16px" src="/icons/delivery.png"/>
										</g:link>
									</g:if>
								</td>
							</tr>
						</g:each>
						</tbody>
					</table>(AdministartorView)
				  </div>
				</g:formRemote>
				<g:if test="${paginate}" >
					<!-- workaround weil paginate die search map zerhackstückelt -->
					<g:set var="searchParams" value="${paramsList}"/>
					<div class="pagination">
						<g:paginate total="${objectInstanceTotal}" params="${searchParams}" />
					</div>
				</g:if>
			</div>
		</div>
	</body>
</html>
