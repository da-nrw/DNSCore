package daweb3

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat
import java.util.List;

import de.uzk.hki.da.core.ActionDescription

import javax.jms.Message;
class CberrorService {
	
static exposes = ['jms'];
List<String> messages = new ArrayList<String>();

@grails.plugin.jms.Queue(name='CB.ERROR')
   def createMessage(messageObject) {
	   
	   def date = new Date()
	   def sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
    println "GOT MESSAGE: $messageObject"
 	messages.add(sdf.format(date) + " " + messageObject)
}
public List<String> getMessages() {
	return messages.reverse()
}

}
