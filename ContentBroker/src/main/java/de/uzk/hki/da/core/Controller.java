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

import java.io.Serializable;
import java.util.List;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.xbean.XBeanBrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controls and coordinates the work of the action factory and its associate
 * classes. Server is now using NIO
 * 
 * @author Daniel M. de Oliveira
 * @author Jens Peters
 * 
 */
public class Controller implements Runnable {

	static final Logger logger = LoggerFactory.getLogger(Controller.class);

	private ActionFactory actionFactory;
	private ActionRegistry actionRegistry;

	private int socketNumber;
	private String clientChannel = "clientChannel";
	private String serverChannel = "serverChannel";
	private String channelType = "channelType";
	private String serverName;

	private XBeanBrokerService mqBroker;
	private ActiveMQConnectionFactory mqConnectionFactory;
	
 	public Controller(String serverName, int socketNumber,
			ActionFactory actionFactory, ActionRegistry actionRegistry, XBeanBrokerService mqBroker, ActiveMQConnectionFactory mqConnectionFactory) {

		this.serverName = serverName;
		this.actionRegistry = actionRegistry;
		this.socketNumber = socketNumber;
		this.actionFactory = actionFactory;
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
			logger.debug("MQ-Broker is started: " + mqBroker.isStarted());
			
			Connection connection = mqConnectionFactory.createConnection();
            connection.start();
            
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("CB.SYSTEM");
            MessageProducer producer1 = session.createProducer(destination);
            producer1.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            String text = "Hello Client, this is ContentBroker running at " + serverName;
            TextMessage message = session.createTextMessage(text);
            Queue responseQueue = session.createTemporaryQueue();
            message.setJMSReplyTo(responseQueue);
            producer1.send(message);
            
            List<ActionDescription> list = null;
            MessageConsumer consumer = session.createConsumer(responseQueue);
            for (;;) {
			
			String messageSend = "";
            Message messageRecieve = consumer.receive(1000);
            
            if (messageRecieve instanceof TextMessage) {
            	MessageProducer producer = session.createProducer(destination);
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                TextMessage textMessage = (TextMessage) messageRecieve;
                String command = textMessage.getText();
                if (!command.equals("")) logger.debug("Received: " + command);
                if (command.indexOf("STOP_FACTORY") >= 0) {
					logger.debug("STOPPING FACTORY");
					messageSend = "...STOPPING FACTORY done";
					actionFactory.pause(true);
					
				} else if (command.indexOf("START_FACTORY")>=0) {
					logger.debug("STARTING FACTORY");
					messageSend = "...STARTING FACTORY done";
					actionFactory.pause(false);
				} else if (command.indexOf("SHOW_ACTIONS")>=0){ 
					list = actionRegistry.getCurrentActionDescriptions();
					logger.debug("SHOW_ACTIONS");
					messageSend = "found " + list.size()+ " working actions"; 
					ObjectMessage om = session.createObjectMessage((Serializable) list);
		            producer.send(om);
				} 
                if (!messageSend.equals("")) {
                	TextMessage message2 = session.createTextMessage(text);
                	message2 = session.createTextMessage(messageSend);
                	producer.send(message2);
                	
                }
                
            }
			}
		} catch (Exception e) {
			logger.error("Error creating/execution of CB-Controller thread " + e.getStackTrace() );
		}
			
		}
}
