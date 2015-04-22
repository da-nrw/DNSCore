package de.uzk.hki.da.at;

import java.io.IOException;

import org.junit.Test;

import de.uzk.hki.da.model.Object;

public class ATUseCaseDeleteObjectFromWorkflow extends AcceptanceTest{
	
	@Test
	public void test() throws InterruptedException, IOException {
		String name = "ATUseCaseDeleteObjectFromWorkflow";
		ath.createObjectAndJob(name,"800","METS","mets.xml");
		ath.awaitObjectState(name,Object.ObjectStatus.ArchivedAndValid);
	}
}
