package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.C;

public class ATDeltaOnURN extends AcceptanceTest {

	private static final String ORIG_NAME_1 = "ATDeltaOnURN_1";
	private static final String ORIG_NAME_2 = "ATDeltaOnURN_2";
	private static final String ORIG_NAME_3 = "ATDeltaOnURN_3";

	private static final String ALIEN_NAME = "ATKeepModDates";

	private String idiName;

	@Before
	public void setUp() throws IOException {
	}

	@Test
	public void test() throws IOException {
		ath.putSIPtoIngestArea(ORIG_NAME_1, C.FILE_EXTENSION_TGZ, ORIG_NAME_1);
 		ath.awaitObjectState(ORIG_NAME_1, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);

		ath.putSIPtoIngestArea(ORIG_NAME_2, C.FILE_EXTENSION_TGZ, ORIG_NAME_2);
		ath.awaitObjectState(ORIG_NAME_1, Object.ObjectStatus.InWorkflow);
		ath.awaitObjectState(ORIG_NAME_1, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);

		ath.putSIPtoIngestArea(ALIEN_NAME, C.FILE_EXTENSION_TGZ, ALIEN_NAME);
		ath.awaitObjectState(ALIEN_NAME, Object.ObjectStatus.InWorkflow);
		ath.awaitObjectState(ALIEN_NAME, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		
		ath.putSIPtoIngestArea(ORIG_NAME_3, C.FILE_EXTENSION_TGZ, ORIG_NAME_3);
		ath.awaitObjectState(ORIG_NAME_1, Object.ObjectStatus.InWorkflow);
		ath.awaitObjectState(ORIG_NAME_1, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		
		Object obbi = ath.getObject(ORIG_NAME_1);
		idiName = obbi.getIdentifier(); 
		
		assertTrue(obbi.getPackages().size() == 3);

		TreeMap<String, de.uzk.hki.da.model.Package> packNames = new TreeMap<String, de.uzk.hki.da.model.Package>();
	
		for (de.uzk.hki.da.model.Package pack : obbi.getPackages()){
			packNames.put(pack.getName(), pack);
		}
		
		for (int nnn=1; nnn<4; nnn++){
			String packName = Integer.toString(nnn);
			de.uzk.hki.da.model.Package pack = packNames.get(packName);
			assertTrue(pack != null);
			String contName = "ATDeltaOnURN_" + packName + ".tgz"; 
			assertTrue(pack.getContainerName().equals(contName));
		}
	}

	@After
	public void tearDown() {
		distributedConversionAdapter.remove("aip/TEST/" + idiName);
	}

}
