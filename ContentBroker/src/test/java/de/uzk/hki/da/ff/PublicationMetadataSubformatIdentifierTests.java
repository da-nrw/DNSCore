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

package de.uzk.hki.da.ff;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.test.TC;

/**
 * @author Daniel M. de Oliveira
 */
public class PublicationMetadataSubformatIdentifierTests {

	private PublicationMetadataSubformatIdentifier identifier = new PublicationMetadataSubformatIdentifier();
	
	@Test
	public void testDetectEAD() throws IOException{
		assertEquals("EAD",identifier.identify(Path.makeFile(TC.TEST_ROOT_FF,"ead.xml")));
	}

	@Test
	public void testDetectMETS() throws IOException{
		assertEquals("METS",identifier.identify(Path.makeFile(TC.TEST_ROOT_FF,"mets.xml")));
	}
	
	@Test
	public void testDetectLIDO() throws IOException{
		assertEquals("LIDO",identifier.identify(Path.makeFile(TC.TEST_ROOT_FF,"lido.xml")));
	}
	
	@Test
	public void testDetectNoSubformat() throws IOException{
		assertEquals("",identifier.identify(Path.makeFile(TC.TEST_ROOT_FF,"other.xml")));
	}
}
