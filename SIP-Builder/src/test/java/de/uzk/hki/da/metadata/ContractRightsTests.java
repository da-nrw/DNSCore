/*
  DA-NRW Software Suite | SIP-Builder
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import de.uzk.hki.da.metadata.ContractRights;
import de.uzk.hki.da.metadata.PublicationRights;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

/**
 * Class under test: ContractRights
 * 
 * @author Thomas Kleinke
 */
public class ContractRightsTests {

	String pathToResourcesFolder = "src/test/resources/ContractRightsTests/";
	
	
	/**
	 * Method under test: ContractRights.loadContractRightsFromFile()
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoadContractRightsFromFile() throws Exception {
		
		File contractRightsFile = new File(pathToResourcesFolder + "contractRights.xml");
		
		ContractRights rights = new ContractRights();
		rights.loadContractRightsFromFile(contractRightsFile);
		
		Date testDate =
				new SimpleDateFormat("yyyy-MM-dd'T00:00:00.000+01:00'").parse("2018-10-20T00:00:00.000+01:00");
		
		PublicationRights publicRights = rights.getPublicRights();
		assertTrue(publicRights.getAllowPublication());
		assertTrue(publicRights.getTempPublication());
		assertFalse(publicRights.getLawPublication());
		assertEquals(testDate, publicRights.getStartDate());
		assertTrue(publicRights.getTextRestriction());
		assertFalse(publicRights.getImageRestriction());
		assertTrue(publicRights.getImageRestrictionText());
		assertFalse(publicRights.getAudioRestriction());
		assertTrue(publicRights.getVideoRestriction());
		assertTrue(publicRights.getVideoDurationRestriction());
		assertEquals("12,15-20,30", publicRights.getPages());
		assertEquals("480", publicRights.getImageWidth());
		assertEquals("360", publicRights.getImageHeight());
		assertEquals("Test 1", publicRights.getFooterText());
		assertEquals(PublicationRights.TextType.center, publicRights.getImageTextType());
		assertEquals("100", publicRights.getWatermarkOpacity());
		assertEquals("40", publicRights.getWatermarkSize());
		assertEquals("5", publicRights.getAudioDuration());
		assertEquals("360", publicRights.getVideoSize());
		assertEquals("60", publicRights.getVideoDuration());
		
		PublicationRights institutionRights = rights.getInstitutionRights();
		assertTrue(institutionRights.getAllowPublication());
		assertFalse(institutionRights.getTempPublication());
		assertTrue(institutionRights.getLawPublication());
		assertEquals(PublicationRights.Law.URHG_DE, institutionRights.getLaw());
		assertFalse(institutionRights.getTextRestriction());
		assertTrue(institutionRights.getImageRestriction());
		assertTrue(institutionRights.getImageRestrictionText());
		assertTrue(institutionRights.getAudioRestriction());
		assertFalse(institutionRights.getVideoRestriction());
		assertFalse(institutionRights.getVideoDurationRestriction());
		assertEquals(null, institutionRights.getPages());
		assertEquals("1280", institutionRights.getImageWidth());
		assertEquals("960", institutionRights.getImageHeight());
		assertEquals("Test 2", institutionRights.getFooterText());
		assertEquals(PublicationRights.TextType.footer, institutionRights.getImageTextType());
		assertEquals("5", institutionRights.getWatermarkOpacity());
		assertEquals("10", institutionRights.getWatermarkSize());
		assertEquals("15", institutionRights.getAudioDuration());
		assertEquals("360", institutionRights.getVideoSize());
		assertEquals("5", institutionRights.getVideoDuration());
		
		assertEquals(ContractRights.ConversionCondition.NOTIFY, rights.getConversionCondition());
		assertFalse(rights.getDdbExclusion());
	}
}
