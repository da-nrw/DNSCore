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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import de.uzk.hki.da.action.ActionDescription;
import de.uzk.hki.da.action.ActionInformation;



/**
 * The Class ActionRegistryTests.
 * @author Jens Peters
 */
public class ActionInformationTests {
	
	/** The context. */
	FileSystemXmlApplicationContext context = null;

	/** The info. */
	ActionInformation info;
	
	/**
	 * Sets the up.
	 * @throws IOException 
	 */
	@Before
	public void setUp() throws IOException{
		FileUtils.copyFile(new File("src/main/conf/config.properties.dev"), 
				new File("conf/config.properties"));
		
		context = new FileSystemXmlApplicationContext(
				"src/test/resources/core/ActionInformationTests/action-definitions.xml" );;
		info = (ActionInformation) context.getBean("actionInformation");
	}
	
	@After 
	public void tearDown(){
		new File("conf/config.properties").delete();
	}
	
	
	/**
	 * Test .
	 */
	@Test
	public void testFindStateOfActionInAvailableActions(){
		ActionDescription ad = info.findStateInActionList("455");
		assertTrue(ad.getActionType().equals("tarAction"));
		assertEquals("Tar packaging",ad.getDescription());
		ad = info.findStateInActionList("500");	
		assertEquals(null, ad);
	
	}
	
}
