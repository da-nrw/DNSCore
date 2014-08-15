package de.uzk.hki.da.at;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.utils.TESTHelper;

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
		createObjectAndJob(name,"800","METS","mets.xml");
		waitForJobsToFinish(name, 500);
	}
}
