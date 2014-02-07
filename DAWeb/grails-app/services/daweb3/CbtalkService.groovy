package daweb3

import java.security.MessageDigest;

import de.uzk.hki.da.core.ActionDescription

import javax.jms.*;
class CbtalkService {
	
static exposes = ['jms'];

List<ActionDescription> ads;
List<String> messages = new ArrayList<String>();

@grails.plugin.jms.Queue(name='CB.CLIENT')
   def createMessage(messageObject) {
    println "GOT MESSAGE: $messageObject"
 	messages.add(new Date().toLocaleString() + " " + messageObject)
}
public List<String> getMessages() {
	return messages.reverse()
}
}
