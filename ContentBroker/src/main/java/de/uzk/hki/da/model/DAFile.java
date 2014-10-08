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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.ff.IFileWithFileFormat;


/**
 * contains the information about one file inside an aip.
 * 
 * for example 
 * repName="representation+a"
 * relativePath="subfolder/file1.txt"
 * 
 * would result in a complete path like
 * "representation+a/subfolder/file1.txt".
 * 
 * also contains information about the file formats of the file.
 * 
 * @author Daniel M. de Oliveira
 *
 */
@Entity
@Table(name="dafiles")
public class DAFile implements IFileWithFileFormat{

	/** The Constant logger. */
	static final Logger logger = LoggerFactory.getLogger(DAFile.class);
	
	/** The conversion_instruction_id. */
	private int conversion_instruction_id;
	
	/** The id. */
	private int id;
	
	/** The relative_path. */
	private String relative_path;
	
	/** The rep_name. */
	private String rep_name = "";
	
	/** The pkg. */
	private Package pkg;

	/** The format puid. */
	private String formatPUID; // encoded as PRONOM-PUID
	
	/** The format secondary attribute. */
	private String formatSecondaryAttribute = ""; // used to store compression or codec information
	
	/** The chksum. */
	private String chksum;
	
	/** The mimetype. */
	private String mimeType;
	
	/** The size. */
	private String size;
	
	/**
	 * Instantiates a new dA file.
	 */
	public DAFile(){}
	
	/**
	 * Instantiates a new dA file.
	 *
	 * @param pkg the pkg
	 * @param repName the rep name
	 * @param relPath rel path beneath representation folder.
	 */
	public DAFile(Package pkg, String repName,String relPath) {
		setRelative_path(relPath);
		this.setRep_name(repName);
		this.pkg=pkg;
	}
		
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public int getId() {
		return id;
	}
	
	/**
	 * Gets the package.
	 *
	 * @return the package
	 */
	@Transient
	public Package getPackage() {
		return pkg;
	}
	
	/**
	 * Sets the package.
	 *
	 * @param pkg the new package
	 */
	public void setPackage (Package pkg) {
		this.pkg = pkg;
	}
	
	/**
	 * To regular file.
	 *
	 * @return the file
	 * @author Daniel M. de Oliveira
	 */
	public File toRegularFile(){
		assert (pkg != null); // TODO why did this did not work?
		if (pkg==null) throw new IllegalStateException("Package not set");
		if (pkg.getTransientBackRefToObject()==null) throw new IllegalStateException("back ref to obj in pkg not set");
		
		String repName = "";
		if ((getRep_name() != null)&&(getRep_name() != ""))
			repName = getRep_name() + "/";
		return Path.make(pkg.getTransientBackRefToObject().getDataPath(),repName,relative_path).toFile();
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override 
	public String toString(){
		return "["+getRep_name()+"]/["+relative_path+"]";
	}
	
	
	/**
	 * Gets the relative_path.
	 *
	 * @return the relative_path
	 */
	public String getRelative_path() {
		return relative_path;
	}

	/**
	 * Sets the relative_path.
	 *
	 * @param relative_path the new relative_path
	 */
	public void setRelative_path(String relative_path) {
		if (relative_path.startsWith("/"))
			relative_path = relative_path.substring(1);
		
		this.relative_path = relative_path;
	}

	/**
	 * Gets the rep_name.
	 *
	 * @return the rep_name
	 */
	public String getRep_name() {
		return rep_name;
	}

	/**
	 * Sets the rep_name.
	 *
	 * @param repName the new rep_name
	 */
	public void setRep_name(String repName) {
		this.rep_name = repName;
	}
	
	@Transient
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	@Transient
	public String getMimeType() {
		return mimeType;
	}
	
//	public
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override 
	public boolean equals(java.lang.Object o){
		if (!(o instanceof DAFile)) return false;
		DAFile other = (DAFile) o;
		if (this.rep_name.equals(other.rep_name)&&
			this.relative_path.equals(other.relative_path)) 
			return true;
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode(){
		return (this.rep_name+"/"+this.relative_path).hashCode();
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the conversion_instruction_id.
	 *
	 * @return the conversion_instruction_id
	 */
	public int getConversion_instruction_id() {
		return conversion_instruction_id;
	}

	/**
	 * Sets the conversion_instruction_id.
	 *
	 * @param conversion_instruction_id the new conversion_instruction_id
	 */
	public void setConversion_instruction_id(int conversion_instruction_id) {
		this.conversion_instruction_id = conversion_instruction_id;
	}

	/**
	 * Gets the chksum.
	 *
	 * @return the chksum
	 */
	@Transient
	public String getChksum() {
		return chksum;
	}

	/**
	 * Sets the chksum.
	 *
	 * @param chksum the new chksum
	 */
	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	@Transient
	public String getSize() {
		return size;
	}

	/**
	 * Sets the size.
	 *
	 * @param size the new size
	 */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * Gets the format puid.
	 *
	 * @return the format puid
	 */
	@Column(name="file_format")
	public String getFormatPUID() {
		return formatPUID;
	}

	/**
	 * Sets the format puid.
	 *
	 * @param formatPUID the new format puid
	 */
	public void setFormatPUID(String formatPUID) {
		this.formatPUID = formatPUID;
	}

	/**
	 * Gets the format secondary attribute.
	 *
	 * @return the format secondary attribute
	 */
	@Column(name="format_second_attribute")
	public String getFormatSecondaryAttribute() {
		return formatSecondaryAttribute;
	}

	/**
	 * Sets the format secondary attribute.
	 *
	 * @param formatSecondaryAttribute the new format secondary attribute
	 */
	public void setFormatSecondaryAttribute(String formatSecondaryAttribute) {
		this.formatSecondaryAttribute = formatSecondaryAttribute;
	}
}
