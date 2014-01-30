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

package de.uzk.hki.da.cb;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.hibernate.classic.Session;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.db.HibernateUtil;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;

public class CleanWorkAreaAction extends AbstractAction{

	private DistributedConversionAdapter distributedConversionAdapter;
	private Node dipNode;
	
	
	@Override
	boolean implementation() {
		if (distributedConversionAdapter==null) throw new ConfigurationException("distributedConversionAdapter not set");
		setKILLATEXIT(true);
		
		// to prevent leftover files from irods collection removal we delete the dirs on the filesystem first.
		try {
			FileUtils.deleteDirectory(new File(object.getPath()));
		} catch (IOException e) {
			throw new RuntimeException("Exception while deleting \""+
					object.getPath()+"\"",e);
		}
		
		distributedConversionAdapter.remove(object.getContractor().getShort_name()+"/"+object.getIdentifier());
		
		object.getLatestPackage().getFiles().clear();
		object.getLatestPackage().getEvents().clear();
		
		createPublicationJob();
		return true;
	}
	
	/**
	 * XXX code duplicated from archivereplicationcheckaction
	 * @author Daniel M. de Oliveira Jens Peters
	 */
	private void createPublicationJob(){
		
		logger.info("Creating child job with state 540 on "+   getDipNode().getName()+" for possible publication of this object.");
		Job child = new Job (job, "540");
		child.setInitial_node(getDipNode().getName());
		child.setObject(getObject());
		child.setDate_created(String.valueOf(new Date().getTime()/1000L));
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.save(child);
		session.getTransaction().commit();
		session.close();
	}

	
	@Override
	void rollback() throws Exception {}


	public DistributedConversionAdapter getDistributedConversionAdapter() {
		return distributedConversionAdapter;
	}


	public void setDistributedConversionAdapter(
			DistributedConversionAdapter distributedConversionAdapter) {
		this.distributedConversionAdapter = distributedConversionAdapter;
	}

	public Node getDipNode() {
		return dipNode;
	}

	public void setDipNode(Node dipNode) {
		this.dipNode = dipNode;
	}
}
