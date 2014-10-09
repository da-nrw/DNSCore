package de.uzk.hki.da.at;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.repository.RepositoryException;

/**
 * 
 * @author Polina Gubaidullina
 *
 */

public class ATUseCaseIngestDeltaEAD extends AcceptanceTest{

	private static final int _1_MINUTE = 60000;
	private static final String ORIG_NAME_ORIG = "ATUseCaseIngestDeltaEAD";
	
	
	@Before
	public void setUp() throws IOException, InterruptedException {
		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+ORIG_NAME_ORIG+"_orig/"+ORIG_NAME_ORIG+".tgz"), new File("src/test/resources/at"));
		ath.ingest(ORIG_NAME_ORIG);
		FileUtils.deleteQuietly(new File("src/test/resources/at/"+ORIG_NAME_ORIG+".tgz"));
		
		Thread.sleep(_1_MINUTE); // to prevent the repnames to match the ones of the previous package
		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+ORIG_NAME_ORIG+"_delta/"+ORIG_NAME_ORIG+".tgz"), new File("src/test/resources/at"));
		ath.ingest(ORIG_NAME_ORIG);
		FileUtils.deleteQuietly(new File("src/test/resources/at/"+ORIG_NAME_ORIG+".tgz"));
	}

	@Test
	public void test() throws IOException, InterruptedException, RepositoryException{
		System.out.println("TEST");
	}
}
