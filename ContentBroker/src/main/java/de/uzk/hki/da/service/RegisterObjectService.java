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

package de.uzk.hki.da.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.db.CentralDatabaseDAO;
import de.uzk.hki.da.db.HibernateUtil;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.utils.Utilities;


/**
 * Registers objects at a certain node.
 * 
 * Checks if package is a delta. Generates an object identifier and creates new object entry in case it is not a delta. 
 * @author Daniel M. de Oliveira
 * @author Thomas Kleinke
 *
 */
public class RegisterObjectService {

	/** The Constant logger. */
	static final Logger logger = LoggerFactory.getLogger(RegisterObjectService.class);

	/** The ops. */
	private CentralDatabaseDAO dao;	
	
	/** The name space. */
	private String nameSpace;
	
	/** The zone. */
	private String zone;	

	private Node localNode;
	
	/**
	 * Register object.
	 *
	 * @param job the job
	 * @param localNode the local node
	 */
	public Object registerObject(String origName,String containerName,Contractor contractor) {
		if (contractor==null) throw new ConfigurationException("Contractor not set up correctly, check the corresponding entry in the contractors table!");
		Object obj = getObject(origName,contractor.getShort_name());
		if (obj != null) { // is delta then

			List<Package> packs = obj.getPackages();

			Package newPkg = new Package();
			newPkg.setName(generateNewPackageName(packs));
			newPkg.setContainerName(containerName);

			logger.info("Package is a delta record for Object with identifier: "+obj.getIdentifier());
			obj.getPackages().add(newPkg);
			
			Session session = HibernateUtil.openSession();
			session.beginTransaction();
			session.update(obj);
			session.getTransaction().commit();
			session.close();

		}else{		
			String identifier;
			/**
			 * Side effects: increments counter urn_index for the initial 
			 * node referenced by Job with jobId and writes job to the database.
			 */
			identifier = generateURNForNode(getLocalNode(), new Date()).replace(nameSpace + "-", "");

			logger.info("Creating new Object with identifier " + identifier);
			obj = new Object();
			
			obj.setIdentifier(identifier);

			logger.debug("Setting package name to 1.");
			
			Package newPkg = new Package();
			newPkg.setName("1");
			newPkg.setContainerName(containerName);
			obj.getPackages().add(newPkg);

			obj.setContractor(contractor);

			obj.setDate_created(String.valueOf(new Date().getTime()));
			obj.setDate_modified(String.valueOf(new Date().getTime()));
			obj.setLast_checked(new Date());
			obj.setInitial_node(localNode.getName());
			obj.setOrig_name(origName);
			obj.setZone(zone);
			
			Session session = HibernateUtil.openSession();
			session.beginTransaction();
			session.save(obj);
			session.getTransaction().commit();
			session.close();
		}
		return obj;
	}

	/**
	 * Gets the object.
	 *
	 * @param job the job
	 * @param localNode the local node
	 * @return null if not found.
	 * @author Daniel M. de Oliveira
	 * @author Thomas Kleinke
	 */
	private Object getObject(String origName,String contractorShortName) {

		Session session = HibernateUtil.openSession();
		session.getTransaction().begin();
		Object object = getDao().getUniqueObject(session,origName, contractorShortName);
		session.close();
		
		if (object != null) {
			for (Package p:object.getPackages()){
//				XXX p.setLocalNode(localNode);
			}
		}

		return object;
	}

	/**
	 * If the Packages contains names like 1,2 the newly created name will be 3.
	 * XXX Sort of a hack because not yet safely implemented due to unclear naming conventions at this moment
	 *
	 * @param packs the packs
	 * @return the string
	 */
	private String generateNewPackageName(List<Package> packs){

		List<String> names = new ArrayList<String>();
		for (Package pkg:packs) names.add(pkg.getName());

		Collections.sort(names);
		Collections.reverse(names);

		String max = names.get(0);
		return Integer.toString(Integer.parseInt( max )+1);		
	}

	/**
	 * To ensure we never generate a URN twice this method is synchronized. Since
	 * we know that every node has a special character in the urn which decodes the node
	 * (-1-,-2-,...) we can guarantee the newly generated URN is unique.
	 *
	 * @param node the node
	 * @param date the date
	 * @return the string
	 * @author Daniel M. de Oliveira
	 */
	public synchronized String generateURNForNode(Node node, Date date){
		Session session = HibernateUtil.openSession();
		session.getTransaction().begin();
		session.refresh(node);

		if (node.getUrn_index() < 0)
			throw new IllegalStateException("Node's urn_index must not be null");
		node.setUrn_index(node.getUrn_index() + 1);

		session.merge(node); // we must ensure that the transaction is commited within this method
		session.getTransaction().commit();
		session.close();
		
		String base = nameSpace+"-"
				+ node.getId()+"-"
				+ Utilities.todayAsSimpleIsoDate(date)
				+ node.getUrn_index();

		return base + (new URNCheckDigitGenerator()).checkDigit( base );
	}

	/**
	 * Sets the name space.
	 *
	 * @param nameSpace the new name space
	 */
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	/**
	 * Sets the zone.
	 *
	 * @param zone the new zone
	 */
	public void setZone(String zone) {
		this.zone = zone;
	}


	public Node getLocalNode() {
		return localNode;
	}

	public void setLocalNode(Node localNode) {
		this.localNode = localNode;
	}

	public CentralDatabaseDAO getDao() {
		return dao;
	}

	public void setDao(CentralDatabaseDAO dao) {
		this.dao = dao;
	}
}
