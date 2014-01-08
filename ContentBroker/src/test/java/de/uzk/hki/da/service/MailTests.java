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

package de.uzk.hki.da.service;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.Test;


/**
 * The Class MailTests.
 */
public class MailTests {

	
	
	/**
	 * Send a mail.
	 *
	 * @throws MessagingException the messaging exception
	 */
	@Test 
	public void sendAMail() throws MessagingException{
		Properties props= new Properties();
//		props.put("mail.smtp.host","smtp.uni-koeln.de");
		
		Session session= Session.getInstance(props,null);
		MimeMessage message= new MimeMessage(session);
		
		message.setContent("Hello","text/plain");
		message.setSubject("First");
		
		Address toAddress= new InternetAddress("d.de-oliveira@uni-koeln.de");
		Address fromAddress= new InternetAddress("noreply@da-nrw.hki.uni-koeln.de");
		message.setFrom(fromAddress);
		message.setRecipient(Message.RecipientType.TO, toAddress);
		
		//Authenticator auth= new MyAuthenticator();
		//Transport transport= session.getTransport("smtp");
		//transport
		Transport.send(message);
		
	}
}
