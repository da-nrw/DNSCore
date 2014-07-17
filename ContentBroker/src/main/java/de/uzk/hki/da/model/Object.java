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

package de.uzk.hki.da.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.PublicationRight.Audience;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.Utilities;


/**
 * The Class Object.
 *
 * @author Jens Peters
 * @author Daniel M. de Oliveira
 */
@Entity
@Table(name="objects")
public class Object {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(Object.class);
	
	/** The data_pk. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int data_pk;

	/** 
	 * The identifier. In javadoc comments throughout the source code base often refered to as oid.
	 * */
	private String identifier;
	
	/** The urn. */
	private String urn;
	
	/** The initial_node. */
	private String initial_node;
	
	/** The orig_name. */
	private String orig_name;
	
	/** The date_created. */
	private String date_created;
	
	/** The date_modified. */
	private String date_modified;
	
	private String package_type;
	
	private String metadata_file;
	
	/** The zone. */
	private String zone;
	
	/** The published_flag. */
	private int published_flag;
	
	/** The object_state. 
	 *
	 * 100: archived and valid
	 * 51: corrupt
	 * 50: in workflow
	 * 60: under integrity check
	 */
	private int object_state;
	
	/** The last_checked. */
	private Date last_checked;
	
	/** The static_nondisclosure_limit. */
	private Date static_nondisclosure_limit;
	
	/** The dynamic_nondisclosure_limit. */
	private String dynamic_nondisclosure_limit;
	
	/**
	 * Should be a member of rightsstatement. Also in PREMIS we serialize it that way.
	 * But here we wanted to transport the right through the db without adding tables for rightsstatement.
	 */
	@Column(name="ddb_exclusion")
	private Boolean ddbExclusion = false;
	
	
	/** The original_formats. */
	@Column(name="original_formats")
	private String originalFormats;
	
	/** The most_recent_formats. */
	@Column(name="most_recent_formats")
	private String mostRecentFormats;
	
	/** The most_recent_secondary_attributes. */
	@Column(name="most_recent_secondary_attributes")
	private String mostRecentSecondaryAttributes = "";
	
	@Transient
	private Node transientNodeRef;
	
	/** The rights. */
	@Transient
	private RightsStatement rights = new RightsStatement();
	
	/** The agents. */
	@Transient
	private Set<Agent> agents = new HashSet<Agent>();
	
	/** The contractor. */
	@ManyToOne
	@JoinColumn(name = "contractor_id")
	private Contractor contractor;
	 
	/** The packages. */
	@OneToMany(targetEntity=Package.class, fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private List<Package> packages = new ArrayList<Package>();
	
	/**
	 * Sets the data_pk.
	 *
	 * @param data_pk the new data_pk
	 */
	public void setData_pk(int data_pk) {
		this.data_pk = data_pk;
	}
	
	/**
	 * Gets the data_pk.
	 *
	 * @return the data_pk
	 */
	public int getData_pk() {
		return data_pk;
	}	
	
	/**
	 * Sets the packages.
	 *
	 * @param packages the new packages
	 */
	public void setPackages(List<Package> packages) {
		this.packages = packages;
	}	
	
	/**
	 * Gets the packages.
	 *
	 * @return the packages
	 */
	public List<Package> getPackages() {
		return this.packages;
	}	
	
	/**
	 * Sets the identifier.
	 *
	 * @param identifier the new identifier
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	/**
	 * Gets the identifier.
	 *
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/**
	 * Gets the urn.
	 *
	 * @return the urn
	 */
	public String getUrn() {
		return urn;
	}
	
	/**
	 * Sets the urn.
	 *
	 * @param urn the new urn
	 */
	public void setUrn(String urn) {
		this.urn = urn;
	}
	
	/**
	 * Sets the orig_name.
	 *
	 * @param orig_name the new orig_name
	 */
	public void setOrig_name(String orig_name) {
		this.orig_name = orig_name;
	}
	
	/**
	 * Gets the orig_name.
	 *
	 * @return the orig_name
	 */
	public String getOrig_name() {
		return orig_name;
	}
	
	/**
	 * Sets the initial_node.
	 *
	 * @param initial_node the new initial_node
	 */
	public void setInitial_node(String initial_node) {
		this.initial_node = initial_node;
	}
	
	/**
	 * Gets the initial_node.
	 *
	 * @return the initial_node
	 */
	public String getInitial_node() {
		return initial_node;
	}
	
	/**
	 * Sets the date_created.
	 *
	 * @param date_created the new date_created
	 */
	public void setDate_created(String date_created) {
		this.date_created = date_created;
	}
	
	/**
	 * Gets the date_created.
	 *
	 * @return the date_created
	 */
	public String getDate_created() {
		return date_created;
	}
	
	/**
	 * Sets the date_modified.
	 *
	 * @param date_modified the new date_modified
	 */
	public void setDate_modified(String date_modified) {
		this.date_modified = date_modified;
	}
	
	/**
	 * Gets the date_modified.
	 *
	 * @return the date_modified
	 */
	public String getDate_modified() {
		return date_modified;
	}
	
	/**
	 * @return physical path to package's data path on local node's working resource
	 * @throws IllegalStateException if reference to particular node is not set.
	 * @throws IllegalStateException if workAreaRoot path of the referenced node is null or empty.
	 * @author Daniel M. de Oliveira
	 */
	public Path getDataPath(){
		return Path.make(getPath(),"data");
	}
	
	
	/**
	 * @return physical path to package on local node's working resource
	 * @throws IllegalStateException if reference to particular node is not set.
	 * @throws IllegalStateException if workAreaRoot path of the referenced node is null or empty.
	 * @author Daniel M. de Oliveira
	 */
	public Path getPath(){
		if (transientNodeRef==null) throw new IllegalStateException("Object is not related to any particular node. So the physical path cannot be calculated.");
		if (transientNodeRef.getWorkAreaRootPath()==null||transientNodeRef.getWorkAreaRootPath().toString().isEmpty()) 
			throw new IllegalStateException("WorkAreaRootPath of related object is null or empty. Physical path cannot be calculated");
		
		return Path.make(transientNodeRef.getWorkAreaRootPath(),"work",contractor.getShort_name(),identifier);
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override 
	public String toString(){
		
		
		return 
				"Object["
		+ identifier + "," + urn + "," + "," + orig_name + "," +
		contractor.getShort_name() +
						"]";
	}
	
	
	
	
	
	
	/**
	 * Sets the zone.
	 *
	 * @param zone the new zone
	 */
	public void setZone(String zone) {
		this.zone = zone;
	}
	
	/**
	 * Gets the zone.
	 *
	 * @return the zone
	 */
	public String getZone() {
		return zone;
	}
	
	/**
	 * Sets the published_flag.
	 *
	 * @param published_flag the new published_flag
	 */
	public void setPublished_flag(int published_flag) {
		this.published_flag = published_flag;
	}
	
	/**
	 * Gets the published_flag.
	 *
	 * @return the published_flag
	 */
	public int getPublished_flag() {
		return published_flag;
	}
	
	/**
	 * Sets the contractor.
	 *
	 * @param contractor the new contractor
	 */
	public void setContractor(Contractor contractor) {
		this.contractor = contractor;
	}
	
	
	/**
	 * Gets the contractor.
	 *
	 * @return the contractor
	 */
	public Contractor getContractor() {
		return contractor;
	}
	
	/**
	 * Sets the object_state.
	 *
	 * @param object_state the new object_state
	 */
	public void setObject_state(int object_state) {
		this.object_state = object_state;
	}
	
	/**
	 * Gets the object_state.
	 *
	 * @return the object_state
	 */
	public int getObject_state() {
		return object_state;
	}
	
	/**
	 * Sets the last_checked.
	 *
	 * @param last_checked the new last_checked
	 */
	public void setLast_checked(Date last_checked) {
		this.last_checked = last_checked;
	}
	
	/**
	 * Gets the last_checked.
	 *
	 * @return the last_checked
	 */
	public Date getLast_checked() {
		return last_checked;
	}
	
	/**
	 * Sets the static_nondisclosure_limit.
	 *
	 * @param static_nondisclosure_limit the new static_nondisclosure_limit
	 */
	public void setStatic_nondisclosure_limit(Date static_nondisclosure_limit) {
		this.static_nondisclosure_limit = static_nondisclosure_limit;
	}
	
	/**
	 * Gets the static_nondisclosure_limit.
	 *
	 * @return the static_nondisclosure_limit
	 */
	public Date getStatic_nondisclosure_limit() {
		return static_nondisclosure_limit;
	}
	
	/**
	 * Sets the dynamic_nondisclosure_limit.
	 *
	 * @param dynamic_nondisclosure_limit the new dynamic_nondisclosure_limit
	 */
	public void setDynamic_nondisclosure_limit(String dynamic_nondisclosure_limit) {
		this.dynamic_nondisclosure_limit = dynamic_nondisclosure_limit;
	}
	
	/**
	 * Gets the dynamic_nondisclosure_limit.
	 *
	 * @return the dynamic_nondisclosure_limit
	 */
	public String getDynamic_nondisclosure_limit() {
		return dynamic_nondisclosure_limit;
	}
	
	/**
	 * Sets the original_formats.
	 *
	 * @param original_formats the new original_formats
	 */
	public void setOriginal_formats(String original_formats) {
		this.originalFormats = original_formats;
	}
	
	/**
	 * Gets the original_formats.
	 *
	 * @return the original_formats
	 */
	public String getOriginal_formats() {
		return originalFormats;
	}
	
	/**
	 * Sets the most_recent_formats.
	 *
	 * @param most_recent_formats the new most_recent_formats
	 */
	public void setMost_recent_formats(String most_recent_formats) {
		this.mostRecentFormats = most_recent_formats;
	}
	
	/**
	 * Gets the most_recent_formats.
	 *
	 * @return the most_recent_formats
	 */
	public String getMost_recent_formats() {
		return mostRecentFormats;
	}
	
	/**
	 * Gets the rights.
	 * @return the rights
	 */
	public RightsStatement getRights(){
		
		return rights;
	}
	
	/**
	 * Sets the rights.
	 * @param rights the new rights
	 */
	public void setRights(RightsStatement rights){
		
		this.rights = rights;
	}
	
	/**
	 * Gets the agents.
	 *
	 * @return the agents
	 */
	public Set<Agent> getAgents() {
		
		return agents;
	}
	
	/**
	 * Sets the agents.
	 *
	 * @param agents the new agents
	 */
	public void setAgents(Set<Agent> agents) {
		
		this.agents = agents;
	}
	
	
	
	
	
	/**
	 * Grants right.
	 *
	 * @param eventType EventType specifies the type of action to be checked
	 * @return boolean true if right is granted, false if not
	 * @author Sebastian Cuy
	 * Checks if metadata contains a RightsStatement which grants
	 * the right to perform a given type of action.
	 */
	public boolean grantsRight(String eventType) {
			
		if (eventType.equals("PUBLICATION")
				&& rights.getPublicationRights() != null
				&& !rights.getPublicationRights().isEmpty()) {
			return true;
		} else if (eventType.equals("MIGRATION") && rights.getMigrationRight() != null) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * 
	 * @author Thomas Kleinke
	 * @return true if the object has deltas, otherwise false
	 */
	public boolean isDelta() {
		return (packages.size() > 1);
	}
	
	
	/**
	 * Grants publication right.
	 *
	 * @param audience Audience specifies the type of audience to be checked
	 * @return boolean true if right is granted, false if not
	 * @author Sebastian Cuy
	 * Checks if metadata contains a RightsStatement which grants
	 * the right to publish the package for the given type of audience.
	 */
	public boolean grantsPublicationRight(Audience audience) {
			
		for (PublicationRight publRight : rights.getPublicationRights()) {
			if (publRight.getAudience().equals(audience)
					&& publRight.getLawID() == null
					&& (publRight.getStartDate() == null ||
						publRight.getStartDate().before(new Date()))) {
				return true;
			}
		}
		return false;
	}
	
	
	
	
	/**
	 * Gets the newest files from all representations.
	 *
	 * @param sidecarExtensions Files with the given extensions are considered sidecar files. Sidecar files are treated differently from other files
	 *  (see <a href="https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/dip_specification.md#sidecar-files">documentation</a> for details)
	 * @return newest DAFile of each Document. Note that by default the DAFile instances are build from scratch and will
	 * not be attached to the object. However in case there are already instances attached to the object which correspond
	 * to the file objects from the file system, the method will return these instead.
	 * @author Thomas Kleinke
	 * @author Daniel M. de Oliveira
	 * @throws RuntimeException if it finds a file on the file system to which it cannot find a corresponding attached instance of
	 * DAFile in this object.
	 */
	
	public List<DAFile> getNewestFilesFromAllRepresentations(String sidecarExtensions)
	{
		Map<String, DAFile> fileMap = new HashMap<String, DAFile>();
			
		File mainFolder = getDataPath().toFile();
		if ((!mainFolder.exists())||((mainFolder.listFiles().length == 0)))
			throw new RuntimeException("Folder " + mainFolder.getAbsolutePath() +
									   " is empty or does not exist!");	
		
		checkFiles(
				FilenameUtils.getFullPath(
						mainFolder.getPath().
						substring(0, mainFolder.getPath().length()-2)
						), 
				mainFolder, 
				fileMap, 
				sidecarExtensions );
	
		return new ArrayList<DAFile>(fileMap.values());
	}
	
	
	
	
	/**
	 * Check files.
	 *
	 * @param rootPath the root path
	 * @param folder the folder
	 * @param fileMap the file map
	 * @param sidecarExtensions the sidecar extensions
	 * @return sidecarExtensions
	 * @author Thomas Kleinke
	 * @author Daniel M. de Oliveira
	 */
	// TODO rename method to specify what is checked here
	private Map<String, DAFile> checkFiles( 
			String rootPath, 
			File folder, 
			Map<String, DAFile> fileMap,
			String sidecarExtensions)
	{
		
		String dataPath = rootPath + "data/";
		
		File[] files = folder.listFiles();
		Arrays.sort(files); // sort rep names chronologically (i.e. alphabetically)
		
		for (File f : files)
		{
			if (f.isDirectory())
				fileMap = checkFiles(rootPath, f, fileMap, sidecarExtensions);
			else
			{				
				String fRelativePath = f.getPath();
				fRelativePath = fRelativePath.replace(dataPath, "");
				
				int indexOfFirstSeparator = fRelativePath.indexOf('/');
				if (indexOfFirstSeparator == -1) continue;
				if (fRelativePath.startsWith("dip")) {
					indexOfFirstSeparator += fRelativePath.substring(indexOfFirstSeparator+1).indexOf('/') + 1;
				}
				String fRepName = fRelativePath.substring(0, indexOfFirstSeparator);
				fRelativePath = fRelativePath.substring(indexOfFirstSeparator + 1); // relative from rep folder

				String relativePathWithoutExtension = fRelativePath;
				if (fRelativePath.lastIndexOf('.') != -1)
				{
					relativePathWithoutExtension = fRelativePath.substring(0, fRelativePath.lastIndexOf('.'));
				}
				
				if (Utilities.isSidecarFile(fRelativePath, sidecarExtensions))
					relativePathWithoutExtension += "?sidecar" + FilenameUtils.getExtension(fRelativePath).toLowerCase();
				
				DAFile newDAFile = new DAFile(this.getLatestPackage(),fRepName,fRelativePath);
				
				newDAFile = replaceByAttachedInstance(dataPath,
						fRelativePath, fRepName, newDAFile);
				
				newDAFile.setRelative_path(fRelativePath);
				fileMap.put(relativePathWithoutExtension, newDAFile);
			}
		}
		
		return fileMap;
	}
	
	/**
	 * Replace by attached instance if available.
	 *
	 * @param dataPath the data path
	 * @param fRelativePath the f relative path
	 * @param fRepName the f rep name
	 * @param newDAFile the new da file
	 * @return the dA file
	 * @author Daniel M. de Oliveira
	 */
	private DAFile replaceByAttachedInstance(String dataPath,
			String fRelativePath, String fRepName, DAFile newDAFile) {
		
		boolean foundAttachedInstance = false;
		for (Package pkg : this.getPackages()) {    // if package already exists, replace by attached package
			
			for (DAFile attachedFile : pkg.getFiles()) {

				if (attachedFile.toRegularFile().getAbsolutePath().equals
						(new File(dataPath+fRepName+"/"+fRelativePath).getAbsolutePath())){
					logger.trace(attachedFile.toString()+". Will use instance already attached to object.");
					newDAFile = attachedFile;
					foundAttachedInstance = true;
					break;
				}
			}
			if (foundAttachedInstance)
				break;
		}
		if (!foundAttachedInstance)
			throw new RuntimeException("cannot find attached instance for "+fRepName+"/"+fRelativePath);
		return newDAFile;
	}
	
	
	
	
	
	/**
	 * Note that since this is not a path we don't close with ending slash as usual.
	 *
	 * @return the name of newest rep
	 * @author daniel
	 */
	public String getNameOfNewestRep(){
		String[] files = getDataPath().toFile().list();
		Arrays.sort(files);
		
		List<String> list = new ArrayList<String>();
		for (String f:files){
			if (!f.startsWith("dip") && !f.startsWith("premis"))
				list.add(f); 
		}
		return list.get(list.size()-1);
	}
	
	
	/**
	 * Note that since this is not a path we don't close with ending slash as usual.
	 *
	 * @return the name of newest a rep
	 * @author Daniel M. de Oliveira
	 * @author Thomas Kleinke
	 */
	public String getNameOfNewestARep(){
		
		String[] files = getDataPath().toFile().list();
		Arrays.sort(files);
		
		List<String> list = new ArrayList<String>();
		for (String f:files){
			if (!f.startsWith("dip") && !f.startsWith("premis") && f.endsWith("a"))
				list.add(f); 
		}
		
		return list.get(list.size()-1);
	}
	
	
	/**
	 * Note that since this is not a path we don't close with ending slash as usual.
	 *
	 * @return the name of newest b rep or null if no b rep exists
	 * @author Daniel M. de Oliveira
	 * @author Thomas Kleinke
	 */
	public String getNameOfNewestBRep(){
		String[] files = getDataPath().toFile().list();
		Arrays.sort(files);
		
		List<String> list = new ArrayList<String>();
		for (String f:files){
			if (!f.startsWith("dip") && !f.startsWith("premis") && f.endsWith("b"))
				list.add(f); 
		}
		
		if (list.size() == 0)
			return null;
		else
			return list.get(list.size()-1);
	}
	
	
	/**
	 * Gets the latest version of a file in a package.
	 * Currently works only for files directly beneath the rep folders.
	 *
	 * @param filename the filename
	 * @return the latest
	 * @author Daniel M. de Oliveira
	 * @throws IllegalStateException if it finds a file without an associated dafile instance.
	 */
	public DAFile getLatest(String filename) {
		
		File[] representations = getDataPath().toFile().listFiles();
		Arrays.sort(representations);
		
		DAFile result = null;
		for (File rep : representations) {
			if (new File(getDataPath()+"/"+rep.getName()+"/"+filename).exists()){

				for (Package p:this.getPackages())
					for (DAFile f:p.getFiles()){
						if (f.equals(new DAFile(this.getLatestPackage(),rep.getName(),filename))) result=f;
					}
				if (result==null) throw new IllegalStateException("found a file without an associated dafile instance "+
						new DAFile(this.getLatestPackage(),rep.getName(),filename));
			}
		}
		
		logger.debug("getLatest(). result is {}", result);
		return result;
	}
	
	
	/**
	 * Gets the latest package.
	 *
	 * @return the newest package which is attached to the object.
	 * @throws IllegalStateException if there are no packages associated to the object.
	 * 
	 * @author Daniel M. de Oliveira
	 */
	public Package getLatestPackage(){
		if (getPackages().size()==0) throw new IllegalStateException("no packages associated");

		Package max = null;
		int maxnumber=0;
		
		for (Package p:getPackages()){
			if (Integer.parseInt(p.getName())>maxnumber){
				maxnumber = Integer.parseInt(p.getName());
				max = p;
			}
		}
		return max;
	}
	
	
	
	
	/**
	 * Gets the most_recent_secondary_attributes.
	 *
	 * @return the most_recent_secondary_attributes
	 */
	public String getMostRecentSecondaryAttributes() {
		return mostRecentSecondaryAttributes;
	}
	
	/**
	 * Sets the most_recent_secondary_attributes.
	 *
	 * @param most_recent_secondary_attributes the new most_recent_secondary_attributes
	 */
	public void setMostRecentSecondaryAttributes(
			String most_recent_secondary_attributes) {
		this.mostRecentSecondaryAttributes = most_recent_secondary_attributes;
	}


	public boolean ddbExcluded() {
		return ddbExclusion;
	}

	public void setDdbExclusion(boolean ddbExclusion) {
		this.ddbExclusion = ddbExclusion;
	}

	public Node getTransientNodeRef() {
		return transientNodeRef;
	}

	public void setTransientNodeRef(Node node) {
		this.transientNodeRef = node;
	}
	
	public void reattach(){
		for (Package p:packages){
			p.setTransientBackRefToObject(this);
			p.reattachPaths();
		}
	}

	public String getPackage_type() {
		return package_type;
	}

	public void setPackage_type(String package_type) {
		this.package_type = package_type;
	}

	public String getMetadata_file() {
		return metadata_file;
	}

	public void setMetadata_file(String metadata_file) {
		this.metadata_file = metadata_file;
	}
}
