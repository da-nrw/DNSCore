package de.uzk.hki.da.events;

import static org.junit.Assert.*;

import java.io.IOException;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.Copy;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.SystemEvent;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.util.Path;

public class SystemEventFactoryTest {

	Node node = null;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	@Before
	public void before() throws IOException {
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		node = new Node();
		node.setUserAreaRootPath(Path.make("tmp"));
		User user = new User();
		user.setShort_name("TEST");

		SystemEvent se = new SystemEvent();
		se.setOwner(user);
		se.setNode(node);
		se.setType("CreateStatusReportEvent");
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.save(user);
		session.save(node);
		session.save(se);
		
		session.getTransaction().commit();
		session.close();  
	}

	@Test
	public void test() {
		SystemEventFactory se = new SystemEventFactory();
		se.setLocalNode(node);
		se.buildStoredEvents();
	}

}
