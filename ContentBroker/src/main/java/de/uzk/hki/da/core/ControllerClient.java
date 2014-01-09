package de.uzk.hki.da.core;

import java.io.Serializable;
import java.util.List;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * 
 * @author Jens Peters
 * The Active MQ Client for communicating to the running CB 
 *
 */
public class ControllerClient {

	/**
	 * For example
	 * tcp://localhost:4455 STOP_FACTORY
	 * 
	 * @author Jens Peters
	 * @param String ConnectionUri String command 
	 * @throws JMSException
	 */
	
	
	public static void main(String[] args) throws JMSException {
		if (args.length!=2){
			System.out.print("Specify Active MQ host and specify your command!");
			throw new ConfigurationException("Needed parameters not set!");
		}
		String serverName =  args[0];
		
		System.out.println("... Client started, talking to " + args[0]);
		
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(serverName);

		Connection connection = connectionFactory.createConnection();
        connection.start();
        
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("CB.SYSTEM");
        String text = args[1];
       	MessageProducer producer = session.createProducer(destination);    
    	producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    	TextMessage message = session.createTextMessage(text);
    	
    	producer.send(message);
      	
        MessageConsumer consumer = session.createConsumer(destination);
        for (;;) {
        Message messageRecieve = consumer.receive(1000);
        	if (messageRecieve instanceof TextMessage) {
        		TextMessage textMessage = (TextMessage) messageRecieve;
            	 textMessage = (TextMessage) messageRecieve;
                 System.out.println("Recieved:" + textMessage.getText());
        	} else if (messageRecieve instanceof ObjectMessage) {
            		  ObjectMessage om = (ObjectMessage)messageRecieve;
            		  List<ActionDescription> ads = (List<ActionDescription>) messageRecieve;
            		  for (ActionDescription ad : ads) {
            			  System.out.println(ad.toString());
            		  }
        	}
        }
              
        
	}

}
