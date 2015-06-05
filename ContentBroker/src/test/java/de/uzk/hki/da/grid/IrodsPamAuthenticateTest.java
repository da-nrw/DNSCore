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
package de.uzk.hki.da.grid;


import static org.junit.Assert.assertEquals;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Test would only pass if being exceuted on a host with appropiate certificates !!.
 *
 * @Author Jens Peters
 */

public class IrodsPamAuthenticateTest {


	/** The irods system connector. */
	public static IrodsSystemConnector irodsSystemConnector;

	/** The pam user. */
	public static String pamUser 		= "testpam";
	
	/** The pam password. */
	public static String pamPassword 	= "9L7pa7U6IItXHYjeEpAmgg==";
 
		/**
		 * Sets the up before class.
		 *
		 * @throws Exception the exception
		 */
		@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		irodsSystemConnector = new IrodsSystemConnector(pamUser,pamPassword, "da-nrw-vm3.hki.uni-koeln.de", "da-nrw", "01-da-nrw-vm3.hki.uni-koeln.de");
		irodsSystemConnector.setPamMode(true);
		irodsSystemConnector.setKeyStorePassword("LWiX/y4piEKvQQV6zHwUzA==");
		irodsSystemConnector.setKeyStore("/data/danrw/ContentBroker/.keystore");
		irodsSystemConnector.setTrustStore("/data/danrw/ContentBroker/.keystore");
		assertEquals(true, irodsSystemConnector.isConnected());
	
	}

	/**
	 * Tear down after class.
	 *
	 * @throws Exception the exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Pamauth.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void pamauth() throws Exception {
	}

}
