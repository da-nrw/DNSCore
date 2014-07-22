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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;




/**
 * The Class Event.
 *
 * @author scuy
 * @author Daniel M. de Oliveira
 */

@Entity
@Table(name="events")
public class Event {
	
	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/** The id type. */
	@Transient
	private IdType idType;

	/** The identifier. */
	@Transient
	private String identifier;
	
	/** The agent_type. */
	private String agent_type;
	
	/** The agent_name. */
	private String agent_name;
	
	/** The agent_long_name. */
	@Transient
	private String agent_long_name;
	
	/** The type. */
	private String type;
	
	/** The source_file. */
	@ManyToOne
    @PrimaryKeyJoinColumn(
        name="source_file_id")
	private DAFile source_file;
	
	/** The target_file. */
	@ManyToOne
    @PrimaryKeyJoinColumn(
        name="target_file_id")
	private DAFile target_file;
	
	/** The date. */
	private Date date;
	
	/** The detail. */
	@Column(length=1000)
	private String detail;
	
	/** The outcome. */
	@Transient
	private String outcome;
	
	/**
	 * The Enum IdType.
	 */
	public enum IdType {
		
		/** The target file path. */
		TARGET_FILE_PATH, 
 /** The ingest id. */
 INGEST_ID, 
 /** The sip creation id. */
 SIP_CREATION_ID
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
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * Sets the date.
	 *
	 * @param date the new date
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String idt = "NULL-";
		if (idType!=null) idt=idType.name();
	
		return String.format("Event[type:%s,detail:%s,agentType:%s,agentName:%s,identifier:%s,idType:%s]",
				getType(),getDetail(),getAgent_type(),getAgent_name(),getIdentifier(),idt);
	}
	
	/**
	 * Gets the outcome.
	 *
	 * @return the outcome
	 */
	public String getOutcome() {
		return outcome;
	}

	/**
	 * Sets the outcome.
	 *
	 * @param outcome the new outcome
	 */
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}
	
	/**
	 * Gets the detail.
	 *
	 * @return the detail
	 */
	public String getDetail() {
		return detail;
	}
	
	/**
	 * Sets the detail.
	 *
	 * @param detail the new detail
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	/**
	 * Gets the id type.
	 *
	 * @return the id type
	 */
	public IdType getIdType() {
		return idType;
	}
	
	/**
	 * Sets the id type.
	 *
	 * @param idType the new id type
	 */
	public void setIdType(IdType idType) {
		this.idType = idType;
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
	 * Sets the identifier.
	 *
	 * @param identifier the new identifier
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Gets the source_file.
	 *
	 * @return the source_file
	 * @author Daniel M. de Oliveira
	 */
	public DAFile getSource_file() {
		if ((getType()==null)||getType().equals("")) throw new IllegalStateException("Type for Event not set");
		if (!getType().equals("CONVERT") && !getType().equals("COPY")) throw new RuntimeException("Operation not allowed for non CONVERT/COPY events");
		
		return source_file;
	}

	/**
	 * Sets the source_file.
	 *
	 * @param source_file the new source_file
	 */
	public void setSource_file(DAFile source_file) {
		this.source_file = source_file;
	}

	/**
	 * Gets the target_file.
	 *
	 * @return the target_file
	 * @author Daniel M. de Oliveira
	 * @throws IllegalStateException if called and event type not set 
	 * @throws IllegalStateException if called and event is not of type CONVERT or COPY
	 */
	public DAFile getTarget_file() {
		if ((getType()==null)||getType().equals("")) throw new IllegalStateException("Type for Event not set");
		if (!getType().equals("CONVERT") && !getType().equals("COPY") && !getType().equals("CREATE")) throw new IllegalStateException("Operation not allowed for non CONVERT/COPY/CREATE events");
		
		return target_file;
	}

	/**
	 * Sets the target_file.
	 *
	 * @param target_file the new target_file
	 */
	public void setTarget_file(DAFile target_file) {
		this.target_file = target_file;
	}

	/**
	 * Gets the agent_name.
	 *
	 * @return the agent_name
	 */
	public String getAgent_name() {
		return agent_name;
	}

	/**
	 * Sets the agent_name.
	 *
	 * @param agentName the new agent_name
	 */
	public void setAgent_name(String agentName) {
		this.agent_name = agentName;
	}
	
	/**
	 * Gets the agent_long_name.
	 *
	 * @return the agent_long_name
	 */
	public String getAgent_long_name() {
		return agent_long_name;
	}

	/**
	 * Sets the agent_long_name.
	 *
	 * @param agentLongName the new agent_long_name
	 */
	public void setAgent_long_name(String agentLongName) {
		this.agent_long_name = agentLongName;
	}

	/**
	 * Gets the agent_type.
	 *
	 * @return the agent_type
	 */
	public String getAgent_type() {
		return agent_type;
	}

	/**
	 * Sets the agent_type.
	 *
	 * @param agentType the new agent_type
	 */
	public void setAgent_type(String agentType) {
		this.agent_type = agentType;
	}
}
