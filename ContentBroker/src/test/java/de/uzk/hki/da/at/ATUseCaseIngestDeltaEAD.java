package de.uzk.hki.da.at;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.path.RelativePath;
import de.uzk.hki.da.repository.RepositoryException;

public class ATUseCaseIngestDeltaEAD extends AcceptanceTest{

	private static final int _1_MINUTE = 60000;
	private static final String ORIG_NAME_ORIG = "ATUseCaseIngestDeltaEAD";
	private Object object;
	
	
//	@Before
//	public void setUp() throws IOException, InterruptedException {
//		FileUtils.copyFileToDirectory(new File("src/test/resources/at/ATUseCaseIngestDeltaEAD_orig/"+ORIG_NAME_ORIG+".tgz"), new File("src/test/resources/at"));
//		object = ath.ingest(ORIG_NAME_ORIG);
//		FileUtils.deleteQuietly(new File("src/test/resources/at/ATUseCaseIngestDeltaEAD.tgz"));
//		
//		Thread.sleep(_1_MINUTE); // to prevent the repnames to match the ones of the previous package
//		FileUtils.copyFileToDirectory(new File("src/test/resources/at/ATUseCaseIngestDeltaEAD_delta/"+ORIG_NAME_ORIG+".tgz"), new File("src/test/resources/at"));
//		object = ath.ingest(ORIG_NAME_ORIG);
//		FileUtils.deleteQuietly(new File("src/test/resources/at/ATUseCaseIngestDeltaEAD.tgz"));
//	}
//	
//	@Test
//	public void test() throws IOException, InterruptedException, RepositoryException{
//		System.out.println("TEST");
//	}
	
	@Test
	public void test() {
		;
	}
}
