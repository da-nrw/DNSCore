package de.uzk.hki.da.at;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.test.TESTHelper;

public class ATUseCaseDeleteObjectFromWorkflow extends Base{
	
	@Before
	public void setUp() throws IOException{
		setUpBase();
	}
	
	@After
	public void tearDown(){
		TESTHelper.clearDB();
		cleanStorage();
	}
	
	@Test
	public void test() throws InterruptedException, IOException {
		String name = "ATUseCaseDeleteObjectFromWorkflow";
		ath.createObjectAndJob(name,"800","METS","mets.xml");
		ath.waitForJobsToFinish(name, 20000);
	}
}
