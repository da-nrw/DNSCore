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

/**
 * 
 */
package de.uzk.hki.da.at;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.TC;

/**
 * @author Daniel M. de Oliveira
 * @author Thomas Kleinke
 */
public class ATUseCaseIngestDeltaPREMISCheck extends PREMISBase {
	
	Object object = null;
	private static final String ORIG_NAME = "ATUseCaseIngestDelta";
	private static final String IDENTIFIER =   "ATUseCaseIngestDeltaIdentifier";
	private static final String containerName = ORIG_NAME+"."+C.TGZ;
	

	@Before
	public void setUp() throws IOException{
		setUpBase();

		object = putPackageToStorage(IDENTIFIER,ORIG_NAME,containerName);
		FileUtils.copyFile(Path.makeFile(TC.TEST_ROOT_AT,ORIG_NAME+"2.tgz"), 
				Path.makeFile(localNode.getIngestAreaRootPath(),C.TEST_USER_SHORT_NAME,containerName));
	}
	
	@After
	public void tearDown(){
		try{
			new File("/tmp/"+object.getIdentifier()+".pack_2.tar").delete();
			FileUtils.deleteDirectory(new File("/tmp/"+object.getIdentifier()+".pack_2"));
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		clearDB();
		cleanStorage();
	}
	

	
	
	@Test
	public void testProperPREMISCreation() throws Exception{
		
		waitForJobsToFinish(ORIG_NAME,500);
		object = retrievePackage(ORIG_NAME,"2");
		
		assertEquals(ORIG_NAME,object.getOrig_name());
		assertEquals(100,object.getObject_state());
		checkPremis(object.getIdentifier(),
				"/tmp/" + object.getIdentifier() + ".pack_2/data/"
				);
	}
	
	
	/**
	 * Check premis.
	 *
	 * @param objectIdentifier the object identifier
	 */
	@SuppressWarnings("unchecked")
	private void checkPremis(String objectIdentifier,String unpackedObjectDataPath) {

		String dataFolder = unpackedObjectDataPath;
		String repB2Name = "";
		File[] reps = new File(dataFolder).listFiles();
		for (File f : reps) {
			if (f.isDirectory() && f.getName().endsWith("b"))
				repB2Name = f.getName();
		}
		
		
		assertTrue(new File(dataFolder + repB2Name + "/premis.xml").exists());
		
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(new File(dataFolder + repB2Name + "/premis.xml"));
		} catch (Exception e) {
			throw new RuntimeException("Failed to read premis file", e);
		}
		
		Element rootElement = doc.getRootElement();
		Namespace ns = rootElement.getNamespace();
		
		List<Element> objectElements = rootElement.getChildren("object", ns);
				
		int checkedObjects = 0;
		for (Element e : objectElements){
			String identifierText = e.getChild("objectIdentifier", ns).getChildText("objectIdentifierValue", ns);
			
			if (identifierText.equals(objectIdentifier)) {
				Element identifierEl = e.getChild("objectIdentifier", ns);
				assertEquals(objectIdentifier, identifierEl.getChildText("objectIdentifierValue", ns));
				String originalName = (String) e.getChildText("originalName", ns);
				assertEquals(object.getOrig_name(),originalName);
				checkedObjects++;
			}				
			
			if (identifierText.equals(objectIdentifier + ".pack_1.tar")) {
				assertThat(e.getChildText("originalName", ns)).isEqualTo("Delta1.tgz");
				checkedObjects++;
			}
			
			if (identifierText.equals(objectIdentifier + ".pack_2.tar")) {
				assertThat(e.getChildText("originalName", ns)).isEqualTo("ATUseCaseIngestDelta.tgz");
				checkedObjects++;
			}
			
			if (identifierText.endsWith("+a/CCITT_1.TIF")){
				verifyPREMISFileObjectHasCertainSubElements(ns, e, "CCITT_1.TIF", "fmt/353");
				System.out.println("checked object: " + identifierText);
				checkedObjects++;
			}
			if (identifierText.endsWith("+a/CCITT_2.TIF")){
				verifyPREMISFileObjectHasCertainSubElements(ns, e, "CCITT_2.TIF", "fmt/353");
				System.out.println("checked object: " + identifierText);
				checkedObjects++;
			}
			if (identifierText.endsWith("+a/CCITT_1_UNCOMPRESSED.TIF")){
				verifyPREMISFileObjectHasCertainSubElements(ns, e, "CCITT_1_UNCOMPRESSED.TIF", "fmt/353");
				System.out.println("checked object: " + identifierText);
				checkedObjects++;
			}
			if (identifierText.endsWith("+b/CCITT_1.TIF")){
				verifyPREMISFileObjectHasCertainSubElements(ns, e, "CCITT_1.TIF", "fmt/353");
				System.out.println("checked object: " + identifierText);
				checkedObjects++;
			}
			if (identifierText.endsWith("+b/CCITT_2.TIF")){
				verifyPREMISFileObjectHasCertainSubElements(ns, e, "CCITT_2.TIF", "fmt/353");
				System.out.println("checked object: " + identifierText);
				checkedObjects++;
			}
			if (identifierText.endsWith("+a/CCITT_3.TIF")){
				verifyPREMISFileObjectHasCertainSubElements(ns, e, "CCITT_3.TIF", "fmt/353");
				System.out.println("checked object: " + identifierText);
				checkedObjects++;
			}
			if (identifierText.endsWith("+b/CCITT_3.TIF")){
				verifyPREMISFileObjectHasCertainSubElements(ns, e, "CCITT_3.TIF", "fmt/353");
				System.out.println("checked object: " + identifierText);
				checkedObjects++;
			}
		}
		assertThat(checkedObjects).isEqualTo(12);
		
		List<Element> eventElements = rootElement.getChildren("event", ns);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		int checkedEvents = 0;
		for (Element e : eventElements){
			String eventType = e.getChildText("eventType", ns);
			
			if (eventType.equals("CONVERT")){
				
				String eventIdentifier = e.getChild("eventIdentifier", ns).getChildText("eventIdentifierValue",ns);
				
				if ( eventIdentifier.endsWith("+b/CCITT_1.TIF") ) {
					checkConvertEvent(ns, e, "CCITT_1.TIF",localNode.getName()); 
					System.out.println("checked CONVERT event: +b/CCITT_1.TIF");
					checkedEvents++;
				}
				if ( eventIdentifier.endsWith("+b/CCITT_2.TIF") ) {
					checkConvertEvent(ns, e, "CCITT_2.TIF",localNode.getName());
					System.out.println("checked CONVERT event: +b/CCITT_2.TIF");
					checkedEvents++;
				}
				if ( eventIdentifier.endsWith("+b/CCITT_3.TIF") ) {
					checkConvertEvent(ns, e, "CCITT_3.TIF",localNode.getName()); 
					System.out.println("checked CONVERT event: +b/CCITT_3.TIF");
					checkedEvents++;
				}
			}
			
			if (eventType.equals("SIP_CREATION")){
				String packageName = e.getChild("linkingObjectIdentifier", ns).getChildText("linkingObjectIdentifierValue", ns);
				
				if (packageName.equals(objectIdentifier + ".pack_1.tar")) {
					assertTrue(e.getChild("eventIdentifier", ns).getChild("eventIdentifierValue", ns) != null);
					try {dateFormat.parse(e.getChild("eventDateTime", ns).getValue());} catch (ParseException ex) {	fail();	}	
					assertThat(e.getChild("linkingAgentIdentifier", ns).getChildText("linkingAgentIdentifierValue", ns)).startsWith("DA NRW SIP-Builder");
					assertThat(e.getChild("linkingObjectIdentifier", ns).getChildText("linkingObjectIdentifierValue", ns)).isEqualTo(objectIdentifier + ".pack_1.tar");
					System.out.println("checked SIP_CREATION event: "+objectIdentifier+".pack_1.tar");
					checkedEvents++;
				}
				if (packageName.equals(objectIdentifier + ".pack_2.tar")) {
					assertTrue(e.getChild("eventIdentifier", ns).getChild("eventIdentifierValue", ns) != null);
					try {dateFormat.parse(e.getChild("eventDateTime", ns).getValue());} catch (ParseException ex) {	fail();	}	
					assertThat(e.getChild("linkingAgentIdentifier", ns).getChildText("linkingAgentIdentifierValue", ns)).startsWith("DA NRW SIP-Builder");
					assertThat(e.getChild("linkingObjectIdentifier", ns).getChildText("linkingObjectIdentifierValue", ns)).isEqualTo(objectIdentifier + ".pack_2.tar");
					System.out.println("checked SIP_CREATION event: "+objectIdentifier+".pack_2.tar");
					checkedEvents++;
				}
			}
			
			if (eventType.equals("INGEST")){
				String ingestId = e.getChild("eventIdentifier", ns).getChildText("eventIdentifierValue", ns);
				if (ingestId.equals("1-20140718220" + "+1")) { // the object id is different from our test object id because it was generated in the run for creating the test tar package
					try {dateFormat.parse(e.getChild("eventDateTime", ns).getValue());} catch (ParseException ex) {	fail();	}	
					assertThat(e.getChild("linkingAgentIdentifier", ns).getChildText("linkingAgentIdentifierValue", ns)).isEqualTo("TEST");
					assertThat(e.getChild("linkingObjectIdentifier", ns).getChildText("linkingObjectIdentifierValue", ns)).isEqualTo(objectIdentifier + ".pack_1.tar");
					System.out.println("checked INGEST event: "+objectIdentifier+"1");
					checkedEvents++;
				}
				if (ingestId.equals(objectIdentifier + "+2")) {
					try {dateFormat.parse(e.getChild("eventDateTime", ns).getValue());} catch (ParseException ex) {	fail();	}	
					assertThat(e.getChild("linkingAgentIdentifier", ns).getChildText("linkingAgentIdentifierValue", ns)).isEqualTo("TEST");
					assertThat(e.getChild("linkingObjectIdentifier", ns).getChildText("linkingObjectIdentifierValue", ns)).isEqualTo(objectIdentifier + ".pack_2.tar");
					System.out.println("checked INGEST event: "+objectIdentifier+"2");
					checkedEvents++;
				}
			}
			
		}
		assertThat(checkedEvents).isEqualTo(8);
		
		List<Element> agentElements = rootElement.getChildren("agent", ns);
		int checkedAgents = 0;
		for (Element e : agentElements){
			Element agentIdentifier = e.getChild("agentIdentifier", ns);
			String agentName = agentIdentifier.getChildText("agentIdentifierValue", ns);			
			System.out.println("checked AGENT: "+agentName);
			if (agentName.startsWith("DA NRW SIP-Builder")){
				assertEquals("APPLICATION", e.getChildText("agentType", ns));
				assertEquals("APPLICATION_NAME", agentIdentifier.getChildText("agentIdentifierType", ns));
				checkedAgents++;
			}else if ("TEST".equals(agentName)){
				assertEquals("CONTRACTOR", e.getChildText("agentType", ns));
				assertEquals("CONTRACTOR_SHORT_NAME", agentIdentifier.getChildText("agentIdentifierType", ns));
				checkedAgents++;
			}else if (localNode.getName().equals(agentName)){
				assertEquals("NODE", e.getChildText("agentType", ns));
				assertEquals("NODE_NAME", agentIdentifier.getChildText("agentIdentifierType", ns));
				checkedAgents++;
			}
		}
		assertEquals(4, checkedAgents);	// change to 3 if both packages are build with the same SIPBuilder
	}
}

