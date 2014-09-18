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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * @author Daniel M. de Oliveira
 * @author Thomas Kleinke
 */
public class PREMISBase extends AcceptanceTest {

	/**
	 * Does some assertions for PREMIS file objects to make sure they're structured according to our specifications.
	 *
	 * @param ns the ns
	 * @param e the e
	 * @param filename the filename
	 * @param puid the puid
	 * @author Thomas Kleinke
	 * @author Daniel M. de Oliveira
	 */
	static void verifyPREMISFileObjectHasCertainSubElements(Namespace ns,Element e,String filename,String puid){
		
		assertEquals(filename, e.getChild("originalName", ns).getValue());
		Element objCharElement = e.getChild("objectCharacteristics", ns);
		assertTrue(objCharElement.getChild("compositionLevel", ns).getValue() != null);
		Element fixityElement = objCharElement.getChild("fixity", ns);
		assertTrue(fixityElement.getChild("messageDigestAlgorithm", ns).getValue() != null);
		assertTrue(fixityElement.getChild("messageDigest", ns).getValue() != null);
		assertTrue(fixityElement.getChild("messageDigestOriginator", ns).getValue() != null);
		Element format = objCharElement.getChild("format", ns);
		assertTrue(format != null);
		Element formatRegistry = format.getChild("formatRegistry", ns);
		assertEquals("PRONOM", formatRegistry.getChild("formatRegistryName", ns).getValue());
		assertEquals(puid, formatRegistry.getChild("formatRegistryKey", ns).getValue());
		assertEquals("specification", formatRegistry.getChild("formatRegistryRole", ns).getValue());				
		assertTrue(objCharElement.getChild("objectCharacteristicsExtension", ns) != null);
	}
	
	
	
	/**
	 * Check convert event.
	 * @param ns the ns
	 * @param e the e
	 * @param fileName the file name
	 */
	static void checkConvertEvent(Namespace ns,Element e,String fileName,String nodeName){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		@SuppressWarnings("unchecked")
		List<Element> linkingObjectIdentifiers = e.getChildren("linkingObjectIdentifier", ns);
		int links = 0;
		for (Element linkingObjectIdentifer:linkingObjectIdentifiers){
			
			if ((linkingObjectIdentifer.getChildText("linkingObjectRole",ns).equals("source"))
				&& (linkingObjectIdentifer.getChildText("linkingObjectIdentifierValue",ns).endsWith("+a/"+fileName)))
					links++;

			if ((linkingObjectIdentifer.getChildText("linkingObjectRole",ns).equals("outcome"))
				&& (linkingObjectIdentifer.getChildText("linkingObjectIdentifierValue",ns).endsWith("+b/"+fileName)))
					links++;
		}
		assertEquals(2,links);

		assertEquals(nodeName,e.getChild("linkingAgentIdentifier",ns).getChildText("linkingAgentIdentifierValue",ns));

		try {dateFormat.parse(e.getChild("eventDateTime", ns).getValue());} catch (ParseException ex) {	fail();	}					
	}
}
