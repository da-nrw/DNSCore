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

public class ATSipBuilderCliEad {
	private static File targetDir = new File("target/atTargetDir/");
	private static File sourceDir = new File("src/test/resources/at/");
	private static String singleSip = "ATBuildSingleEadSip.tgz";
	private static String singleSipError = "ATBuildSingleEadSipWrongRefError.tgz";
	private static Process p;
	
	@Before
	public void setUp() throws IOException{	
		FileUtils.deleteDirectory(targetDir);
	}
	
	@After
	public void tearDown() throws IOException{
		FileUtils.deleteDirectory(targetDir);
		p.destroy();
	}
	
	@Test
	public void testBuildSingleSipCorrectReferences() throws IOException {
		
		File source = new File(sourceDir, "ATBuildSingleEadSip");
		
		String cmd = "./SipBuilder-Unix.sh -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite";
		
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
	    
	    assertTrue(new File("target/atTargetDir/"+singleSip).exists());
	}

	@Test
	public void testBuildSingleSipErrorWrongReferences() throws IOException {
		
		File source = new File(sourceDir, "ATBuildSingleEadSipWrongRefErrorCase/ATBuildSingleEadSipWrongRefError");
		
		String cmd = "./SipBuilder-Unix.sh -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite";
	
		p=Runtime.getRuntime().exec(cmd,
		        null, new File("target/installation"));
		
		BufferedReader stdInput = new BufferedReader(new
	    InputStreamReader(p.getInputStream()));
	
		BufferedReader stdError = new BufferedReader(new
	    InputStreamReader(p.getErrorStream()));
		 
		boolean falseReferencesInFileMsg = false;
		boolean fileListMsg = false;
		boolean metsFile45Missed = false;
		boolean identifiedMetadataType = false;
		boolean noSipCreated = false;
		String s = "";
		// read the output from the command
	    System.out.println("Here is the standard output of the command:\n");
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	         if(s.contains("Identified metadata file") && s.contains("DNSCore/SIP-Builder/src/test/resources/at/ATBuildSingleEadSipWrongRefErrorCase/ATBuildSingleEadSipWrongRefError/EAD_Export.XML=EAD}")) {
	        	 identifiedMetadataType = true;
	         }
	         if(s.contains("EAD_Export.XML enthält falsche Referenzen")) {
	        	 falseReferencesInFileMsg = true;
	         }
	         if(s.contains("Folgende Dateien konnten nicht gefunden werden")) {
	        	 fileListMsg = true;
	         }
	         if(s.contains("[../mets_2_32045.xml]")); {
	        	 metsFile45Missed = true;
	         }
	         if(s.contains("Aus dem Verzeichnis") &&
	        		 s.contains("DNSCore/SIP-Builder/src/test/resources/at/ATBuildSingleEadSipWrongRefErrorCase/ATBuildSingleEadSipWrongRefError "
	         		+ "wird kein SIP erstellt.")) {
	        	 noSipCreated = true;
	         }
	    }
	    
	    assertTrue(identifiedMetadataType);
	    assertTrue(falseReferencesInFileMsg);
	    assertTrue(fileListMsg);
	    assertTrue(metsFile45Missed);
	    assertTrue(noSipCreated);
	
	    // read any errors from the attempted command
	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertFalse(new File("target/atTargetDir/"+singleSipError).exists());
		}
}
