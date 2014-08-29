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

public class ATUseCaseIngestMetsMods extends Base{
	
	private static final String origName = 		"ATUseCaseIngestMetsMods";
	private Object object;
	private static final Namespace METS_NS = Namespace.getNamespace("http://www.loc.gov/METS/");
	private static final Namespace XLINK_NS = Namespace.getNamespace("http://www.w3.org/1999/xlink");
	private String METS_XPATH_EXPRESSION = 		"//mets:file";
//	private static final String MODS_NS = 		"http://www.loc.gov/mods/v3";
	private static Document metsDoc;
	
	
	@Before
	public void setUp() throws IOException{
		setUpBase();
		ingest(origName);
		object = retrievePackage(origName,"1");
		System.out.println("object identifier: "+object.getIdentifier());
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
	public void checkReferencesAndMimetype() throws JDOMException, FileNotFoundException, IOException {
		
		SAXBuilder builder = new SAXBuilder();
		
		metsDoc = builder.build
				(new FileReader(Path.make(localNode.getWorkAreaRootPath(),"pips", "public", "TEST", object.getIdentifier(), object.getPackage_type()+".xml").toFile()));

		
		XPath xPath = XPath.newInstance(METS_XPATH_EXPRESSION);
		
		@SuppressWarnings("rawtypes")
		List allNodes = xPath.selectNodes(metsDoc);
		
		for (java.lang.Object node : allNodes) {
			Element fileElement = (Element) node;
			Attribute attr = fileElement.getChild("FLocat", METS_NS).getAttribute("href", XLINK_NS);
			Attribute attrMT = fileElement.getAttribute("MIMETYPE");
			assertTrue(attr.getValue().contains("http://data.danrw.de/") && attr.getValue().endsWith(".jpg"));
			assertTrue(attrMT.getValue().equals("image/jpeg"));
		}
	}
}
