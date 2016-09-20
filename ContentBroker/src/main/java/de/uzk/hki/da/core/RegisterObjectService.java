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

package de.uzk.hki.da.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.Session;
import org.hibernate.UnresolvableObjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.ObjectNamedQueryDAO;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.StringUtilities;


/**
 * Provides facility to register objects at a certain node.
 * 
 * This object is intended to be wired up as a Spring bean and as a singleton. It should get created once and then 
 * the localNode and preservationSystem ids have to be set. Then it should get initialized via the init method. 
 * Only then registerObject should be called.
 * 
 * @author Daniel M. de Oliveira
 * @author Thomas Kleinke
 *
 */
public class RegisterObjectService {

	private static final Logger logger = LoggerFactory.getLogger(RegisterObjectService.class);

	private String localNodeName;
	private String urnNameSpace;
	
	private int localNodeId;
	private int preservationSystemId;
	
	private static boolean singletonInstanceCreated = false;
	private static boolean initialized = false;
	
	/**
	 * 
	 */
	public RegisterObjectService(){
		if (singletonInstanceCreated) throw new IllegalStateException("Will not instantiate a second instance.");
		singletonInstanceCreated = true;
	}
	
	/**
	 * Init method for getting wired up by Spring.
	 */
	public void init(){
		Session session = HibernateUtil.openSession();
		session.getTransaction().begin();
		Node node=null;
		PreservationSystem pSystem=null;
		try {
			pSystem = (PreservationSystem) session.get(PreservationSystem.class,preservationSystemId);
			node = (Node) session.get(Node.class,localNodeId);
		} catch (UnresolvableObjectException e){
			throw new IllegalStateException(e);
		}
		if (node.getUrn_index() < 0)
			throw new IllegalStateException("Node's urn_index must not be lower than 0");
		urnNameSpace=pSystem.getUrnNameSpace();
		localNodeName=node.getName();
		session.close();
		initialized=true;
	}
	
	
	/**
	 * Compares the csn/origName pair of a SIP which identifies an object uniquely against the database.
	 * If there is a match, the SIP is considered a delta. If not is considered a primary import.
	 * <br><br>
	 * If the SIP is a delta, a new package gets created and attached to the object (which gets fetched from db).
	 * If it is a primary import, a new object gets created. In this case a new technical identifier gets created, which
	 * is base on the nodes urn index and the preservation systems urnNameSpace. The nodes urn index gets incremented when
	 * generating the identifier. The database record gets updated. For security reasons the the object database gets checked of
	 * the identifier does not already exist.
	 *
	 * @param containerName the file name of the SIP container
	 * @param contractor the contractor who owns the container
	 * 
	 * @throws UserException when trying to register a delta record for an object which is not archived (<50) yet
	 * @throws IllegalStateException if the system tries to generate an identifier for which an object already exists. 
	 * 
	 * @return the object.
	 */
	public Object registerObject(String containerName,User contractor){
		if (!initialized) throw new IllegalStateException("call init first");
		if (contractor==null) 
			throw new IllegalArgumentException("contractor is null");
		if (contractor.getShort_name()==null||contractor.getShort_name().isEmpty())
			throw new IllegalArgumentException("contractor short name not set");
		
		String origName = convertMaskedSlashes(FilenameUtils.removeExtension(containerName));

		Object obj;
		if ((obj=(new ObjectNamedQueryDAO().getUniqueObject(origName,contractor.getShort_name()))) 
			!= null) { // is delta then

			logger.info("Package is a delta record for Object with identifier: "+obj.getIdentifier());
			updateExistingObject(obj, containerName);
		}else{
			final String technicalIdentifier = convertURNtoTechnicalIdentifier(generateURNForNode(localNodeId));
			
			// check identifier
			if (getUniqueObject(technicalIdentifier)!=null) throw new IllegalStateException("CRITICAL SYSTEM ERROR: DUPLICATE IDENTIFIER");
			
			logger.info("Creating new Object with identifier " + technicalIdentifier);
			obj = createNewObject(containerName,origName,contractor);
			obj.setIdentifier(technicalIdentifier);
		}
		return obj;
	}

	
	private void updateExistingObject(Object obj,String containerName){
		
		List<Package> packs = obj.getPackages();

		Package newPkg = new Package();
		newPkg.setName(generateNewPackageName(packs));
		newPkg.setContainerName(containerName);
		if (obj.getObject_state()<50) throw new UserException(UserExceptionId.DELTA_RECIEVED_BEFORE_ARCHIVED, "Delta Record für ein nicht fertig archiviertes Objekt");
		obj.getPackages().add(newPkg);
	}
	
	
	
	private Object createNewObject(String containerName,String origName,User contractor) {
		
		Object obj = new Object();
		obj.setObject_state(Object.ObjectStatus.InitState);
		
		Package newPkg = new Package();
		newPkg.setName("1");
		newPkg.setContainerName(containerName);
		obj.getPackages().add(newPkg);
		
		obj.setContractor(contractor);
		
		obj.setDate_created(String.valueOf(new Date().getTime()));
		obj.setDate_modified(String.valueOf(new Date().getTime()));
		obj.setLast_checked(new Date());
		obj.setInitial_node(localNodeName);
		obj.setOrig_name(origName);
		
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
	 * @param urn
	 * @return
	 */
	private String convertURNtoTechnicalIdentifier(String urn) {
		
		return urn.replace(urnNameSpace + "-", "");
	}

	private Object getUniqueObject(String identifier) {
		Session session = HibernateUtil.openSession();
		session.getTransaction().begin();
		
		@SuppressWarnings("rawtypes")
		List l = null;
	
			l = session.createQuery("from Object where identifier=?1")
							.setParameter("1", identifier)
						.list();
		session.close();
		try {
			l.get(0);
		}catch(IndexOutOfBoundsException e)
		{
			return null;
		}
		return (Object) l.get(0);
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
	 * 
	 * @return the generated URN.
	 * @author Daniel M. de Oliveira
	 */
	private String generateURNForNode(int nodeId){
		
		String base = urnNameSpace+"-"
				+ nodeId+"-"
				+ StringUtilities.todayAsSimpleIsoDate(new Date())
				+ incrementURNindex(nodeId);

		return base + (new URNCheckDigitGenerator()).checkDigit( base );
	}

	
	
	/**
	 * Increments the urn_index of node and writes it back to the 
	 * database immediately on every call.
	 *
	 * The generated number is ensured to be unique per node_id 
	 * (the system never generates the same
	 * number twice for any given [nodeId] across the database).
	 *
	 * @return the new value of the urn index for the node with the id node id. 
	 */
	private 
	synchronized // only one thread per node is allowed to increment the nodes urn index. 
	int incrementURNindex(int nodeId){
		Session session = HibernateUtil.openSession();
		session.getTransaction().begin();
		
		Node node = // making sure to work with a local copy of node object to prevent it from being modified by other threads. 
				(Node) session.get(Node.class,nodeId); 

		int incrementedURNIndex = node.getUrn_index()+1;
		logger.debug("Updating local node urn index "+node.getUrn_index()+" to "+incrementedURNIndex);

		session.update(node); // further changes are tracked. 
		node.setUrn_index(incrementedURNIndex);
		session.getTransaction().commit();
		session.close();
		
		if (incrementedURNIndex!=node.getUrn_index()){ 
			throw new RuntimeException("SERIOUS TROUBLE. It seems the database has not been updated properly (value:"+node.getUrn_index()+")");
		}
		
		return incrementedURNIndex;
	}

	public String getLocalNodeId() {
		return new Integer(localNodeId).toString();
	}

	public void setLocalNodeId(String localNodeId) {
		this.localNodeId = Integer.parseInt(localNodeId);
	}

	public String getPreservationSystemId() {
		return new Integer(preservationSystemId).toString();
	}

	public void setPreservationSystemId(String preservationSystemId) {
		this.preservationSystemId = Integer.parseInt(preservationSystemId);
	}
}
