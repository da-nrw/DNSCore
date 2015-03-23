package de.uzk.hki.da.model;

import static org.junit.Assert.*;

import org.hibernate.Hibernate;
import org.hibernate.Session;
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
	public void test() {
		assertFalse(n.getCooperatingNodes().isEmpty());
		Node cn =
				n.getCooperatingNodes().iterator().next();
		assertEquals("cooperatingnode", cn.getName());
	}

}
