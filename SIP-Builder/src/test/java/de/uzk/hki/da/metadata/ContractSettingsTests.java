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

import org.junit.Test;

import de.uzk.hki.da.metadata.ContractSettings;
import static org.junit.Assert.assertEquals;

/**
 * Class under test: ContractSettings
 * 
 * @author Thomas Kleinke
 */
public class ContractSettingsTests {

	String pathToResourcesFolder = "src/test/resources/ContractSettingsTests/";
	
	
	/**
	 * Method under test: ContractSettings.loadContractSettingsFromFile()
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoadContractSettingsFromFile() throws Exception {
	
		ContractSettings settings = new ContractSettings(pathToResourcesFolder);
		
		assertEquals("10", settings.getDuration(0));
		assertEquals("20", settings.getDuration(1));
		assertEquals("30", settings.getDuration(2));
		assertEquals("40", settings.getDuration(3));
		
		assertEquals("800", settings.getWidthImage(0));
		assertEquals("1024", settings.getWidthImage(1));
		assertEquals("1600", settings.getWidthImage(2));
		
		assertEquals("600", settings.getHeightImage(0));
		assertEquals("768", settings.getHeightImage(1));
		assertEquals("1200", settings.getHeightImage(2));
		
		assertEquals("20%", settings.getPercentImage(0));
		assertEquals("40%", settings.getPercentImage(1));
		assertEquals("70%", settings.getPercentImage(2));
		
		assertEquals("10", settings.getOpacityImage(0));
		assertEquals("20", settings.getOpacityImage(1));
		assertEquals("30", settings.getOpacityImage(2));
		assertEquals("40", settings.getOpacityImage(3));
		assertEquals("50", settings.getOpacityImage(4));
		
		assertEquals("25", settings.getTextSizeImage(0));
		assertEquals("50", settings.getTextSizeImage(1));
		assertEquals("75", settings.getTextSizeImage(2));
		assertEquals("100", settings.getTextSizeImage(3));
		
		assertEquals("720", settings.getHeightVideo(0));
		assertEquals("1280", settings.getHeightVideo(1));
		assertEquals("1920", settings.getHeightVideo(2));
	}
	
	/**
	 * Method under test: ContractSettings.setStandardSettings()
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetStandardSettings() throws Exception {
	
		ContractSettings settings = new ContractSettings("wrong/Path/");
		
		assertEquals("5", settings.getDuration(0));
		assertEquals("15", settings.getDuration(1));
		assertEquals("60", settings.getDuration(2));
		assertEquals("120", settings.getDuration(3));
		
		assertEquals("480", settings.getWidthImage(0));
		assertEquals("800", settings.getWidthImage(1));
		assertEquals("1280", settings.getWidthImage(2));
		
		assertEquals("360", settings.getHeightImage(0));
		assertEquals("600", settings.getHeightImage(1));
		assertEquals("960", settings.getHeightImage(2));
		
		assertEquals("12.5%", settings.getPercentImage(0));
		assertEquals("25%", settings.getPercentImage(1));
		assertEquals("50%", settings.getPercentImage(2));
		
		assertEquals("5", settings.getOpacityImage(0));
		assertEquals("10", settings.getOpacityImage(1));
		assertEquals("25", settings.getOpacityImage(2));
		assertEquals("50", settings.getOpacityImage(3));
		assertEquals("100", settings.getOpacityImage(4));
		
		assertEquals("10", settings.getTextSizeImage(0));
		assertEquals("20", settings.getTextSizeImage(1));
		assertEquals("40", settings.getTextSizeImage(2));
		assertEquals("60", settings.getTextSizeImage(3));
		
		assertEquals("360", settings.getHeightVideo(0));
		assertEquals("720", settings.getHeightVideo(1));
		assertEquals("1080", settings.getHeightVideo(2));
	}
}
