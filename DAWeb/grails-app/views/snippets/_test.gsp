<!doctype html>
<html>
<head>
<meta name="layout" content="main" />
<title>Willkommen bei der DA-NRW Weboberfläche</title>
<style type="text/css" media="screen">
#controller-list ul {
	list-style-position: inside;
}

#controller-list li {
	line-height: 1.3;
	list-style-position: inside;
	margin: 0.5em;
}

.bs-example {
	margin: 1.2em;
}
</style>

<r:script>
				$(function() {
					$("#legend").accordion({ collapsible: true, active: false, autoHeight: false });
				});
				$(function() {
					$("#filter").accordion({ collapsible: true, active: false });
				});
</r:script>
	





<!--  <script type="text/javascript">
	$(document).ready(function() {
		var isMobile = window.matchMedia("only screen and (max-width: 760px)");

		if (isMobile.matches) {
			$("#accordion").accordion({
				collapsible : true
			});
		}
	});
</script>-->

</head>

<body>
	<h1 style="text-align: center; margin-bottom: 1em; font-size: 34px;">
		Willkommen
		${user}
		!
		<g:if test="${admin==1}"> (Administrator)</g:if>
	</h1>

	<div class="bs-example">
		<ul class="nav nav-tabs">
			<li class="active"><a data-toggle="tab" href="#sectionA">Bearbeitungsübersicht</a></li>
			<li><a data-toggle="tab" href="#sectionB">Entscheidungsübersicht</a></li>
			<li><a data-toggle="tab" href="#sectionC">Eingelieferte Objekte (AIP)</a></li>
			<li><a data-toggle="tab" href="#sectionD">Verarbeitung für abgelieferte SIP starten</a></li>
			<li><a data-toggle="tab" href="#sectionE">Objekt entnehmen (DIP)</a></li>
			<li class="dropdown">
				<a data-toggle="dropdown" class="dropdown-toggle" href="#">Hinweise zur Ansteuerung überexterne Systeme<b class="caret"></b></a>
				<ul class="dropdown-menu">
					<li><a style="background-color: white;" data-toggle="tab" href="#dropdown1">Erstellung von Retrievalanfragen </a></li>
					<li><a style="background-color: white;" data-toggle="tab" href="#dropdown2">Abfrage der Verarbeitung und Archivierung</a></li>
				</ul>
			<li><a data-toggle="tab" href="#sectionF">Konfigurierte Konversionen</a></li>
			<li><a data-toggle="tab" href="#sectionG">Abfragen verarbeiten</a></li>
		</ul>
		<div class="tab-content">
			<div id="sectionA" class="tab-pane fade in active">
				<g:include controller="queueEntry" action="list" />
			</div>
			<div id="sectionB" class="tab-pane fade">
				<g:include controller="queueEntry" action="listRequests" />
			</div>
			<div id="sectionC" class="tab-pane fade">
				<g:include controller="object" action="list" />
			</div>
			<div id="sectionD" class="tab-pane fade">
				<g:include controller="incoming" action="index" />
			</div>
			<div id="sectionE" class="tab-pane fade">
				<g:include controller="outgoing" action="index" />
			</div>
			<div id="dropdown1" class="tab-pane fade">
				<g:include controller="automatedRetrieval" action="index" />
			</div>
			<div id="dropdown2" class="tab-pane fade">
				<g:include controller="status" action="teaser" />
			</div>
			<div id="sectionF" class="tab-pane fade">
				<g:include controller="conversionPolicies" action="list" params="[conversionPoliciesInstanceTotal: 100]"/>
			</div>
			<div id="sectionG" class="tab-pane fade">
				<g:include controller="report" action="index" />
			</div>
		</div>
	</div>
</body>
</html>