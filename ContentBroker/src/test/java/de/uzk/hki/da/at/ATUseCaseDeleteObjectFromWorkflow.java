package de.uzk.hki.da.at;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Date;

import org.junit.Test;

import de.uzk.hki.da.model.Object;

public class ATUseCaseDeleteObjectFromWorkflow extends AcceptanceTest{
	
	String origName = "ATDeleteObject";
	String identifier = "ATDeleteObject_identifier";
	
	@Test
	public void test() throws InterruptedException, IOException {
//		ath.createObjectAndJob(origName,"800","METS","mets.xml");
		ath.putAIPToLongTermStorage(identifier, origName, new Date(), Object.ObjectStatus.ArchivedAndValid);
		assertNotNull(ath.getObject(origName));
		ath.createJob(origName, "800");
		
		Thread.sleep(3000);
		assertNull(ath.getObject(origName));
	}
}
