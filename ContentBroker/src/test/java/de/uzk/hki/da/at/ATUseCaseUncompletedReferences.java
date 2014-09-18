package de.uzk.hki.da.at;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.test.TESTHelper;
import de.uzk.hki.da.utils.C;

/**
 * 
 * @author Polina Gubaidullina
 *
 */

public class ATUseCaseUncompletedReferences extends Base {
	
	private static final String AT_UncompletedReferences_METS = "ATUseCaseIngestMetsWithUncompletedReferences";
	private static final String AT_UncompletedReferences_EAD = "ATUseCaseIngestEadWithUncompletedReferences";
	private static final String AT_UncompletedReferences_METS_in_EAD = "ATUseCaseIngestEadMetsWithUncompletedReferences";
	private static final String AT_UncompletedReferences_LIDO = "ATUseCaseIngestLidoWithUncompletedReferences";
	private static final String AT_UncompletedReferences_XMP = "ATUseCaseIngestXMPWithUncompletedReferences";
	private static final String YEAH = "yeah!";
	
	@Before
	public void setUpBeforeClass() throws IOException{
		setUpBase();
	}
	
	@After
	public void tearDown(){
		TESTHelper.clearDB();
		cleanStorage();
	}
	
	@Test
	public void testMETS() throws IOException, InterruptedException {
		ath.ingestAndWaitForErrorState(AT_UncompletedReferences_METS, C.STATE_DIGIT_USER_ERROR);
		System.out.println(YEAH);
	}
	
	@Test
	public void testEAD() throws IOException, InterruptedException {
		ath.ingestAndWaitForErrorState(AT_UncompletedReferences_EAD, C.STATE_DIGIT_USER_ERROR);
		System.out.println(YEAH);
	}
	
	@Test
	public void testMetsInEAD() throws IOException, InterruptedException {
		ath.ingestAndWaitForErrorState(AT_UncompletedReferences_METS_in_EAD, C.STATE_DIGIT_USER_ERROR);
		System.out.println(YEAH);
	}
	
	@Test
	public void testLIDO() throws IOException, InterruptedException {
		ath.ingestAndWaitForErrorState(AT_UncompletedReferences_LIDO, C.STATE_DIGIT_USER_ERROR);
		System.out.println(YEAH);
	}
	
	@Test
	public void testXMP() throws IOException, InterruptedException {
		ath.ingestAndWaitForErrorState(AT_UncompletedReferences_XMP, C.STATE_DIGIT_USER_ERROR);
		System.out.println(YEAH);
	}
}
