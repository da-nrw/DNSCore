package de.uzk.hki.da.at;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.SystemEvent;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.repository.MetadataIndexException;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;

public class ATRecreateEDM extends AcceptanceTest {
	private static final String sip = "ATMetadataRoleTermNonSortTitle";
	
	Path contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),WorkArea.PIPS, WorkArea.PUBLIC, testContractor.getUsername());
	private static Object object1;
	private  String PORTAL_CI_TEST =getTestIndex();
	
	@BeforeClass
	public static void setUp() throws IOException, InterruptedException {
		
		ath.putSIPtoIngestArea(sip, "tgz", sip);
		ath.awaitObjectState(sip,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(sip);
		ath.waitForObjectPublishedState(sip, C.PUBLISHEDFLAG_PUBLIC);
		
		object1=ath.getObject(sip);
	}

	@Test
	public void testRecreactionOfEDM() throws IOException, JDOMException, MetadataIndexException, RepositoryException, InterruptedException {
		File edmFile1 = ath.loadFileFromPip(object1.getIdentifier(), "EDM.xml");
		//assert edm file and index entry exists
		assertTrue(edmFile1.exists());	
		assertTrue(indexHasObj(object1.getIdentifier()));
		//remove edm file and index entry
		edmFile1.delete();
		metadataIndex.deleteFromIndex(PORTAL_CI_TEST, object1.getIdentifier());

		assertTrue(!edmFile1.exists());
		assertTrue(!indexHasObj(object1.getIdentifier()));
		
		//initiate recreation of edm file and index entry
		SystemEvent se=createReindexSystemEvent();
		while(systemEventExists(se)){
			Thread.sleep(5000);
			System.out.println("SystemEvent is not finished (yet). ");
		}
		ath.awaitObjectState(sip,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		
		//assert edm file and index entry exists
		edmFile1 = ath.loadFileFromPip(object1.getIdentifier(), "EDM.xml");
		assertTrue(edmFile1.exists());	
		assertTrue(indexHasObj(object1.getIdentifier()));
	}
	
	private boolean indexHasObj(String id){
		String jsonString=metadataIndex.getIndexedMetadata(PORTAL_CI_TEST, id);
		JSONObject jsonObj = (new JSONObject(jsonString)).getJSONObject("hits");
		if(1==jsonObj.getInt("total"))
			return true;
		else if(0==jsonObj.getInt("total"))
			return false;
		else
			assertEquals("Es sollte nur 0 oder 1 Objekt in dem Index auffindbar sein",-1,jsonObj.getInt("total"));
		return false;
	}
	
	private SystemEvent createReindexSystemEvent() {
		Session session = HibernateUtil.openSession();
		Transaction transaction = session.beginTransaction();
		SystemEvent se=new SystemEvent();
		se.setOwner(this.testContractor);
		se.setType("RecreateEDMAndReindexEvent");
		se.setNode(this.localNode);
		session.saveOrUpdate(se);
		transaction.commit();
		session.close();
		return se;
	}
	
	private boolean systemEventExists(SystemEvent se) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		
		List<SystemEvent> l = null;
		l = session.createQuery("SELECT e FROM SystemEvent e where e.id=?1"	)
			.setParameter("1", se.getId()).setReadOnly(true).list();
		
		session.close();
		
		if(l.size()>1)
			throw new RuntimeException("More as one System Event found");
		if(l.size()==1)
			return true;
		
		return false;
	}
}
