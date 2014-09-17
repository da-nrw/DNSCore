package de.uzk.hki.da.at;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.path.Path;
import de.uzk.hki.da.test.TESTHelper;

public class ATUseCaseIngestEadMetsVariousRefs extends Base{
	private static final String origName = "ATUseCaseIngestEadMetsVariousRefs";
	private static Object object;
	private static Path contractorsPipsPublic;
	
	@BeforeClass
	public static void setUp() throws IOException{
		setUpBase();
		object = ath.ingest(origName);
	}
	
	@AfterClass
	public static void tearDown(){
		TESTHelper.clearDB();
		cleanStorage();
	}
	
	@Test
	public void test() {
		contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),C.WA_PIPS, C.WA_PUBLIC, C.TEST_USER_SHORT_NAME);
	}
}
