package de.uzk.hki.da.at;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.Object;

public class ATIngestMultPackagesAndCheckCreatedCopies extends AcceptanceTest{
	
	private static final String origName = "ATMetadataUpdatesLIDO";
	
	@BeforeClass
	public static void setUp() throws IOException, InterruptedException{
		ath.putSIPtoIngestArea(origName, "tgz", origName+"1");
		ath.awaitObjectState(origName+"1",Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(origName+"1");
		
		ath.putSIPtoIngestArea(origName, "tgz", origName+"2");
		ath.awaitObjectState(origName+"2",Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(origName+"2");
		
		ath.putSIPtoIngestArea(origName, "tgz", origName+"3");
		ath.awaitObjectState(origName+"3",Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(origName+"3");
				
	}
	
	@Test
	public void test() {
		System.out.println(ath.getObject(origName+"1"));
		System.out.println(ath.getObject(origName+"2"));
		System.out.println(ath.getObject(origName+"3"));
	}
}
	
