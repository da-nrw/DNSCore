package de.uzk.hki.da.at;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.Object;

public class ATMetadataWithManyRefsToOneFile extends AcceptanceTest {

	private static String eadOrigName = "ATUseCaseIngestEadMetsManyRefsToOneFile";
	private static String metsOrigName = "ATUseCaseIngestMetsManyRefsToOneFile";
	private static String lidoOrigName = "ATUseCaseIngestLIDOManyRefsToOneFile";
	
	@BeforeClass
	public static void setUp() throws IOException {
		ath.putPackageToIngestArea(eadOrigName, "tgz", eadOrigName);
		ath.putPackageToIngestArea(metsOrigName, "tgz", metsOrigName);
		ath.putPackageToIngestArea(lidoOrigName, "tgz", lidoOrigName);
	}
	
	@Test
	public void test() {
		ath.awaitObjectState(eadOrigName,Object.ObjectStatus.ArchivedAndValid);
		ath.awaitObjectState(metsOrigName,Object.ObjectStatus.ArchivedAndValid);
		ath.awaitObjectState(lidoOrigName,Object.ObjectStatus.ArchivedAndValid);
	}
}
