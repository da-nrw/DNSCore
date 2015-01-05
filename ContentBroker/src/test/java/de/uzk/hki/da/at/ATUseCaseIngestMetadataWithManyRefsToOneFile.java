package de.uzk.hki.da.at;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ATUseCaseIngestMetadataWithManyRefsToOneFile extends AcceptanceTest {

	private static String eadOrigName = "ATUseCaseIngestEadMetsManyRefsToOneFile";
	private static String metsOrigName = "ATUseCaseIngestMetsManyRefsToOneFile";
	private static String lidoOrigName = "ATUseCaseIngestLIDOManyRefsToOneFile";
	private static final File retrievalFolder = new File("/tmp/unpackedDIP");
	
	@BeforeClass
	public static void setUp() throws IOException {
		ath.ingest(eadOrigName);
		ath.ingest(metsOrigName);
		ath.ingest(lidoOrigName);
	}
	
	@AfterClass
	public static void tearDown() throws IOException{
		FileUtils.deleteDirectory(retrievalFolder);
	}
	
	@Test
	public void test() {
		System.out.println("Test");
	}
}
