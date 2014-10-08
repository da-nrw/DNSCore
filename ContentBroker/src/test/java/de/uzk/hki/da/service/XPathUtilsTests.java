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
package de.uzk.hki.da.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.w3c.dom.Document;

import de.uzk.hki.da.metadata.XPathUtils;


/**
 * The Class XPathUtilsTests.
 */
public class XPathUtilsTests {

	/**
	 * Test get x path element text.
	 *
	 * @author Thomas Kleinke
	 */
	@Test
	public void testGetXPathElementText() {
		
		Document dom = XPathUtils.parseDom("src/test/resources/service/XPathUtilsTests/premis.xml");
		if (dom == null)
			fail();
		
		String width = "";
		String height = "";
		String watermark = "";
		
		width = XPathUtils.getXPathElementText(dom, "/premis:premis/premis:rights/premis:rightsExtension/contract:rightsGranted/contract:publicationRight[contract:audience/text()='PUBLIC']/contract:restrictions/contract:restrictImage/contract:width/text()");
		height = XPathUtils.getXPathElementText(dom, "/premis:premis/premis:rights/premis:rightsExtension/contract:rightsGranted/contract:publicationRight[contract:audience/text()='PUBLIC']/contract:restrictions/contract:restrictImage/contract:height/text()");
		watermark = XPathUtils.getXPathElementText(dom, "/premis:premis/premis:rights/premis:rightsExtension/contract:rightsGranted/contract:publicationRight[contract:audience/text()='PUBLIC']/contract:restrictions/contract:restrictImage/contract:watermark/contract:watermarkString/text()");
		
		if (width == null || height == null) {
			fail();
		}
		assertEquals("Hallo", watermark);
		assertEquals("480", width);
		assertEquals("360", height);		
	}
}
