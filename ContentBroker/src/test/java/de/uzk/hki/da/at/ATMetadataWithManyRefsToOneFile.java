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
		ath.putSIPtoIngestArea(eadOrigName, "tgz", eadOrigName);
		ath.putSIPtoIngestArea(metsOrigName, "tgz", metsOrigName);
		ath.putSIPtoIngestArea(lidoOrigName, "tgz", lidoOrigName);
	}
	
	@Test
	public void test() {
		ath.awaitObjectState(eadOrigName,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(eadOrigName);
		ath.awaitObjectState(metsOrigName,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(metsOrigName);
		ath.awaitObjectState(lidoOrigName,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(lidoOrigName);
	}
}
