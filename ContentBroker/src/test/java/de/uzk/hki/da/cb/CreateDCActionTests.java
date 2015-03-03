/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2015 LVR-Infokom
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

package de.uzk.hki.da.cb;

import static de.uzk.hki.da.core.C.ENCODING_UTF_8;
import static de.uzk.hki.da.core.C.METADATA_STREAM_ID_DC;
import static de.uzk.hki.da.core.C.WA_PUBLIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.uzk.hki.da.core.SubsystemNotAvailableException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.metadata.XMLUtils;
import de.uzk.hki.da.repository.RepositoryException;

/**
 * @author Daniel M. de Oliveira
 */
public class CreateDCActionTests extends ConcreteActionUnitTest{

	@ActionUnderTest
	CreateDCAction action = new CreateDCAction();
	
	@Test
	public void test() throws FileNotFoundException, UserException, IOException, 
			RepositoryException, JDOMException, ParserConfigurationException, SAXException, SubsystemNotAvailableException {
		
		Map<String, String> dcMappings = new HashMap<String,String>();
		dcMappings.put("EAD", "conf/xslt/dc/ead_to_dc.xsl");
		action.setDcMappings(dcMappings);
		
		action.implementation();
	}
	
	
//	@Test
//	public void addURNToDC() throws IOException {
//		action.implementation(); 
//		FileInputStream in = new FileInputStream(makeMetadataFile(METADATA_STREAM_ID_DC, WA_PUBLIC));
//		String dcContent = IOUtils.toString(in, ENCODING_UTF_8);
//		assertTrue(dcContent.contains("<DC:identifier xmlns:DC=\"http://purl.org/dc/elements/1.1/\">urn</DC:identifier>"));
//	}
	
//	SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
//	Document doc = builder.build(new FileReader(publicDcFile));
//	Element child = doc.getRootElement()
//			.getChild("format", Namespace.getNamespace("http://purl.org/dc/elements/1.1/"));
//	
//	assertNotNull(child);
//	assertEquals("TEST", child.getText());
//	
//	doc = builder.build(new FileReader(instDcFile));
//	child = doc.getRootElement().getChild("format", Namespace.getNamespace("http://purl.org/dc/elements/1.1/"));
//	
//	assertNotNull(child);
//	assertEquals("TEST", child.getText());
//	
}
