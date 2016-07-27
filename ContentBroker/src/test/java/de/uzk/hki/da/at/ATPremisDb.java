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
import org.hibernate.Session;

import de.uzk.hki.da.cb.WritePremisDBAction;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.PremisReader;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PremisDAFile;
import de.uzk.hki.da.model.PremisEvent;
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

public class ATPremisDb extends PREMISBase {

	private static final String originalName = "ATUseCaseIngest1"; //"testpaket"; 
	private static final File unpackedDIP = new File("/tmp/ATPremisDb");
	private Object object = null;
	
	@After
	public void tearDown() throws IOException{
		FileUtils.deleteDirectory(unpackedDIP);
		Path.makeFile("tmp",object.getIdentifier()+".pack_1.tar").delete(); // retrieved dip
	}
	
	@Test
	public void testProperPREMISCreation() throws Exception {
		String destName = "test3";
		ath.putSIPtoIngestArea(originalName, "tgz", destName);
		ath.awaitObjectState(destName,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		object=ath.getObject(destName);
		
		ath.retrieveAIP(object,unpackedDIP,"1");
		assertThat(object.getObject_state()).isEqualTo(100);
		String unpackedObjectPath = unpackedDIP.getAbsolutePath()+"/";
		
		String folders[] = new File(unpackedObjectPath + "data/").list();
		String repAName="";
		String repBName="";
		for (String f:folders){
			if (f.contains("+a")) repAName = f;
			if (f.contains("+b")) repBName = f;
		}
		verifyPREMISContainsSpecifiedElements(unpackedObjectPath,object,repAName,repBName,localNode.getName());
	}
	
	
	@SuppressWarnings("unchecked")
	private void verifyPREMISContainsSpecifiedElements(
			String unpackedObjectPath,
			Object object,
			String repAName,
			String repBName,
			String nodeName) throws IOException {
		
		assertTrue(new File(unpackedObjectPath + "data/" +  repBName + "/premis.xml").exists());
		
		File premis = new File("/home/julia/Desktop/premis/premis_" + object.getIdentifier() + ".xml");
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc;
		try {
			doc = builder.build(new File(unpackedObjectPath +  "data/" + repBName + "/premis.xml"));
			File in = new File(unpackedObjectPath + "data/" + repBName + "/premis.xml");
			copyFile(in, premis);
		} catch (Exception e) {
			throw new RuntimeException("Failed to read premis file", e);
		}
		
		
		WritePremisDBAction pdb = new WritePremisDBAction("/home/julia/Desktop/premis/premis_" + object.getIdentifier() + ".xml"); //premis);
		pdb.implementation();
		
		String objectIdentifier = object.getIdentifier();
		System.out.println(objectIdentifier);
		

		
		Element rootElement = doc.getRootElement();
		Namespace ns = rootElement.getNamespace();
		
		List<Element> objectElements = rootElement.getChildren("object", ns);
		
		int checkedObjects = 0;
		for (Element e:objectElements){
			String identifierText = e.getChild("objectIdentifier",ns).getChildText("objectIdentifierValue",ns);
			
			if (identifierText.equals(objectIdentifier)) {
				List<Element> identifierEls = e.getChildren("objectIdentifier", ns);
				assertEquals(object.getUrn(), identifierEls.get(1).getChildText("objectIdentifierValue", ns)); // TODO shouldn't it be the unique object identifier?
				String originalName = e.getChildText("originalName", ns);
				assertEquals(object.getOrig_name(),originalName);
				checkedObjects++;
			}
			
			if (identifierText.equals(objectIdentifier + ".pack_1.tar")) {
				//assertThat(e.getChildText("originalName",ns)).isEqualTo(originalName+".tgz");
				//assertThat(e.getChildText("originalName",ns)).isEqualTo("ATUseCaseIngest1.tgz");
				checkedObjects++;
			}
						
			if (identifierText.contains("a/CCITT_1.TIF")){
				verifyPREMISFileObjectHasCertainSubElements(ns, e, "CCITT_1.TIF", "fmt/353");
				checkedObjects++;
			}
			if (identifierText.contains("a/CCITT_2.TIF")){
				verifyPREMISFileObjectHasCertainSubElements(ns, e, "CCITT_2.TIF", "fmt/353");
				checkedObjects++;
			}
			if (identifierText.contains("a/CCITT_1_UNCOMPRESSED.TIF")){
				verifyPREMISFileObjectHasCertainSubElements(ns, e, "CCITT_1_UNCOMPRESSED.TIF", "fmt/353");
				checkedObjects++;
			}
			if (identifierText.contains("b/CCITT_1.TIF")){
				verifyPREMISFileObjectHasCertainSubElements(ns, e, "CCITT_1.TIF", "fmt/353");
				checkedObjects++;
			}
			if (identifierText.contains("b/CCITT_2.TIF")){
				verifyPREMISFileObjectHasCertainSubElements(ns, e, "CCITT_2.TIF", "fmt/353");
				checkedObjects++;
			}
		}
		assertThat(checkedObjects).isEqualTo(7);
		
		
		
		
		List<Element> eventElements = rootElement.getChildren("event", ns);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		int checkedEvents = 0;
		
		Session session2 = HibernateUtil.openSession();
		session2.beginTransaction();
		List events = session2.createCriteria(PremisEvent.class).list();
		
		System.out.println("Events: " + events.size());
		Iterator i = events.iterator();
		while(i.hasNext()) {
			PremisEvent pe = (PremisEvent) i.next();
			System.out.println(pe.getType() + " " + (pe.getSource_file()==null?"":pe.getSource_file().getRelative_path()));
		}
		List files = session2.createCriteria(PremisDAFile.class).list();
		System.out.println("Files: " + files.size());
		Iterator i2 = files.iterator();
		while(i2.hasNext()) {
			PremisDAFile pdf = (PremisDAFile) i2.next();
			System.out.println(pdf.getRelative_path());
		}
		/*List p = session2.createQuery("select e.id, e.identifier, e.type, s.relative_path, t.relative_path from premis_events e left join premis_dafiles s on e.source_file_id=s.id left join premis_dafiles t on e.target_file_id=t.id").list();
		Iterator i3 = p.iterator();
		while(i3.hasNext()) {
			PremisEvent pe = (PremisEvent) i3.next();
			System.out.println(pe.getId() + " " + pe.getIdentifier() + " " + pe.getType());
		}*/
		
		for (Element e:eventElements){
			String eventType = e.getChildText("eventType", ns);
			
			if (eventType.equals("CONVERT")){
				String eventDetail = e.getChildText("eventDetail",ns);
				String event1fileName = "CCITT_1.TIF";
				if (eventDetail.contains(event1fileName)){
					checkConvertEvent(ns, e, event1fileName,nodeName);
					checkedEvents++;
				}
				String event2fileName = "CCITT_1.TIF";
				if (eventDetail.contains(event2fileName)){
					checkConvertEvent(ns, e, event2fileName,nodeName);
					checkedEvents++;
				}
			}
			
			if (eventType.equals("SIP_CREATION")){
				assertTrue(e.getChild("eventIdentifier", ns).getChild("eventIdentifierValue", ns) != null);
				try {dateFormat.parse(e.getChild("eventDateTime", ns).getValue());} catch (ParseException ex) {	fail();	}	
				assertThat(e.getChild("linkingAgentIdentifier", ns).getChildText("linkingAgentIdentifierValue", ns)).isEqualTo("DA NRW SIP-Builder 0.5.3");
				assertThat(e.getChild("linkingObjectIdentifier", ns).getChildText("linkingObjectIdentifierValue", ns)).isEqualTo(objectIdentifier+".pack_1.tar");
				checkedEvents++;
			}
			
			if (eventType.equals("INGEST")){
//				assertEquals("7654321", e.getChild("eventIdentifier", ns).getChild("eventIdentifierValue", ns).
//						getValue());
				try {dateFormat.parse(e.getChild("eventDateTime", ns).getValue());} catch (ParseException ex) {	fail();	}	
				assertThat(e.getChild("linkingAgentIdentifier", ns).getChildText("linkingAgentIdentifierValue", ns)).isEqualTo("TEST");
				assertThat(e.getChild("linkingObjectIdentifier", ns).getChildText("linkingObjectIdentifierValue", ns)).isEqualTo(objectIdentifier+".pack_1.tar");
				checkedEvents++;
			}
			
		}
		assertThat(checkedEvents).isEqualTo(4);
		
		

		/*for (PremisEvent pe : pes) {
			System.out.println(pe.getType());
			if(pe.getType().equals("CONVERT") || pe.getType().equals("COPY")) {
				session2.persist(pe.getSource_file());
				session2.persist(pe.getTarget_file());
			}
			session2.persist(pe);
		}
		session2.getTransaction().commit();*/
		session2.close();
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
