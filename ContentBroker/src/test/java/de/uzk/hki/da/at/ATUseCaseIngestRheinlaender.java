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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.utils.Path;

import java.io.InputStream;

/**
 * Relates to AK-T/02 Ingest - Sunny Day Scenario.
 * The Rheinlaender package is of type EAD.
 * This test checks if the metadata have been updated correctly. 
 * @author Daniel M. de Oliveira
 */
public class ATUseCaseIngestRheinlaender extends Base{

	private static final String origName = "ATUseCaseIngestRheinlaender";
	private Object object;
	private static final Namespace METS_NS = Namespace.getNamespace("http://www.loc.gov/METS/");
	private static final Namespace XLINK_NS = Namespace.getNamespace("http://www.w3.org/1999/xlink");
	
	@Before
	public void setUp() throws IOException{
		setUpBase();
		ingest(origName);
	}
	
	@After
	public void tearDown(){
		try{
			new File("/tmp/"+object.getIdentifier()+".pack_1.tar").delete();
			FileUtils.deleteDirectory(new File("/tmp/"+object.getIdentifier()+".pack_1"));
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		clearDB();
		cleanStorage();
	}
	
	@Test
	public void test() throws FileNotFoundException, JDOMException, IOException, RepositoryException{
		object = retrievePackage(origName,"1");
		System.out.println("object identifier: "+object.getIdentifier());
		
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build
				(new FileReader(Path.make(localNode.getWorkAreaRootPath(),"pips", "public", "TEST", object.getIdentifier(), "mets_2_32044.xml").toFile()));
		assertTrue(getURL(doc).contains("http://data.danrw.de"));
		assertEquals("URL", getLoctype(doc));
		assertEquals("image/jpeg", getMimetype(doc));
		
//		SAXBuilder new_builder = new SAXBuilder();
//		Document new_doc;
//		InputStream metsStreamPublic = repositoryFacade.retrieveFile(object.getIdentifier(), "danrw", "METS");
//		assertNotNull(metsStreamPublic);
//		new_doc = new_builder.build(metsStreamPublic);
	}
	
	private String getURL(Document doc){
		return doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getChild("FLocat", METS_NS)
				.getAttributeValue("href", XLINK_NS);
	}
	
	private String getLoctype(Document doc){
		return doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getChild("FLocat", METS_NS)
				.getAttributeValue("LOCTYPE");
	}

	private String getMimetype(Document doc){
		return doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getAttributeValue("MIMETYPE");
	}
}
	
