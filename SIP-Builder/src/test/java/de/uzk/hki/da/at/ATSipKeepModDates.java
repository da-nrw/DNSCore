package de.uzk.hki.da.at;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.pkg.ArchiveBuilder;
import de.uzk.hki.da.pkg.ArchiveBuilderFactory;
import de.uzk.hki.da.utils.FolderUtils;


public class ATSipKeepModDates {
	private static String sip = "KeepModDates";
	
	private static File targetDir = new File("target/atTargetDir/");
	private static File workDir = new File("target/atTargetDirWorking/");
	
	private static String sourceName = "src/test/resources/at/KeepModDates";
	private static File sourceDir = new File(sourceName);
	
	private static Process p;
	
	@Before
	public void setUp() throws IOException{	
		FolderUtils.deleteDirectorySafe(targetDir);
		FolderUtils.deleteDirectorySafe(workDir);
		FolderUtils.deleteDirectorySafe(sourceDir);
	}
	
	@After
	public void tearDown() throws IOException{
		FolderUtils.deleteQuietlySafe(new File("target/atTargetDir/"+sip));
		FolderUtils.deleteDirectorySafe(targetDir);
		FolderUtils.deleteDirectorySafe(workDir);
		FolderUtils.deleteDirectorySafe(sourceDir);
		p.destroy();
	}

	@Test
	public void testTar() throws Exception {
		doTest(false);
	}

	@Test
	public void testTgz() throws Exception {
		doTest(true);
	}

	public void doTest(boolean withCompression) throws Exception {
		File tarArchive = new File(sourceName+".tgz");
	    ArchiveBuilder builder = ArchiveBuilderFactory.getArchiveBuilderForFile(tarArchive); 
		builder.unarchiveFolder(tarArchive, sourceDir);

		File s1;
		if (withCompression){
			s1 = new File("target/atTargetDir/" +sip + ".tgz");
		}else{
			s1 = new File("target/atTargetDir/" +sip + ".tar");
		}
			
		File unpackedSip = new File("target/atTargetDir/"+ sip);

		String cmd = "./SipBuilder-Unix.sh -source=\""+sourceDir.getAbsolutePath()
				+"/\" -destination=\""+targetDir.getAbsolutePath()
				+"/\" -workspace=\""
				+workDir.getAbsolutePath()
				+"/\" -single";
		if (!withCompression){
			cmd += " -noCompression";
		}
		
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
	    
	    assertTrue(s1.exists());
	    
	    assertFalse(new File(workDir +sip).exists());
	    
//	    Tests content of the first SIP
	    builder = ArchiveBuilderFactory.getArchiveBuilderForFile(s1); 
	    
	    try {
			builder.unarchiveFolder(s1, targetDir);
		} catch (Exception e) {
			throw new RuntimeException("couldn't unpack archive", e);
		}

		long moddi;
		Date modDate;
		String tagStr;
		SimpleDateFormat dateForm = new SimpleDateFormat("dd.MM.yyyy");
		dateForm.setTimeZone(TimeZone.getTimeZone("GMT"));

		File file = new File(unpackedSip, "data/unter1/unter2/M00000.jpg");
        System.out.println("File:" + file.getAbsolutePath() + " exists:" + file.exists());
		
		moddi = file.lastModified();
		modDate = new Date(moddi);
		tagStr = dateForm.format(modDate);
		if (!tagStr.equals("21.10.2015")){
	        System.out.println("Unexpected Date: >" + tagStr + "< Expected: >21.10.2015<");
		}
		assertTrue(tagStr.equals("21.10.2015"));
		
		moddi = new File(unpackedSip, "data/West_mets.xml").lastModified();
		modDate = new Date(moddi);
		tagStr = dateForm.format(modDate);
		assertTrue(tagStr.equals("23.12.1978"));

		moddi = new File(unpackedSip, "data/unter1/Pest17.bmp").lastModified();
		modDate = new Date(moddi);
		tagStr = dateForm.format(modDate);
		assertTrue(tagStr.equals("25.10.2012"));
	}
}
