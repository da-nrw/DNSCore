package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ATSipBuilderCliNestedLAV {
	
	private static final String sip1 = "urn+nbn+de+danrw+de2189-48c69c71-b98e-4229-a1c1-69a5930d44103.tgz";
	private static final String sip2 = "urn+nbn+de+danrw+de2189-89532c28-d082-4c38-8783-21b9019225988.tgz";
	private static final String sip3 = "urn+nbn+de+danrw+de2189-0c6ab310-f2f6-4f66-80e2-a138bd4db6938.tgz";

	private static File targetDir = new File("target/atTargetDir/");
	private static File sourceDir = new File("src/test/resources/at/ATSipBuilderCliNestedLAV");
	private static Process p;
	
	@Before
	public void setUp() throws IOException{	
		FileUtils.deleteDirectory(new File("target/atTargetDir/"));
	}
	
	@After
	public void tearDown() throws IOException{
		FileUtils.deleteQuietly(new File("target/atTargetDir/"+sip1));
		FileUtils.deleteQuietly(new File("target/atTargetDir/"+sip2));
		FileUtils.deleteQuietly(new File("target/atTargetDir/"+sip3));
		FileUtils.deleteDirectory(targetDir);
		p.destroy();
	}
	
	@Test
	public void testLAVData() throws IOException {
		
		String cmd = "./SipBuilder-Unix.sh -source=\""+sourceDir.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -nested -alwaysOverwrite";
		
		p=Runtime.getRuntime().exec(cmd,
		        null, new File("target/installation"));
		
		BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
        InputStreamReader(p.getErrorStream()));
		 
		String s = "";
		// read the output from the command
	    System.out.println("Here is the standard output of the command:\n");
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	    }

	    // read any errors from the attempted command
	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertTrue(new File("target/atTargetDir/"+sip1).exists());
	    assertTrue(new File("target/atTargetDir/"+sip2).exists());
	    assertTrue(new File("target/atTargetDir/"+sip3).exists());
	}
}
