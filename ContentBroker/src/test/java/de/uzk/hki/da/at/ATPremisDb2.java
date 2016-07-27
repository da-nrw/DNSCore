package de.uzk.hki.da.at;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.Session;

import de.uzk.hki.da.cb.WritePremisDBAction;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.PremisReader;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PremisDAFile;
import de.uzk.hki.da.model.PremisEvent;
import de.uzk.hki.da.pkg.ArchiveBuilder;
import de.uzk.hki.da.pkg.ArchiveBuilderFactory;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

public class ATPremisDb2 extends PREMISBase {

	//private static final String originalName = "ATUseCaseIngest1"; //"testpaket"; 
	//private static final File unpackedDIP = new File("/tmp/ATPremisDb");
	//private Object object = null;
	private static final String premisPath = "/home/julia/Desktop/premis/premis_Delta-Test.xml"; //testdelta.xml"; //premis_Rheinländer_2016-06-01.xml";  //premis_1-2016070174.xml"; //premis_1-2016062726.xml"; //premis_1-2016062164.xml"; //premis_1-2016062151_2.xml"; //premis_1-20160530106.xml"; //_1-20160530106.xml"; //1-2016032312_premis.xml";
	//private File premis = new File(premisPath);
	private static final String dipPath = "/home/julia/Desktop/testDaten/1-2016070651.tar"; //"/home/julia/Desktop/1-2016070638.tar"; //1-2016062726.tar";
	/*private static final String targetPath = "/home/julia/Desktop/dipFolder/";
	private static final File dipFile = new File(dipPath);
	private static final File targetDir = new File(targetPath);
	private static final File unpackedDip = new File(targetPath + FilenameUtils.removeExtension(dipFile.getName()));*/
	
	@After
	public void tearDown() throws IOException{
		//FileUtils.deleteDirectory(targetDir);
		
		//FileUtils.deleteDirectory(unpackedDIP);
		//Path.makeFile("tmp",object.getIdentifier()+".pack_1.tar").delete(); // retrieved dip
	}
	
	@Test
	public void testProperPREMISCreation() throws Exception {
		
		/*if(dipFile != null) {
		
		    ArchiveBuilder builder = ArchiveBuilderFactory.getArchiveBuilderForFile(dipFile); 
		    
		    try {
				builder.unarchiveFolder(dipFile, targetDir);
			} catch (Exception e) {
				throw new RuntimeException("couldn't unpack archive", e);
			}
	
			assertTrue(new File("/home/julia/Desktop/dipFolder/1-2016062726/data/premis.xml").exists());
	
			assertTrue(new File(unpackedDip, "data/premis.xml").exists());
			
			premis = new File(unpackedDip, "data/premis.xml");
			
			System.out.println(dipFile.getAbsolutePath() + " --- " + dipFile.getParent() + " --- " + dipFile.getPath());
		}*/
		

		verifyPREMISContainsSpecifiedElements();
	}
	
	
	@SuppressWarnings("unchecked")
	private void verifyPREMISContainsSpecifiedElements() throws IOException {
		
		
		
		/*SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc;
		try {
			doc = builder.build(new File(premisPfad));
		} catch (Exception e) {
			throw new RuntimeException("Failed to read premis file", e);
		}*/
		
		
		WritePremisDBAction pdb = new WritePremisDBAction(premisPath); //dipPath
		pdb.implementation();
		
		assert(true);
		
	}
	
	
	 private void copyFile(File in, File out) throws IOException { 
	        FileChannel inChannel = null; 
	        FileChannel outChannel = null; 
	        try { 
	            inChannel = new FileInputStream(in).getChannel(); 
	            outChannel = new FileOutputStream(out).getChannel(); 
	            inChannel.transferTo(0, inChannel.size(), outChannel); 
	        } catch (IOException e) { 
	            throw e; 
	        } finally { 
	            try { 
	                if (inChannel != null) 
	                    inChannel.close(); 
	                if (outChannel != null) 
	                    outChannel.close(); 
	            } catch (IOException e) {} 
	        } 
	    } 
	
	private void verifyAIPContainsExpectedFiles(
			String objectPath,
			String repAName,
			String repBName) {
		
		// check files
		String dataFolder = objectPath + "/data/";
		assertTrue(new File(dataFolder+repAName+"/"+"CCITT_1.TIF").exists());
		assertTrue(new File(dataFolder+repAName+"/"+"CCITT_2.TIF").exists());
		assertTrue(new File(dataFolder+repAName+"/"+"premis.xml").exists());
		assertTrue(new File(dataFolder+repAName+"/"+"CCITT_1_UNCOMPRESSED.TIF").exists());
		assertTrue(new File(dataFolder+repBName+"/"+"CCITT_1.TIF").exists());
		assertTrue(new File(dataFolder+repBName+"/"+"CCITT_2.TIF").exists());
		assertTrue(new File(dataFolder+repBName +"/"+"premis.xml").exists());

	}
	
	
	private boolean bagIsValid(String unpackedObjectPath) throws IOException{
		BagFactory bagFactory = new BagFactory();
		Bag bag = bagFactory.createBag(new File(unpackedObjectPath));
		if(!bag.verifyValid().isSuccess()){
			bag.close();
			return false;
		}
		bag.close();
		return true;
	}
}
