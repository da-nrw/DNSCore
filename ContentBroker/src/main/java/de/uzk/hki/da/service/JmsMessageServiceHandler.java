/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVR-InfoKom

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

/**
 * Implements Handler for JMS sending and recieving messages
 * @author Jens Peters
 * 
 */


package de.uzk.hki.da.service;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsMessageServiceHandler {

	private Connection connection = null;

	private ActiveMQConnectionFactory mqConnectionFactory;
	static final Logger logger = LoggerFactory.getLogger(JmsMessageServiceHandler.class);

	private void openConnection() throws JMSException {
		connection = mqConnectionFactory.createConnection();
		connection.start();
	}

	private void closeConnection() throws JMSException {
		connection.close();
	}

	public void sendJMSMessage(JmsMessage jms) {
		if (mqConnectionFactory!=null) {
			try {
				openConnection();
				javax.jms.Session session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
				Destination toClient = session.createQueue(jms.getToClient());
				Destination toServer = session.createQueue(jms.getQueueToServer());
				MessageProducer producer;
				producer = session.createProducer(toClient);
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);   
				Message message = jms.getMessage(session);
				message.setJMSReplyTo(toServer);
				producer.send(message);
				logger.debug("JMS sended to " + jms.getToClient());
				producer.close();
				session.close();
				closeConnection();
			}catch (JMSException e1) {
				logger.error("Error while connecting to ActiveMQ Broker " + e1.getCause());
			}
		} else logger.error("send JMS Message failed!");
	}

	public JmsMessage recieveJMSMessage(String toServer) {
		JmsMessage jms = new JmsMessage("",toServer,"");
		if (mqConnectionFactory!=null) {
			try {
				openConnection();
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				Destination destToServer = session.createQueue(toServer);
				MessageConsumer consumer = session.createConsumer(destToServer);
				Message messageRecieve = consumer.receive(1000);
				if (messageRecieve instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) messageRecieve;
					jms.setBody(textMessage.getText());
				} else if (messageRecieve instanceof ObjectMessage){
					ObjectMessage oMessage = (ObjectMessage) messageRecieve;
					jms.setBody(oMessage.getObject());
				}
				consumer.close();
				session.close();
				connection.close();;
			}catch (JMSException e1) {
				logger.error("Error while connecting to ActiveMQ Broker " + e1.getCause());
			} 
		} else logger.error("recieve JMS Message failed");
		return jms;
	}

	public ActiveMQConnectionFactory getMqConnectionFactory() {
		return mqConnectionFactory;
	}

	public void setMqConnectionFactory(ActiveMQConnectionFactory mqConnectionFactory) {
		this.mqConnectionFactory = mqConnectionFactory;
	}
}

