package de.uzk.hki.da.at;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.path.Path;
import de.uzk.hki.da.test.TESTHelper;
import de.uzk.hki.da.utils.C;

/**
 * 
 * @author Polina Gubaidullina
 *
 */

public class ATUseCaseIngestEadMetsVariousRefs extends Base{
	private static final String origName = "ATUseCaseIngestEadMetsVariousRefs";
	@BeforeClass
	public static void setUp() throws IOException{
		setUpBase();
		ath.ingest(origName);
	}
	
	@AfterClass
	public static void tearDown(){
		TESTHelper.clearDB();
		cleanStorage();
	}
	
	@Test
	public void test() {
		Path.make(localNode.getWorkAreaRootPath(),C.WA_PIPS, C.WA_PUBLIC, C.TEST_USER_SHORT_NAME);
	}
}
