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

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.TESTHelper;

/**
 * @author Daniel M. de Oliveira
 */
public class ATUseCaseIngestXMP extends Base{

	private static final Namespace RDF_NS = Namespace.getNamespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	private static final String origName = "ATUseCaseIngestXMP";
	private static Object object;
	private static Path contractorsPipsPublic;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException{
		setUpBase();
		object = ingest(origName);
	}
	
	
	@AfterClass
	public static void tearDownAfterClass(){
		TESTHelper.clearDB();
		cleanStorage();
	}
	
	@Test
	public void testReplacements() throws FileNotFoundException, JDOMException, IOException {
		contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),C.WA_PIPS, C.WA_PUBLIC, C.TEST_USER_SHORT_NAME);
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build
				(new FileReader(Path.make(contractorsPipsPublic, object.getIdentifier(), "XMP.rdf").toFile()));
		assertTrue(getURL(doc).contains("http://data.danrw.de/file/"+object.getIdentifier()) && (getURL(doc).endsWith(".jpg")));
	}
	
	
	@Test
	public void testIndex() throws JDOMException, FileNotFoundException, IOException {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		assertTrue(repositoryFacade.getIndexedMetadata("portal_ci_test", object.getIdentifier()+"-1").
				contains("Dieser Brauch zum Sankt Martinstag"));
	}
	
	public String getURL(Document doc) {
		return doc.getRootElement()
				.getChild("Description", RDF_NS)
				.getAttributeValue("about", RDF_NS);
	}
}
