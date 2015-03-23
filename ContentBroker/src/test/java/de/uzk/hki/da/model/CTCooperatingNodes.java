package de.uzk.hki.da.model;

import static org.junit.Assert.*;

import java.util.Date;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.service.HibernateUtil;

public class CTCooperatingNodes {

	private static Node n; 
	@BeforeClass
	public static void setupBefore() {
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.ci");
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		n = (Node) session.get(Node.class, 1);
		for (Node cn : n.getCooperatingNodes()) {
			cn.getId(); // prevent lazy fetching
			System.out.print(cn.getId());
		}
		session.close();
	}
	
	@Test
	public void testModel() {
		assertFalse(n.getCooperatingNodes().isEmpty());
		Node cn =
				n.getCooperatingNodes().iterator().next();
		assertEquals("cooperatingnode", cn.getName());
	}
	
	@Test 
	public void testCopy() {
		Package p = new Package();
		Copy copy1 = new Copy();
		copy1.setChecksum("abcdef");
		copy1.setChecksumDate(new Date());
		
		Copy copy2 = new Copy();
		copy2.setChecksum("abcdef");
		copy2.setChecksumDate(new Date());
		
		Object obj = new Object();
		obj.getPackages().add(p);
		
		p.getCopies().add(copy1);

		p.getCopies().add(copy2);
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.save(obj);
		session.getTransaction().commit();
		session.close();
		
		Object reobj;
		reobj = null;
		Session session2 = HibernateUtil.openSession();
		session2.beginTransaction();
		reobj = (Object) session2.get(Object.class,obj.getData_pk());
		assertTrue(reobj.getPackages().get(0).getCopies().size()==2);
		session2.close();
	}
	
	@Test
	public void createCopies() {
		
		for (Node cn : n.getCooperatingNodes()) {
			Session session = HibernateUtil.openSession();
			session.beginTransaction();
			session.saveOrUpdate(cn);
			
			session.refresh(cn);
			
			Copy copy = new Copy(); 
			cn.getCopies().add(copy);
			// TODO create dao method for updating node association with copy without overwriting 
			// urn_index
			session.getTransaction().commit();
			session.close();
		}
		Session session2 = HibernateUtil.openSession();
		session2.beginTransaction();
		Node reNode = (Node) session2.get(Node.class,2);
		assertFalse(reNode.getCopies().isEmpty());
		session2.close();
	}
	
	@After 
	public void after() {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.createSQLQuery("Delete from copies").executeUpdate();
		session.getTransaction().commit();
		session.close();
			
	}
}
