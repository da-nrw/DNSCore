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
	private static String singleSip = "ATBuildSingleLidoSip";
	private static String singleSipLidoLicense = "ATBuildSingleLidoSipLicense";
	private static String singleSipLidoLicenseMultipleAM ="ATLidoSipNoLicenseMultipleEmptyAM";
	private static String singleSipLidoLicenseMultipleAMError ="ATLidoSipNoLicenseMultipleAMError";
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
		
		File source = new File(sourceDir,singleSip);
		
		String cmd = "./SipBuilder-Unix.sh -rights=\""+ATWorkingDirectory.CONTRACT_RIGHT_LICENSED.getAbsolutePath()+"\" -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite";
		
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
	         if(s.contains("Identified metadata file") && s.contains("DNSCore/SIP-Builder/src/test/resources/at/"+singleSip+"/LIDO-Testexport2014-07-04-FML-Auswahl.xml=LIDO}")) {
	        	 identifiedMetadataType = true;
	         }
	    }
	    
	    // read any errors from the attempted command
	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertTrue(new File("target/atTargetDir/"+singleSip+".tgz").exists());
	    assertTrue(identifiedMetadataType);
	}
	
	@Test
	public void testBuildSingleSipLicenseInPremis() throws IOException {
		
		File source = new File(sourceDir, singleSip);
		
		String cmd = "./SipBuilder-Unix.sh -rights=\""+ATWorkingDirectory.CONTRACT_RIGHT_LICENSED.getAbsolutePath()+"\" -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite";
		
		p=Runtime.getRuntime().exec(cmd,
		        null, new File("target/installation"));
		
		BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
        InputStreamReader(p.getErrorStream()));
		 
		boolean identifiedMetadataType = false;
		boolean successfulMSG=false;
	    boolean rightLicense=false;
		String s = "";
		// read the output from the command
	    System.out.println("Here is the standard output of the command:\n");
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	         if(s.contains("Identified metadata file") && s.contains("DNSCore/SIP-Builder/src/test/resources/at/"+singleSip+"/LIDO-Testexport2014-07-04-FML-Auswahl.xml=LIDO}")) {
	        	 identifiedMetadataType = true;
	         }
	         if(s.contains("License is satisfiable: Premis-License:true Mets-License:false Lido-License:false Publication-Decision:true"))
	        	 rightLicense=true;
	         if(s.contains("Die SIP-Erstellung wurde erfolgreich abgeschlossen."))
		    	   successfulMSG=true;
	    }
	    System.out.println("successful: "+successfulMSG+" rightLicense: "+rightLicense);
	    assertTrue(rightLicense && successfulMSG);
	    // read any errors from the attempted command
	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertTrue(new File("target/atTargetDir/"+singleSip+".tgz").exists());
	    assertTrue(identifiedMetadataType);
	}
	
	@Test
	public void testBuildSingleSipLicenseInPremisMultipleAM() throws IOException {
		
		File source = new File(sourceDir, singleSipLidoLicenseMultipleAM);
		
		String cmd = "./SipBuilder-Unix.sh -rights=\""+ATWorkingDirectory.CONTRACT_RIGHT_LICENSED.getAbsolutePath()+"\" -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite";
		
		p=Runtime.getRuntime().exec(cmd,
		        null, new File("target/installation"));
		
		BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
        InputStreamReader(p.getErrorStream()));
		 
		boolean identifiedMetadataType = false;
		boolean successfulMSG=false;
	    boolean rightLicense=false;
		String s = "";
		// read the output from the command
	    System.out.println("Here is the standard output of the command:\n");
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	         if(s.contains("Identified metadata file") && s.contains("DNSCore/SIP-Builder/src/test/resources/at/"+singleSipLidoLicenseMultipleAM+"/LIDO-Testexport2014-07-04-FML-Auswahl.xml=LIDO}")) {
	        	 identifiedMetadataType = true;
	         }
	         if(s.contains("License is satisfiable: Premis-License:true Mets-License:false Lido-License:false Publication-Decision:true"))
	        	 rightLicense=true;
	         if(s.contains("Die SIP-Erstellung wurde erfolgreich abgeschlossen."))
		    	   successfulMSG=true;
	    }
	    System.out.println("successful: "+successfulMSG+" rightLicense: "+rightLicense);
	    assertTrue(rightLicense && successfulMSG);
	    // read any errors from the attempted command
	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertTrue(new File("target/atTargetDir/"+singleSipLidoLicenseMultipleAM+".tgz").exists());
	    assertTrue(identifiedMetadataType);
	}
	
	
	@Test
	public void testBuildSingleSipLicenseInPremisMultipleAMError() throws IOException {
		
		File source = new File(sourceDir, singleSipLidoLicenseMultipleAMError);
		
		String cmd = "./SipBuilder-Unix.sh -rights=\""+ATWorkingDirectory.CONTRACT_RIGHT_LICENSED.getAbsolutePath()+"\" -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite";
		
		p=Runtime.getRuntime().exec(cmd,
		        null, new File("target/installation"));
		
		BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
        InputStreamReader(p.getErrorStream()));
		 
		boolean identifiedMetadataType = false;
		boolean successfulMSG=false;
	    boolean rightLicense=false;
		String s = "";
		// read the output from the command
	    System.out.println("Here is the standard output of the command:\n");
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	         if(s.contains("Identified metadata file") && s.contains("DNSCore/SIP-Builder/src/test/resources/at/"+singleSipLidoLicenseMultipleAMError+"/LIDO-Testexport2014-07-04-FML-Auswahl.xml=LIDO}")) {
	        	 identifiedMetadataType = true;
	         }
	         if(s.contains("Die Lizenzangaben in den Metadaten sind ungültig: Lizenzen nicht eindeutig interpretierbar."))
	        	 rightLicense=true;
	         if(s.contains("SIP-Erstellungsvorgang abgebrochen"))
		    	   successfulMSG=true;
	    }
	    System.out.println("successful: "+successfulMSG+" rightLicense: "+rightLicense);
	    assertTrue(rightLicense && successfulMSG);
	    // read any errors from the attempted command
	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertTrue(!new File("target/atTargetDir/"+singleSipLidoLicenseMultipleAMError+".tgz").exists());
	    assertTrue(identifiedMetadataType);
	}
	
	
	@Test
	public void testBuildSingleSipLicenseInLido() throws IOException {
		
		File source = new File(sourceDir, singleSipLidoLicense);
		
String cmd = "./SipBuilder-Unix.sh -rights=\""+ATWorkingDirectory.CONTRACT_RIGHT_NON_LICENSED.getAbsolutePath()+"\" -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite";
		
		p=Runtime.getRuntime().exec(cmd,
		        null, new File("target/installation"));
		
		BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
        InputStreamReader(p.getErrorStream()));
		 
		boolean identifiedMetadataType = false;
		boolean successfulMSG=false;
	    boolean rightLicense=false;
		String s = "";
		// read the output from the command
	    System.out.println("Here is the standard output of the command:\n");
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	         if(s.contains("Identified metadata file") && s.contains("DNSCore/SIP-Builder/src/test/resources/at/"+singleSipLidoLicense+"/LIDO-Testexport2014-07-04-FML-Auswahl.xml=LIDO}")) {
	        	 identifiedMetadataType = true;
	         }
	         if(s.contains("License is satisfiable: Premis-License:false Mets-License:false Lido-License:true Publication-Decision:true"))
	        	 rightLicense=true;
	         if(s.contains("Die SIP-Erstellung wurde erfolgreich abgeschlossen."))
		    	   successfulMSG=true;
	    }
	    System.out.println("successful: "+successfulMSG+" rightLicense: "+rightLicense);
	    assertTrue(rightLicense && successfulMSG);
	    // read any errors from the attempted command
	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertTrue(new File("target/atTargetDir/"+singleSipLidoLicense+".tgz").exists());
	    assertTrue(identifiedMetadataType);
	}
	
	
	
	@Test
	public void testBuildSingleSipLicenseInLidoAndPremis() throws IOException {
		
		File source = new File(sourceDir, singleSipLidoLicense);
		
String cmd = "./SipBuilder-Unix.sh -rights=\""+ATWorkingDirectory.CONTRACT_RIGHT_LICENSED.getAbsolutePath()+"\" -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite";
		
		p=Runtime.getRuntime().exec(cmd,
		        null, new File("target/installation"));
		
		BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
        InputStreamReader(p.getErrorStream()));
		 
		boolean identifiedMetadataType = false;
		boolean successfulMSG=false;
	    boolean rightLicense=false;
		String s = "";
		// read the output from the command
	    System.out.println("Here is the standard output of the command:\n");
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	         if(s.contains("Identified metadata file") && s.contains("DNSCore/SIP-Builder/src/test/resources/at/"+singleSipLidoLicense+"/LIDO-Testexport2014-07-04-FML-Auswahl.xml=LIDO}")) {
	        	 identifiedMetadataType = true;
	         }
	         if(s.contains("Die Lizenzangaben sind nicht eindeutig: Lizenangaben dürfen nicht gleichzeitig im SIP-Builder und in den Metadaten angegeben werden."))
	        	 rightLicense=true;
	         if(s.contains("SIP-Erstellungsvorgang abgebrochen"))
		    	   successfulMSG=true;
	    }
	    System.out.println("successful: "+successfulMSG+" rightLicense: "+rightLicense);
	    assertTrue(rightLicense && successfulMSG);
	    // read any errors from the attempted command
	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertTrue(!new File("target/atTargetDir/"+singleSipLidoLicense+".tgz").exists());
	    assertTrue(identifiedMetadataType);
	}
	
	
	@Test
	public void testBuildSingleSipNoLicensePubl() throws IOException {
		
		File source = new File(sourceDir, singleSip);
		
String cmd = "./SipBuilder-Unix.sh -rights=\""+ATWorkingDirectory.CONTRACT_RIGHT_NON_LICENSED.getAbsolutePath()+"\" -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite";
		
		p=Runtime.getRuntime().exec(cmd,
		        null, new File("target/installation"));
		
		BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
        InputStreamReader(p.getErrorStream()));
		 
		boolean identifiedMetadataType = false;
		boolean successfulMSG=false;
	    boolean rightLicense=false;
		String s = "";
		// read the output from the command
	    System.out.println("Here is the standard output of the command:\n");
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	         if(s.contains("Identified metadata file") && s.contains("DNSCore/SIP-Builder/src/test/resources/at/"+singleSip+"/LIDO-Testexport2014-07-04-FML-Auswahl.xml=LIDO}")) {
	        	 identifiedMetadataType = true;
	         }
	         if(s.contains("Die Lizenzangaben sind nicht vorhanden: Um publizieren zu können, muss eine gültige Lizenz angegeben werden."))
	        	 rightLicense=true;
	         if(s.contains("SIP-Erstellungsvorgang abgebrochen"))
		    	   successfulMSG=true;
	    }
	    System.out.println("successful: "+successfulMSG+" rightLicense: "+rightLicense);
	    assertTrue(rightLicense && successfulMSG);
	    // read any errors from the attempted command
	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertTrue(!new File("target/atTargetDir/"+singleSip+".tgz").exists());
	    assertTrue(identifiedMetadataType);
	}
	
	@Test
	public void testBuildSingleSipNoLicenseNoPubl() throws IOException {
		
		File source = new File(sourceDir, singleSip);
		
		String cmd = "./SipBuilder-Unix.sh -rights=\""+sourceDir.getAbsolutePath()+"/contractRightsNoLicenseNoPublication.xml"+"\" -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite";
		
		p=Runtime.getRuntime().exec(cmd,
		        null, new File("target/installation"));
		
		BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
        InputStreamReader(p.getErrorStream()));
		 
		boolean identifiedMetadataType = false;
		boolean successfulMSG=false;
	    boolean rightLicense=false;
		String s = "";
		// read the output from the command
	    System.out.println("Here is the standard output of the command:\n");
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	         if(s.contains("Identified metadata file") && s.contains("DNSCore/SIP-Builder/src/test/resources/at/"+singleSip+"/LIDO-Testexport2014-07-04-FML-Auswahl.xml=LIDO}")) {
	        	 identifiedMetadataType = true;
	         }
	         if(s.contains("License is satisfiable: Premis-License:false Mets-License:false Lido-License:false Publication-Decision:false"))
	        	 rightLicense=true;
	         if(s.contains("Die SIP-Erstellung wurde erfolgreich abgeschlossen."))
		    	   successfulMSG=true;
	    }
	    System.out.println("successful: "+successfulMSG+" rightLicense: "+rightLicense);
	    assertTrue(rightLicense && successfulMSG);
	    // read any errors from the attempted command
	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertTrue(new File("target/atTargetDir/"+singleSip+".tgz").exists());
	    assertTrue(identifiedMetadataType);
	}
	
	@Test
	public void testBuildSingleSipNoLicensePrivatePubl() throws IOException {
		
		File source = new File(sourceDir, singleSip);
		
		String cmd = "./SipBuilder-Unix.sh -rights=\""+sourceDir.getAbsolutePath()+"/contractRightsNoLicensePrivatePublication.xml"+"\" -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite";
		
		p=Runtime.getRuntime().exec(cmd,
		        null, new File("target/installation"));
		
		BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
        InputStreamReader(p.getErrorStream()));
		 
		boolean identifiedMetadataType = false;
		boolean successfulMSG=false;
	    boolean rightLicense=false;
		String s = "";
		// read the output from the command
	    System.out.println("Here is the standard output of the command:\n");
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	         if(s.contains("Identified metadata file") && s.contains("DNSCore/SIP-Builder/src/test/resources/at/"+singleSip+"/LIDO-Testexport2014-07-04-FML-Auswahl.xml=LIDO}")) {
	        	 identifiedMetadataType = true;
	         }
	         if(s.contains("License is satisfiable: Premis-License:false Mets-License:false Lido-License:false Publication-Decision:false"))
	        	 rightLicense=true;
	         if(s.contains("Die SIP-Erstellung wurde erfolgreich abgeschlossen."))
		    	   successfulMSG=true;
	    }
	    System.out.println("successful: "+successfulMSG+" rightLicense: "+rightLicense);
	    assertTrue(rightLicense && successfulMSG);
	    // read any errors from the attempted command
	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertTrue(new File("target/atTargetDir/"+singleSip+".tgz").exists());
	    assertTrue(identifiedMetadataType);
	}
	
}
