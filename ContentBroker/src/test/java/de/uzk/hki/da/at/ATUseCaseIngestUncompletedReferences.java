package de.uzk.hki.da.at;

import java.io.IOException;

import org.junit.Test;

import de.uzk.hki.da.core.C;

/**
 * 
 * @author Polina Gubaidullina
 *
 */
public class ATUseCaseIngestUncompletedReferences extends AcceptanceTest {
	
	private static final String AT_UncompletedReferences_METS = "ATUseCaseIngestMetsWithUncompletedReferences";
	private static final String AT_UncompletedReferences_EAD = "ATUseCaseIngestEadWithUncompletedReferences";
	private static final String AT_UncompletedReferences_METS_in_EAD = "ATUseCaseIngestEadMetsWithUncompletedReferences";
	private static final String AT_UncompletedReferences_LIDO = "ATUseCaseIngestLidoWithUncompletedReferences";
	private static final String AT_UncompletedReferences_XMP = "ATUseCaseIngestXMPWithUncompletedReferences";
	private static final String YEAH = "yeah!";
	
	@Test
	public void testMETS() throws IOException, InterruptedException {
		ath.ingestAndWaitForErrorState(AT_UncompletedReferences_METS, C.WORKFLOW_STATE_DIGIT_USER_ERROR);
		System.out.println(YEAH);
	}
	
	@Test
	public void testEAD() throws IOException, InterruptedException {
		ath.ingestAndWaitForErrorState(AT_UncompletedReferences_EAD, C.WORKFLOW_STATE_DIGIT_USER_ERROR);
		System.out.println(YEAH);
	}
	
	@Test
	public void testMetsInEAD() throws IOException, InterruptedException {
		ath.ingestAndWaitForErrorState(AT_UncompletedReferences_METS_in_EAD, C.WORKFLOW_STATE_DIGIT_USER_ERROR);
		System.out.println(YEAH);
	}
	
	@Test
	public void testLIDO() throws IOException, InterruptedException {
		ath.ingestAndWaitForErrorState(AT_UncompletedReferences_LIDO, C.WORKFLOW_STATE_DIGIT_USER_ERROR);
		System.out.println(YEAH);
	}
	
	@Test
	public void testXMP() throws IOException, InterruptedException {
		ath.ingestAndWaitForErrorState(AT_UncompletedReferences_XMP, C.WORKFLOW_STATE_DIGIT_USER_ERROR);
		System.out.println(YEAH);
	}
}
