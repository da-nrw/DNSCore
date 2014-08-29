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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;


/**
 * Represents a rights statement in the preservation system.
 * @see <a href="http://www.loc.gov/standards/premis/">PREMIS</a>
 * @author scuy
 */
@Entity
public class RightsStatement {
	
	/** The id. */
	private String id;
	
	/** The fid. */
	private String fid;
	
	/** The deliverer id. */
	private String delivererId;
	
	/** The publication rights. */
	private List<PublicationRight> publicationRights = new ArrayList<PublicationRight>();
	
	/** The migration right. */
	private MigrationRight migrationRight;
	
	/**
	 * Instantiates a new rights statement.
	 */
	public RightsStatement() {
		
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	@Id
	@XmlID
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets the deliverer id.
	 *
	 * @param delivererId the new deliverer id
	 */
	public void setDelivererId (String delivererId){
		this.delivererId= delivererId;
	}
	
	/**
	 * Sets the fid.
	 *
	 * @param fid the new fid
	 */
	public void setFid (String fid){
		this.fid= fid;
	}
	
	/**
	 * Gets the fid.
	 *
	 * @return the fid
	 */
	public String getFid (){
		return fid;
	}
	
	/**
	 * Gets the deliverer id.
	 *
	 * @return the deliverer id
	 */
	public String getDelivererId (){
		return delivererId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {		
		return String.format("RightsStatement[id:%s]",
				getId());
	}

	/**
	 * Gets the publication rights.
	 *
	 * @return the publication rights
	 */
	@XmlElement(name="publicationRight")
	public List<PublicationRight> getPublicationRights() {
		return publicationRights;
	}

	/**
	 * Sets the publication rights.
	 *
	 * @param publicationRights the new publication rights
	 */
	public void setPublicationRights(List<PublicationRight> publicationRights) {
		this.publicationRights = publicationRights;
	}

	/**
	 * Gets the migration right.
	 *
	 * @return the migration right
	 */
	public MigrationRight getMigrationRight() {
		return migrationRight;
	}

	/**
	 * Sets the migration right.
	 *
	 * @param migrationRight the new migration right
	 */
	public void setMigrationRight(MigrationRight migrationRight) {
		this.migrationRight = migrationRight;
	}
}
