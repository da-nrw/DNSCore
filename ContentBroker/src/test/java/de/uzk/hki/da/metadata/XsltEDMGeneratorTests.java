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

package de.uzk.hki.da.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.utils.C;

/**
 * @author Daniel M. de Oliveira
 * @author Sebastian Cuy 
 */
public class XsltEDMGeneratorTests {

	private static final String XSLT_EDM_EAD_TO_EDM_XSL = "src/main/xslt/edm/ead_to_edm.xsl";
	private static final String XSLT_EDM_XMP_TO_EDM_XSL = "src/main/xslt/edm/xmp_to_edm.xsl";
	private static final File edmSourceFile = new File("src/test/resources/metadata/xsltEDMGeneratorTestsEADSource.xml");
	private static final File xmpSourceFile = new File("src/test/resources/metadata/xsltEDMGeneratorTestsXMPSource.rdf");
	private static InputStream edmInputStream; 
	
//	File file = new File("src/test/resources/convert/XsltConversionStrategyTests/data/ead_correct.xml"); // info: die file wurde in ursprünglichen Test mitbenutzt
	
	@Before
	public void setUp() throws IOException{
		String content = FileUtils.readFileToString(edmSourceFile, Charset.forName("UTF-8"));
		edmInputStream = IOUtils.toInputStream(content, "utf-8");
	}
	
	@Test
	public void test() throws TransformerException, IOException{
		
		XsltGenerator xsltEDMGenerator = new XsltGenerator(
				XSLT_EDM_EAD_TO_EDM_XSL, 
				edmInputStream);
		
		xsltEDMGenerator.setParameter("urn", "urn:nbn:de:danrw-1-20111111");
		xsltEDMGenerator.setParameter("cho-base-uri", "http://data.danrw.de/cho/1-20111111");
		xsltEDMGenerator.setParameter("aggr-base-uri", "http://data.danrw.de/aggregation/1-20111111");
		
		String result = xsltEDMGenerator.generate();
		
		System.out.println(":"+ result);
		
		assertNotNull(result);
		assertFalse(result.isEmpty());
	}
	
	@Test
	public void testConstructorWithNotExistentXSL(){
		try {
			new XsltGenerator(
					"filenotexistent", 
					null);
			fail();
		} catch (FileNotFoundException e) {
		} catch (TransformerConfigurationException e) {
			fail();
		}
	}
	
	@Test
	public void testXmpAggregationAboutEqualsAggregatedCho() throws TransformerException, IOException, JDOMException {
		String content = FileUtils.readFileToString(xmpSourceFile, Charset.forName("UTF-8"));
		XsltGenerator xsltEDMGenerator = new XsltGenerator(
				XSLT_EDM_XMP_TO_EDM_XSL,
				IOUtils.toInputStream(content, "utf-8"));

		xsltEDMGenerator.setParameter("urn", "urn:nbn:de:danrw-1-20111111");
		xsltEDMGenerator.setParameter("cho-base-uri", "http://data.danrw.de/cho/1-20111111");
		xsltEDMGenerator.setParameter("aggr-base-uri", "http://data.danrw.de/aggregation/1-20111111");

		String result = xsltEDMGenerator.generate();

		org.jdom.Document edmDoc = new SAXBuilder().build(new StringReader(result));
		List<Element> aggregations = (List<Element>) edmDoc.getRootElement().getChildren("Aggregation", C.ORE_NS);
		assertEquals(2, aggregations.size());
		for (Element aggregation : aggregations) {
			String about = aggregation.getAttributeValue("about", C.RDF_NS);
			assertTrue(about.startsWith("http://data.danrw.de/cho/1-20111111"));
			assertEquals(about, aggregation.getChild("aggregatedCHO", C.EDM_NS).getAttributeValue("resource", C.RDF_NS));
		}
	}
	
	
	
}
