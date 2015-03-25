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

/**
 * The package integrity.
 */
package de.uzk.hki.da.core;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.MDC;

import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.grid.IrodsCommandLineConnector;
import de.uzk.hki.da.grid.IrodsSystemConnector;
import de.uzk.hki.da.model.Copy;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.service.HibernateUtil;


/**
 * Computes Checksum on secondary 
 * copies
 * @author Jens Peters
 *
 */
public class ChecksumWorker extends Worker{

	/** The local node id. */
	private int localNodeId;
	
	private PreservationSystem pSystem;
	
	/** The irods grid connector. */
	private GridFacade gridFacade;
	
	private String secondaryCopyPrefix;
	
	private Node node;
	
	public void init(){
		node = new Node(); 
		node.setId(localNodeId);
		setpSystem(new PreservationSystem()); getPSystem().setId(1);
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.refresh(getPSystem());
		session.refresh(node);
		session.getTransaction().commit();
		session.close();
	}
	
	@Override
	public void setMDC() {
		MDC.put(WORKER_ID, "checksum");
	} 
	
	
	/**
	 * Computing Checksum for Copies
	 * @author Jens Peters
	 */
	@Override
	public void scheduleTaskImplementation(){
		logger.trace("Computing Checksum for Node-ID : " + node.getId()  );
		try {
			
			Copy copy = null;
			if ((copy=fetchCopy(node.getId()))==null) { 
				logger.warn("Found no secondary copy to compute Checksum for.") ;
				return;
			}
			if (secondaryCopyPrefix==null) {
				logger.error("SecondaryCopyPrefix is null");
				return;
			}
			String dest = secondaryCopyPrefix + "/"+ copy.getPath();
			
			if (gridFacade.exists(dest)){
				updateCopy(copy, gridFacade.reComputeAndGetChecksumInCustody(dest));
			} else logger.info(dest + " does not yet exist.");
			
		} catch (Exception e) {
			logger.error("Error in integrityCheck schedule Task " + e.getMessage(),e);
		}
	}
	
	/** 
	 * 
	 * @return the next copy
	 * @author Jens Peters
	 */
	private synchronized Copy fetchCopy(int localNodeId) {
		
		Session session = null;
		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();
			@SuppressWarnings("rawtypes")
			List l = null;
			l = session.createSQLQuery("select id from copies c where c.node_id = ?1 "
			+ "order by c.checksumDate asc")
					.setParameter("1", localNodeId)
							.setReadOnly(true).list();
	         
			System.out.println("Hallo " + l.get(0));
			@SuppressWarnings("rawtypes")
			List k = null;
			k = session.createQuery("from Copy c where c.id = ?1 ").setParameter("1",l.get(0))
					.setReadOnly(true).list();;

			
			Copy copy = (Copy)k.get(0);
			session.close();
			
			return copy;
		
		} catch (IndexOutOfBoundsException e){
			if (session!=null) session.close();
			return null;
		}
	}
	

	public String getSecondaryCopyPrefix() {
		return secondaryCopyPrefix;
	}

	public void setSecondaryCopyPrefix(String secondaryCopyPrefix) {
		this.secondaryCopyPrefix = secondaryCopyPrefix;
	}

	/**
	 * Updates the Copy with found checksum
	 * @param copy
	 * @param checksum
	 * 	
	 *  */
	private synchronized void updateCopy(Copy copy, String checksum ){
		
		copy.setChecksumDate(new Date());
		copy.setChecksum(checksum);
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.update(copy);
		session.getTransaction().commit();
		session.close();
	}
	

	
	public int getLocalNodeId() {
		return localNodeId;
	}

	public void setLocalNodeId(int i) {
		this.localNodeId = i;
	}


	public PreservationSystem getPSystem() {
		return pSystem;
	}


	public void setpSystem(PreservationSystem pSystem) {
		this.pSystem = pSystem;
	}
	/**
	 * For testing purposes only
	 * @param node
	 */
	public void setNode(Node node){
		this.node = node;
	}

	public GridFacade getGridFacade() {
		return gridFacade;
	}

	public void setGridFacade(GridFacade gridFacade) {
		this.gridFacade = gridFacade;
	}

}
