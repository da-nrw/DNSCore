package de.uzk.hki.da.at;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.utils.FolderUtils;

public class ATDuplicateFileNames {
	private static File targetDir = new File("target/atTargetDir/");
	private static File sourceDir = new File("src/test/resources/at/");
	private static String nestedSip1 = "urn+nbn+de+hbz+42.tgz";
	private static String nestedSip2 = "urn+nbn+de+hbz+6+1-3602.tgz";
	private static Process p;
	
	@Before
	public void setUp() throws IOException{	
		FolderUtils.deleteDirectorySafe(targetDir);
	}
	
	@After
	public void tearDown() throws IOException{
		FolderUtils.deleteDirectorySafe(targetDir);
		p.destroy();
	}
	
	@Test
	public void test() throws IOException {
		
		File source = new File(sourceDir, "ATDuplicateFileNames");
		
		String cmd = "./SipBuilder-Unix.sh -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -nested -alwaysOverwrite -alwaysIgnoreWrongReferencesInMetadata";
		
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
	    
	    assertFalse(new File("target/atTargetDir/"+nestedSip1).exists());
	    assertTrue(new File("target/atTargetDir/"+nestedSip2).exists());
	}
}
