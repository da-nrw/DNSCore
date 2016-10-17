/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln
  Copyright (C) 2014 LVRInfoKom
  Landschaftsverband Rheinland

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class DNS Object.
 *
 * @author Jens Peters
 * @author Daniel M. de Oliveira
 */
@Entity
@Table(name="premis_objects")
public class PremisObject {
	

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(PremisObject.class);

	
	
	
	public static final String REPRESENTATION_FILTER = "^.*[+][ab]";
	
	
	
	
	
	/** The data_pk. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int data_pk;

	/** 
	 * The identifier. In javadoc comments throughout the source code base often refered to as oid.
	 * */
	@Column(unique=true, columnDefinition="varchar(60)")
	private String identifier;
	
	/** The urn. */
	private String urn;
	
	/** The initial_node. */
	@Column(columnDefinition="varchar(150)")
	private String initial_node;
	
	/** The orig_name. */
	private String orig_name;
	
	/** The date_created. */
	@Column(columnDefinition="varchar(100)")
	private String date_created;
	
	/** The date_modified. */
	@Column(columnDefinition="varchar(100)")
	private String date_modified;
	
	@Column(columnDefinition="varchar(50)")
	private String package_type;
	
	@Column(columnDefinition="varchar(120)")
	private String metadata_file;

	/** The last_checked. */
	private Date last_checked;
	
	/** The original_formats. */
	@Column(name="original_formats")
	private String originalFormats;
	
	/** The most_recent_formats. */
	@Column(name="most_recent_formats")
	private String mostRecentFormats;
	
	/** The most_recent_secondary_attributes. */
	@Column(name="most_recent_secondary_attributes", columnDefinition="varchar(2048)")
	private String mostRecentSecondaryAttributes = "";
	
	/** last publication try (time based) */
	@Column(name="last_publication_try")
	private Date lastPublicationTry;

	/** The packages. */
	@OneToMany(targetEntity=PremisPackage.class, fetch=FetchType.EAGER)
	@Cascade(CascadeType.ALL)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<PremisPackage> packages = new ArrayList<PremisPackage>();
	
	
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
	public void setPackages(List<PremisPackage> packages) {
		this.packages = packages;
	}	
	
	/**
	 * Gets the packages.
	 *
	 * @return the packages
	 */
	public List<PremisPackage> getPackages() {
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
		/*ObjectPremisXmlReader premis = new ObjectPremisXmlReader();
		try {
			Object o = premis.deserialize(new File("src/test/resources/metadata/premistest.xml"));
			System.out.println("--- setUrn // deserialisiere xml ---");
			//Thread.sleep(5000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
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
	
	
	
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override 
	public String toString(){
		
		
		return 
				"Object["
		+ identifier + "," + urn + "," + "," + orig_name + "," +
						"]";
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
	 * 
	 * @author Thomas Kleinke
	 * @return true if the object has deltas, otherwise false
	 */
	public boolean isDelta() {
		return (packages.size() > 1);
	}

	
	/**
	 * @author Daniel M. de Oliveira 
	 * @return
	 */
	private Collection<PremisDAFile> getFilesFromRepresentation(String rep){

		ArrayList<PremisDAFile> list = new ArrayList<PremisDAFile>();
		
		for (PremisPackage pkg:getPackages()){
			for (PremisDAFile f:pkg.getFiles()){
				if (f.getRep_name().equals(rep)) list.add(f);
			}
		}
		
		return list;
	}
 	
	
	
	/**
	 * Gets the latest package.
	 *
	 * @return the newest package which is attached to the object.
	 * @throws IllegalStateException if there are no packages associated to the object.
	 * 
	 * @author Daniel M. de Oliveira
	 */
	public PremisPackage getLatestPackage(){
		if (getPackages().size()==0) throw new IllegalStateException("no packages associated");

		PremisPackage max = null;
		int maxnumber=0;
		
		for (PremisPackage p:getPackages()){
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
	
	public Date getLastPublicationTry() {
		return lastPublicationTry;
	}

	public void setLastPublicationTry(Date lastPublicationTry) {
		this.lastPublicationTry = lastPublicationTry;
	}

	/**
	 * Gets the files of a representation based on the information stored
	 * in the object tree. 
	 * 
	 * @author Daniel M. de Oliveira
	 * @param repName
	 * @return
	 */
	private Collection<PremisDAFile> getFilesOfRepresentation(String repName){
		
		Collection<PremisDAFile> files = new ArrayList<PremisDAFile>();
		
		for (PremisPackage pkg:this.getPackages()){
			for (PremisDAFile f:pkg.getFiles()){
				if (f.getRep_name().equals(repName))
					files.add(f);
			}
		}
		
		return files;
	}
	
	
	
	
	
}
