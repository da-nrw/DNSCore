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

import java.io.File;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * @author Daniel M. de Oliveira
	 * @param configFilePath
	 */
	public static void createSchema(String configFilePath){
		System.out.println("Creating database schema based on the setting in "+configFilePath);
		
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		configuration.configure(new File(configFilePath));
		
		String encryptedPassword = configuration.getProperty("hibernate.connection.password");
		configuration.setProperty("hibernate.connection.password", PasswordUtils.decryptPassword(encryptedPassword));
		
		configuration.setProperty("hibernate.hbm2ddl.auto", "create");
		configuration.buildSessionFactory();
	}
	
	
	
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
			logger.info("starting HibernateUtil and creating SessionFactory ...");

			AnnotationConfiguration configuration = new AnnotationConfiguration();
			configuration.configure(new File(configFilePath));
			
			String encryptedPassword = configuration.getProperty("hibernate.connection.password");
			configuration.setProperty("hibernate.connection.password", PasswordUtils.decryptPassword(encryptedPassword) );
			
			sessionFactory = configuration.buildSessionFactory();
			logger.info("HibernateUtil and SessionFactory successfully created");

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
}
