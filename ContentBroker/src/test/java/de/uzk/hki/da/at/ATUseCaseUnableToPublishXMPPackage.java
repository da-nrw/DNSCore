package de.uzk.hki.da.at;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Object;

/**
 * @author Polina Gubaidullina
 */

public class ATUseCaseUnableToPublishXMPPackage extends AcceptanceTest{
	
	private static final String origName = "ATUseCaseUnableToPublishXMPPackage";
	private static final File retrievalFolder = new File("/tmp/XMPunpacked");
	
	@Before
	public void setUp() throws IOException{
		
		ath.putSIPtoIngestArea(origName, "tgz", origName);
		ath.awaitObjectState(origName,Object.ObjectStatus.ArchivedAndValid);
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
