/*
 DA-NRW Software Suite | ContentBroker
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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;
import de.uzk.hki.da.utils.XMLUtils;

/**
 * <a href="../../../../src/main/markdown/feature_restructure_action_scan.md">Feature Description</a>
 * 
 * @author Gaby Bender
 */
public class ATRestructureActionScanVirus extends AcceptanceTest{

	private static File sourceDir = new File("src/test/resources/at/");
	private static ProcessInformation pi;
	public static final String originalName = "ATRestructureActionScanVirusPremis";
	public static final File unpackedDIP = new File("/tmp/ATRestructureActionVPREMISCheck");
	
	@Before
	public void setUp() throws IOException{	
//		FileUtils.deleteDirectory(targetDir);
	}
	
	@After
	public void tearDown() throws IOException{
//		pi.destroy();
	}

	@Test
	public void testNoVirus() throws IOException {

		File source = new File(sourceDir, "ATRestructureActionScanVirus/noVirus");

 		pi = new CommandLineConnector().runCmdSynchronously(new String[] {
                "clamscan", "-r",
                source.getAbsolutePath()}, 0);
 		
 		assertTrue(pi.getExitValue() == 0);
	}
	
	@Test
	public void testVirus() throws IOException {

		File source = new File(sourceDir, "ATRestructureActionScanVirus/virus");

		pi = new CommandLineConnector().runCmdSynchronously(new String[] {
                "clamscan", "-r",
                source.getAbsolutePath()}, 0);

 		assertTrue(pi.getExitValue() == 1);
	}
	
	@Test
	public void testPremisNoVirus() throws IOException {
		Object object = null;
		
		ath.putSIPtoIngestArea(originalName, "tar", originalName);
		ath.awaitObjectState(originalName,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		object=ath.getObject(originalName);
		
		ath.retrieveAIP(object,unpackedDIP,"1");
		assertThat(object.getObject_state()).isEqualTo(100);
		String unpackedObjectPath = unpackedDIP.getAbsolutePath()+"/";
		String folders[] = new File(unpackedObjectPath + "data/").list();
		String repBName="";
		for (String f:folders){
			if (f.contains("+b")) repBName = f;
		}
		assertTrue(new File(unpackedObjectPath + "data/" + repBName + "/premis.xml").exists());
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc;
		try {
			doc = builder.build(new File(unpackedObjectPath +  "data/" +repBName + "/premis.xml"));
		} catch (Exception e) {
			throw new RuntimeException("Failed to read premis file", e);
		}
		
		Element rootElement = doc.getRootElement();
		Namespace ns = rootElement.getNamespace();
		
		
		@SuppressWarnings("unchecked")
		List<Element> eventElements = rootElement.getChildren("event", ns);
		
		for (Element e:eventElements){
			String eventType = e.getChildText("eventType", ns);
			
			if (eventType.equals(C.EVENT_TYPE_VIRUS_SCAN)){
				String eventDetail = e.getChildText("eventDetail",ns);
				String eventDetailText = "Gescannt mit 'ClamAV";
				if (eventDetail.contains(eventDetailText)){
					assertTrue(true);
				} else 
				 assertTrue(false);
			}
			
		}
	}
	
}
