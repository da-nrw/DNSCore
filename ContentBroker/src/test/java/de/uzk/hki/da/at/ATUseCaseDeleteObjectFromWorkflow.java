package de.uzk.hki.da.at;

import java.io.IOException;

import org.junit.Test;

public class ATUseCaseDeleteObjectFromWorkflow extends AcceptanceTest{
	
	@Test
	public void test() throws InterruptedException, IOException {
		String name = "ATUseCaseDeleteObjectFromWorkflow";
		ath.createObjectAndJob(name,"800","METS","mets.xml");
		ath.waitForJobsToFinish(name);
	}
}
