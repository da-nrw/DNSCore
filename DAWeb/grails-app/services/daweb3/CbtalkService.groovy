package daweb3

import java.security.MessageDigest;

import de.uzk.hki.da.core.ActionDescription

import javax.jms.*;
class CbtalkService {
	
static exposes = ['jms'];

List<ActionDescription> ads;
List<String> messages = new ArrayList<String>();
@grails.plugin.jms.Queue(name='CB.CLIENT')
public createMessage(msg)	{
	println "GOT MESSAGE $msg"
	messages.add(msg);
	if (msg instanceof TextMessage) {
		TextMessage textMessage = (TextMessage) msg;
		 textMessage = (TextMessage) msg;
		 
	} else if (msg instanceof ObjectMessage) {
			  ObjectMessage om = (ObjectMessage)messageRecieve;
			  
			  ads = (List<ActionDescription>) om.getObject();
			  for (ActionDescription ad : ads) {
				  println(ad.toString());
			  }
	}
}
public List<String> getMessages() {
	return messages
}
public List<ActionDescription> getActions() {
	return ads
}
}
