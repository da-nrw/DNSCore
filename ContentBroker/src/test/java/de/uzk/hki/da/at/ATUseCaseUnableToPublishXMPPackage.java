package de.uzk.hki.da.at;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.jdom.Namespace;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.path.Path;

/**
 * @author Polina Gubaidullina
 */

public class ATUseCaseUnableToPublishXMPPackage extends AcceptanceTest{
	
	private static final Namespace RDF_NS = Namespace.getNamespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	private static final String origName = "ATUseCaseUnableToPublishXMPPackage";
	private static Object object;
	private static Path contractorsPipsPublic;
	private static final File retrievalFolder = new File("/tmp/XMPunpacked");
	
	@Before
	public void setUp() throws IOException{
		object = ath.ingest(origName);
	}
	
	@After
	public void tearDown() throws IOException{
		FileUtils.deleteDirectory(retrievalFolder);
	}
	
	@Test
	public void test() {
		System.out.println("TEST");
	}
}
