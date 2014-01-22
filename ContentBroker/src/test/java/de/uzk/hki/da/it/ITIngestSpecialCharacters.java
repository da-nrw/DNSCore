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
package de.uzk.hki.da.it;

import java.io.File;
import java.io.IOException;

import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import de.uzk.hki.da.db.HibernateUtil;


/**
 * The Class ITIngestSpecialCharacters.
 */
public class ITIngestSpecialCharacters extends ITIngestBase{

	/**
	 * Sets the up before class.
	 *
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		HibernateUtil.init(hibernateConfigFilePath);
		context = new FileSystemXmlApplicationContext("conf/beans.xml");
		setUpNode();
	}
	
	/**
	 * Sets the up.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Before
	public void setUp() throws IOException{

		setupSysConnector();
	}
	
	/**
	 * Tear down.
	 */
	@After
	public void tearDown(){
		
		new File("/data/danrw/ingest/TEST/integrationTest_ÜmläuteInPakötnamen.tgz").delete();
		new File("/data/danrw/ingest/TEST/integrationTest_Leerzeichen_in_Dateinamen.tgz").delete();
		new File("/data/danrw/ingest/TEST/integrationTest_Sonderzeichen_in_Dateinamen.tgz").delete();
		new File("/data/danrw/ingest/TEST/integrationTest_Umlaute_in_Dateinamen.tgz").delete();
		new File("/data/danrw/ingest/TEST/integrationTest_&Sonderzeichen%in#Paketnamen.tgz").delete();
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.delete(object);
		session.getTransaction().commit();
		session.close();

		irodsSystemConnector.removeFileAndEatException("/da-nrw/aip/TEST/it+ingestBase+identifier/it+ingestBase+identifier.pack_1.tar");
		irodsSystemConnector.removeCollectionAndEatException("/da-nrw/aip/TEST/it+ingestBase+identifier/");
	}
	
	
	/**
	 * Test special chars in file names.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testSpecialCharsInFileNames() throws IOException{
		
		prepareSIPAndInsertJobAndObject("integrationTest_Sonderzeichen_in_Dateinamen");
		stepThroughIngestWorkflow();
	}
	
	/**
	 * Test special chars in file names.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testUmlautsInPackageName() throws IOException{
		
		prepareSIPAndInsertJobAndObject("integrationTest_ÜmläuteInPakötnamen");
		stepThroughIngestWorkflow();
	}

	/**
	 * Test umlauts in file names.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testUmlautsInFileNames() throws IOException{

		prepareSIPAndInsertJobAndObject("integrationTest_Umlaute_in_Dateinamen");
		stepThroughIngestWorkflow();
	}
}
