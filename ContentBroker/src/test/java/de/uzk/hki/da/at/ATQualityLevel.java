package de.uzk.hki.da.at;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.SubformatIdentificationStrategyPuidMapping;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;

public class ATQualityLevel extends AcceptanceTest {

	private static final String SOURCE_NAME_1_OnlyFidoFailed = "ATQualityLevel1NoPUIDFilesOnly";
	private static final String SOURCE_NAME_1_FidoFailed = "ATQualityLevel1NoPUIDFiles";
	private static final String SOURCE_NAME_2 = "ATQualityLevel_2";
	private static final String SOURCE_NAME_3 = "ATQualityLevel_3";
	private static final String SOURCE_NAME_4_WithNonSupportedF = "ATQualityLevel_4_WithNonSupportedF";
	private static final String SOURCE_NAME_4_OnlyNonSupported = "ATQualityLevel_4_OnlyNonSupported";

	private static final String SOURCE_NAME_5 = "ATQualityLevel_5";
	
	private static final String SOURCE_NAME_MINIMAL_4 = "ATMinimalQualityLevel4";
	private static final String SOURCE_NAME_MINIMAL_5_FAIL = "ATMinimalQualityLevel5";
	
	private static final String ORIG_NAME_DELTA = "ATQualityDeltaTest";

	private static final String ORIG_NAME = "ATQualityTest";
	
	private static final String  FAKE_SUBCONVERTER_PUID="x-fmt/266";

	//private static final String ALIEN_NAME = "ATKeepModDates";

	private String idiName;

	@Before
	public void setUp() throws IOException {
	}
	
/*	
	@Test
	public void testMinimalQualityLevelPremis4OK() throws IOException {
		String origName=SOURCE_NAME_MINIMAL_4+"PremisOK";
		ath.putSIPtoIngestArea(SOURCE_NAME_MINIMAL_4, C.FILE_EXTENSION_TGZ, origName);
		ath.awaitObjectState(origName, Object.ObjectStatus.InWorkflow);
 		//ath.waitForObjectPublishedState(ORIG_NAME,0);
 		ath.awaitObjectState(origName, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
 		
 		Object obbi = ath.getObject(origName);

		idiName = obbi.getIdentifier();
 		assertEquals("Object Level: "+obbi.getQuality_flag(),obbi.getQuality_flag(),4);
 		assertEquals("Package count: "+obbi.getPackages().size(),obbi.getPackages().size(),1);
	}
	
	
	@Test
	public void testMinimalQualityLevelUser4OK() throws IOException {
		String origName=SOURCE_NAME_MINIMAL_4+"UserOK";
		setTestUserMinimalQualityLevel(4);
		ath.putSIPtoIngestArea(SOURCE_NAME_MINIMAL_4, C.FILE_EXTENSION_TGZ, origName);
		ath.awaitObjectState(origName, Object.ObjectStatus.InWorkflow);
 		//ath.waitForObjectPublishedState(ORIG_NAME,0);
 		ath.awaitObjectState(origName, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
 		
 		Object obbi = ath.getObject(origName);

		idiName = obbi.getIdentifier();
 		assertEquals("Object Level: "+obbi.getQuality_flag(),obbi.getQuality_flag(),4);
 		assertEquals("Package count: "+obbi.getPackages().size(),obbi.getPackages().size(),1);
	}
	
	
	
	@Test
	public void testMinimalQualityLevelPremis5Fail() throws IOException, InterruptedException {
		String origName=SOURCE_NAME_MINIMAL_5_FAIL+"Premis";
		ath.putSIPtoIngestArea(SOURCE_NAME_MINIMAL_5_FAIL, C.FILE_EXTENSION_TGZ, origName);
		ath.awaitObjectState(origName, Object.ObjectStatus.InWorkflow);
		ath.waitForJobToBeInErrorStatus(origName, C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
		
		assertEquals(ath.getJob(origName).getStatus(),"274");
 		
 		Object obbi = ath.getObject(origName);

		idiName = obbi.getIdentifier();
 		assertEquals("Object Level: "+obbi.getQuality_flag(),obbi.getQuality_flag(),4);
	}
	
	
	@Test
	public void testMinimalQualityLevelUser5Fail() throws IOException, InterruptedException {
		String origName=SOURCE_NAME_MINIMAL_5_FAIL+"User";
		
		setTestUserMinimalQualityLevel(5);
		ath.putSIPtoIngestArea(SOURCE_NAME_MINIMAL_5_FAIL, C.FILE_EXTENSION_TGZ, origName);
		ath.awaitObjectState(origName, Object.ObjectStatus.InWorkflow);
		ath.waitForJobToBeInErrorStatus(origName, C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
		
		assertEquals(ath.getJob(origName).getStatus(),"274");
 		
 		Object obbi = ath.getObject(origName);

		idiName = obbi.getIdentifier();
 		assertEquals("Object Level: "+obbi.getQuality_flag(),obbi.getQuality_flag(),4);
 		assertEquals("Package count: "+obbi.getPackages().size(),obbi.getPackages().size(),1);
 		
	}
	*/
	
	// de.uzk.hki.da.format.ImageMagickSubformatIdentifier
	//de.uzk.hki.da.format.FFmpegSubformatIdentifier
	//de.uzk.hki.da.format.XMLSubformatIdentifier
	@Test
	public void testOnlyFidoFailedIdentificationFiles() throws IOException {
		try{
			//activateFakeSubformatIdentificationStrategies("de.uzk.hki.da.format.ImageMagickSubformatIdentifier");
			String origName="testOnlyFidoFailedIdentificationFiles";
			ath.putSIPtoIngestArea(SOURCE_NAME_1_OnlyFidoFailed, C.FILE_EXTENSION_TGZ, origName);
			ath.awaitObjectState(origName, Object.ObjectStatus.InWorkflow);
	 		//ath.waitForObjectPublishedState(ORIG_NAME,0);
	 		ath.awaitObjectState(origName, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
	 		
	 		Object obbi = ath.getObject(origName);

			idiName = obbi.getIdentifier();
	 		assertEquals("Object Level: "+obbi.getQuality_flag(),obbi.getQuality_flag(),1);
	 		assertEquals("Package count: "+obbi.getPackages().size(),obbi.getPackages().size(),1);
		}finally{
		//	dectivateAllFakeSubformatIdentificationStrategies();
		}
		
	}
	
	@Test
	public void testFidoFailedIdentificationFiles() throws IOException {
		try{
			//activateFakeSubformatIdentificationStrategies("de.uzk.hki.da.format.ImageMagickSubformatIdentifier");
			String origName="testFidoFailedIdentificationFiles";
			ath.putSIPtoIngestArea(SOURCE_NAME_1_FidoFailed, C.FILE_EXTENSION_TGZ, origName);
			ath.awaitObjectState(origName, Object.ObjectStatus.InWorkflow);
	 		//ath.waitForObjectPublishedState(ORIG_NAME,0);
	 		ath.awaitObjectState(origName, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
	 		
	 		Object obbi = ath.getObject(origName);

			idiName = obbi.getIdentifier();
	 		assertEquals("Object Level: "+obbi.getQuality_flag(),obbi.getQuality_flag(),1);
	 		assertEquals("Package count: "+obbi.getPackages().size(),obbi.getPackages().size(),1);
		}catch(Throwable  t){
			t.printStackTrace(System.err);
			throw new RuntimeException (t);
		}finally{
		//	dectivateAllFakeSubformatIdentificationStrategies();
		}
		
	}
	
	@Test
	public void testSubformatXMLFailedIdentificationFiles() throws IOException {
		testSpecificSubformatFailedIdentificationFiles("de.uzk.hki.da.format.XMLSubformatIdentifier");
	}

		
	@Test
	public void testSubformatImageMagicFailedIdentificationFiles() throws IOException {
		testSpecificSubformatFailedIdentificationFiles("de.uzk.hki.da.format.ImageMagickSubformatIdentifier");
	}
	@Test
	public void testSubformatFFMPEGFailedIdentificationFiles() throws IOException {
		testSpecificSubformatFailedIdentificationFiles("de.uzk.hki.da.format.FFmpegSubformatIdentifier");
	}
	
	
	public void testSpecificSubformatFailedIdentificationFiles(String subFormatIdentifier) throws IOException {
		try{
			activateFakeSubformatIdentificationStrategies(subFormatIdentifier);
			int lastDotIndex=0;
			while(subFormatIdentifier.indexOf(".", lastDotIndex+1)!=-1)
				lastDotIndex=subFormatIdentifier.indexOf(".", lastDotIndex+1);
			String origName="testFidoFailedIdentificationFiles"+subFormatIdentifier.substring(lastDotIndex, subFormatIdentifier.length());
			ath.putSIPtoIngestArea(SOURCE_NAME_1_FidoFailed, C.FILE_EXTENSION_TGZ, origName);
			ath.awaitObjectState(origName, Object.ObjectStatus.InWorkflow);
	 		//ath.waitForObjectPublishedState(ORIG_NAME,0);
	 		ath.awaitObjectState(origName, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
	 		
	 		Object obbi = ath.getObject(origName);

			idiName = obbi.getIdentifier();
	 		assertEquals("Object Level: "+obbi.getQuality_flag(),obbi.getQuality_flag(),1);
	 		assertEquals("Package count: "+obbi.getPackages().size(),obbi.getPackages().size(),1);
		}catch(Throwable  t){
			t.printStackTrace(System.err);
			throw new RuntimeException (t);
		}finally{
			dectivateAllFakeSubformatIdentificationStrategies();
		}
	}
	
	

	@Test
	public void testOnlyNonSupported() throws IOException {
		ath.putSIPtoIngestArea(SOURCE_NAME_4_OnlyNonSupported, C.FILE_EXTENSION_TGZ, ORIG_NAME);
		ath.awaitObjectState(ORIG_NAME, Object.ObjectStatus.InWorkflow);
 		//ath.waitForObjectPublishedState(ORIG_NAME,0);
 		ath.awaitObjectState(ORIG_NAME, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
 		
 		Object obbi = ath.getObject(ORIG_NAME);

		idiName = obbi.getIdentifier();
 		assertEquals("Object Level: "+obbi.getQuality_flag(),obbi.getQuality_flag(),4);
 		assertEquals("Package count: "+obbi.getPackages().size(),obbi.getPackages().size(),1);
	}
	

	
	@Test
	public void deltaTest() throws IOException {		
		ath.putSIPtoIngestArea(SOURCE_NAME_2, C.FILE_EXTENSION_TGZ, ORIG_NAME_DELTA);
		ath.awaitObjectState(ORIG_NAME_DELTA, Object.ObjectStatus.InWorkflow);
 		ath.waitForObjectPublishedState(ORIG_NAME_DELTA,0);
 		ath.awaitObjectState(ORIG_NAME_DELTA, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
 		
 		Object obbi = ath.getObject(ORIG_NAME_DELTA);
		idiName = obbi.getIdentifier();
 		assertEquals("Object Level: "+obbi.getQuality_flag(),obbi.getQuality_flag(),2);
 		assertEquals("Package count: "+obbi.getPackages().size(),obbi.getPackages().size(),1);
 		
		ath.putSIPtoIngestArea(SOURCE_NAME_3, C.FILE_EXTENSION_TGZ, ORIG_NAME_DELTA);
		ath.awaitObjectState(ORIG_NAME_DELTA, Object.ObjectStatus.InWorkflow);
		ath.waitForDefinedPublishedState(ORIG_NAME_DELTA);
		ath.awaitObjectState(ORIG_NAME_DELTA, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		
		obbi = ath.getObject(ORIG_NAME_DELTA);
 		assertEquals("Object Level: "+obbi.getQuality_flag(),obbi.getQuality_flag(),3);
		assertEquals("Package count: "+obbi.getPackages().size(),obbi.getPackages().size(),2);
		
		ath.putSIPtoIngestArea(SOURCE_NAME_4_WithNonSupportedF, C.FILE_EXTENSION_TGZ, ORIG_NAME_DELTA);
		ath.awaitObjectState(ORIG_NAME_DELTA, Object.ObjectStatus.InWorkflow);
		ath.waitForDefinedPublishedState(ORIG_NAME_DELTA);
		ath.awaitObjectState(ORIG_NAME_DELTA, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		
		obbi = ath.getObject(ORIG_NAME_DELTA);
 		assertEquals("Object Level: "+obbi.getQuality_flag(),obbi.getQuality_flag(),4);
		assertEquals("Package count: "+obbi.getPackages().size(),obbi.getPackages().size(),3);
		
		ath.putSIPtoIngestArea(SOURCE_NAME_5, C.FILE_EXTENSION_TGZ, ORIG_NAME_DELTA);
		ath.awaitObjectState(ORIG_NAME_DELTA, Object.ObjectStatus.InWorkflow);
		ath.waitForDefinedPublishedState(ORIG_NAME_DELTA);
		ath.awaitObjectState(ORIG_NAME_DELTA, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		
		obbi = ath.getObject(ORIG_NAME_DELTA);
 		assertEquals("Object Level: "+obbi.getQuality_flag(),obbi.getQuality_flag(),5);
		assertEquals("Package count: "+obbi.getPackages().size(),obbi.getPackages().size(),4);
		
		ath.putSIPtoIngestArea(SOURCE_NAME_3, C.FILE_EXTENSION_TGZ, ORIG_NAME_DELTA);
		ath.awaitObjectState(ORIG_NAME_DELTA, Object.ObjectStatus.InWorkflow);
		ath.waitForDefinedPublishedState(ORIG_NAME_DELTA);
		ath.awaitObjectState(ORIG_NAME_DELTA, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		
		obbi = ath.getObject(ORIG_NAME_DELTA);
 		assertEquals("Object Level: "+obbi.getQuality_flag(),obbi.getQuality_flag(),3);
		assertEquals("Package count: "+obbi.getPackages().size(),obbi.getPackages().size(),5);
		
		ath.putSIPtoIngestArea(SOURCE_NAME_2, C.FILE_EXTENSION_TGZ, ORIG_NAME_DELTA);
		ath.awaitObjectState(ORIG_NAME_DELTA, Object.ObjectStatus.InWorkflow);
		ath.waitForDefinedPublishedState(ORIG_NAME_DELTA);
		ath.awaitObjectState(ORIG_NAME_DELTA, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		
		obbi = ath.getObject(ORIG_NAME_DELTA);
 		assertEquals("Object Level: "+obbi.getQuality_flag(),obbi.getQuality_flag(),2);
		assertEquals("Package count: "+obbi.getPackages().size(),obbi.getPackages().size(),6);
				
		idiName = obbi.getIdentifier();
	}

	@After
	public void tearDown() {
		distributedConversionAdapter.remove("aip/"+testContractor.getUsername()+"/" + idiName);
		setTestUserMinimalQualityLevel(0);
		dectivateAllFakeSubformatIdentificationStrategies();
	}
	

	synchronized static protected void activateFakeSubformatIdentificationStrategies(String strategyName) {

		System.out.println("Fake subformat activation start");
		Session session = HibernateUtil.openSession();
		Transaction transaction = session.beginTransaction();
		try {

			// AUTO generation of id is failed -> dirty workaround
		/*	List<SubformatIdentificationStrategyPuidMapping> l = session
					.createQuery("from SubformatIdentificationStrategyPuidMapping").list();

			Set<Integer> usedKey = new HashSet<Integer>();

			for (SubformatIdentificationStrategyPuidMapping strategy : l) {
				System.out.println(strategy + " " + strategy.getId());
				usedKey.add(strategy.getId());
			}*/

			int id =111+ Math.abs(strategyName.hashCode())%10000;
			/*while (usedKey.contains(id)){
				id++;
			}*/
			SubformatIdentificationStrategyPuidMapping fakeMapping = new SubformatIdentificationStrategyPuidMapping();
			fakeMapping.setFormatPuid(FAKE_SUBCONVERTER_PUID);
			fakeMapping.setSubformatIdentificationStrategyName(strategyName);
			fakeMapping.setId(id);
			System.out.println("New Stategy: " + fakeMapping + " " + fakeMapping.getId());
			session.save(fakeMapping);
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			throw new RuntimeException(t);
		} finally {
			transaction.commit();
			session.close();
		}
		System.out.println("Fake subformat activation end");
	}
	
	synchronized static protected void dectivateAllFakeSubformatIdentificationStrategies(){
		System.out.println("dectivateAllFakeSubformatIdentificationStrategies Fake subformat start");
		Session session = HibernateUtil.openSession();
		Transaction transaction = session.beginTransaction();
		
		Query query=session.createQuery("delete SubformatIdentificationStrategyPuidMapping where formatPuid=:PUID");
		query.setParameter("PUID",FAKE_SUBCONVERTER_PUID );
		int result = query.executeUpdate();

		transaction.commit();
		session.close();
		System.out.println("dectivateAllFakeSubformatIdentificationStrategies Fake subformat end");
	}

}
