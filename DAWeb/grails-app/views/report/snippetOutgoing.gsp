
<ul>
<g:each in="${currentFileOutgoing}" var="currentFile" status="i">
    <li><a href="${httpurl + "/" + currentFile.getName()}">${currentFile.getName()}</a></label></li>
	<g:if test="${i == currentFileOutgoing.size() - 1}">
    </g:if>
</g:each>
</ul>