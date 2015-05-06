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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class Document.
 * 
 * Implemented as a linked list where the top element 
 * DAFile represents the actual state of the file.
 * Previous versions of the file can be retrieved
 * by getting the last file and from there 
 * by calling {@link DAFile#getPreviousDAFile()} repeatedly.
 *
 * @author Polina Gubaidullina
 * @author Daniel M. de Oliveira
 * 
 */

@Entity
@Table(name="documents")
public class Document {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(Package.class);
	
	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/** The document name. */
	@Column(name="doc_name")
	private String docname;

	/** The last dafiles. */
	@ManyToOne
	@PrimaryKeyJoinColumn(
      name="last_dafile_id")
	private DAFile lastDAFile;
	
	/**
	 * Instantiates a new document.
	 *
	 */
	public Document() {
	}
	
	/**
	 * Instantiates a new document.
	 *
	 * @param name the name
	 * @param first the first DAFile
	 */
	public Document(DAFile dafile) {
		this.docname = FilenameUtils.removeExtension(dafile.getRelative_path());
		this.lastDAFile = dafile;
		this.getLasttDAFile().setPreviousDAFile(null);
		logger.debug("Create new document "+getLasttDAFile());
	}
	
	/**
	 * Gets the last dafile.
	 *
	 * @return the last dafile
	 */
	public DAFile getLasttDAFile() {
		return lastDAFile;
	}
	
	/**
	 * Sets the last DAFile.
	 *
	 * @param daf the last DAFile
	 */
	public void setLastDAFile(DAFile daf) {
		this.lastDAFile = daf;
	}
	
	
	/**
	 * Adds dafile.
	 *
	 * @param daf the DAFile
	 */
	public void addDAFile(DAFile daf) {
		logger.debug("Add dafile "+daf+" to document "+this.getName());
		DAFile previousDAFile = this.lastDAFile;
		daf.setPreviousDAFile(previousDAFile);
		this.setLastDAFile(daf);
	}
	
	
	/**
	 * Remove daf from the top of the DAFile 
	 * stack which the document represents.
	 * 
	 * Removes it only if daf is really 
	 * the last file.  
	 * 
	 * @param daf
	 * @return true if removed successfully. 
	 * false if the file has not been removed. 
	 */
	public boolean removeDAFile(DAFile daf) {
		
		if (daf==null) return false;
		if (!daf.equals(getLasttDAFile())) return false;
		
		DAFile previous = getLasttDAFile().getPreviousDAFile();
		setLastDAFile(previous);

		return true;
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
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return docname;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String docname) {
		this.docname = docname;
	}
}
