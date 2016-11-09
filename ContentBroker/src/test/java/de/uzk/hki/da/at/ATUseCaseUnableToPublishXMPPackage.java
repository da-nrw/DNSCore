package de.uzk.hki.da.at;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.FolderUtils;

/**
 * @author Polina Gubaidullina
 */

public class ATUseCaseUnableToPublishXMPPackage extends AcceptanceTest{
	
	private static final String origName = "ATUseCaseUnableToPublishXMPPackage";
	private static final File retrievalFolder = new File("/tmp/XMPunpacked");
	
	@Before
	public void setUp() throws IOException{
		
		ath.putSIPtoIngestArea(origName, "tgz", origName);
		ath.awaitObjectState(origName,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
	}
	
	@After
	public void tearDown() throws IOException{
		FolderUtils.deleteDirectorySafe(retrievalFolder);
	}
	
	@Test
	public void test() {
		System.out.println("TEST");
	}
}
