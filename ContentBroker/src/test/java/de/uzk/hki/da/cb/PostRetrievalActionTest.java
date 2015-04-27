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

package de.uzk.hki.da.cb;

import static org.junit.Assert.*;
import static de.uzk.hki.da.core.C.*;

import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.util.Path;

/**
 * @author: Jens Peters
 */


/**
 * The Class PostRetrievalActionTest.
 */
public class PostRetrievalActionTest  extends ConcreteActionUnitTest {
	
	private static final String UNDERSCORE = "_";
	private static final String OUTGOING = "outgoing";

	private static final Path userAreaRootPath = Path.make(TC.TEST_ROOT_CB,"PostRetrievalAction");
	
	
	@ActionUnderTest
	PostRetrievalAction action = new PostRetrievalAction();


	@Before
	public void setUp() throws IOException {
		n.setUserAreaRootPath(userAreaRootPath);
		o.setObject_state(Object.ObjectStatus.InWorkflow);
		j.setStatus("950");
		FileUtils.copyDirectory(Path.makeFile(userAreaRootPath,TEST_USER_SHORT_NAME+UNDERSCORE), 
			Path.makeFile(userAreaRootPath,TEST_USER_SHORT_NAME));
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.deleteQuietly(Path.makeFile(userAreaRootPath,TEST_USER_SHORT_NAME));
	}
	
	/**
	 * Post retrieval.
	 */
	@Test 
	public void testPostRetrievalDeletionAfterSomeTime() {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.HOUR_OF_DAY, -49);
		j.setDate_created(String.valueOf(now.getTimeInMillis()/1000L));
		
		
		assertTrue(Path.makeFile(userAreaRootPath,TEST_USER_SHORT_NAME,OUTGOING,o.getIdentifier()+FILE_EXTENSION_TAR).exists());
		assertTrue(action.implementation());
		assertFalse(Path.makeFile(userAreaRootPath,TEST_USER_SHORT_NAME,OUTGOING,o.getIdentifier()+FILE_EXTENSION_TAR).exists());
		assertTrue(o.getObject_state()==Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		
	}
//	/**
//	 * Post retrieval.
//	 */
//	@Test 
//	public void testPostRetrievalNoDeletionAfterSomeTime() {
//		Calendar now = Calendar.getInstance();
//		now.add(Calendar.HOUR, -1);
//		j.setDate_created(String.valueOf(now.getTimeInMillis()/1000L));
//		assertFalse(action.implementation());
//		assertTrue(o.getObject_state()==Object.ObjectStatus.InWorkflow);
//	}

}
