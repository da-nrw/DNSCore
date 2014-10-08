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

package de.uzk.hki.da.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.RightsSectionMetsXmlReader;
import de.uzk.hki.da.model.PublicationRight.Audience;
import de.uzk.hki.da.model.RightsStatement;


/**
 * The Class MetsRightsSectionXmlReaderTest.
 */
public class MetsRightsSectionXmlReaderTest {

	/**
	 * Test rights.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testRights() throws IOException {

		RightsSectionMetsXmlReader reader = new RightsSectionMetsXmlReader();
		List<RightsStatement> rights = reader.deserialize(new File("src/test/resources/metadata/darights_xml_metadata_reader_test.xml"));

		System.out.println(rights);
		assertNotNull(rights.get(0));
		assertNotNull(rights.get(0).getPublicationRights());
		assertFalse(rights.get(0).getPublicationRights().isEmpty());
		assertEquals(640,Integer.parseInt(rights.get(0).getPublicationRights().get(0).getImageRestriction().getWidth()));
		assertEquals(480,Integer.parseInt(rights.get(0).getPublicationRights().get(0).getImageRestriction().getHeight()));

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		calendar.set(2023, 5, 23, 0, 0, 0);

		assertEquals("EPFLICHT",rights.get(0).getPublicationRights().get(0).getLawID());
		assertEquals(2,rights.get(0).getPublicationRights().get(0).getTextRestriction().getCertainPages().length);
		assertEquals(23,rights.get(0).getPublicationRights().get(0).getTextRestriction().getCertainPages()[0]);
		assertEquals(42,rights.get(0).getPublicationRights().get(0).getTextRestriction().getCertainPages()[1]);

		Object object = new Object();
		object.setRights(rights.get(0));
		
		assertTrue(!object.grantsPublicationRight(Audience.PUBLIC));
		assertTrue(!object.grantsPublicationRight(Audience.INSTITUTION));

	}

}
