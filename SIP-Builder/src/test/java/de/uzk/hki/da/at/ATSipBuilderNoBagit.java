package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.utils.FolderUtils;

/**
 * 
 * @author Gaby Bender
 *
 */
public class ATSipBuilderNoBagit {
	
	private static File targetDir = new File("target/atTargetDir/");
	private static File sourceDir = new File("src/test/resources/at/");
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
	
	/**
	 * the sourcefolder contains only one subfolder
	 * @throws IOException
	 */
	@Test
	public void test() throws IOException {

		File source = new File(sourceDir, "ATSipBuilderNoBagit");
		
 		String cmd = "./SipBuilder-Unix.sh -source=\""+source.getAbsolutePath()+ "/\" -destination=\""+
 						targetDir.getAbsolutePath()+"/\" -noTar" +  " -noBagit";
 		
 		p=Runtime.getRuntime().exec(cmd, null, new File("target/installation"));

 		System.out.println("cmd = " + cmd);
 		
 		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

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
	    
	    File[] directorys = new File(targetDir.getAbsoluteFile() + File.separator + "data").listFiles();
	    File noBagitFolder = null;
		if ( directorys.length > 1) {
			System.out.println("ERROR: More than one subfolder found!!!");
		} else {
	    	 noBagitFolder = new File(targetDir.getAbsolutePath() + File.separator + "data" + File.separator +  directorys[0].getName());
		}
	    
		assertTrue(new File(noBagitFolder + File.separator + "data/test1.jpg").exists());
		assertTrue(new File(noBagitFolder + File.separator + "data/test2.jpg").exists());
		assertTrue(new File(noBagitFolder + File.separator + "data/test3.jpg").exists());
		assertTrue(new File(noBagitFolder + File.separator + "data/premis.xml").exists());
			
	}
	
}
