package de.uzk.hki.da.at;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ATUseCaseDeleteObjectFromWorkflow extends Base{
	
	@Before
	public void setUp() throws IOException{
		setUpBase();
	}
	
	@After
	public void tearDown(){
		clearDB();
		cleanStorage();
	}
	
	@Test
	public void test() throws InterruptedException, IOException {
		String name = "ATUseCaseDeleteObjectFromWorkflow";
		createObjectAndJob(name,"800","METS","mets.xml");
		waitForJobsToFinish(name, 500);
	}
}
