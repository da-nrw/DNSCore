Messages:
<form>
<textarea name="messages"><g:each var="message" in="${messages}">${message}
</g:each> 
</textarea></form>
Errors:
<form>
<textarea name="messages"><g:each var="error" in="${errors}">${error}
</g:each> 
</textarea></form>
