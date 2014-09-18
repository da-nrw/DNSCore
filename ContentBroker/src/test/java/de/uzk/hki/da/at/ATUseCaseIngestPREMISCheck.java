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
package de.uzk.hki.da.at;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.Test;

import de.uzk.hki.da.model.Object;


/**
 * Relates to AK-T/02 Ingest - Sunny Day Scenario.
 * @author Daniel M. de Oliveira
 */
public class ATUseCaseIngestPREMISCheck extends PREMISBase{
	
	private static final String originalName = "ATUseCaseIngest1";
	private static final File unpackedDIP = new File("/tmp/ATUseCaseIngestPREMISCheck");
	private Object object = null;
	
	@After
	public void tearDown() throws IOException{
		FileUtils.deleteDirectory(unpackedDIP);
	}
	
	@Test
	public void testProperPREMISCreation() throws Exception {
		
		object = ath.ingest(originalName);
		
		ath.retrievePackage(object,unpackedDIP,"1");
		assertThat(object.getObject_state()).isEqualTo(100);
		String unpackedObjectPath = unpackedDIP.getAbsolutePath()+"/";
		
		String folders[] = new File(unpackedObjectPath + "data/").list();
		String repAName="";
		String repBName="";
		for (String f:folders){
			if (f.contains("+a")) repAName = f;
			if (f.contains("+b")) repBName = f;
		}
		verifyAIPContainsExpectedFiles(unpackedObjectPath, repAName, repBName);
		verifyPREMISContainsSpecifiedElements(unpackedObjectPath,object,repAName,repBName,localNode.getName());

		assertTrue(bagIsValid(unpackedObjectPath));
	}
	
	
	/**
	 * Specified at {@link https://wiki1.hbz-nrw.de/display/DAN/PREMIS-Spezifikationen}.
	 * Fullfilling this specification is part of the use case ingest.
	 *
	 * @param unpackedObjectPath
	 * @param object
	 * @param repAName the rep a name
	 * @param repBName the rep b name
	 * @see {@link https://wiki1.hbz-nrw.de/display/DAN/Ingest}
	 */
	@SuppressWarnings("unchecked")
	private void verifyPREMISContainsSpecifiedElements(
			String unpackedObjectPath,
			Object object,
			String repAName,
			String repBName,
			String nodeName) {
		assertTrue(new File(unpackedObjectPath + "data/" +  repBName + "/premis.xml").exists());
		String objectIdentifier = object.getIdentifier();
		
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(new File(unpackedObjectPath +  "data/" + repBName + "/premis.xml"));
		} catch (Exception e) {
			throw new RuntimeException("Failed to read premis file", e);
		}
		
		Element rootElement = doc.getRootElement();
		Namespace ns = rootElement.getNamespace();
		
		List<Element> objectElements = rootElement.getChildren("object", ns);
		
		int checkedObjects = 0;
		for (Element e:objectElements){
			String identifierText = e.getChild("objectIdentifier",ns).getChildText("objectIdentifierValue",ns);
			
			if (identifierText.equals(objectIdentifier)) {
				List<Element> identifierEls = e.getChildren("objectIdentifier", ns);
				assertEquals(object.getUrn(), identifierEls.get(1).getChildText("objectIdentifierValue", ns)); // TODO shouldn't it be the unique object identifier?
				String originalName = e.getChildText("originalName", ns);
				assertEquals(object.getOrig_name(),originalName);
				checkedObjects++;
			}
			
			if (identifierText.equals(objectIdentifier + ".pack_1.tar")) {
				assertThat(e.getChildText("originalName",ns)).isEqualTo("ATUseCaseIngest1.tgz");
				checkedObjects++;
			}
						
			if (identifierText.contains("a/CCITT_1.TIF")){
				verifyPREMISFileObjectHasCertainSubElements(ns, e, "CCITT_1.TIF", "fmt/353");
				checkedObjects++;
			}
			if (identifierText.contains("a/CCITT_2.TIF")){
				verifyPREMISFileObjectHasCertainSubElements(ns, e, "CCITT_2.TIF", "fmt/353");
				checkedObjects++;
			}
			if (identifierText.contains("a/CCITT_1_UNCOMPRESSED.TIF")){
				verifyPREMISFileObjectHasCertainSubElements(ns, e, "CCITT_1_UNCOMPRESSED.TIF", "fmt/353");
				checkedObjects++;
			}
			if (identifierText.contains("b/CCITT_1.TIF")){
				verifyPREMISFileObjectHasCertainSubElements(ns, e, "CCITT_1.TIF", "fmt/353");
				checkedObjects++;
			}
			if (identifierText.contains("b/CCITT_2.TIF")){
				verifyPREMISFileObjectHasCertainSubElements(ns, e, "CCITT_2.TIF", "fmt/353");
				checkedObjects++;
			}
		}
		assertThat(checkedObjects).isEqualTo(7);
		
		
		List<Element> eventElements = rootElement.getChildren("event", ns);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		int checkedEvents = 0;
		for (Element e:eventElements){
			String eventType = e.getChildText("eventType", ns);
			
			if (eventType.equals("CONVERT")){
				String eventDetail = e.getChildText("eventDetail",ns);
				String event1fileName = "CCITT_1.TIF";
				if (eventDetail.contains(event1fileName)){
					checkConvertEvent(ns, e, event1fileName,nodeName);
					checkedEvents++;
				}
				String event2fileName = "CCITT_1.TIF";
				if (eventDetail.contains(event2fileName)){
					checkConvertEvent(ns, e, event2fileName,nodeName);
					checkedEvents++;
				}
			}
			
			if (eventType.equals("SIP_CREATION")){
				assertTrue(e.getChild("eventIdentifier", ns).getChild("eventIdentifierValue", ns) != null);
				try {dateFormat.parse(e.getChild("eventDateTime", ns).getValue());} catch (ParseException ex) {	fail();	}	
				assertThat(e.getChild("linkingAgentIdentifier", ns).getChildText("linkingAgentIdentifierValue", ns)).isEqualTo("DA NRW SIP-Builder 0.5.3");
				assertThat(e.getChild("linkingObjectIdentifier", ns).getChildText("linkingObjectIdentifierValue", ns)).isEqualTo(objectIdentifier+".pack_1.tar");
				checkedEvents++;
			}
			
			if (eventType.equals("INGEST")){
//				assertEquals("7654321", e.getChild("eventIdentifier", ns).getChild("eventIdentifierValue", ns).
//						getValue());
				try {dateFormat.parse(e.getChild("eventDateTime", ns).getValue());} catch (ParseException ex) {	fail();	}	
				assertThat(e.getChild("linkingAgentIdentifier", ns).getChildText("linkingAgentIdentifierValue", ns)).isEqualTo("TEST");
				assertThat(e.getChild("linkingObjectIdentifier", ns).getChildText("linkingObjectIdentifierValue", ns)).isEqualTo(objectIdentifier+".pack_1.tar");
				checkedEvents++;
			}
			
		}
		assertThat(checkedEvents).isEqualTo(4);
	}
	
	
	
	
	
	/**
	 * Assert true that aip contains expected files.
	 *
	 * @param repAName the rep a name
	 * @param repBName the rep b name
	 */
	private void verifyAIPContainsExpectedFiles(
			String objectPath,
			String repAName,
			String repBName) {
		
		// check files
		String dataFolder = objectPath + "/data/";
		assertTrue(new File(dataFolder+repAName+"/"+"CCITT_1.TIF").exists());
		assertTrue(new File(dataFolder+repAName+"/"+"CCITT_2.TIF").exists());
		assertTrue(new File(dataFolder+repAName+"/"+"premis.xml").exists());
		assertTrue(new File(dataFolder+repAName+"/"+"CCITT_1_UNCOMPRESSED.TIF").exists());
		assertTrue(new File(dataFolder+repBName+"/"+"CCITT_1.TIF").exists());
		assertTrue(new File(dataFolder+repBName+"/"+"CCITT_2.TIF").exists());
		assertTrue(new File(dataFolder+repBName +"/"+"premis.xml").exists());

	}
	
	
	private boolean bagIsValid(String unpackedObjectPath) throws IOException{
		BagFactory bagFactory = new BagFactory();
		Bag bag = bagFactory.createBag(new File(unpackedObjectPath));
		if(!bag.verifyValid().isSuccess()){
			bag.close();
			return false;
		}
		bag.close();
		return true;
	}
	
	
	
}
