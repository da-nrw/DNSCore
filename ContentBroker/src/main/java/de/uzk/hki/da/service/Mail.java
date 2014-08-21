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


/**
 * The Class Mail. 
 * @author Jens Peters
 */
public class Mail {

	/**
	 * Send a mail.
	 *
	 * @param toAdress the to adress
	 * @param subject the subject
	 * @param mailText the mail text
	 * @throws MessagingException the messaging exception
	 */
	public static void sendAMail(String fromAdress, String toAdress, String subject, String mailText) throws MessagingException{
		
		Properties props= new Properties();
		
		Session session= Session.getInstance(props,null);
		MimeMessage message= new MimeMessage(session);
		
		message.setContent(mailText,"text/plain; charset=utf-8");
		message.setSubject(subject);
		
		Address toAddress= new InternetAddress(toAdress);
		Address fromAddress= new InternetAddress(fromAdress);
		message.setFrom(fromAddress);
		message.setRecipient(Message.RecipientType.TO, toAddress);
		
		Transport.send(message);
		
	}
}
