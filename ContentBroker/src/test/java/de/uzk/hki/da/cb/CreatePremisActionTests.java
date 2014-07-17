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

package de.uzk.hki.da.cb;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.format.JhoveScanService;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;


/**
 * The Class CreatePremisActionTests.
 *
 * @author Thomas Kleinke
 * @author Daniel M. de Oliveira
 */
public class CreatePremisActionTests {

	
	/** The Constant logger. */
	static final Logger logger = LoggerFactory.getLogger(CreatePremisActionTests.class);
	
	/** The action. */
	CreatePremisAction action = new CreatePremisAction();
	
	/** The main folder. */
	Path workAreaRootPath = new RelativePath("src/test/resources/cb/CreatePremisActionTests/");

	String objCharTifAFilePath = workAreaRootPath + "/jhove_output_2013_07_21+14_28+a_image_tif.xml";
	String objCharTifBFilePath = workAreaRootPath + "/jhove_output_2013_07_21+14_28+b_image_tif.xml";
	String objCharPremisAFilePath = workAreaRootPath + "/jhove_output_2013_07_21+14_28+a_premis_xml.xml";
	
	/** The object. */
	Object object;

	/** The job. */
	Job job;
	
	private Package pkg;  // normal test: unused ,  delta test: the aip 
	private Package pkg2; // normal test: the sip,  delta test: the delta-sip
	
	/**
	 * Sets the up.
	 * @throws IOException 
	 */
	@Before
	public void setUp() throws IOException {
		FileUtils.copyFileToDirectory(C.PREMIS_XSD, new File("conf/"));
		FileUtils.copyFileToDirectory(C.XLINK_XSD, new File("conf/"));
		
		Node node = new Node();
		node.setWorkAreaRootPath(workAreaRootPath);
		
		pkg = new Package();
		pkg.setId(1234565);
		pkg.setName("1");
		pkg.setContainerName("testpackage.tgz");
				
		pkg2 = new Package();
		pkg2.setId(1);
		pkg2.setName("2");
		pkg2.setContainerName("testpackage.tgz");
		
		Contractor contractor = new Contractor();
		contractor.setShort_name("TEST");
		
		object = new Object();
		object.getPackages().add(pkg);
		object.getPackages().add(pkg2);
		object.setIdentifier("identifier");
		object.setUrn("urn:nbn:de:danrw-1-20130731121553");
		object.setContractor(contractor);
		object.setTransientNodeRef(node);
		
		CentralDatabaseDAO dao = mock (CentralDatabaseDAO.class);
		action.setDao(dao);
		

		JhoveScanService jhoveScanService = mock(JhoveScanService.class);
		when(jhoveScanService.getJhoveFolder()).
			thenReturn(workAreaRootPath + "/JhoveFolder");				
		action.setJhoveScanService(jhoveScanService);		
		
		DAFile a = new DAFile(pkg2,"2013_07_31+11_54+a","140864.tif");
		a.setFormatPUID("fmt/10");
		a.setPathToJhoveOutput(objCharTifAFilePath);
		DAFile b = new DAFile(pkg2,"2013_07_31+11_54+b","140864.tif");
		b.setFormatPUID("fmt/10");
		b.setPathToJhoveOutput(objCharTifBFilePath);
		DAFile c = new DAFile(pkg2,"2013_07_31+11_54+a","premis.xml");
		c.setFormatPUID("da-fmt/1");
		c.setPathToJhoveOutput(objCharPremisAFilePath);
		
		pkg2.getFiles().add(a);
		pkg2.getFiles().add(b);
		pkg2.getFiles().add(c);
		
		Event e = new Event();
		e.setType("CONVERT");
		e.setId(1);
		e.setSource_file(a);
		e.setTarget_file(b);
		e.setDetail("this was a conversion");
		e.setAgent_type("NODE");
		e.setAgent_name("TESTNODE");
		e.setDate(new Date());
		pkg2.getEvents().add(e);
		
		job = new Job();
		object.setOrig_name("testpackage");
		job.setId(7654321);
		job.setContainer_extension("tgz");
		job.setObject(object);
		job.setRep_name("2013_07_31+11_54+");
		action.setJob(job);
		action.setLocalNode(node);
		action.setObject(object);
		object.reattach();
	}
	
	/**
	 * Clean up.
	 */
	@After
	public void tearDown() {
		new File("conf/premis.xsd").delete();
		new File("conf/xlink.xsd").delete();
		Path.make(workAreaRootPath,"work/TEST/identifier_deltas/data/premis_old.xml").toFile().delete();
		Path.make(workAreaRootPath,"work/TEST/identifier/data/2013_07_31+11_54+b/premis.xml").toFile().delete();
		Path.make(workAreaRootPath,"work/TEST/identifier_deltas/data/2013_07_31+11_54+b/premis.xml").toFile().delete();
	}
	

	/**
	 * Test premis file creation.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @author Thomas Kleinke
	 * @author Daniel M. de Oliveira
	 */
	@Test
	public void testPremisFileCreation() throws IOException {
				
		object.getPackages().remove(pkg);
		
		action.implementation();
		checkPremisFile();
		
		
		
		assertTrue(job.getStatic_nondisclosure_limit() != null) ;
		assertTrue(job.getDynamic_nondisclosure_limit() == null);
	}
	
	/**
	 * Test delta premis file creation.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testDeltaPremisFileCreation() throws IOException {
		
		object.setIdentifier("identifier_deltas");
		
		FileUtils.copyFile(Path.make(workAreaRootPath,"premis_deltatest.xml").toFile(),
						   Path.make(workAreaRootPath,"work/TEST/identifier_deltas/data/premis_old.xml").toFile());
		
		action.implementation();
		checkDeltaPremisFile();		
	}
	
	/**
	 * Test check convert events.
	 */
	@Test
	public void testCheckConvertEvents() {
		
		object.getPackages().get(1).getEvents().clear();
		
		try {
			action.implementation();
			fail();
		} catch (Exception e) {
			logger.debug("Caught exception as expected: " + e.getMessage());
		}		
	}
	
	/**
	 * @author Thomas Kleinke
	 * @throws Exception 
	 */
	@Test
	public void testRollback() throws Exception {
		
		object.setIdentifier("identifier_deltas");
		
		FileUtils.copyFile(Path.makeFile(workAreaRootPath,"premis_deltatest.xml"),
						   Path.makeFile(workAreaRootPath,"work/TEST/identifier_deltas/data/premis_old.xml"));
		
		action.implementation();
		action.rollback();
		
		assertFalse(Path.makeFile(object.getDataPath(),object.getNameOfNewestBRep(),"premis.xml").exists());
		assertFalse(Path.makeFile(workAreaRootPath,"JhoveFolder","temp",new Integer(job.getId()).toString(),"premis_output").exists());
		
		assertEquals(1, object.getLatestPackage().getEvents().size());
		
		assertEquals(null, job.getStatic_nondisclosure_limit());
		assertEquals(null, job.getDynamic_nondisclosure_limit());
	}
	
	/**
	 * Check premis file.
	 *
	 * @author Thomas Kleinke
	 * @author Daniel M. de Oliveira
	 */
	@SuppressWarnings({ "unchecked" })
	private void checkPremisFile() {
		
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		Element objectCharTifARoot;
		Element objectCharTifBRoot;
		Element objectCharPremisARoot;
		try {
			doc = builder.build(Path.makeFile(workAreaRootPath,"work/TEST/identifier/data/2013_07_31+11_54+b/premis.xml"));
			
			objectCharTifARoot = builder.build(objCharTifAFilePath).getRootElement();
			objectCharTifBRoot = builder.build(objCharTifBFilePath).getRootElement();
			objectCharPremisARoot = builder.build(objCharPremisAFilePath).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Failed to read premis file", e);
		}
				
		Element rootElement = doc.getRootElement();
		Namespace ns = rootElement.getNamespace();
		Namespace jhoveNs = objectCharTifARoot.getNamespace();
		
		List<Element> objectElements = rootElement.getChildren("object", ns);
		
		int checkedObjects = 0;
		for (Element e:objectElements){
			String identifierText = e.getChild("objectIdentifier",ns).getChildText("objectIdentifierValue",ns);
			
			logger.debug(identifierText);
			
			if (identifierText.equals("2013_07_31+11_54+a/140864.tif")){
				assertEquals("140864.tif", e.getChild("originalName", ns).getValue());
				Element tifAObjCharElement = e.getChild("objectCharacteristics", ns);
				assertTrue(tifAObjCharElement.getChild("compositionLevel", ns).getValue() != null);
				Element tifAFixityElement = tifAObjCharElement.getChild("fixity", ns);
				assertTrue(tifAFixityElement.getChild("messageDigestAlgorithm", ns).getValue() != null);
				assertTrue(tifAFixityElement.getChild("messageDigest", ns).getValue() != null);
				assertTrue(tifAFixityElement.getChild("messageDigestOriginator", ns).getValue() != null);
				assertEquals("3999283", tifAObjCharElement.getChild("size", ns).getValue());
				Element format = tifAObjCharElement.getChild("format", ns);
				assertTrue(format != null);
				Element formatRegistry = format.getChild("formatRegistry", ns);
				assertEquals("PRONOM", formatRegistry.getChild("formatRegistryName", ns).getValue());
				assertEquals("fmt/10", formatRegistry.getChild("formatRegistryKey", ns).getValue());
				assertEquals("specification", formatRegistry.getChild("formatRegistryRole", ns).getValue());				
				assertEquals(objectCharTifARoot.getChild("repInfo", jhoveNs).getAttribute("uri").getValue(),
						tifAObjCharElement.getChild("objectCharacteristicsExtension", ns)
						.getChild("mdSec", ns).getChild("mdWrap", ns).getChild("xmlData", ns).getChild("jhove", jhoveNs)
						.getChild("repInfo", jhoveNs).getAttribute("uri").getValue());
				Element tifAStorageElement = e.getChild("storage", ns).getChild("contentLocation", ns).getChild("contentLocationValue", ns);
				assertEquals("2013_07_31+11_54+a/140864.tif", tifAStorageElement.getValue());
				assertEquals("identifier.pack_2.tar", e.getChild("relationship", ns)
						.getChild("relatedObjectIdentification", ns).getChildText("relatedObjectIdentifierValue", ns));
				
				checkedObjects++;
			}
			
			if (identifierText.equals("2013_07_31+11_54+a/premis.xml")){
				assertEquals("premis.xml", e.getChild("originalName", ns).getValue());
				Element premisAObjCharElement = e.getChild("objectCharacteristics", ns);
				assertTrue(premisAObjCharElement.getChild("compositionLevel", ns).getValue() != null);
				Element premisAFixityElement = premisAObjCharElement.getChild("fixity", ns);
				assertTrue(premisAFixityElement.getChild("messageDigestAlgorithm", ns).getValue() != null);
				assertTrue(premisAFixityElement.getChild("messageDigest", ns).getValue() != null);
				assertTrue(premisAFixityElement.getChild("messageDigestOriginator", ns).getValue() != null);
				assertEquals("3856", premisAObjCharElement.getChild("size", ns).getValue());
				Element format = premisAObjCharElement.getChild("format", ns);
				assertTrue(format != null);
				Element formatRegistry = format.getChild("formatRegistry", ns);
				assertEquals("PRONOM", formatRegistry.getChild("formatRegistryName", ns).getValue());
				assertEquals("da-fmt/1", formatRegistry.getChild("formatRegistryKey", ns).getValue());
				assertEquals("specification", formatRegistry.getChild("formatRegistryRole", ns).getValue());	
				assertEquals(objectCharPremisARoot.getChild("repInfo", jhoveNs).getAttribute("uri").getValue(),
						premisAObjCharElement.getChild("objectCharacteristicsExtension", ns)
						.getChild("mdSec", ns).getChild("mdWrap", ns).getChild("xmlData", ns).getChild("jhove", jhoveNs)
						.getChild("repInfo", jhoveNs).getAttribute("uri").getValue());
				Element premisBStorageElement = e.getChild("storage", ns).getChild("contentLocation", ns).getChild("contentLocationValue", ns);
				assertEquals("2013_07_31+11_54+a/premis.xml", premisBStorageElement.getValue());
				assertEquals("identifier.pack_2.tar", e.getChild("relationship", ns)
						.getChild("relatedObjectIdentification", ns).getChildText("relatedObjectIdentifierValue", ns));
				checkedObjects++;
			}
			
			if (identifierText.equals("2013_07_31+11_54+b/140864.tif")){
				assertEquals("140864.tif", e.getChild("originalName", ns).getValue());
				Element tifBObjCharElement = e.getChild("objectCharacteristics", ns);
				assertTrue(tifBObjCharElement.getChild("compositionLevel", ns).getValue() != null);
				Element tifBFixityElement = tifBObjCharElement.getChild("fixity", ns);
				assertTrue(tifBFixityElement.getChild("messageDigestAlgorithm", ns).getValue() != null);
				assertTrue(tifBFixityElement.getChild("messageDigest", ns).getValue() != null);
				assertTrue(tifBFixityElement.getChild("messageDigestOriginator", ns).getValue() != null);
				assertEquals("3999283", tifBObjCharElement.getChild("size", ns).getValue());
				Element format = tifBObjCharElement.getChild("format", ns);
				assertTrue(format != null);
				Element formatRegistry = format.getChild("formatRegistry", ns);
				assertEquals("PRONOM", formatRegistry.getChild("formatRegistryName", ns).getValue());
				assertEquals("fmt/10", formatRegistry.getChild("formatRegistryKey", ns).getValue());
				assertEquals("specification", formatRegistry.getChild("formatRegistryRole", ns).getValue());
				assertEquals(objectCharTifBRoot.getChild("repInfo", jhoveNs).getAttribute("uri").getValue(),
						tifBObjCharElement.getChild("objectCharacteristicsExtension", ns)
						.getChild("mdSec", ns).getChild("mdWrap", ns).getChild("xmlData", ns).getChild("jhove", jhoveNs)
						.getChild("repInfo", jhoveNs).getAttribute("uri").getValue());
				Element tifBStorageElement = e.getChild("storage", ns).getChild("contentLocation", ns).getChild("contentLocationValue", ns);
				assertEquals("2013_07_31+11_54+b/140864.tif", tifBStorageElement.getValue());
				assertEquals("identifier.pack_2.tar", e.getChild("relationship", ns)
						.getChild("relatedObjectIdentification", ns).getChildText("relatedObjectIdentifierValue", ns));
				checkedObjects++;
			}
			
			if (identifierText.equals("identifier")) {
				List<Element> identifierEls = e.getChildren("objectIdentifier", ns);
				assertEquals("urn:nbn:de:danrw-1-20130731121553", identifierEls.get(1).getChildText("objectIdentifierValue", ns));
				checkedObjects++;
			}
				
			if (identifierText.equals("identifier.pack_2.tar")) {
				assertEquals("testpackage.tgz", e.getChild("originalName", ns).getValue());
				checkedObjects++;
			}
		}
		assertEquals(5, checkedObjects);		
	
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		List<Element> eventElements = rootElement.getChildren("event", ns);
		
		int checkedEvents = 0;
		for (Element e:eventElements){
			String identifierText = e.getChildText("eventType",ns);
			
			if (identifierText.equals("CONVERT")){

				assertEquals("2013_07_31+11_54+b/140864.tif", e.getChild("eventIdentifier", ns).getChild("eventIdentifierValue", ns).
						getValue());
				try {dateFormat.parse(e.getChild("eventDateTime", ns).getValue());} catch (ParseException ex) {	fail();	}	
				assertEquals("this was a conversion", e.getChildText("eventDetail", ns));
				
				List<Element> linkingObjectIdentifiers = e.getChildren("linkingObjectIdentifier", ns);
				int checkedLOIs = 0;
				for (Element loi : linkingObjectIdentifiers) {
					if (loi.getChild("linkingObjectRole", ns).getValue().equals("source")) {
						assertEquals("2013_07_31+11_54+a/140864.tif", loi.getChild("linkingObjectIdentifierValue", ns).
								getValue());
						checkedLOIs++;
					}
					else if (loi.getChild("linkingObjectRole", ns).getValue().equals("outcome")) {
						assertEquals("2013_07_31+11_54+b/140864.tif", loi.getChild("linkingObjectIdentifierValue", ns).
								getValue());
						checkedLOIs++;
					}
				}
				
				assertEquals(2, checkedLOIs);				
				checkedEvents++;
			}
			if (identifierText.equals("SIP_CREATION")){
				assertTrue(e.getChild("eventIdentifier", ns).getChild("eventIdentifierValue", ns) != null);
				try {dateFormat.parse(e.getChild("eventDateTime", ns).getValue());} catch (ParseException ex) {	fail();	}	
				assertEquals("DA-NRW SIP-Builder 0.5.3", e.getChild("linkingAgentIdentifier", ns).getChild("linkingAgentIdentifierValue", ns).
						getValue());
				assertEquals("identifier.pack_2.tar",
						e.getChild("linkingObjectIdentifier", ns).getChild("linkingObjectIdentifierValue", ns).
						getValue());
				checkedEvents++;
			}
			if (identifierText.equals("INGEST")){
				assertEquals("identifier+2", e.getChild("eventIdentifier", ns).getChild("eventIdentifierValue", ns).
						getValue());
				try {dateFormat.parse(e.getChild("eventDateTime", ns).getValue());} catch (ParseException ex) {	fail();	}	
				assertEquals("TEST", e.getChild("linkingAgentIdentifier", ns).getChild("linkingAgentIdentifierValue", ns).
						getValue());
				assertEquals("identifier.pack_2.tar", e.getChild("linkingObjectIdentifier", ns).getChild("linkingObjectIdentifierValue", ns).
						getValue());
				checkedEvents++;
			}
		}
		assertEquals(3, checkedEvents);		

		List<Element> agents = rootElement.getChildren("agent", ns);
		int checkedAgents = 0;
		for (Element agentElement:agents){
			Element agentIdentifier = agentElement.getChild("agentIdentifier",ns);
			String agentName = agentIdentifier.getChildText("agentIdentifierValue", ns);			
			if ("DA-NRW SIP-Builder 0.5.3".equals(agentName)){
				assertEquals("APPLICATION", agentElement.getChild("agentType", ns).getValue());
				
				assertEquals("DA-NRW SIP-Builder 0.5.3", agentIdentifier.getChildText("agentIdentifierValue", ns));
				assertEquals("APPLICATION_NAME", agentIdentifier.getChildText("agentIdentifierType", ns));
				checkedAgents++;
			}else if ("TEST".equals(agentName)){
				assertEquals("CONTRACTOR", agentElement.getChild("agentType", ns).getValue());
				
				assertEquals("TEST", agentIdentifier.getChild("agentIdentifierValue", ns).getValue());
				assertEquals("CONTRACTOR_SHORT_NAME", agentIdentifier.getChild("agentIdentifierType", ns).getValue());
				checkedAgents++;
			}else if ("TESTNODE".equals(agentName)){
				assertEquals("NODE", agentElement.getChild("agentType", ns).getValue());
				
				assertEquals("NODE_NAME", agentIdentifier.getChild("agentIdentifierType", ns).getValue());
				assertEquals("TESTNODE", agentIdentifier.getChild("agentIdentifierValue", ns).getValue());
				checkedAgents++;
			}
		}
		assertEquals(3, checkedAgents);		
		
		Element rightsElement = rootElement.getChild("rights", ns);
		assertTrue(rightsElement != null);
		assertEquals("identifier#rights",rightsElement.getChild("rightsStatement", ns).getChild("rightsStatementIdentifier", ns).
				getChild("rightsStatementIdentifierValue", ns).getValue());
	}
	
	
	/**
	 * Check delta premis file.
	 *
	 * @author Thomas Kleinke
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private void checkDeltaPremisFile() {
		
		Element objectCharTifARoot;
		Element objectCharTifBRoot;
		Element objectCharPremisARoot;
		
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(workAreaRootPath + "/work/TEST/identifier_deltas/data/2013_07_31+11_54+b/premis.xml");
			
			objectCharTifARoot = builder.build(objCharTifAFilePath).getRootElement();
			objectCharTifBRoot = builder.build(objCharTifBFilePath).getRootElement();
			objectCharPremisARoot = builder.build(objCharPremisAFilePath).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Failed to read premis file", e);
		}
		
		Element rootElement = doc.getRootElement();
		Namespace ns = rootElement.getNamespace();
		Namespace jhoveNs = objectCharTifARoot.getNamespace();
		
		List<Element> objectElements = rootElement.getChildren("object", ns);
		
		int checkedObjects = 0;
		for (Element e:objectElements){
			String identifierText = e.getChild("objectIdentifier",ns).getChildText("objectIdentifierValue",ns);
			
			if (identifierText.equals("2013_07_21+14_28+a/image.tif")){
				assertEquals("image.tif", e.getChild("originalName", ns).getValue());
				Element tifAObjCharElement = e.getChild("objectCharacteristics", ns);
				assertTrue(tifAObjCharElement.getChild("compositionLevel", ns).getValue() != null);
				Element tifAFixityElement = tifAObjCharElement.getChild("fixity", ns);
				assertTrue(tifAFixityElement.getChild("messageDigestAlgorithm", ns).getValue() != null);
				assertTrue(tifAFixityElement.getChild("messageDigest", ns).getValue() != null);
				assertTrue(tifAFixityElement.getChild("messageDigestOriginator", ns).getValue() != null);
				assertEquals("201671", tifAObjCharElement.getChild("size", ns).getValue());
				Element format = tifAObjCharElement.getChild("format", ns);
				assertTrue(format != null);
				Element formatRegistry = format.getChild("formatRegistry", ns);
				assertEquals("PRONOM", formatRegistry.getChild("formatRegistryName", ns).getValue());
				assertEquals("fmt/10", formatRegistry.getChild("formatRegistryKey", ns).getValue());
				assertEquals("specification", formatRegistry.getChild("formatRegistryRole", ns).getValue());				
				assertTrue(tifAObjCharElement.getChild("objectCharacteristicsExtension", ns) != null);
				Element tifAStorageElement = e.getChild("storage", ns).getChild("contentLocation", ns).getChild("contentLocationValue", ns);
				assertEquals("2013_07_21+14_28+a/image.tif", tifAStorageElement.getValue());
				assertEquals("identifier_deltas.pack_1.tar", e.getChild("relationship", ns)
						.getChild("relatedObjectIdentification", ns).getChildText("relatedObjectIdentifierValue", ns));
				
				checkedObjects++;
			}
			
			if (identifierText.equals("2013_07_21+14_28+b/image.tif")){
				assertEquals("image.tif", e.getChild("originalName", ns).getValue());
				Element tifAObjCharElement = e.getChild("objectCharacteristics", ns);
				assertTrue(tifAObjCharElement.getChild("compositionLevel", ns).getValue() != null);
				Element tifAFixityElement = tifAObjCharElement.getChild("fixity", ns);
				assertTrue(tifAFixityElement.getChild("messageDigestAlgorithm", ns).getValue() != null);
				assertTrue(tifAFixityElement.getChild("messageDigest", ns).getValue() != null);
				assertTrue(tifAFixityElement.getChild("messageDigestOriginator", ns).getValue() != null);
				assertEquals("201671", tifAObjCharElement.getChild("size", ns).getValue());
				Element format = tifAObjCharElement.getChild("format", ns);
				assertTrue(format != null);
				Element formatRegistry = format.getChild("formatRegistry", ns);
				assertEquals("PRONOM", formatRegistry.getChild("formatRegistryName", ns).getValue());
				assertEquals("fmt/10", formatRegistry.getChild("formatRegistryKey", ns).getValue());
				assertEquals("specification", formatRegistry.getChild("formatRegistryRole", ns).getValue());				
				assertTrue(tifAObjCharElement.getChild("objectCharacteristicsExtension", ns) != null);
				Element tifAStorageElement = e.getChild("storage", ns).getChild("contentLocation", ns).getChild("contentLocationValue", ns);
				assertEquals("2013_07_21+14_28+b/image.tif", tifAStorageElement.getValue());
				assertEquals("identifier_deltas.pack_1.tar", e.getChild("relationship", ns)
						.getChild("relatedObjectIdentification", ns).getChildText("relatedObjectIdentifierValue", ns));
				
				checkedObjects++;
			}
			
			if (identifierText.equals("2013_07_21+14_28+a/premis.xml")){
				assertEquals("premis.xml", e.getChild("originalName", ns).getValue());
				Element tifAObjCharElement = e.getChild("objectCharacteristics", ns);
				assertTrue(tifAObjCharElement.getChild("compositionLevel", ns).getValue() != null);
				Element tifAFixityElement = tifAObjCharElement.getChild("fixity", ns);
				assertTrue(tifAFixityElement.getChild("messageDigestAlgorithm", ns).getValue() != null);
				assertTrue(tifAFixityElement.getChild("messageDigest", ns).getValue() != null);
				assertTrue(tifAFixityElement.getChild("messageDigestOriginator", ns).getValue() != null);
				assertEquals("3843", tifAObjCharElement.getChild("size", ns).getValue());
				Element format = tifAObjCharElement.getChild("format", ns);
				assertTrue(format != null);
				Element formatRegistry = format.getChild("formatRegistry", ns);
				assertEquals("PRONOM", formatRegistry.getChild("formatRegistryName", ns).getValue());
				assertEquals("da-fmt/1", formatRegistry.getChild("formatRegistryKey", ns).getValue());
				assertEquals("specification", formatRegistry.getChild("formatRegistryRole", ns).getValue());				
				assertTrue(tifAObjCharElement.getChild("objectCharacteristicsExtension", ns) != null);
				Element tifAStorageElement = e.getChild("storage", ns).getChild("contentLocation", ns).getChild("contentLocationValue", ns);
				assertEquals("2013_07_21+14_28+a/premis.xml", tifAStorageElement.getValue());
				assertEquals("identifier_deltas.pack_1.tar", e.getChild("relationship", ns)
						.getChild("relatedObjectIdentification", ns).getChildText("relatedObjectIdentifierValue", ns));
				
				checkedObjects++;
			}
			
			if (identifierText.equals("2013_07_31+11_54+a/140864.tif")){
				assertEquals("140864.tif", e.getChild("originalName", ns).getValue());
				Element tifAObjCharElement = e.getChild("objectCharacteristics", ns);
				assertTrue(tifAObjCharElement.getChild("compositionLevel", ns).getValue() != null);
				Element tifAFixityElement = tifAObjCharElement.getChild("fixity", ns);
				assertTrue(tifAFixityElement.getChild("messageDigestAlgorithm", ns).getValue() != null);
				assertTrue(tifAFixityElement.getChild("messageDigest", ns).getValue() != null);
				assertTrue(tifAFixityElement.getChild("messageDigestOriginator", ns).getValue() != null);
				assertEquals("3999283", tifAObjCharElement.getChild("size", ns).getValue());
				Element format = tifAObjCharElement.getChild("format", ns);
				assertTrue(format != null);
				Element formatRegistry = format.getChild("formatRegistry", ns);
				assertEquals("PRONOM", formatRegistry.getChild("formatRegistryName", ns).getValue());
				assertEquals("fmt/10", formatRegistry.getChild("formatRegistryKey", ns).getValue());
				assertEquals("specification", formatRegistry.getChild("formatRegistryRole", ns).getValue());				
				assertEquals(objectCharTifARoot.getChild("repInfo", jhoveNs).getAttribute("uri").getValue(),
						tifAObjCharElement.getChild("objectCharacteristicsExtension", ns)
						.getChild("mdSec", ns).getChild("mdWrap", ns).getChild("xmlData", ns).getChild("jhove", jhoveNs)
						.getChild("repInfo", jhoveNs).getAttribute("uri").getValue());
				Element tifAStorageElement = e.getChild("storage", ns).getChild("contentLocation", ns).getChild("contentLocationValue", ns);
				assertEquals("2013_07_31+11_54+a/140864.tif", tifAStorageElement.getValue());
				assertEquals("identifier_deltas.pack_2.tar", e.getChild("relationship", ns)
						.getChild("relatedObjectIdentification", ns).getChildText("relatedObjectIdentifierValue", ns));
				
				checkedObjects++;
			}
			
			if (identifierText.equals("2013_07_31+11_54+a/premis.xml")){
				assertEquals("premis.xml", e.getChild("originalName", ns).getValue());
				Element premisAObjCharElement = e.getChild("objectCharacteristics", ns);
				assertTrue(premisAObjCharElement.getChild("compositionLevel", ns).getValue() != null);
				Element premisAFixityElement = premisAObjCharElement.getChild("fixity", ns);
				assertTrue(premisAFixityElement.getChild("messageDigestAlgorithm", ns).getValue() != null);
				assertTrue(premisAFixityElement.getChild("messageDigest", ns).getValue() != null);
				assertTrue(premisAFixityElement.getChild("messageDigestOriginator", ns).getValue() != null);
				assertEquals("3856", premisAObjCharElement.getChild("size", ns).getValue());
				Element format = premisAObjCharElement.getChild("format", ns);
				assertTrue(format != null);
				Element formatRegistry = format.getChild("formatRegistry", ns);
				assertEquals("PRONOM", formatRegistry.getChild("formatRegistryName", ns).getValue());
				assertEquals("da-fmt/1", formatRegistry.getChild("formatRegistryKey", ns).getValue());
				assertEquals("specification", formatRegistry.getChild("formatRegistryRole", ns).getValue());	
				assertEquals(objectCharPremisARoot.getChild("repInfo", jhoveNs).getAttribute("uri").getValue(),
						premisAObjCharElement.getChild("objectCharacteristicsExtension", ns)
						.getChild("mdSec", ns).getChild("mdWrap", ns).getChild("xmlData", ns).getChild("jhove", jhoveNs)
						.getChild("repInfo", jhoveNs).getAttribute("uri").getValue());
				Element premisBStorageElement = e.getChild("storage", ns).getChild("contentLocation", ns).getChild("contentLocationValue", ns);
				assertEquals("2013_07_31+11_54+a/premis.xml", premisBStorageElement.getValue());
				assertEquals("identifier_deltas.pack_2.tar", e.getChild("relationship", ns)
						.getChild("relatedObjectIdentification", ns).getChildText("relatedObjectIdentifierValue", ns));
				checkedObjects++;
			}
			
			if (identifierText.equals("2013_07_31+11_54+b/140864.tif")){
				assertEquals("140864.tif", e.getChild("originalName", ns).getValue());
				Element tifBObjCharElement = e.getChild("objectCharacteristics", ns);
				assertTrue(tifBObjCharElement.getChild("compositionLevel", ns).getValue() != null);
				Element tifBFixityElement = tifBObjCharElement.getChild("fixity", ns);
				assertTrue(tifBFixityElement.getChild("messageDigestAlgorithm", ns).getValue() != null);
				assertTrue(tifBFixityElement.getChild("messageDigest", ns).getValue() != null);
				assertTrue(tifBFixityElement.getChild("messageDigestOriginator", ns).getValue() != null);
				assertEquals("3999283", tifBObjCharElement.getChild("size", ns).getValue());
				Element format = tifBObjCharElement.getChild("format", ns);
				assertTrue(format != null);
				Element formatRegistry = format.getChild("formatRegistry", ns);
				assertEquals("PRONOM", formatRegistry.getChild("formatRegistryName", ns).getValue());
				assertEquals("fmt/10", formatRegistry.getChild("formatRegistryKey", ns).getValue());
				assertEquals("specification", formatRegistry.getChild("formatRegistryRole", ns).getValue());
				assertEquals(objectCharTifBRoot.getChild("repInfo", jhoveNs).getAttribute("uri").getValue(),
						tifBObjCharElement.getChild("objectCharacteristicsExtension", ns)
						.getChild("mdSec", ns).getChild("mdWrap", ns).getChild("xmlData", ns).getChild("jhove", jhoveNs)
						.getChild("repInfo", jhoveNs).getAttribute("uri").getValue());
				Element tifBStorageElement = e.getChild("storage", ns).getChild("contentLocation", ns).getChild("contentLocationValue", ns);
				assertEquals("2013_07_31+11_54+b/140864.tif", tifBStorageElement.getValue());
				assertEquals("identifier_deltas.pack_2.tar", e.getChild("relationship", ns)
						.getChild("relatedObjectIdentification", ns).getChildText("relatedObjectIdentifierValue", ns));
				checkedObjects++;
			}
			
			if (identifierText.equals("identifier_deltas")) {
				List<Element> identifierEls = e.getChildren("objectIdentifier", ns);
				assertEquals("urn:nbn:de:danrw-1-20130731121553", identifierEls.get(1).getChildText("objectIdentifierValue", ns));
				checkedObjects++;
			}
				
			if (identifierText.equals("identifier_deltas.pack_1.tar")) {
				assertEquals("testpackage.tgz", e.getChild("originalName", ns).getValue());
				checkedObjects++;
			}
			
			if (identifierText.equals("identifier_deltas.pack_2.tar")) {
				assertEquals("testpackage.tgz", e.getChild("originalName", ns).getValue());
				checkedObjects++;
			}
		}
		assertEquals(9, checkedObjects);	
	
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		List<Element> eventElements = rootElement.getChildren("event", ns);
		
		int checkedEvents = 0;
		for (Element e:eventElements){
			String identifierText = e.getChildText("eventType",ns);
			
			if (identifierText.equals("CONVERT")){
				if (e.getChild("eventIdentifier", ns).getChild("eventIdentifierValue", ns).
						getValue().equals("2013_07_21+14_28+b/image.tif")) {				
					assertEquals("2013-08-26T16:37:24.258+01:00", e.getChild("eventDateTime", ns).getValue());
					assertEquals("this was a conversion", e.getChildText("eventDetail", ns));
					
					List<Element> linkingObjectIdentifiers = e.getChildren("linkingObjectIdentifier", ns);
					int checkedLOIs = 0;
					for (Element loi : linkingObjectIdentifiers) {
						if (loi.getChild("linkingObjectRole", ns).getValue().equals("source")) {
							assertEquals("2013_07_21+14_28+a/image.tif", loi.getChild("linkingObjectIdentifierValue", ns).
									getValue());
							checkedLOIs++;
						}
						else if (loi.getChild("linkingObjectRole", ns).getValue().equals("outcome")) {
							assertEquals("2013_07_21+14_28+b/image.tif", loi.getChild("linkingObjectIdentifierValue", ns).
									getValue());
							checkedLOIs++;
						}
					}				
					
					assertEquals(2, checkedLOIs);					
					checkedEvents++;
				} else if (e.getChild("eventIdentifier", ns).getChild("eventIdentifierValue", ns).
						getValue().equals("2013_07_31+11_54+b/140864.tif")) {
					try {
						Date date = dateFormat.parse(e.getChild("eventDateTime", ns).getValue());
					} catch (ParseException ex) {
						throw new RuntimeException("Invalid date: " +
								e.getChild("eventDateTime", ns).getValue());
					}
					assertEquals("this was a conversion", e.getChildText("eventDetail", ns));
					
					List<Element> linkingObjectIdentifiers = e.getChildren("linkingObjectIdentifier", ns);
					int checkedLOIs = 0;
					for (Element loi : linkingObjectIdentifiers) {
						if (loi.getChild("linkingObjectRole", ns).getValue().equals("source")) {
							assertEquals("2013_07_31+11_54+a/140864.tif", loi.getChild("linkingObjectIdentifierValue", ns).
									getValue());
							checkedLOIs++;
						}
						else if (loi.getChild("linkingObjectRole", ns).getValue().equals("outcome")) {
							assertEquals("2013_07_31+11_54+b/140864.tif", loi.getChild("linkingObjectIdentifierValue", ns).
									getValue());
							checkedLOIs++;
						}
					}
					
					assertEquals(2, checkedLOIs);
					checkedEvents++;
				}
			}
			
			if (identifierText.equals("SIP_CREATION")){
				if (e.getChild("linkingObjectIdentifier", ns).getChild("linkingObjectIdentifierValue", ns).
						getValue().equals("identifier_deltas.pack_1.tar")) {
					assertEquals("Sip_Creation_2013-07-20T13:25:14.432", e.getChild("eventIdentifier", ns).getChild("eventIdentifierValue", ns).getValue());
					assertEquals("2013-07-20T13:25:14.432+01:00", e.getChild("eventDateTime", ns).getValue());
					assertEquals("DA-NRW SIP-Builder 0.5.3", e.getChild("linkingAgentIdentifier", ns).getChild("linkingAgentIdentifierValue", ns).
							getValue());
					checkedEvents++;
				} else if (e.getChild("linkingObjectIdentifier", ns).getChild("linkingObjectIdentifierValue", ns).
						getValue().equals("identifier_deltas.pack_2.tar")) {
					assertTrue(e.getChild("eventIdentifier", ns).getChild("eventIdentifierValue", ns) != null);
					try {
						Date date = dateFormat.parse(e.getChild("eventDateTime", ns).getValue());
					} catch (ParseException ex) {
						throw new RuntimeException("Invalid date: " +
								e.getChild("eventDateTime", ns).getValue());
					}
					assertEquals("DA-NRW SIP-Builder 0.5.3", e.getChild("linkingAgentIdentifier", ns).getChild("linkingAgentIdentifierValue", ns).
							getValue());
					checkedEvents++;
				}
			}

			if (identifierText.equals("INGEST")){
				if (e.getChild("eventIdentifier", ns).getChild("eventIdentifierValue", ns).
						getValue().equals("1-20130731121553+1")) {
					assertEquals("2013-08-26T16:37:24.317+01:00", e.getChild("eventDateTime", ns).getValue());
					assertEquals("TEST", e.getChild("linkingAgentIdentifier", ns).getChild("linkingAgentIdentifierValue", ns).
							getValue());
					assertEquals("identifier_deltas.pack_1.tar", e.getChild("linkingObjectIdentifier", ns).getChild("linkingObjectIdentifierValue", ns).
							getValue());
					checkedEvents++;
				} else if (e.getChild("eventIdentifier", ns).getChild("eventIdentifierValue", ns).
						getValue().equals("identifier_deltas+2")) {
					try {
						Date date = dateFormat.parse(e.getChild("eventDateTime", ns).getValue());
					} catch (ParseException ex) {
						throw new RuntimeException("Invalid date: " +
								e.getChild("eventDateTime", ns).getValue());
					}
					assertEquals("TEST", e.getChild("linkingAgentIdentifier", ns).getChild("linkingAgentIdentifierValue", ns).
							getValue());
					assertEquals("identifier_deltas.pack_2.tar", e.getChild("linkingObjectIdentifier", ns).getChild("linkingObjectIdentifierValue", ns).
							getValue());
					checkedEvents++;
				}
			}
		}
		assertEquals(6, checkedEvents);		

		List<Element> agents = rootElement.getChildren("agent", ns);
		int checkedAgents = 0;
		for (Element agentElement:agents){
			Element agentIdentifier = agentElement.getChild("agentIdentifier", ns);
			String agentName = agentIdentifier.getChildText("agentIdentifierValue", ns);			
			if ("DA-NRW SIP-Builder 0.5.3".equals(agentName)){
				assertEquals("APPLICATION", agentElement.getChild("agentType", ns).getValue());
				
				assertEquals("DA-NRW SIP-Builder 0.5.3", agentIdentifier.getChildText("agentIdentifierValue", ns));
				assertEquals("APPLICATION_NAME", agentIdentifier.getChildText("agentIdentifierType", ns));
				checkedAgents++;
			}else if ("TEST".equals(agentName)){
				assertEquals("Test-Contractor", agentElement.getChild("agentName", ns).getValue());
				assertEquals("CONTRACTOR", agentElement.getChild("agentType", ns).getValue());
				
				assertEquals("TEST", agentIdentifier.getChild("agentIdentifierValue", ns).getValue());
				assertEquals("CONTRACTOR_SHORT_NAME", agentIdentifier.getChild("agentIdentifierType", ns).getValue());
				checkedAgents++;
			}else if ("TESTNODE".equals(agentName)){
				assertEquals("NODE", agentElement.getChild("agentType", ns).getValue());
				
				assertEquals("NODE_NAME", agentIdentifier.getChild("agentIdentifierType", ns).getValue());
				assertEquals("TESTNODE", agentIdentifier.getChild("agentIdentifierValue", ns).getValue());
				checkedAgents++;
			}
		}
		assertEquals(3, checkedAgents);		
		
		Element rightsElement = rootElement.getChild("rights", ns);
		assertTrue(rightsElement != null);
		assertEquals("identifier_deltas#rights",rightsElement.getChild("rightsStatement", ns).getChild("rightsStatementIdentifier", ns).
				getChild("rightsStatementIdentifierValue", ns).getValue());
	}
}
