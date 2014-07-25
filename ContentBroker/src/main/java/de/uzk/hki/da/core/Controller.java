/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln

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

package de.uzk.hki.da.core;

import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.xbean.XBeanBrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.uzk.hki.da.utils.C;

/**
 * Controls and coordinates the work of the action factory and its associate
 * classes. 
 * 
 * @author Daniel M. de Oliveira
 * @author Jens Peters
 * 
 */
public class Controller implements Runnable {

	static final Logger logger = LoggerFactory.getLogger(Controller.class);

	private ActionFactory actionFactory;
	private ActionRegistry actionRegistry;
	private ActionInformation actionInformation;

	private int socketNumber;
	private String serverName;

	private XBeanBrokerService mqBroker;
	private ActiveMQConnectionFactory mqConnectionFactory;
	public Controller(String serverName, int socketNumber,
			ActionFactory actionFactory, ActionInformation actionInformation, XBeanBrokerService mqBroker, ActiveMQConnectionFactory mqConnectionFactory) {
		this.actionInformation = actionInformation;
		this.serverName = serverName;
		this.socketNumber = socketNumber;
		this.actionFactory = actionFactory;
		this.actionRegistry = actionFactory.getActionRegistry();
		this.mqBroker = mqBroker;
		this.mqConnectionFactory = mqConnectionFactory;
	}

 	/**
 	 * (non-Javadoc)
 	 * @see java.lang.Runnable#run()
 	 * @Author Jens Peters
 	 */
	@Override
	public void run() {
		try {
			if (mqBroker==null) {
				logger.error("");
				return;
			}
			logger.debug("starting JMS -Service at: " + serverName + " "+ socketNumber);
			mqBroker.start();
		} catch (Exception e) {
			logger.error("Error creating CB-Controller thread: " + e,e );
		}
		
			logger.debug("MQ-Broker is started: " + mqBroker.isStarted());
		    List<ActionDescription> list = null;
          
		    
		    for (;;) {
		    	try {
            	Connection connection = mqConnectionFactory.createConnection();
                connection.start();
                
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination toServer = session.createQueue(C.QUEUE_TO_SERVER);
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
                if (command.indexOf(C.STOP_FACTORY) >= 0) {
					
                	logger.debug(C.STOP_FACTORY);
					messageSend = "...STOPPING FACTORY done";
					actionFactory.pause(true);
					
				} else if (command.indexOf(C.START_FACTORY)>=0) {
					logger.debug(C.START_FACTORY);
					messageSend = "...STARTING FACTORY done";
					actionFactory.pause(false);
				} else if (command.startsWith(C.SHOW_ACTIONS)) {
					String []arr = command.split("=");
					if (arr.length==2) {
						logger.debug("SHOW_DESCRIPTION OF STATE: "+arr[1]);
						ActionDescription ad = actionInformation.findStateInActionList(arr[1]);
						if (ad!=null) messageSend = ad.getDescription();
						else messageSend = "Action is unknown to CB at " + serverName;
					} else messageSend = "Command not understood!";
				} else if (command.indexOf(C.SHOW_VERSION)>=0) {
					logger.debug(C.SHOW_VERSION);
					FileReader fr = null;
						int c;
						StringBuffer buff = new StringBuffer();
				        try {
				        	fr = new FileReader("./README.txt");
				            while ((c = fr.read()) != -1) {
				                buff.append((char) c);
				            }
				            fr.close();
				        } catch (IOException e) {
				            logger.error("Readme not found");
				        } 
				        messageSend = buff.toString();
				} else if (command.indexOf(C.SHOW_ACTIONS)>=0){ 
					list = actionRegistry.getCurrentActionDescriptions();
					logger.debug(C.SHOW_ACTIONS);
					messageSend = "found " + list.size()+ " working actions"; 
					ObjectMessage om = session.createObjectMessage((Serializable) list);
		            om.setJMSReplyTo(toServer);
					producer.send(om);
				} else if (command.indexOf(C.GRACEFUL_SHUTDOWN)>=0){ 
					list = actionRegistry.getCurrentActionDescriptions();
					actionFactory.pause(true);
					while (list.size()>0) {
						String text = "waiting for actions to complete before shut down (" + list.size() +")";
						TextMessage message = session.createTextMessage(text);
	                    message.setJMSReplyTo(toServer);
						producer.send(message);
	                    Thread.sleep(3000);
	                    list = actionRegistry.getCurrentActionDescriptions();
	                }
					String text = "ContentBroker at " + serverName + " exiting now!";
					TextMessage message = session.createTextMessage(text);
                    message.setJMSReplyTo(toServer);
					producer.send(message);
                    Thread.sleep(3000);
                    System.exit(0);
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
				logger.error("Error using CB-Controller thread: " + e,e );
			} finally {
				if (!mqBroker.isStarted()){
					logger.error("Controller thread seems to be dead, too!");
					break;
				}
			}
		    }
		}
}
