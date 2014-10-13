/*

  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVR InfoKom

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.uzk.hki.da.grid;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.C;

/**
 * Controller to the iRODS DataGrid
 * @author Jens Peters
 *
 */

public class IrodsController  {
	
	static final Logger logger = LoggerFactory.getLogger(IrodsController.class);
	private ActiveMQConnectionFactory mqConnectionFactory;
	
	private IrodsSystemConnector irodsSystemConnector;
	
	private String systemRuleFolder;
	
	private Connection connection;
	
	private Session session;
 	
	public void init() throws JMSException{
		logger.debug("started iRODS Controller Thread");
		   
	}
	

	public void scheduleTask() {
	 	    	try {
	 	    	connection = mqConnectionFactory.createConnection();
	 	        connection.start(); 
	 	    	session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	 	    	Destination toServer = session.createQueue(C.QUEUE_TO_IRODS_SERVER);
	 	    	Destination toClient = session.createQueue(C.QUEUE_TO_CLIENT);
		    	MessageProducer producer = session.createProducer(toClient);
		    	producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		    	MessageConsumer consumer = session.createConsumer(toServer);
		    
		    	String messageSend = "";
		    	Message messageRecieve = consumer.receive(1000);
		    
		    if (messageRecieve instanceof TextMessage) {
		    	TextMessage textMessage = (TextMessage) messageRecieve;
		        String command = textMessage.getText();
		        if (!command.equals("")) logger.debug("Received: " + command);
		        if (command.indexOf(C.IRODS_STOP_DELAYED) >= 0) {
		        	logger.debug(C.IRODS_STOP_DELAYED);
		        	irodsSystemConnector.stopAllDelayedRules();
					messageSend = "...STOPPING DELAYED done";
					
				} else if (command.indexOf(C.IRODS_START_DELAYED)>=0) {
					logger.debug(C.IRODS_START_DELAYED);
					irodsSystemConnector.stopAllDelayedRules();
					irodsSystemConnector.startAllDelayedRules(systemRuleFolder);
					messageSend = "...START DELAYED done";
				} 
		        if (!messageSend.equals("")) {
		        	TextMessage message = session.createTextMessage(messageSend);
		        	message.setJMSReplyTo(toServer);
		        	producer.send(message);
		        }
		    }
		    consumer.close();
		    producer.close();
		    session.close();
		    connection.close();
		    } catch (Exception e) {
				logger.error("Error using IRODS-Controller thread: " + e,e );
			} 
	}

	public ActiveMQConnectionFactory getMqConnectionFactory() {
		return mqConnectionFactory;
	}

	public void setMqConnectionFactory(ActiveMQConnectionFactory mqConnectionFactory) {
		this.mqConnectionFactory = mqConnectionFactory;
	}

	public IrodsSystemConnector getIrodsSystemConnector() {
		return irodsSystemConnector;
	}

	public void setIrodsSystemConnector(IrodsSystemConnector irodsSystemConnector) {
		this.irodsSystemConnector = irodsSystemConnector;
	}

	public String getSystemRuleFolder() {
		return systemRuleFolder;
	}

	public void setSystemRuleFolder(String systemRuleFolder) {
		this.systemRuleFolder = systemRuleFolder;
	} 
}
