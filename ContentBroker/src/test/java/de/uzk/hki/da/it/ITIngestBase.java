/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln

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

package de.uzk.hki.da.it;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.hibernate.Session;

import de.uzk.hki.da.db.HibernateUtil;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;


/**
 * Common Base of integration tests ingest and ingestdelta.
 *
 * @author Daniel M. de Oliveira
 */
public abstract class ITIngestBase extends ITBase{
	
	/** The job. */
	protected static Job job = null;
	
	/** The object. */
	protected static Object object = null;
	
	/**
	 * Like the IngestAreaScannerWorker does.
	 *
	 * @param packagename the packagename
	 * @return the job
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected Job prepareSIPAndInsertJobAndObject(String packagename) throws IOException{
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		
		String suffix=".tgz";
		if (packagename.endsWith(".zip")) {
			packagename=packagename.substring(0, packagename.length()-4);
			suffix=".zip";
		}
		if (packagename.endsWith(".tar")) {
			packagename=packagename.substring(0, packagename.length()-4);
			suffix=".tar";
		}
		
		System.out.println("preparing sip and inserting job for: "+packagename);
		
		Contractor c = new Contractor();
		c.setShort_name("TEST"); // XXX why the hell do I now have to do that?
		c.setId(1); //TEST
		
		Package p = new Package();
		p.setName("1");
		p.setContainerName(packagename + suffix);
		object = new Object();
		object.getPackages().add(p);
		object.setUrn("it:ingest:urn"); // TODO test RegisterURNAction
		object.setIdentifier("it+ingestBase+identifier");
		object.setContractor(c);
		object.setOrig_name(packagename);
		object.setInitial_node(nameOfOurIntegrationTestNode);
		
		Job job = new Job();
//		job.setOrig_name(packagename);
		job.setInitial_node("da-nrw-vm3.hki.uni-koeln.de");
		job.setStatus("110");
		job.setObject(object);

		FileUtils.copyFile(new File("/data/danrw/testPackages/"+packagename+suffix),
				new File("/tmp/"+packagename+suffix));
		FileUtils.moveFile(new File("/tmp/"+packagename+suffix), new File("/data/danrw/ingest/TEST/"+packagename+suffix));
		new File("/tmp/"+packagename+suffix).delete();
		
		session.save(job);
		session.getTransaction().commit();
		session.close();
		return job;
	}
	
	
	

	/**
	 * Step through ingest workflow.
	 *
	 * @author Daniel M. de Oliveira
	 */
	protected void stepThroughIngestWorkflow() {
		connectAndRunAction("IngestUnpackAction");
		connectAndRunAction("IngestScanAction");
		connectAndRunAction("IngestRegisterURNAction");
		connectAndRunAction("IngestConvertAction");
		connectAndRunAction("IngestUpdateMetadataAction");
		connectAndRunAction("IngestCheckFormatsAction");
		connectAndRunAction("IngestCreatePremisAction");
		connectAndRunAction("IngestScanForPresentationAction");
		connectAndRunAction("IngestConvertForPresentationAction");
		connectAndRunAction("IngestPreprocessForPresentationAction");
		connectAndRunAction("IngestShortenFileNamesAction");
		connectAndRunAction("IngestPresUpdateMetadataAction");
		connectAndRunAction("IngestPrepareSendToPresenterAction");
		connectAndRunAction("IngestBuildAIPAction");
		connectAndRunAction("IngestTarAction");
		connectAndRunAction("IngestArchiveReplicationAction");
		do{
			job = connectAndRunAction("IngestArchiveReplicationCheckAction");
			System.out.println("Job: "+job);
		}
		while (!job.getStatus().equals("500"));
	}

}
