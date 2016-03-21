package de.uzk.hki.da.metadata;

import static org.junit.Assert.*;
import groovy.xml.Entity;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.*;

import de.uzk.hki.da.model.PremisDAFile;
import de.uzk.hki.da.model.ObjectPremisXmlReader;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.PremisEvent;

public class PremisTest {
	
	@Test
	public void premisParsen() throws IOException, ParseException {
		ObjectPremisXmlReader premis = new ObjectPremisXmlReader();
		Object o = premis.deserialize(new File("src/test/resources/metadata/premistest.xml"));
		
		Package p = o.getPackages().get(0);
		
		EntityManager em = Persistence.createEntityManagerFactory("test").createEntityManager();
		em.getTransaction().begin();
		
		PremisEvent pe = new PremisEvent();
		pe.setIdentifier("1-2016022925");
		pe.setType("CONVERT");
		pe.setDetail("convert +compress /ci/storage/WorkArea/work/TEST/1-20160212723/data/2016_02_12+14_34_31+a/CCITT_2.TIF /ci/storage/WorkArea/work/TEST/1-20160212723/data/2016_02_12+14_34_31+b/CCITT_2.TIF");
		Date d = new Date("2016-02-12T14:34:36.114+01:00");
		pe.setDate(d);
		pe.setAgent_name("localnode");
		pe.setAgent_type("NODE_NAME");
		PremisDAFile source = new PremisDAFile("2016_02_29+11_39_44+a", "CCITT_2.TIF");
		PremisDAFile target = new PremisDAFile("2016_02_29+11_39_44+b", "CCITT_2.TIF");
		//source.setConversion_instruction_id(1);
		//target.setConversion_instruction_id(2);
		//pe.setSource_file(source);
		//pe.setTarget_file(target);
		
		
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			
		}
		
		em.persist(pe);
		em.getTransaction().commit();
		
		PremisEvent pe1 = em.find(PremisEvent.class, pe.getIdentifier());
		
		assertEquals(pe.getDetail(), pe1.getDetail());
		
		System.out.println(p.getId());
		System.out.println(p.getChecksum());
		System.out.println(p.getEvents().get(0).getIdentifier());
		
		assertTrue(true);
		em.close();
	}
}
