package daweb3

import java.security.MessageDigest;

import de.uzk.hki.da.core.ActionDescription

import javax.jms.*;
class CberrorService {
	
static exposes = ['jms'];
List<String> messages = new ArrayList<String>();
@grails.plugin.jms.Queue(name='CB.ERROR')
public createMessage(msg)	{
	println "GOT ERROR MESSAGE $msg"
	messages.add(msg);
	if (msg instanceof TextMessage) {
		TextMessage textMessage = (TextMessage) msg;
		 textMessage = (TextMessage) msg;
		 
	} 
}
public List<String> getMessages() {
	return messages
}

}
