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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uzk.hki.da.service.IngesterFacade;


/**
 * The Class IngesterFacadeTest.
 */
public class IngesterFacadeTest {

	
	/**
	 * Test generate dsid.
	 */
	@Test
	public void testGenerateDSID() {
		
		assertEquals("_0001-täst.jpg", IngesterFacade.generateDSID("0001/täst.jpg"));
		assertEquals("_xmlFiles-élève.23_asdf_-gefräßig_42.xml", IngesterFacade.generateDSID("xmlFiles/élève.23[asdf]/gefräßig=42.xml"));
		assertEquals("allesok.xml",  IngesterFacade.generateDSID("allesok.xml"));
		assertEquals("nur-mit-pfad.png",  IngesterFacade.generateDSID("nur/mit/pfad.png"));
		
	}
	
	/*@Test
	public void testIngest() throws Exception {
		
		Fedora fedora = new Fedora("http://da-nrw.hki.uni-koeln.de:8080/fedora",
				"fedoraAdmin","Herrlich456FFEE");
		IngesterFacade ingester = new IngesterFacade(fedora);
		ingester.ingestPackage("urn:nbn:de:danrw-1-2342","/home/scuy/Desktop/urn+nbn+de+danrw-1-20111222412","KMB");
		
	}*/


}
