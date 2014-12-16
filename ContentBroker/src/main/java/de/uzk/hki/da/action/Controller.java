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

package de.uzk.hki.da.action;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.activemq.xbean.XBeanBrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.service.JmsMessage;
import de.uzk.hki.da.service.JmsMessageServiceHandler;

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
	private JmsMessageServiceHandler jms;
	public Controller(String serverName, int socketNumber,
			ActionFactory actionFactory, ActionInformation actionInformation, XBeanBrokerService mqBroker, JmsMessageServiceHandler ams) {
		this.actionInformation = actionInformation;
		this.serverName = serverName;
		this.socketNumber = socketNumber;
		this.actionFactory = actionFactory;
		this.actionRegistry = actionFactory.getActionRegistry();
		this.mqBroker = mqBroker;
		this.jms = ams;
	}

	/**
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 * @Author Jens Peters
	 */
	@Override
	public void run() {
		List<ActionDescription> list = null;
		try {
			if (mqBroker==null) {
				logger.error("no Broker defined!");
				return;
			}
			logger.debug("starting JMS -Service at: " + serverName + " "+ socketNumber);
			mqBroker.start();
		} catch (Exception e) {
			logger.error("Error creating CB-Controller thread: " + e,e );
			logger.debug("MQ-Broker is started: " + mqBroker.isStarted());
			return;
		} 
		while (true) {
				try {
				JmsMessage incoming = jms.recieveJMSMessage(C.QUEUE_TO_SERVER);
				JmsMessage outgoing = new JmsMessage(C.QUEUE_TO_CLIENT, C.QUEUE_TO_SERVER, "");
				String messageSend = "";
				if (!incoming.getText().isEmpty()) {
					String command = incoming.getText();
					if (!command.equals("")) logger.debug("Received: " + command);
					if (command.indexOf(C.COMMAND_STOP_FACTORY) >= 0) {

						logger.debug(C.COMMAND_STOP_FACTORY);
						messageSend = "...STOPPING FACTORY done";
						actionFactory.pause(true);	
					} else if (command.indexOf(C.COMMAND_START_FACTORY)>=0) {
						logger.debug(C.COMMAND_START_FACTORY);
						messageSend = "...STARTING FACTORY done";
						actionFactory.pause(false);
					} else if (command.equals(C.COMMAND_SHOW_ACTION)) {
						String []arr = command.split("=");
						if (arr.length==2) {
							logger.debug("SHOW_DESCRIPTION OF STATE: "+arr[1]);
							ActionDescription ad = actionInformation.findStateInActionList(arr[1]);
							if (ad!=null) messageSend = ad.getDescription();
							else messageSend = "Action is unknown to CB at " + serverName;
						} else messageSend = "Command not understood!";
					} else if (command.indexOf(C.COMMAND_SHOW_VERSION)>=0) {
						logger.debug(C.COMMAND_SHOW_VERSION);
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
					} else if (command.indexOf(C.COMMAND_SHOW_ACTIONS)>=0){ 
						list = actionRegistry.getCurrentActionDescriptions();
						logger.debug(C.COMMAND_SHOW_ACTIONS);
						messageSend = "found " + list.size()+ " working actions"; 
						outgoing.setBody(list);
						jms.sendJMSMessage(outgoing);
						outgoing.setBody(messageSend);
						jms.sendJMSMessage(outgoing);
					} else if (command.indexOf(C.COMMAND_GRACEFUL_SHUTDOWN)>=0){ 
						list = actionRegistry.getCurrentActionDescriptions();
						actionFactory.pause(true);
						while (list.size()>0) {
							String text = "waiting for actions to complete before shut down (" + list.size() +")";
							outgoing.setBody(text);
							jms.sendJMSMessage(outgoing);
							Thread.sleep(3000);
							list = actionRegistry.getCurrentActionDescriptions();
						}
						String text = "ContentBroker at " + serverName + " exiting now!";
						outgoing.setBody(text);
						jms.sendJMSMessage(outgoing);
						Thread.sleep(3000);
						System.exit(0);
					} 
					if (!messageSend.equals("")) {
						outgoing.setBody(messageSend);
						jms.sendJMSMessage(outgoing);
					}
				}
			

			} catch (Exception e) {
				logger.error("Error using CB-Controller thread: " + e,e );
			} finally {
				if (!mqBroker.isStarted()){
					logger.error("Controller thread seems to be dead, too!");
					break;
				}
			}} 
		}
}
			
		
