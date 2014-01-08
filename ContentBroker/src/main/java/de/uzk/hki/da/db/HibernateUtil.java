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

package de.uzk.hki.da.db;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.uzk.hki.da.utils.PasswordUtils;


/**
 * Provides access to hibernate sessions.
 * @author Daniel M. de Oliveira
 */
public class HibernateUtil {

	/**
	 */
	private static boolean initialized = false;

	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(HibernateUtil.class);
	
	/** The session factory. */
	private static SessionFactory sessionFactory;
	
	private static ThreadLocal<Session> threadLocalSession = new ThreadLocal<Session>();
	
	/**
	 * Instantiates a new hibernate util.
	 */
	public HibernateUtil(){}
	
	
	/**
	 * @param configFilePath the config file path
	 */
	public static void init(String configFilePath){
		if (initialized){ 
			logger.warn("HibernateUtil has already been initialized");
			return;
		}
		
		if (sessionFactory!=null) throw new IllegalStateException("Error while "+
				"initializing HibernateUtil. HibernateUtil already initialized.");
		
		try {
			logger.trace("starting HibernateUtil and creating SessionFactory ...");

			sessionFactory = new AnnotationConfiguration().configure(
					createDecryptedConfigFile(configFilePath)).buildSessionFactory();

		} catch (Throwable ex) {

			logger.error("Initial SessionFactory creation failed.", ex);
			throw new ExceptionInInitializerError(ex);
		}
		
		initialized = true;
	}
	
	/**
	 * prints the stats of a thread bound session.
	 */
	public static void printStats() {
		logger.info(threadLocalSession.toString());
	}
	
	/**
	 * Opens a entirely new session hibernate session (by openSession). The session is not threadbound.
	 *
	 * @return the session
	 */
	public static Session openSession(){
		if (sessionFactory==null) throw new IllegalStateException("sessionFactory is null in HibernateUtil");
		return sessionFactory.openSession();
	}
	
	/** The err. */
	private static ErrorHandler err = new ErrorHandler(){

		@Override
		public void error(SAXParseException e) throws SAXException {
			throw new RuntimeException("---sax error");
		}

		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			throw new RuntimeException("---fatal error");
		}

		@Override
		public void warning(SAXParseException e) throws SAXException {
			throw new RuntimeException("---saxparseexception");
		}
	};
	
	/**
	 * Creates the decrypted config file.
	 *
	 * @param configFilePath the config file path
	 * @return the document
	 * @author: Christian Weitz
	 * @author: Thomas Kleinke
	 */
	private static Document createDecryptedConfigFile(String configFilePath) {
		if (!new File(configFilePath).exists()) throw new IllegalStateException(configFilePath+" does not exist");
		
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		Document doc = null;
		try {
			docBuilderFactory.setValidating(false);
			docBuilderFactory.setFeature("http://xml.org/sax/features/validation", false);
			docBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			docBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			docBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			docBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			docBuilder = docBuilderFactory.newDocumentBuilder();
			docBuilder.setErrorHandler(err);
			doc = docBuilder.parse(new File(configFilePath));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error in createDecryptedConfigFile - Maybe schema from hibernate"
					+ " config couldn't validate against an external resource.");
		}
		err.toString();
		NodeList properties = doc.getElementsByTagName("property");
		for (int i = 0; i < properties.getLength(); i++) {
			Node property = properties.item(i);
			NamedNodeMap attributes = property.getAttributes();
			
			for (int j = 0; j < attributes.getLength(); j++) {
				Node attribute = attributes.item(j);
				if (attribute.getNodeValue().equals("connection.password"))
				{
					Node encryptedPasswordNode = property.getFirstChild();
					if (encryptedPasswordNode == null)
						return doc;
					String encryptedPassword = property.getFirstChild().getNodeValue();
					if (encryptedPassword == null || encryptedPassword.equals(""))
						return doc;
	
					String decryptedPassword = PasswordUtils.decryptPassword(encryptedPassword);
					property.getFirstChild().setNodeValue(decryptedPassword);
					return doc;
				}				
			}
		}
		
		return null;
	}
}
