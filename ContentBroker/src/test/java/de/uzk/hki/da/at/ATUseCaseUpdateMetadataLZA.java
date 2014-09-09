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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.TESTHelper;

/**
 * @author Polina Gubaidullina
 */
public class ATUseCaseUpdateMetadataLZA extends Base{

	private static final Namespace LIDO_NS = Namespace.getNamespace("http://www.lido-schema.org");
	private static final Namespace METS_NS = Namespace.getNamespace("http://www.loc.gov/METS/");
	private static final Namespace XLINK_NS = Namespace.getNamespace("http://www.w3.org/1999/xlink");
	private static final Namespace RDF_NS = Namespace.getNamespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	private String METS_XPATH_EXPRESSION = 		"//mets:file";
	
	private static String origName;
	private Object object;
	
	@Before
	public void setUp() throws IOException{
		setUpBase();
	}
	
	@After
	public void tearDown(){
		try{
			new File("/tmp/"+object.getIdentifier()+".pack_1.tar").delete();
			FileUtils.deleteDirectory(new File("/tmp/"+object.getIdentifier()+".pack_1"));
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		TESTHelper.clearDB();
		cleanStorage();
	}

	
	@Test
	public void updateXMPMetadataForLZA_BMPtoTIFF() throws Exception{
		
		origName = "ATUseCaseUpdateMetadataLZA_XMP";
		
		ingest(origName);
		
		object = retrievePackage(origName,"1");
		System.out.println("object identifier: "+object.getIdentifier());
		
		Path tmpObjectDirPath = Path.make("tmp", object.getIdentifier()+".pack_1", "data");	
		File[] tmpObjectSubDirs = new File (Path.make("tmp", object.getIdentifier()+".pack_1", "data").toString()).listFiles();
		String bRep = "";
		
		for (int i=0; i<tmpObjectSubDirs.length; i++) {
			if(tmpObjectSubDirs[i].getName().contains("+b")) {
				bRep = tmpObjectSubDirs[i].getName();
			}
		}
		
		String xmpFileName = "XMP.rdf";
		
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, xmpFileName).toFile()));
		assertTrue(getXmpRdfDescription(doc).equals("LVR_ILR_0000008126.tif"));
	}
	
	
	
	@Test
	public void updateLidoMetadataForLZA_BMPtoTIFF() throws Exception{
		
		origName = "ATUseCaseUpdateMetadataLZA_LIDO";
		
		String LidoFileName = "LIDO-Testexport2014-07-04-FML-Auswahl.xml";
		
		ingest(origName);
		
		object = retrievePackage(origName,"1");
		System.out.println("object identifier: "+object.getIdentifier());
		
		Path tmpObjectDirPath = Path.make("tmp", object.getIdentifier()+".pack_1", "data");	
		File[] tmpObjectSubDirs = new File (Path.make("tmp", object.getIdentifier()+".pack_1", "data").toString()).listFiles();
		String bRep = "";
		
		for (int i=0; i<tmpObjectSubDirs.length; i++) {
			if(tmpObjectSubDirs[i].getName().contains("+b")) {
				bRep = tmpObjectSubDirs[i].getName();
			}
		}
		
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, LidoFileName).toFile()));
		assertTrue(getLIDOURL(doc).equals("Picture2.tif"));
	}
	
	
	@Test
	public void updateMetsMetadataForLZA_BMPtoTIFF() throws Exception{
		
		origName = "ATUseCaseUpdateMetadataLZA_METS";
		
		String metsFileName = "export_mets.xml";
		
		ingest(origName);
		
		object = retrievePackage(origName,"1");
		System.out.println("object identifier: "+object.getIdentifier());		
		
		Path tmpObjectDirPath = Path.make("tmp", object.getIdentifier()+".pack_1", "data");	
		File[] tmpObjectSubDirs = new File (Path.make("tmp", object.getIdentifier()+".pack_1", "data").toString()).listFiles();
		String bRep = "";
		
		for (int i=0; i<tmpObjectSubDirs.length; i++) {
			if(tmpObjectSubDirs[i].getName().contains("+b")) {
				bRep = tmpObjectSubDirs[i].getName();
			}
		}
		
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, metsFileName).toFile()));
		checkReferencesAndMimetype(doc);
	}
	
	
	@Test
	public void updateEADMetadataForLZA_BMPtoTIFF() throws Exception{
		
		origName = "ATUseCaseUpdateMetadataLZA_EAD";
		
		ingest(origName);
		
		object = retrievePackage(origName,"1");
		System.out.println("object identifier: "+object.getIdentifier());
		
		Path tmpObjectDirPath = Path.make("tmp", object.getIdentifier()+".pack_1", "data");	
		File[] tmpObjectSubDirs = new File (Path.make("tmp", object.getIdentifier()+".pack_1", "data").toString()).listFiles();
		String bRep = "";
		
		for (int i=0; i<tmpObjectSubDirs.length; i++) {
			if(tmpObjectSubDirs[i].getName().contains("+b")) {
				bRep = tmpObjectSubDirs[i].getName();
			}
		}
		
		SAXBuilder builder = new SAXBuilder();
		Document doc1 = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_2_32044.xml").toFile()));
		assertTrue(getMetsURL(doc1).equals("Picture1.tif"));
		assertTrue(getMetsMimetype(doc1).equals("image/tiff"));
		
		Document doc2 = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_2_32045.xml").toFile()));
		assertTrue(getMetsURL(doc2).equals("Picture2.tif"));
		assertTrue(getMetsMimetype(doc2).equals("image/tiff"));
		
		Document doc3 = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_2_32046.xml").toFile()));
		assertTrue(getMetsURL(doc3).equals("Picture3.tif"));
		assertTrue(getMetsMimetype(doc3).equals("image/tiff"));
		
		Document doc4 = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_2_32047.xml").toFile()));
		assertTrue(getMetsURL(doc4).equals("Picture4.tif"));
		assertTrue(getMetsMimetype(doc4).equals("image/tiff"));
		
		Document doc5 = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_2_32048.xml").toFile()));
		assertTrue(getMetsURL(doc5).equals("Picture5.tif"));
		assertTrue(getMetsMimetype(doc5).equals("image/tiff"));
	}
	
	
	private String getLIDOURL(Document doc){
		return doc.getRootElement()
				.getChild("lido", LIDO_NS)
				.getChild("administrativeMetadata", LIDO_NS)
				.getChild("resourceWrap", LIDO_NS)
				.getChild("resourceSet", LIDO_NS)
				.getChild("resourceRepresentation", LIDO_NS)
				.getChild("linkResource", LIDO_NS)
				.getValue();
	}
	
	private String getMetsURL(Document doc){
		
		return doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getChild("FLocat", METS_NS)
				.getAttributeValue("href", XLINK_NS);
	}
	
	private String getMetsMimetype(Document doc){
		
		return doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getAttributeValue("MIMETYPE");
	}
	
	
	public String getXmpRdfDescription(Document doc) {
		return doc.getRootElement()
				.getChild("Description", RDF_NS)
				.getAttributeValue("about", RDF_NS);
	}
	
	public void checkReferencesAndMimetype(Document doc) throws JDOMException, FileNotFoundException, IOException {
		
		XPath xPath = XPath.newInstance(METS_XPATH_EXPRESSION);
		
		@SuppressWarnings("rawtypes")
		List allNodes = xPath.selectNodes(doc);
		
		for (java.lang.Object node : allNodes) {
			Element fileElement = (Element) node;
			Attribute attr = fileElement.getChild("FLocat", METS_NS).getAttribute("href", XLINK_NS);
//			Attribute attrLoctype = fileElement.getChild("FLocat", METS_NS).getAttribute("LOCTYPE");
			Attribute attrMT = fileElement.getAttribute("MIMETYPE");
			assertTrue(attr.getValue().endsWith(".tif"));
			assertTrue(attrMT.getValue().equals("image/tiff"));
		}
	}
}
