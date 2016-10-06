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

import de.uzk.hki.da.utils.FolderUtils;

/**
 * 
 * @author Polina Gubaidullina
 *
 */

public class ATSipBuilderCliLido {
	
	private static File targetDir = new File("target/atTargetDir/");
	private static File sourceDir = new File("src/test/resources/at/");
	private static String singleSip = "ATBuildSingleLidoSip.tgz";
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
	public void testBuildSingleSipCorrectReferences() throws IOException {
		
		File source = new File(sourceDir, "ATBuildSingleLidoSip");
		
		String cmd = "./SipBuilder-Unix.sh -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite";
		
		p=Runtime.getRuntime().exec(cmd,
		        null, new File("target/installation"));
		
		BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
        InputStreamReader(p.getErrorStream()));
		 
		boolean identifiedMetadataType = false;
		String s = "";
		// read the output from the command
	    System.out.println("Here is the standard output of the command:\n");
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	         if(s.contains("Identified metadata file") && s.contains("DNSCore/SIP-Builder/src/test/resources/at/ATBuildSingleLidoSip/LIDO-Testexport2014-07-04-FML-Auswahl.xml=LIDO}")) {
	        	 identifiedMetadataType = true;
	         }
	    }
	    
	    // read any errors from the attempted command
	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertTrue(new File("target/atTargetDir/"+singleSip).exists());
	    assertTrue(identifiedMetadataType);
	}
}
