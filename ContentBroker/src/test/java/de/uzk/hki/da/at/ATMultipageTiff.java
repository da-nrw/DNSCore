package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;

public class ATMultipageTiff extends AcceptanceTest {
	private static final String ORIG_NAME = "ATMultipageTiff";

	@Test
	public void test() throws Exception{
		ath.putSIPtoIngestArea(ORIG_NAME, C.FILE_EXTENSION_TGZ, ORIG_NAME);
		ath.awaitObjectState(ORIG_NAME,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		Object obbi = ath.getObject(ORIG_NAME);
		String idiName = obbi.getIdentifier(); 
		
		String hash = DigestUtils.md5Hex("Best/A1_456789_A2_456789_A3_456789_A4_456789_A5_456789_A6_456789_A7_456789_A8_456789_.jpg");
		File file = Path.makeFile(localNode.getWorkAreaRootPath(),"pips","public",
				C.TEST_USER_SHORT_NAME, idiName, '_' + hash + ".jpg");

		assertTrue(file.exists());
	}
}
