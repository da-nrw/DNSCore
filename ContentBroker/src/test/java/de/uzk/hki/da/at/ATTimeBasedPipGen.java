package de.uzk.hki.da.at;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;

public class ATTimeBasedPipGen extends AcceptanceTest {
	private static final String ORIG_NAME_PREFIX =  "ATTimeBasedPubl";

	@Test
	public void testPublishPublic() throws InterruptedException, IOException, RepositoryException, ParseException{
		String fullName = ORIG_NAME_PREFIX + "StartDatePublic";
		String fullIdentifier = ORIG_NAME_PREFIX + "StartDatePublic_id";
		
		ath.putAIPToLongTermStorage(fullIdentifier, fullName, new Date(), Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);

		Object object = ath.getObject(fullName);
	
		Session session = HibernateUtil.openSession();
		session.refresh(object);

		Date pubDate;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		pubDate = sdf.parse("1978-12-23");
		
		object.setStatic_nondisclosure_limit(pubDate);
		object.setPublished_flag(C.PUBLISHEDFLAG_NO_PUBLICATION);

		Transaction transaction = session.beginTransaction();

		session.save(object);
		
		transaction.commit();
		
		ath.waitForDefinedPublishedState(fullName);

		session.refresh(object);
		session.close();
		assertNotNull(object);
		assertTrue(repositoryFacade.objectExists(object.getIdentifier(), preservationSystem.getOpenCollectionName()));
		assertFalse(repositoryFacade.objectExists(object.getIdentifier(), preservationSystem.getClosedCollectionName()));
		assertEquals(C.PUBLISHEDFLAG_PUBLIC, object.getPublished_flag());
	}

	@Test
	public void testNoRepublishDueToAllreadyPublished() throws InterruptedException, IOException, RepositoryException, ParseException{
		String fullName = ORIG_NAME_PREFIX + "NoPubWithStartDateSet";
		String fullIdentifier = ORIG_NAME_PREFIX + "NoPubWithStartDateSet_id";
		
		ath.putAIPToLongTermStorage(fullIdentifier, fullName, new Date(), Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);

		Object object = ath.getObject(fullName);
	
		Session session = HibernateUtil.openSession();
		session.refresh(object);

		Date pubDate;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		pubDate = sdf.parse("2050-09-17");
		object.setStatic_nondisclosure_limit(pubDate);
		
		pubDate = sdf.parse("2013-09-17");
		object.setStatic_nondisclosure_limit_institution(pubDate);
		
		object.setPublished_flag(C.PUBLISHEDFLAG_INSTITUTION);
		String lastModified = object.getDate_modified();
		
		Transaction transaction = session.beginTransaction();

		session.save(object);
		
		transaction.commit();
		
		Thread.sleep(5000);
		session.refresh(object);
		session.close();
		assertNotNull(object);
		assertEquals(C.PUBLISHEDFLAG_INSTITUTION, object.getPublished_flag());
		assertEquals(lastModified, object.getDate_modified());
	}

	@Test
	public void testNoRepublishDueToStaticNondisclosureLimit() throws InterruptedException, IOException, RepositoryException, ParseException{
		String fullName = ORIG_NAME_PREFIX + "PublishNothing";
		String fullIdentifier = ORIG_NAME_PREFIX + "PublishNothing_id";
		
		ath.putAIPToLongTermStorage(fullIdentifier, fullName, new Date(), Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);

		Object object = ath.getObject(fullName);
	
		Session session = HibernateUtil.openSession();
		session.refresh(object);

		Date pubDate;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		pubDate = sdf.parse("3333-09-17");
		
		object.setStatic_nondisclosure_limit(pubDate);
		object.setStatic_nondisclosure_limit_institution(pubDate);
		object.setPublished_flag(C.PUBLISHEDFLAG_NO_PUBLICATION);
		String lastModified = object.getDate_modified();
		
		Transaction transaction = session.beginTransaction();

		session.save(object);
		
		transaction.commit();
		
		Thread.sleep(5000);
		session.refresh(object);
		session.close();
		assertNotNull(object);
		assertEquals(C.PUBLISHEDFLAG_NO_PUBLICATION, object.getPublished_flag());
		assertEquals(lastModified, object.getDate_modified());
	}

	@Test
	public void testNoRepublishDueToLasTry() throws InterruptedException, IOException, RepositoryException, ParseException{
		String fullName = ORIG_NAME_PREFIX + "NoPubWithLawSet";
		String fullIdentifier = ORIG_NAME_PREFIX + "NoPubWithLawSet_id";
		
		ath.putAIPToLongTermStorage(fullIdentifier, fullName, new Date(), Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);

		Object object = ath.getObject(fullName);
	
		Session session = HibernateUtil.openSession();
		session.refresh(object);

		Date pubDate;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		pubDate = sdf.parse("2013-09-17");
		
		object.setStatic_nondisclosure_limit(pubDate);
		object.setStatic_nondisclosure_limit_institution(pubDate);
		object.setPublished_flag(C.PUBLISHEDFLAG_NO_PUBLICATION);
		object.setLastPublicationTry(new Date());
		String lastModified = object.getDate_modified();
		
		Transaction transaction = session.beginTransaction();

		session.save(object);
		
		transaction.commit();
		
		Thread.sleep(5000);
		session.refresh(object);
		session.close();
		assertNotNull(object);
		assertEquals(C.PUBLISHEDFLAG_NO_PUBLICATION, object.getPublished_flag());
		assertEquals(lastModified, object.getDate_modified());
	}

	@Test
	public void testPublish() throws InterruptedException, IOException, RepositoryException, ParseException{
		String fullName = ORIG_NAME_PREFIX + "AllPublic";
		String fullIdentifier = ORIG_NAME_PREFIX + "AllPublic_id";
		
		ath.putAIPToLongTermStorage(fullIdentifier, fullName, new Date(), Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);

		Object object = ath.getObject(fullName);
	
		Session session = HibernateUtil.openSession();
		session.refresh(object);

		Date pubDate;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		pubDate = sdf.parse("2013-09-17");
		
		object.setStatic_nondisclosure_limit(pubDate);
		object.setStatic_nondisclosure_limit_institution(pubDate);
		object.setPublished_flag(C.PUBLISHEDFLAG_NO_PUBLICATION);

		Transaction transaction = session.beginTransaction();

		session.save(object);
		
		transaction.commit();
		
		ath.waitForDefinedPublishedState(fullName);

		session.refresh(object);
		session.close();
		assertNotNull(object);
		assertTrue(repositoryFacade.objectExists(object.getIdentifier(), preservationSystem.getOpenCollectionName()));
		assertTrue(repositoryFacade.objectExists(object.getIdentifier(), preservationSystem.getClosedCollectionName()));
		assertEquals(C.PUBLISHEDFLAG_PUBLIC+C.PUBLISHEDFLAG_INSTITUTION, object.getPublished_flag());
	}
}
