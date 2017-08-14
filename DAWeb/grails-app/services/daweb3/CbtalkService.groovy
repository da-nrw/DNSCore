package daweb3

import java.security.MessageDigest;
import java.text.DateFormat

import de.uzk.hki.da.core.ActionDescription

import javax.jms.*;

class CbtalkService {
	
//static exposes = ['jms'];

List<ActionDescription> ads;
List<String> messages = new ArrayList<String>();

@grails.plugin.jms.Queue(name='CB.CLIENT')
   def onMessage(messageObject) {
		if (messageObject instanceof ObjectMessage) {
			   ObjectMessage om = (ObjectMessage)messageObject
			   ads = (List<ActionDescription>) om.getObject();
			   for (ActionDescription ad : ads) {
				   messages.add(DateFormat.format(new Date()) + "> " + ad.toString());
			   }
		} else {
			println "GOT MESSAGE: $messageObject"
			messages.add(DateFormat.format(new Date()) + " " + messageObject)
	
		}
	}
	public List<String> getMessages() {
		return messages.reverse()
	}
}
