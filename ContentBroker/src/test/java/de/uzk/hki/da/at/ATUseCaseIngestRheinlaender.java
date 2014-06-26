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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Object;

/**
 * Relates to AK-T/02 Ingest - Sunny Day Scenario.
 * The Rheinlaender package is of type EAD.
 * This test checks if the metadata have been updated correctly. 
 * @author Daniel M. de Oliveira
 */
public class ATUseCaseIngestRheinlaender extends Base{

	private static final String origName = "ATUseCaseIngestRheinlaender";
	
	@Before
	public void setUp(){
		setUpBase();
	}
	
	@After
	public void tearDown(){
		clearDB();
		cleanStorage();
	}
	
	@Test
	public void test(){
		ingest(origName);
		Object object = retrievePackage(origName,"1");
		System.out.println("object identifier: "+object.getIdentifier());
	}
}
