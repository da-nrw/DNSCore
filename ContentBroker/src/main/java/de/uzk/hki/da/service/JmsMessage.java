/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014
  LVR-InfoKom
  
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
 * Envelope for Message in JMS
 * @author Jens Peters
 * 
 */

package de.uzk.hki.da.service;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import de.uzk.hki.da.core.C;

public class JmsMessage {
	
	
	private String queueToClient;
	private String queueToServer;
	private Object body;
	
	public JmsMessage(String queueToClient, String queueToServer, Object body){
		this.queueToClient = queueToClient;
		this.queueToServer = queueToServer;
		this.body = body;
	}
	
	public String getToClient() {
		return queueToClient;
	}



	public void setQueueToClient(String replyTo) {
		this.queueToClient = replyTo;
	}



	public String getQueueToServer() {
		return queueToServer;
	}


	public void setQueueToServer(String from) {
		this.queueToServer = from;
	}

	public Message getMessage(Session session) throws JMSException {
		if (body instanceof String){
			if (body.equals(""))
			body = C.JMS_NO_BODY; 
			return session.createTextMessage(body.toString());
		} else {
			return session.createObjectMessage((Serializable) body);
		}
	}
	
	public String getText() {
		if (body instanceof String){
		return body.toString();
		} return "";
	}

	public void setBody(Object body) {
		this.body = body;
	}

}
