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

import org.apache.commons.io.FilenameUtils;
import org.hibernate.UnresolvableObjectException;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.utils.Utilities;


/**
 * Registers objects at a certain node.
 * 
 * @author Daniel M. de Oliveira
 * @author Thomas Kleinke
 *
 */
public class RegisterObjectService {

	static final Logger logger = LoggerFactory.getLogger(RegisterObjectService.class);

	private CentralDatabaseDAO dao;	
	
	/** The name space. */
	private String nameSpace;
	
	private Node localNode;
	
	/**
	 * @throws new IllegalStateException if there exists no db entry for localNode or its urn_index is < 0.
	 * @author Daniel M. de Oliveira
	 */
	public void init(){
		
		Session session = HibernateUtil.openSession();
		session.getTransaction().begin();
		try {
			session.refresh(localNode);
		} catch (UnresolvableObjectException e){
			throw new IllegalStateException("Node "+localNode.getId()+"does not exist in db");
		}
		if (localNode.getUrn_index() < 0)
			throw new IllegalStateException("Node's urn_index must not be lower than 0");
	}
	
	
	
	/**
	 * 
	 * Checks if a container is a new object to the system or 
	 * if it is a delta to an existing object. Depending on the outcome,
	 * either an object with a new technical identifier and a package 
	 * get created or just a new package which
	 * will be attached to an existing object.
	 * When generating new a technical identifiers, 
	 * the urn_index of localNode gets incremented and 
	 * written back to the db immediately.
	 * 
	 * As a side effect sets the objects state always to 50, even if it already exists.
	 *
	 * @param containerName the file name of the container
	 * @param contractor the contractor who owns the container
	 */
	public Object registerObject(String containerName,Contractor contractor){
		
		if (contractor==null) 
			throw new ConfigurationException("contractor is null");
		if (contractor.getShort_name()==null||contractor.getShort_name().isEmpty())
			throw new ConfigurationException("contractor short name not set");
		if (localNode==null)
			throw new ConfigurationException("localNode is null");
		
		String origName = convertMaskedSlashes(FilenameUtils.removeExtension(containerName));

		Object obj = getObject(origName,contractor.getShort_name());
		if (obj != null) { // is delta then

			List<Package> packs = obj.getPackages();

			Package newPkg = new Package();
			newPkg.setName(generateNewPackageName(packs));
			newPkg.setContainerName(containerName);

			logger.info("Package is a delta record for Object with identifier: "+obj.getIdentifier());
			obj.getPackages().add(newPkg);
			obj.setObject_state(50);
			
			Session session = HibernateUtil.openSession();
			session.beginTransaction();
			session.update(obj);
			session.getTransaction().commit();
			session.close();

		}else{		
			String identifier;

			identifier = convertURNtoTechnicalIdentifier(generateURNForNode());

			logger.info("Creating new Object with identifier " + identifier);
			obj = new Object();
			obj.setObject_state(40);
			
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
			
			Session session = HibernateUtil.openSession();
			session.beginTransaction();
			session.save(obj);
			session.getTransaction().commit();
			session.close();
		}
		return obj;
	}

	
	/**
	 * Replaces %2F inside a string to /.
	 *
	 * @param input the input
	 * @return the string
	 */
	private String convertMaskedSlashes(String input){
		return input.replaceAll("%2F", "/");
	}

	/**
	 * @author Daniel M. de Oliveira
	 * @param urn
	 * @return
	 */
	private String convertURNtoTechnicalIdentifier(String urn) {
		
		return urn.replace(nameSpace + "-", "");
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
		Object object = dao.getUniqueObject(session,origName, contractorShortName);
		session.close();
		return object;
	}

	/**
	 * If the Packages contains names like 1,2 the newly created name will be 3.
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
	 * Generates a URN of the form [nameSpace]-[node_id]-[number].
	 * The generated number is ensured to be unique per node_id 
	 * (the system never generates the same
	 * number twice for any given [nodeId] across the database).
	 * Increments the urn_index of node and writes it back to the 
	 * database immediately on every call.
	 * 
	 * @return the generated URN.
	 * @author Daniel M. de Oliveira
	 */
	private synchronized String generateURNForNode(){
		// Must be synchronized to block other processes from 
		// fetching and incrementing the same urn_index, upon which [number] is based.
		
		String base = nameSpace+"-"
				+ localNode.getId()+"-"
				+ Utilities.todayAsSimpleIsoDate(new Date())
				+ incrementURNindex();

		return base + (new URNCheckDigitGenerator()).checkDigit( base );
	}

	
	
	
	private int incrementURNindex(){
		
		Session session = HibernateUtil.openSession();
		session.getTransaction().begin();
		session.refresh(localNode);
		localNode.setUrn_index(localNode.getUrn_index() + 1);
		session.merge(localNode);
		session.getTransaction().commit();
		session.close();
		
		return localNode.getUrn_index();
	}
	
	/**
	 * Sets the name space.
	 *
	 * @param nameSpace the new name space
	 */
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}


	public void setLocalNode(Node localNode) {
		if (localNode==null) 
			throw new IllegalArgumentException("localNode is null");
		this.localNode = localNode;
	}

	public void setDao(CentralDatabaseDAO dao) {
		this.dao = dao;
	}
}
