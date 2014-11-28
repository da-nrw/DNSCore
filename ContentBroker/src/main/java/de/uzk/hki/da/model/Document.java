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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class Document.
 *
 * @author Polina Gubaidullina
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
	
//	/** The names of dafiles. */
//	@ElementCollection
//	@CollectionTable(name="doc_dafiles", joinColumns=@JoinColumn(name="doc_id"))
//	@Column(name="dafile_name")
//	private List<String> dafiles = new ArrayList<String>();
//	
//	/**
//	 * Instantiates a new document.
//	 *
//	 */
//	public Document() {
//	}
//	
//	public Document(DAFile dafile) {
//		String docname = FilenameUtils.getBaseName(dafile.toRegularFile().getName());
//		String relPath = dafile.getRelative_path();
//		logger.debug("Create new document "+getName());
//		this.name = docname;
//		this.addDAFile(relPath);
//	}
//	
//	/**
//	 * Gets the last dafile.
//	 *
//	 * @return the last dafile
//	 */
//	public String getLastDAFile() {
//		return dafiles.get(dafiles.size()-1);
//	}
//
//	/**
//	 * Gets the name.
//	 *
//	 * @return the name
//	 */
//	public void addDAFile(String relPath) {
//		logger.debug("Add new dafile "+relPath);
//		dafiles.add(relPath);
//	}
//	

	
//	
	
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
	 * @param first the first dafile
	 */
	public Document(DAFile dafile) {
		this.docname = FilenameUtils.getBaseName(dafile.toRegularFile().getName());
		this.lastDAFile = dafile;
		logger.debug("Create new document "+getLasttDAFile());
	}
	
	/**
	 * Gets the first dafile.
	 *
	 * @return the first dafile
	 */
	public DAFile getLasttDAFile() {
		return lastDAFile;
	}
	
	/**
	 * Sets the first dafile.
	 *
	 * @param dafile the first dafile
	 */
	public void setLastDAFile(DAFile dafile) {
		this.lastDAFile = dafile;
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
