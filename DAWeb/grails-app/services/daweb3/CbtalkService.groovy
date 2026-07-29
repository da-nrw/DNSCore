package daweb3

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat
import de.uzk.hki.da.core.ActionDescription

import javax.jms.*;

class CbtalkService {
	
static exposes = ['jms'];

List<ActionDescription> ads;
List<String> messages = new ArrayList<String>();

@grails.plugin.jms.Queue(name='CB.CLIENT')
   def onMessage(messageObject) {
	   def date = new Date()
	   def sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
		if (messageObject instanceof ObjectMessage) {
			   ObjectMessage om = (ObjectMessage)messageObject
			   ads = (List<ActionDescription>) om.getObject();
			   for (ActionDescription ad : ads) {
				   messages.add(sdf.format(date) + "> " + ad.toString());
			   }
		} else {
			messages.add(sdf.format(date) + "> " + messageObject)
	
		}
	}
	public List<String> getMessages() {
		return messages.reverse()
	}
}
