package de.uzk.hki.da.at;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.C;

/**
 * 
 * @author Polina Gubaidullina
 *
 */
public class ATDetectUncompletedReferences extends AcceptanceTest {
	
	private static final String AT_UncompletedReferences_METS = "ATDetectUncompletedReferencesMets";
	private static final String AT_UncompletedReferences_EAD = "ATDetectUncompletedReferencesEad";
	private static final String AT_UncompletedReferences_METS_in_EAD = "ATDetectUncompletedReferencesEadMets";
	private static final String AT_UncompletedReferences_LIDO = "ATDetectUncompletedReferencesLido";
	private static final String AT_UncompletedReferences_XMP = "ATDetectUncompletedReferencesXMP";
	private static final String YEAH = "yeah!";

	@BeforeClass
	public static void setUp() throws IOException {
		ath.putSIPtoIngestArea(AT_UncompletedReferences_METS, "tgz", AT_UncompletedReferences_METS);
		ath.putSIPtoIngestArea(AT_UncompletedReferences_EAD, "tgz", AT_UncompletedReferences_EAD);
		ath.putSIPtoIngestArea(AT_UncompletedReferences_METS_in_EAD, "tgz", AT_UncompletedReferences_METS_in_EAD);
		ath.putSIPtoIngestArea(AT_UncompletedReferences_LIDO, "tgz", AT_UncompletedReferences_LIDO);
		ath.putSIPtoIngestArea(AT_UncompletedReferences_XMP, "tgz", AT_UncompletedReferences_XMP);
		
	}
	
	@Test
	public void testMETS() throws IOException, InterruptedException {
		ath.waitForJobToBeInErrorStatus(AT_UncompletedReferences_METS, C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
		System.out.println(YEAH);
	}
	
	@Test
	public void testEAD() throws IOException, InterruptedException {
		ath.waitForJobToBeInErrorStatus(AT_UncompletedReferences_EAD, C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
		System.out.println(YEAH);
	}
	
	@Test
	public void testMetsInEAD() throws IOException, InterruptedException {
		ath.waitForJobToBeInErrorStatus(AT_UncompletedReferences_METS_in_EAD, C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
		System.out.println(YEAH);
	}
	
	@Test
	public void testLIDO() throws IOException, InterruptedException {
		ath.waitForJobToBeInErrorStatus(AT_UncompletedReferences_LIDO, C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
		System.out.println(YEAH);
	}
	
	@Test
	public void testXMP() throws IOException, InterruptedException {
		ath.waitForJobToBeInErrorStatus(AT_UncompletedReferences_XMP, C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
		System.out.println(YEAH);
	}
}
