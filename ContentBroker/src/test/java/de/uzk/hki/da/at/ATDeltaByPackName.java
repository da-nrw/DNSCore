package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.C;

public class ATDeltaByPackName extends AcceptanceTest {

	private static final String ORIG_NAME = "ATUseCaseIngest1"; 
	private static final int COUNT=11;

	private String idiName;

	@Before
	public void setUp() throws IOException {
	}

	@Test
	public void test() throws IOException {

		Object obbi ;
		for (int i=1;i<=COUNT;i++){
			System.out.println("PutSip: "+i);
			ath.putSIPtoIngestArea(ORIG_NAME, C.FILE_EXTENSION_TGZ, ORIG_NAME);
			ath.awaitObjectState(ORIG_NAME, Object.ObjectStatus.InWorkflow);
			ath.awaitObjectState(ORIG_NAME, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
			obbi= ath.getObject(ORIG_NAME);
			System.out.println(obbi.getPackages().size()+" "+obbi.getLatestPackage());
			System.out.println(Arrays.toString(obbi.getPackages().toArray()));
		}
		
		obbi= ath.getObject(ORIG_NAME);
		idiName = obbi.getIdentifier(); 
		
		assertTrue(obbi.getPackages().size() == COUNT);
		assertTrue(obbi.getLatestPackage().getName().equals(""+COUNT));

		TreeMap<String, de.uzk.hki.da.model.Package> packNames = new TreeMap<String, de.uzk.hki.da.model.Package>();
	
		for (de.uzk.hki.da.model.Package pack : obbi.getPackages()){
			packNames.put(pack.getName(), pack);
		}
		
		for (int nnn=1; nnn<COUNT; nnn++){
			String packName = Integer.toString(nnn);
			de.uzk.hki.da.model.Package pack = packNames.get(packName);
			assertTrue(pack != null);
		}
	}

	@After
	public void tearDown() {
		distributedConversionAdapter.remove("aip/"+testContractor.getUsername()+"/" + idiName);
	}
}
