package de.uzk.hki.da.at;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ATTwoMetadataFiles {
	
	private static File targetDir = new File("target/atTargetDir/");
	private static File sourceDir = new File("src/test/resources/at/");
	private static String sip = "ATTwoMetadataFiles.tgz";
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
		
		boolean manyMetadataFiles = false;
		boolean existingMetsFiles = false;
		
		File source = new File(sourceDir, "ATTwoMetadataFiles");
		
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
	         if(s.contains("Im Verzeichnis ATTwoMetadataFiles wurde mehr als eine Metadatendatei gefunden.")) {
	        	 manyMetadataFiles = true;
	         }
	         if(s.startsWith("[") && s.contains("export_mets1.xml") && s.contains("export_mets2.xml") && s.endsWith("]")) {
	        	 existingMetsFiles = true;
	         }
	    }

	    assertTrue(manyMetadataFiles && existingMetsFiles);
	    
	    // read any errors from the attempted command
	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertFalse(new File("target/atTargetDir/"+sip).exists());
	}
}
