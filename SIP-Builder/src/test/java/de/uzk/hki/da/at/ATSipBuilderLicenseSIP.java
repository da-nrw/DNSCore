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

public class ATSipBuilderLicenseSIP {
	
	private static File targetDir = new File("target/atTargetDir/");
	private static File sourceDir = new File("src/test/resources/at/");
	private static String sipMetsLicense = "ATLicenseInMets";
	private static String sipNoMetsLicense = "ATNoLicenseInMets";
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
	public void testContainsMetsLicense() throws IOException {
		File source = new File(sourceDir, sipMetsLicense);
		
		String cmd = "./SipBuilder-Unix.sh -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite ";
		
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
	    boolean rightLicense=false;
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	         if(s.contains("License is satisfiable: Premis-License:false Mets-License:true Lido-License:false Publication-Decision:true"))
	        	 rightLicense=true;
	         if(s.contains("Die SIP-Erstellung wurde erfolgreich abgeschlossen."))
	    	   successful=true;
	    }
	    System.out.println("successful: "+successful+" rightLicense: "+rightLicense);
	    assertTrue(rightLicense && successful);
	    
	    // read any errors from the attempted command
	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    System.out.println("End\n");
	    assertTrue(new File("target/atTargetDir/"+sipMetsLicense+".tgz").exists());
	}
	
	
	@Test
	public void testNoLicense() throws IOException {
		File source = new File(sourceDir, sipNoMetsLicense);
		
		String cmd = "./SipBuilder-Unix.sh -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite ";
		
		p=Runtime.getRuntime().exec(cmd,
		        null, new File("target/installation"));
		
		BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
        InputStreamReader(p.getErrorStream()));
		 
		String s = "";
		// read the output from the command
	    System.out.println("Here is the standard output of the command:\n");
	    boolean successfulMSG=false;
	    boolean rightLicense=false;
	    //boolean errMsg=false;
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	         if(s.contains("Die Lizenzangaben sind nicht vorhanden: Um publizieren zu können, muss eine gültige Lizenz angegeben werden."))
	        	 rightLicense=true;
	         if(s.contains("SIP-Erstellungsvorgang abgebrochen"))
	    	   successfulMSG=true;
	    }
	    System.out.println("successful: "+successfulMSG+" rightLicense: "+rightLicense);
	  assertTrue(rightLicense && successfulMSG);

	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertFalse(new File("target/atTargetDir/"+sipNoMetsLicense+".tgz").exists());
	}
	
	
	
	

	@Test
	public void testLicenseByContract() throws IOException {

		File source = new File(sourceDir, sipNoMetsLicense);
		
		String cmd = "./SipBuilder-Unix.sh -rights=\""+ATWorkingDirectory.CONTRACT_RIGHT_LICENSED.getAbsolutePath()+"\" -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite ";
		
		p=Runtime.getRuntime().exec(cmd,
		        null, new File("target/installation"));
		
		BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
        InputStreamReader(p.getErrorStream()));
		 
		String s = "";
		// read the output from the command
	    System.out.println("Here is the standard output of the command:\n");
	    boolean successfulMSG=false;
	    boolean rightLicense=false;
	    //boolean errMsg=false;
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	         if(s.contains("License is satisfiable: Premis-License:true Mets-License:false Lido-License:false Publication-Decision:true"))
	        	 rightLicense=true;
	         if(s.contains("Die SIP-Erstellung wurde erfolgreich abgeschlossen."))
	    	   successfulMSG=true;
	    }
	    System.out.println("successful: "+successfulMSG+" rightLicense: "+rightLicense);
	    assertTrue(rightLicense && successfulMSG);

	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertTrue(new File("target/atTargetDir/"+sipNoMetsLicense+".tgz").exists());
	}
	
	
	
	@Test
	public void testLicenseByContractAndMets() throws IOException {

		File source = new File(sourceDir, sipMetsLicense);
		
		String cmd = "./SipBuilder-Unix.sh -rights=\""+ATWorkingDirectory.CONTRACT_RIGHT_LICENSED.getAbsolutePath()+"\" -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite ";
		
		p=Runtime.getRuntime().exec(cmd,
		        null, new File("target/installation"));
		
		BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
        InputStreamReader(p.getErrorStream()));
		 
		String s = "";
		// read the output from the command
	    System.out.println("Here is the standard output of the command:\n");
	    boolean successfulMSG=false;
	    boolean rightLicense=false;
	    //boolean errMsg=false;
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	         if(s.contains("Die Lizenzangaben sind nicht eindeutig: Lizenangaben dürfen nicht gleichzeitig im SIP-Builder und in den Metadaten angegeben werden."))
	        	 rightLicense=true;
	         if(s.contains("SIP-Erstellungsvorgang abgebrochen"))
	    	   successfulMSG=true;
	    }
	    System.out.println("successful: "+successfulMSG+" rightLicense: "+rightLicense);
	    assertTrue(rightLicense && successfulMSG);

	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertFalse(new File("target/atTargetDir/"+sipNoMetsLicense+".tgz").exists());
	}
	
	
	
	@Test
	public void testNoLicenseNoPublicationByContract() throws IOException {
		

		File source = new File(sourceDir, sipNoMetsLicense);
		
		String cmd = "./SipBuilder-Unix.sh -rights=\""+sourceDir.getAbsolutePath()+"/contractRightsNoLicenseNoPublication.xml"+"\" -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite ";
		
		p=Runtime.getRuntime().exec(cmd,
		        null, new File("target/installation"));
		
		BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
        InputStreamReader(p.getErrorStream()));
		 
		String s = "";
		// read the output from the command
	    System.out.println("Here is the standard output of the command:\n");
	    boolean successfulMSG=false;
	    boolean rightLicense=false;
	    //boolean errMsg=false;
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	         if(s.contains("License is satisfiable: Premis-License:false Mets-License:false Lido-License:false Publication-Decision:false"))
	        	 rightLicense=true;
	         if(s.contains("Die SIP-Erstellung wurde erfolgreich abgeschlossen."))
	    	   successfulMSG=true;
	    }
	    System.out.println("successful: "+successfulMSG+" rightLicense: "+rightLicense);
	    assertTrue(rightLicense && successfulMSG);


	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertTrue(new File("target/atTargetDir/"+sipNoMetsLicense+".tgz").exists());
	}
	
	
	@Test
	public void testNoLicensePrivatePublicationByContract() throws IOException {
		

		File source = new File(sourceDir, sipNoMetsLicense);
		
		String cmd = "./SipBuilder-Unix.sh -rights=\""+sourceDir.getAbsolutePath()+"/contractRightsNoLicensePrivatePublication.xml"+"\" -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite ";
		
		p=Runtime.getRuntime().exec(cmd,
		        null, new File("target/installation"));
		
		BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
        InputStreamReader(p.getErrorStream()));
		 
		String s = "";
		// read the output from the command
	    System.out.println("Here is the standard output of the command:\n");
	    boolean successfulMSG=false;
	    boolean rightLicense=false;
	    //boolean errMsg=false;
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	         if(s.contains("License is satisfiable: Premis-License:false Mets-License:false Lido-License:false Publication-Decision:false"))
	        	 rightLicense=true;
	         if(s.contains("Die SIP-Erstellung wurde erfolgreich abgeschlossen."))
	    	   successfulMSG=true;
	    }
	    System.out.println("successful: "+successfulMSG+" rightLicense: "+rightLicense);
	    assertTrue(rightLicense && successfulMSG);


	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertTrue(new File("target/atTargetDir/"+sipNoMetsLicense+".tgz").exists());
	}
	
}
