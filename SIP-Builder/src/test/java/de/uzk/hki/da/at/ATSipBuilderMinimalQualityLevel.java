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

import de.uzk.hki.da.utils.FolderUtils;

public class ATSipBuilderMinimalQualityLevel {
	
	private static File targetDir = new File("target/atTargetDir/");
	private static File sourceDir = new File("src/test/resources/at/");
	private static String sipMinimalQuality = "ATMinimalQualityLevel";
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
	public void testBuildSipWithMinimalQL() throws IOException {
		File source = new File(sourceDir, sipMinimalQuality);
		//standardRightsMinQualityLevel.xml
		String cmd = "./SipBuilder-Unix.sh -rights=\""+sourceDir.getAbsolutePath()+"/standardRightsMinQualityLevel.xml"+"\" -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite ";
		
		p=Runtime.getRuntime().exec(cmd,
		        null, new File("target/installation"));
		
		BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
        InputStreamReader(p.getErrorStream()));
		 
		String s = "";
		// read the output from the command
	    System.out.println("Here is the standard output of the command:\n");
	    boolean successful=false;
	    boolean rightMinQuality=false;
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	         if(s.contains("Take over minimal ingest quality: 3"))
	        	 rightMinQuality=true;
	         if(s.contains("Die SIP-Erstellung wurde erfolgreich abgeschlossen."))
	    	   successful=true;
	    }
	    System.out.println("successful: "+successful+" rightMinQualityLevel: "+rightMinQuality);
	    assertTrue(rightMinQuality && successful);
	    
	    // read any errors from the attempted command
	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    System.out.println("End\n");
	    assertTrue(new File("target/atTargetDir/"+sipMinimalQuality+".tgz").exists());
	}
	
}
