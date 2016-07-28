<%@page import="com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList"%>
<%@ page import="daweb3.User" %>
<%@page import="daweb3.Object" %>
<%@ page import="daweb3.Event" %>
<%@ page import="daweb3.DAFile" %>

<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>Premis</title>
		<style type="text/css" media="screen">

			.list ul {
				list-style: none;
				list-style-position: inside;
			}
	
			.list li {
				line-height: 1.3;
				list-style-position: inside;
				margin: 0.5em;
			}

			#detail {
				background-color: #f4f4f4;
				padding: 10px;
				border: 1px solid #d0d0d0;
				box-shadow: 4px 4px 4px #888888;
				margin-left: 30px;
				margin-top: 5px;
				margin-bottom: 15px;
			}

			#pkg_file {
				/*font-size: 13pt;*/
				font-weight: bold;
			}
			
			.file {
				cursor: auto;
			}

			button {
				cursor: pointer;
			}

			.list, .list * {
				padding: 0;
				margin: 0;
				padding-left: 5px;
				/*list-style: none;*/
			}
			 
			.list {
				margin-left: 20px;
			}

			.list label.inputList[for]::before {
				content: url(../images/icons/plus-circle-icon.png);
				display: inline-block;
				margin: 2px 0 0;
				width: 12px;
				height: 12px;
				vertical-align: top;
				text-align: center;
				color: #51194B;
				font-size: 12px;
				line-height: 12px;
			}

			.list li ul {
				margin: 0 0 0 22px;
			}

			.list {
				font: normal 14px/16px ;
				color: #333;
			}

			.list input.inputList[type="checkbox"] {
				display: none;
			}
	
			.list label.inputList[for]::before {
				-webkit-transform: translatex(-24px);
				-moz-transform: translatex(-24px);
				-ms-transform: translatex(-24px);
				-o-transform: translatex(-24px);
				transform: translatex(-24px);
			}

			.list input.inputList[type="checkbox"][id]:checked ~ label[for]::before {
				content: url(../images/icons/minus-circle-icon.png);
			}

			.list input.inputList[type="checkbox"][id]:not(:checked) ~ ul,
			.list input.inputList[type="checkbox"][id]:not(:checked) ~ div,
			.list input.inputList[type="checkbox"][id]:not(:checked) ~ button
			 {
				display: none;
			}

			.list label.inputList:not([for]) {
				margin: 0 8px 0 0;
			}
			
			.question {
				width:16px; 
				height:16px;
				vertical-align: middle;
			}
			
			.questionDet {
				font-size: 11pt;
			}
			
			.testTable label.inputTable[for]::before {
				/*content: url(../images/icons/plus-circle-icon.png);*/
				display: inline-block;
				margin: 2px 0 0;
				width: 12px;
				height: 12px;
				vertical-align: top;
				text-align: center;
				color: #51194B;
				font-size: 12px;
				line-height: 12px;
			}
			
			.testTable input.inputTable[type="checkbox"] {
				display: none;
			}
	
			.testTable label.inputTable[for]::before {
				-webkit-transform: translatex(-24px);
				-moz-transform: translatex(-24px);
				-ms-transform: translatex(-24px);
				-o-transform: translatex(-24px);
				transform: translatex(-24px);
			}

			.testTable input.inputTable[type="checkbox"][id]:checked ~ label[for]::before {
				/*content: url(../images/icons/minus-circle-icon.png);*/
			}

			.testTable input.inputTable[type="checkbox"][id]:not(:checked) ~ ul,
			.testTable input.inputTable[type="checkbox"][id]:not(:checked) ~ div,
			.testTable input.inputTable[type="checkbox"][id]:not(:checked) ~ button
			 {
				display: none;
			}
			
		</style>
		
		
				<r:require module="messagebox"/>
	<r:script>
			$(function() {
				$("#filter").accordion({ collapsible: true, active: false });
			});
			function queuedFor(result) {
				var type = "error";
				if (result.success) type = "info";
				var messageBox = $("<div class='message-box'></div>");
				$("#page-body").prepend(messageBox);
				messageBox.message({
					type: type, message: result.msg
				});
			}
		<!-- </r:script>
		
		
		 <r:script type="text/javascript"> -->
	
			<!-- $(function() {
				$("#button_test").click(){
				
				}
			});-->

	
			$(window).load(function() {
			
				var countLists = parseInt($("#lists").val());
				
				for(var h = 0; h < 2; h++) {
					for(var i = 0; i < countLists; i++) {
						$("#button_" + h + "_" + i).click({index:i, hindex:h}, function(event) {
							console.log("click");
							var j = event.data.index;
							var k = event.data.hindex;
							$(this).remove();
							$("#list_" + k + "_" + (j+1)).show();
							$("#button_" + k + "_" + (j+1)).show();
						});
					}
				}
			});

			function xmlAnzeigen() {
				console.log("xml");
				var xml = window.open();
				xml.document.write("/home/julia/Desktop/premis_neu.xml");
				xml.focus();
			};


			$(function() {
				$("#filter").accordion({ collapsible: true, active: false });
			});


			function showMore(visible) {
				$('').style,.display = visible ? "inline" : "none";
				}
				Ajax.Responders.register({
				onLoading: function() {
					showMore(true);
				},
				onComplete: function() {
					if(!Ajax.activeRequestCount) showMore(false);
				}
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
		
		<script type="text/javascript">
		 $(document).ready(function(){
				$("#filter").accordion({ collapsible: true, active: false });
				
		 });
		</script>
		<div id="filter" style="margin: 0.8em 0 0.3em">
			<h1><a href="#">Filter</a></h1> 
            <g:form name="searchForm" action="premis" params="['objName': params.objName, 'pkg': params.pkg, 'objectIdentifier': params.objectIdentifier]">
            
            	<table>
            		<tr>
            			<td>Dateiname:</td>
            			<td><g:textField name="search.dataName" value="${params.search?.dataName}" size="50"/></td>
            		</tr>
            		<tr>
            			<td>Dateiendung:</td>
            			<td><g:textField name="search.dataExtension" value="${params.search?.dataExtension}" size="50"/></td>
            		</tr>
            		
            		<!-- <tr>
            			<td>URN:</td>
            			<td><g:textField name="search.urn" value="${params.search?.urn}" size="50"/></td>
            		</tr>
            			<tr>
            			<td>Identifier:</td>
            			<td><g:textField name="search.identifier" value="${params.search?.identifier}" size="50"/></td>
            		</tr>-->


            		<tr>
            			<td></td>
            			<td><g:submitButton name="submit" value="Filter anwenden"/><g:submitButton name="loeschen" type="submit" value="Filter löschen"/></td>
            			<script type="text/javascript">
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
			                            }
			            			});
			            			params.offset1 = 0;
	            				    params.offset2 = 0;
	            				    params.first = true;
	            				    
            				    });
            				    
            			});</script>
            		</tr>
            	</table>     
            </g:form>
        </div>
<!-- 
        ${params.offset1 }offset${isOff }${dataName }
tmpF${tmpDafiles.size() }
tmpM${tmpMeta.size() }
<br/>
${zeit1 }
<br/>
${zeit2 }
<br/>

<br/>
${o1 }
<br/>
${oo1 }
<br/>
${o2 }
<br/>
${oo2 }
<br/>
${first }${params.first }
<br/>
${s }

${zeit1 }<br/>
${new Date() }


${dafiles.size() }
${dafiles }
<br/>
${tmpDafiles.size() }
${tmpDafiles }
-->



		<div id="list-object" class="content scaffold-list" role="main">
					<!-- ${params.objectIdentifier } -->
			<h1 style="font-weight: bold;">Premis zu Objekt ${object.getOrigName() } (${object.getIdentifier() }, ${object.getUrn() })</h1>
			<div class="list">
				<div style="padding: 0px;">In dem Objekt sind folgende Dateien enthalten: </div>

				<ul>

				<!--<g:render template="premisFileList" model="[dafiles: tmpDafiles, dafilesSize: dafilesMaxSize, fileMeta: "Digitalisate", i: 0]"/>
				-->
				<%
				def writeFiles = {dafiles, dafilesSize, fileMeta, i -> 
				%>

					<input id="lists" style="display:none;" value="${(int)(max/numberShowFiles) + 1}" />

					<g:if test="${dafiles.size() > 0}">
					
						<li><input class="inputList" type="checkbox" id="${fileMeta}"/>
							<label class="inputList" id="pkg_file" for="${fileMeta}">${fileMeta} (${dafiles.size()} von ${dafilesSize})</label>
				
							<g:set var="counter" value="${0}"/>
							<g:set var="listCounter" value="${0}"/>

							<g:each var="dafile" in="${dafiles}">
								<g:if test="${counter % numberShowFiles == 0}">
									<g:if test="${listCounter > 0}">
										<ul id="list_${i}_${listCounter}" style="list-style: disc outside none; margin-left: 0;"> <!-- display:none;"> -->
									</g:if>	
									<g:else>
										<ul id="list_${i}_${listCounter}" style="list-style: disc outside none; margin-left: 0;">
									</g:else>
								</g:if>		
								<g:set var="f" value="${dafile.getValue().first()}"/>
								<li><input class="inputList" type="checkbox" id="${fileMeta + counter}"/>
								<label class="inputList" id="pkg_file" for=${fileMeta + counter}> ${dafile.getKey() + " (" + f[0][1]?.getExtension()}) </label><div id="detail">
									Die aktuelle Version der Datei
									<g:if test="${f[0][0]?.getClass() == Event?.class}">
										"${targetNames.get(dafile.getKey())}"
									</g:if>
									<g:elseif test="${f[0][0]?.getClass() == DAFile?.class}">
										"${f[0][0]?.getRelative_path()}"
									</g:elseif>
									liegt im Paket ${f[1].getName()} (${f[1].getContainer_name()} vom ${pkgEvents.get(f[1].getId())[0].getFormatedDate()}) in dem Format 
									<g:if test="${f[0][0]?.getClass() == Event?.class}">
										"${f[0][2]?.getExtension()}" (${f[0][2]?.getPuid()}).
									</g:if>
									<g:elseif test="${f[0][0]?.getClass() == DAFile?.class}">
										"${f[0][1]?.getExtension()}" (${f[0][1]?.getPuid()}).
									</g:elseif>
									<br/>
									<!--
									<ol>
									<g:each var="file" in="${dafile.getValue()}">
										<li> in ${file[1]}, 
										<g:if test="${file[0][0]?.getClass() == Event?.class}"> 
											konvertiert, ursprüngliches Format: "${file[0][1]?.getExtension()}" (${file[0][1]?.getPuid()}), konvertiert nach: "${file[0][2]?.getExtension()}" (${file[0][2]?.getPuid()})
										</g:if>
										<g:elseif test="${file[0][0]?.getClass() == DAFile?.class}"> 
											nicht konvertiert, Format: "${file[0][1]?.getExtension()}" (${file[0][1]?.getPuid()})
										</g:elseif>
										
										</li>
									</g:each>
									</ol>	-->
									<table>
										<tr>
											<th>Paket</th>
											<g:if test="${fileMeta!='Metadaten'}">
												<th>konvertiert</th>
												<th>altes Format</th>
												<th>neues Format</th>
											</g:if>
										</tr>
										<g:each var="file" in="${dafile.getValue()}" status="countVersion">
											<g:if test="${countVersion == 0}">	
												<tr class="testTable" style="background-color: #F7F7DA;">	
											</g:if>
											<g:else>
												<tr class="testTable">
											</g:else>
												<td>
													<input class="inputTable" type="checkbox" id="${fileMeta + counter}_pkg_${countVersion}"/>
													<label class="inputTable" for=${fileMeta + counter}_pkg_${countVersion}>
														Paket ${file[1].getName()} (${file[1].getContainer_name()} vom ${pkgEvents.get(file[1].getId())[0].getFormatedDate()}, ${file[1].getPkgName()}) 
														<g:img class="question" uri="/images/icons/questionbook32.png"/>
													</label>
													<g:set var="sip" value="${pkgEvents.get(file[1].getId())[0]}" />
													<g:set var="ing" value="${pkgEvents.get(file[1].getId())[1]}" />
													<div class="questionDet">
														Das SIP wurde mit ${sip.getAgentName()} am ${sip.getFormatedDate()} um ${sip.getFormatedTime()} Uhr erstellt. 
														<br/>
														Am ${ing.getFormatedDate()} um ${ing.getFormatedTime()} Uhr wurde das Paket von ${ing.getAgentName()} eingeliefert.
													</div>												
												</td>
												<g:if test="${fileMeta!='Metadaten'}">
													<g:if test="${file[0][0]?.getClass() == Event?.class}"> 
														<td>
															<input class="inputTable" type="checkbox" id="${fileMeta + counter}_det_${countVersion}"/>
															<label class="inputTable" for=${fileMeta + counter}_det_${countVersion}>
																Details
																<g:img class="question" uri="/images/icons/questionbook32.png"/>
															</label>
															<div class="questionDet">
																${file[0][0]?.getFormatedDate()} ${file[0][0]?.getFormatedTime()}
																<br/>
																${file[0][0]?.getDetail()}
															</div>
														</td>
														<td>"${file[0][1]?.getExtension()}" (${file[0][1]?.getPuid()})</td>
														<td>"${file[0][2]?.getExtension()}" (${file[0][2]?.getPuid()})</td>
													</g:if>
													<g:elseif test="${file[0][0]?.getClass() == DAFile?.class}"> 
														<td></td>
														<td>"${file[0][1]?.getExtension()}" (${file[0][1]?.getPuid()})</td>
														<td></td>
													</g:elseif>
												</g:if>
											</tr>
										</g:each>
									</table>			
								</div></li>

								<g:if test="${(counter+1) % (numberShowFiles) == 0 || counter >= dafiles.size()-1}">
									</ul>
									<!--<g:if test="${counter < dafiles.size()-1}">
										<g:if test="${listCounter > 0}">
											<button id="button_${i}_${listCounter}" style="padding:2px; margin-left: 15px; display:none;"> weitere Dateien anzeigen </button>
										</g:if>
										<g:else>
											<button id="button_${i}_${listCounter}" style="padding:2px; margin-left: 15px;"> weitere Dateien anzeigen </button>
										</g:else>	
									</g:if>-->
									<g:set var="listCounter" value="${listCounter + 1}"/>
								</g:if>
								<g:set var="counter" value="${counter + 1}"/>
							</g:each>
							<g:if test="${dafiles?.size() < dafilesSize}">
								<g:form name="test_form" action="premis" params="['objName': params.objName, 'pkg': params.pkg, 'fileMeta': fileMeta, 'objectIdentifier': params.objectIdentifier, ('offset'+i): (params.int('offset'+i) + 10), 'first': params.boolean('first')]">
									<button id="button_test_${fileMeta}" style="padding:2px; margin-left: 15px;">weitere Dateien anzeigen</button>
									<!--<g:submitToRemote oncomplete="showMore(false)" onloading="showMore(true)" update="premisfilelist" url="[controller:'object', action:'showNext']" value="mehr">
									</g:submitToRemote>-->
								</g:form>
							</g:if>
						</li>
					</g:if>
					
				<%
				}
				writeFiles(tmpDafiles, dafilesMaxSize, "Digitalisate", 1)
				writeFiles(tmpMeta, metaMaxSize, "Metadaten", 2)
				%>
					
						<!-- 	
					<g:if test="${meta.size() > 0}">
					
						<li><input type="checkbox" id="meta"/>
							<label id="pkg_file" for="meta">Metadaten</label>
				
							<g:set var="counter" value="${0}"/>
							<g:set var="listCounter" value="${0}"/>

							<g:each var="metaDat" in="${meta}">
								<g:if test="${counter % numberShowFiles == 0}">
									<g:if test="${listCounter > 0}">
										<ul id="list_1_${listCounter}" style="list-style: disc outside none; margin-left: 0; display:none;">
									</g:if>	
									<g:else>
										<ul id="list_1_${listCounter}" style="list-style: disc outside none; margin-left: 0;">
									</g:else>
								</g:if>							
								<li><label class="file" id="pkg_file"> ${metaDat.getKey()} </label><div id="detail">
									<g:each var="file" in="${metaDat.getValue()}">
										<g:if test="${file[0][0]?.getClass() == Event?.class}"> 
											konvertiert von ${file[0][1]?.getExtension()} ( ${file[0][1]?.getPuid()} ) nach ${file[0][2]?.getExtension()} ( ${file[0][1]?.getPuid()} )
										</g:if>
										<g:elseif test="${file[0][0]?.getClass() == DAFile?.class}"> 
											nicht konvertiert, Format: ${file[0][1]?.getExtension()} ( ${file[0][1]?.getPuid()} )
										</g:elseif>
										<div>Datei in ${file[1]}</div>
									</g:each>				
								</div></li>

								<g:if test="${(counter+1) % (numberShowFiles) == 0 || counter >= meta.size()-1}">
									</ul>
									<g:if test="${counter < meta.size()-1}">
										<g:if test="${listCounter > 0}">
											<button id="button_1_${listCounter}" style="padding:2px; margin-left: 15px; display:none;"> weitere Dateien anzeigen </button>
										</g:if>
										<g:else>
											<button id="button_1_${listCounter}" style="padding:2px; margin-left: 15px;"> weitere Dateien anzeigen </button>
										</g:else>	
									</g:if>
									<g:set var="listCounter" value="${listCounter + 1}"/>
								</g:if>
								<g:set var="counter" value="${counter + 1}"/>
							</g:each>

						</li>
					</g:if>-->
					
				</ul>
				
			</div>
		</div>
	</body>
</html>