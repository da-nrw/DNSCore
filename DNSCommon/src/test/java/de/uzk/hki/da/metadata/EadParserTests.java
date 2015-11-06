package de.uzk.hki.da.metadata;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.jaxen.JaxenException;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.XMLUtils;

public class EadParserTests {
	
	private static final Path WORK_AREA_ROOT_PATH = new RelativePath("src/test/resources/metadata/");
	private static String ddbEad = "NewDDBEad.XML";
	private static File ddbEadFile = Path.makeFile(WORK_AREA_ROOT_PATH, ddbEad);
	
	
	@Test
	public void testParsingReferencesFromEad() throws JDOMException, IOException, JaxenException {

		String metsReference1 = "mets_1_280920.xml";
		String metsReference2 = "mets_2_32042.xml";
		
		boolean metsReference1exists = false;
		boolean metsReference2exists = false;
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileReader fr1 = new FileReader(ddbEadFile);
		Document ddbEadDoc = builder.build(fr1);
		EadParser ep = new EadParser(ddbEadDoc);
		List<String> references = ep.getReferences();
		
		assertTrue(references.size()==2);
		
		for(String r : references) {
			if(r.equals(metsReference1)) {
				metsReference1exists = true;
			} else if(r.equals(metsReference2)) {
				metsReference2exists = true;
			}
		}
		assertTrue(metsReference1exists&&metsReference2exists);
	}	
}
