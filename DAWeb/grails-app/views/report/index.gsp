<html>
	<head>
		<title>Abfragen verarbeiten</title>
		<meta name="layout" content="main">
		<r:require modules="periodicalupdater, jqueryui"/>
		<g:javascript>
			$(function() {
				$("#legend").accordion({ collapsible: true, active: false, autoHeight: false });
			});
			$(function() {
				$("#filter").accordion({ collapsible: true, active: false });
			});
			
			$.PeriodicalUpdater("./snippetIncoming", 
				{
					method: "get",
					minTimeout: 3000,
					maxTimeout: 3000,
					success: function(data) {
						$("#entry-list1").html(data);
					}
				}
			);
			$.PeriodicalUpdater("./snippetOutgoing",
				{
					method: "get",
					minTimeout: 1000,
					maxTimeout: 1000,
					success: function(data) {
						$("#entry-list2").html(data);
					}
				}
			);
		</g:javascript>
	</head>
	<body>
		<div class="page-body">
			
			<div id="items" style="margin-top: -8px;">	
				<div>
					<div class="blue-box" style="padding-top: 6px;"></div>
					<h2>Bericht hochladen</h2>
				</div>
				<g:if test="${msg}">
					<div class="message" role="status">${msg}</div>
				</g:if>
				<g:uploadForm controller="report" method="POST" action="save" enctype="multipart/form-data">
					<div style="float:left; margin-right: 10px"" >
						<input type="file" name="file" />
					</div>
					<div style="float:center"> 
		       			<input type="submit" value="Hochladen" class="style-buttons" />
		       		</div>
				</g:uploadForm><br>
				
				(<strong>Spaltenkopf:</strong> identifier;origName;statuscode;createddate;updateddate;erfolg;bemerkung; </br>semikolongetrennt, EXCEL)	
				<script language="JavaScript">
				function toggle(source) {
					  checkboxes = document.getElementsByName('currentFiles');
					  for(var i in checkboxes)
					    checkboxes[i].checked = source.checked;
				}
				</script>
				<div>
					<div class="blue-box" style="margin-top: 6px;"></div>
					<h2>Wartend auf Aktion</h2>
				</div>	
				<form id="form2" action="decider" >
					<!-- This div is updated through the periodical updater -->
					<div class="entry-list-report">
						<g:include action="snippetIncoming" />
					</div>
					<g:select name="answer" from="${['start': 'Bericht generieren', 'retrieval': 'Retrieval']}" optionKey="key" optionValue="value"/>
					<g:actionSubmit value="Starten" action="decider" class="style-buttons"/>
				</form>
				<div>
					<div class="blue-box" style="margin-top: 6px;"></div>
					<h2>Bereits erstellte Berichte:</h2>
				</div>
				<!-- This div is updated through the periodical updater -->
				<div class="entry-list-report">
					<g:include action="snippetOutgoing" />
				</div>
			</div>
		</div>
	</body>
</html>