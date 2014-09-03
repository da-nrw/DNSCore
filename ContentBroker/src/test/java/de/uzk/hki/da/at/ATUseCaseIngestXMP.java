/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
  Landschaftsverband Rheinland

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

package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jdom.JDOMException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.TESTHelper;

/**
 * @author Daniel M. de Oliveira
 */
public class ATUseCaseIngestXMP extends Base{

	private static final String origName = "ATUseCaseIngestXMP";
	private static Object object;
	

	@BeforeClass
	public static void setUpBeforeClass() throws IOException{
		setUpBase();
		object = ingest(origName);
	}
	
	
	@AfterClass
	public static void tearDownAfterClass(){
		TESTHelper.clearDB();
		cleanStorage();
	}
	
	
	@Test
	public void testIndex() throws JDOMException, FileNotFoundException, IOException {
		assertTrue(repositoryFacade.getIndexedMetadata("portal_ci_test", object.getIdentifier()+"-1").
				contains("Dieser Brauch zum Sankt Martinstag"));
	}
	
}
