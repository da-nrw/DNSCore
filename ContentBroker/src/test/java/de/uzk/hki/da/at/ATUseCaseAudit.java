/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln, 2014 LVR InfoKom

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uzk.hki.da.at;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.hibernate.classic.Session;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.grid.IrodsSystemConnector;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.service.Mail;


/**
 * Relates to AK-T Audit 
 * @author jpeters
 * 
 */
public class ATUseCaseAudit extends Base{
	
	private String originalName = "ATUseCaseAudit";
	private String containerName = originalName+".tgz";
	private String archiveStoragePath = "/ci/archiveStorage/aip/TEST/";
	private Object object = null;
	
	@Before
	public void setUp() throws IOException{
		setUpBase();
	}
	
	@After
	public void tearDown(){ 
		try{
			new File(localNode.getIngestAreaRootPath()+"/TEST/"+containerName).delete();
			new File("/tmp/"+object.getIdentifier()+".pack_1.tar").delete();
			FileUtils.deleteDirectory(new File("/tmp/"+object.getIdentifier()+".pack_1"));
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		clearDB();
		//cleanStorage();
	}
	
	@Test
	public void testHappyPath() throws Exception {
		ingest(originalName);
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		object = dao.getUniqueObject(session, originalName, "TEST");
		session.close();
		// We'll destroy it now, if we 're on CI
		// on dev machines FakeGridFacade will find special file in ATUseCaseAudit
		if (System.getProperty("env").equals("ci"))
		destroyFileInCIEnvironment(object.getIdentifier());
		assertTrue(waitForObjectInStatus(51));
	}
		
	private void destroyFileInCIEnvironment(String identifier) {
		
		
		String filename = archiveStoragePath + "TEST/" + identifier + "/"+identifier+".pack_1.tar";
		File file = new File(filename);
		if (!file.exists()) {	
			fail(filename  + " does not exist!" );
		}
		Writer writer = null;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(filename), "utf-8"));
		    writer.write("Something");
		} catch (IOException ex) {
		  fail("writing to file");
		} finally {
		   try {writer.close();} catch (Exception ex) { fail();}
		}
		
	}

	private boolean waitForObjectInStatus(int status) throws InterruptedException {	
		while (true){
			Thread.sleep(6000);
			Session session = HibernateUtil.openSession();
			session.beginTransaction();
			Object object = dao.getUniqueObject(session, originalName, "TEST");
			session.close();		
			if (object!=null){
				
				System.out.println("waiting for Object to be in state " + status + ". Is: "+object.getObject_state() + " last checked " + object.getLast_checked() );
				if (object.getObject_state()==status){
					System.out.println("reached state : " + status );
					return true;
			}
		} else return false;
			
				
		}	
		}
}
	
	
