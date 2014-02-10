package daweb3

import java.security.MessageDigest;
import java.util.List;

import de.uzk.hki.da.core.ActionDescription

import javax.jms.*;
class CberrorService {
	
static exposes = ['jms'];
List<String> messages = new ArrayList<String>();

@grails.plugin.jms.Queue(name='CB.ERROR')
   def createMessage(messageObject) {
    println "GOT MESSAGE: $messageObject"
 	messages.add(new Date().toLocaleString() + " " + messageObject)
}
public List<String> getMessages() {
	return messages.reverse()
}

}
