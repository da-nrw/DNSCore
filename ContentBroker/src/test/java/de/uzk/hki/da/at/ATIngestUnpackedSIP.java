package de.uzk.hki.da.at;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.Object;

public class ATIngestUnpackedSIP extends AcceptanceTest {

	private static final String ORIG_NAME = "ATIngestUnpackedSIP";
	@BeforeClass
	public static void setUp() throws IOException, InterruptedException {
		
		
	}
	@Test
	public void test() throws IOException {
		ath.putSIPtoIngestArea(ORIG_NAME, null, ORIG_NAME);
		ath.awaitObjectState(ORIG_NAME,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		
	}	
	
}
