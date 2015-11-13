

<ul>
<g:each in="${currentFileIncoming}" var="currentFile" status="i">
    <li>${currentFile.getName()} - ${new Date(currentFile.lastModified())}</li>
	<g:if test="${i == currentFileIncoming.size() - 1}">
    </g:if>
</g:each>
</ul>
<p>