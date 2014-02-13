<form>
<textarea name="messages"><g:each var="message" in="${messages}">${message}
</g:each> 
</textarea></form>
Fehler in der Paketverarbeitung:
<form>
<textarea name="messages"><g:each var="error" in="${errors}">${error}
</g:each> 
</textarea></form>
aktualisiert: Date: ${date}