package de.uzk.hki.da.grid;

import java.util.Date;

import org.hibernate.Session;

import de.uzk.hki.da.model.CopyJob;
import de.uzk.hki.da.service.HibernateUtil;

public class CreateCopyJob {
	
	public void createCopyJob(String sourceDao, String dest_node_identifier, String source_node_identifier, String params){	
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		CopyJob cj = new CopyJob();
		cj.setParams(params);
		cj.setSource(sourceDao);
		cj.setDest_name(dest_node_identifier);
		cj.setLast_tried(new Date());
		cj.setSource_name(source_node_identifier);
		session.save(cj);
		session.getTransaction().commit();
		session.close();
	}
}
