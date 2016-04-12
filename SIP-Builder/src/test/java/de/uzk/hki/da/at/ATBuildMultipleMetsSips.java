package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.pkg.ArchiveBuilder;
import de.uzk.hki.da.pkg.ArchiveBuilderFactory;

public class ATBuildMultipleMetsSips {
	
	private static String sip1 = "ATBuildMultipleMetsSip1.tgz";
	private static String sip2 = "ATBuildMultipleMetsSip2.tgz";
	private static String sip3 = "ATBuildMultipleMetsSip3.tgz";
	
	private File s1 = new File("target/atTargetDir/"+sip1);
	private File unpackedSip1 = new File("target/atTargetDir/"+ FilenameUtils.removeExtension(s1.getName()));
	
	private static File targetDir = new File("target/atTargetDir/");
	private static File sourceDir = new File("src/test/resources/at/ATBuildMultipleMetsSips");
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
		FileUtils.deleteQuietly(new File("target/atTargetDir/"+sip3));
		FileUtils.deleteDirectory(s1);
		FileUtils.deleteDirectory(targetDir);
		p.destroy();
	}
	
	@Test
	public void test() throws IOException {
		
		String cmd = "./SipBuilder-Unix.sh -source=\""+sourceDir.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -multiple";
		
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
	    

//	    Tests content of the first SIP
	    ArchiveBuilder builder = ArchiveBuilderFactory.getArchiveBuilderForFile(s1); 
	    
	    try {
			builder.unarchiveFolder(s1, targetDir);
		} catch (Exception e) {
			throw new RuntimeException("couldn't unpack archive", e);
		}

	    assertTrue(new File(unpackedSip1, "bag-info.txt").exists());
		assertTrue(new File(unpackedSip1, "bagit.txt").exists());
		assertTrue(new File(unpackedSip1, "manifest-md5.txt").exists());
		assertTrue(new File(unpackedSip1, "tagmanifest-md5.txt").exists());
		assertTrue(new File(unpackedSip1, "data/export_mets.xml").exists());
		assertTrue(new File(unpackedSip1, "data/image").exists() && new File(unpackedSip1, "data/image").isDirectory());
		assertTrue(new File(unpackedSip1, "data/premis.xml").exists());
	    
		for(File f : new File (unpackedSip1, "data/image").listFiles()) {
			assertTrue(f.getName().endsWith(".bmp"));
		}
		assertTrue(new File (unpackedSip1, "data/image").listFiles().length==29);
	}
}
