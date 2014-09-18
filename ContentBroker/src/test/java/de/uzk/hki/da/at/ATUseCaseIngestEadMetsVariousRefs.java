package de.uzk.hki.da.at;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.path.Path;
import de.uzk.hki.da.utils.C;

public class ATUseCaseIngestEadMetsVariousRefs extends AcceptanceTest{
	private static final String origName = "ATUseCaseIngestEadMetsVariousRefs";
	
	@Before
	public void setUp() throws IOException{
		ath.ingest(origName);
	}
	
	@Test
	public void test() {
		Path.make(localNode.getWorkAreaRootPath(),C.WA_PIPS, C.WA_PUBLIC, C.TEST_USER_SHORT_NAME);
	}
}
