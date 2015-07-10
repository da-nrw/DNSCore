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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.slf4j.MDC;

import de.uzk.hki.da.grid.GridFacade;
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
	
	private int trustChecksumForDays;
	
	private int startTime;

	private int endTime;
	
	private int allowedNumOfCopyjobs;

	public int getAllowedNumOfCopyjobs() {
		return allowedNumOfCopyjobs;
	}

	public void setAllowedNumOfCopyjobs(int allowedNumOfCopyjobs) {
		this.allowedNumOfCopyjobs = allowedNumOfCopyjobs;
	}

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
		MDC.put(WORKER_ID, "integrity");
	} 
	
	
	/**
	 * Re-Computing Checksum for Copies
	 * @author Jens Peters
	 */
	@Override
	public void scheduleTaskImplementation(){
		GregorianCalendar cal = new GregorianCalendar();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int numCopyjobs = getNumCopyjobs(hour);
        
        logger.debug("number of copyjobs: current="+numCopyjobs +" & allowed="+allowedNumOfCopyjobs);
        logger.debug("current time: "+hour+"; allowed time: "+startTime+" - "+endTime+" hour");
        
        if(allowedTime(hour) || numCopyjobs<=allowedNumOfCopyjobs) {
        	try {
    			Copy copy = null;
    			if ((copy=fetchCopy(node.getId()))==null) { 
    				logger.warn("Found no copy in custody to compute Checksum for ...") ;
    				return;
    			}
    			if (secondaryCopyPrefix==null) {
    				logger.error("SecondaryCopyPrefix is null");
    				return;
    			}
    			String dest = secondaryCopyPrefix + "/"+ copy.getPath();
    			logger.info("Checking existence in custody " + dest );
    			if (gridFacade.exists(dest)){
    				Calendar oneMonthAgo = Calendar.getInstance();
    				oneMonthAgo.add(Calendar.DAY_OF_YEAR, trustChecksumForDays*(-1));
    				logger.debug("will look for Checksums older than " + oneMonthAgo.getTime() );
    				String cs = "";
    				if (copy.getChecksumDate()==null) {
    					cs = gridFacade.reComputeAndGetChecksumInCustody(dest);
    					logger.info("checksum in custody is " + cs + " for " + dest);
    				} else if (copy.getChecksumDate().before(oneMonthAgo.getTime())) {
    					cs = gridFacade.reComputeAndGetChecksumInCustody(dest);
    					logger.info("recompute old checksum in custody, now is " + cs + " for " + dest);
    				} else {
    					cs = copy.getChecksum();
    					logger.info("Checksum does not yet need recomputation. Checksum is " + cs);
    				}
    				updateCopy(copy, cs);
    				
    			} else logger.error(dest + " does not exist.");
    			
    		} catch (Exception e) {
    			logger.error("Error in ChecksumWorker " + e.getMessage(),e);
    		}
        } else {
        	logger.debug("Skipping copy checking ...");
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
			+ "order by c.checksumdate asc NULLS FIRST")
					.setParameter("1", localNodeId)
					.setReadOnly(true).list();
	         
			@SuppressWarnings("rawtypes")
			List k = null;
			k = session.createQuery("from Copy c where c.id = ?1 ").setParameter("1",l.get(0))
					.setReadOnly(true).list();
			
			Copy copy = (Copy)k.get(0);
			session.close();	
			return copy;
		
		} catch (Exception e){
			if (session!=null) session.close();
			return null;
		}
	}
	
	/** 
	 * 
	 * @return the number of copyjobs
	 * @author Polina Gubaidullina
	 */
	private int getNumCopyjobs(int localNodeId) {
		int numCopyJobs = 0;
		Session session = null;
		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();
			@SuppressWarnings("rawtypes")
			List l = null;
			l = session.createSQLQuery("select count(id) as count from copyjob;")
					.addScalar("count", IntegerType.INSTANCE)
					.list();
			numCopyJobs = (Integer) l.get(0);
			session.close();
		} catch (Exception e){
			if (session!=null) session.close();
		}
		return numCopyJobs;
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
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		copy.setChecksumDate(new Date());
		copy.setChecksum(checksum);
		session.update(copy);
		session.getTransaction().commit();
		session.close();
	}
	
	
	private boolean allowedTime(int currentHour) {
		if(currentHour>=getStartTime() && currentHour<=getEndTime()) {
			return true;
		} else return false;
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

	public int getTrustChecksumForDays() {
		return trustChecksumForDays;
	}

	public void setTrustChecksumForDays(int trustChecksumForDays) {
		this.trustChecksumForDays = trustChecksumForDays;
	}
	
	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int trustStartTime) {
		this.startTime = trustStartTime;
	}
	
	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int trustEndTime) {
		this.endTime = trustEndTime;
	}

}
