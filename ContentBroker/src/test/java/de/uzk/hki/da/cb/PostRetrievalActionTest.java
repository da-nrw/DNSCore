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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import javax.validation.constraints.AssertTrue;

import org.junit.After;
import org.junit.Test;

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.test.TC;

/**
 * @author: Jens Peters
 */


/**
 * The Class PostRetrievalActionTest.
 */
public class PostRetrievalActionTest  extends ConcreteActionUnitTest {
	
	private static final Path userAreaRootPath = Path.make(TC.TEST_ROOT_CB,"RetrievalActionTests","user");
	
	
	@ActionUnderTest
	PostRetrievalAction action = new PostRetrievalAction();


	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Post retrieval.
	 */
	@Test 
	public void testPostRetrievalDeletionAfterSomeTime() {
		n.setUserAreaRootPath(userAreaRootPath);
		j.setStatus("950");
		Calendar now = Calendar.getInstance();
		now.add(Calendar.HOUR_OF_DAY, -25);
		j.setDate_created(String.valueOf(now.getTimeInMillis()/1000L));
		assertTrue(action.implementation());
	}
	/**
	 * Post retrieval.
	 */
	@Test 
	public void testPostRetrievalNoDeletionAfterSomeTime() {
		n.setUserAreaRootPath(userAreaRootPath);
		j.setStatus("950");
		Calendar now = Calendar.getInstance();
		now.add(Calendar.HOUR_OF_DAY, -9);
		j.setDate_created(String.valueOf(now.getTimeInMillis()/1000L));
		assertFalse(action.implementation());
	}

}
