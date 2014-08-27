package de.uzk.hki.da.at;

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
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.TESTHelper;

public class ATUseCaseIngestMetsMods extends Base{
	
	private static final String DATA_DANRW_DE = "http://data.danrw.de";
	private static final String origName = "ATUseCaseIngestMetsMods";
	private Object object;
	private static final String METS_NS = "http://www.loc.gov/METS/";
	private static final String MODS_NS = "http://www.loc.gov/mods/v3";
	
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
		
		TESTHelper.clearDB();
		cleanStorage();
	}
	
	@Test
	public void test() throws FileNotFoundException, JDOMException, IOException{
		object = retrievePackage(origName,"1");
		System.out.println("object identifier: "+object.getIdentifier());
		
		String packageType = object.getPackage_type();
		System.out.println("package type: "+packageType);
		
		SAXBuilder builder = new SAXBuilder();
		
		Document doc = builder.build
				(new FileReader(Path.make(localNode.getWorkAreaRootPath(),"pips", "public", "TEST", object.getIdentifier(), object.getPackage_type()+".xml").toFile()));
//		assertTrue(getLIDOURL(doc).contains(DATA_DANRW_DE));
	}
	
//	private String getLIDOURL(Document doc){
//		return doc.getRootElement()
//				.getChild("lido", LIDO_NS)
//				.getChild("administrativeMetadata", LIDO_NS)
//				.getChild("resourceWrap", LIDO_NS)
//				.getChild("resourceSet", LIDO_NS)
//				.getChild("resourceRepresentation", LIDO_NS)
//				.getChild("linkResource", LIDO_NS)
//				.getValue();
//	}
	
}
