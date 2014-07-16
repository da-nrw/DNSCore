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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;



/**
 * The Class Job.
 */
@Entity
@Table(name="queue")
public class Job {
	
	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/** The parent_id. */
	private Integer parent_id;
	
	/** The status. */
	private String status;

	/** 
	 * The name of the node of the system which is instructed to do the job.
	 */
	@Column(name="initial_node")
	private String responsibleNodeName;
	
	/** The repl_destinations. */
	private String repl_destinations;
	
	/** The date_created. */
	private String date_created;
	
	/** The date_modified. */
	private String date_modified;
	
	/** The rep_name. */
	private String rep_name;
	
	private Date static_nondisclosure_limit;
	
	private String dynamic_nondisclosure_limit;
	
	private String container_extension;
	
	
	
	/** The contractor. */
	@ManyToOne
	@PrimaryKeyJoinColumn( name = "contractor_id" )
	private Contractor contractor;
	
	/** The children. */
	@OneToMany (orphanRemoval = true )
	@JoinColumn (name = "parent_id")
	@Cascade({CascadeType.SAVE_UPDATE,CascadeType.DELETE})
	private List<Job> children = new ArrayList<Job>();
	
	/** The conversion_instructions. */
	@OneToMany(  orphanRemoval = true )
	@JoinColumn( name = "job_id" )
	@Cascade({CascadeType.SAVE_UPDATE,CascadeType.DELETE})
	private Set<ConversionInstruction> conversion_instructions = 
			new HashSet<ConversionInstruction>();
	
	/**  
	 * 
	 * */
	@ManyToOne
	@JoinColumn(name = "objects_id")
	private Object obj;
	
	
	/**
	 * Instantiates a new job.
	 */
	public Job(){}
	
	/**
	 * Creates a new Job by copying most of the field with the important
	 * exception of the id field (database!).
	 * The parent id field gets set to the id of the job copied.
	 *
	 * @param rhs the rhs
	 * @param status the status
	 */
	public Job(Job rhs,String status){
		this.status=status;
		this.rep_name=rhs.rep_name;
		this.responsibleNodeName=rhs.responsibleNodeName;
		this.contractor = rhs.contractor;
	}

	
	
	/**
	 * Instantiates a new job.
	 *
	 * @param object_identifier the object_identifier
	 * @param initialNodeName the initial node name
	 * @param status the status
	 * @author daniel
	 */
	public Job(String initialNodeName, String status){
		this.responsibleNodeName = initialNodeName;
		this.status=status;
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
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * Sets the initial_node.
	 *
	 * @param responsibleNodeName the new initial_node
	 */
	public void setResponsibleNodeName(String responsibleNodeName) {
		this.responsibleNodeName = responsibleNodeName;
	}
	
	/**
	 * Gets the initial_node.
	 *
	 * @return the initial_node
	 */
	public String getResponsibleNodeName() {
		return responsibleNodeName;
	}
	
	/**
	 * Sets the repl_destinations.
	 *
	 * @param repl_destinations the new repl_destinations
	 */
	public void setRepl_destinations(String repl_destinations) {
		this.repl_destinations = repl_destinations;
	}
	
	/**
	 * Gets the repl_destinations.
	 *
	 * @return the repl_destinations
	 */
	public String getRepl_destinations() {
		return repl_destinations;
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
		
		return String.format(
				"%5s" +
				"|%5s"+
				"|%10s"+
				"|%20s"+
				"]"
		,getId(),getStatus(),getResponsibleNodeName(),getRepl_destinations());
	}

	
	/**
	 * Gets the conversion_instructions.
	 *
	 * @return the conversion_instructions
	 */
	public Set<ConversionInstruction> getConversion_instructions() {
		return conversion_instructions;
	}

	/**
	 * Sets the conversion_instructions.
	 *
	 * @param conversion_instructions the new conversion_instructions
	 */
	public void setConversion_instructions(Set<ConversionInstruction> conversion_instructions) {
		this.conversion_instructions = conversion_instructions;
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

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public List<Job> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	public void setChildren(List<Job> children) {
		this.children = children;
	}

	/**
	 * Gets the parent_id.
	 *
	 * @return the parent_id
	 */
	public int getParent_id() {
		return parent_id;
	}

	/**
	 * Sets the parent_id.
	 *
	 * @param parent_id the new parent_id
	 */
	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}

	
	
	/**
	 * Gets the obj.
	 *
	 * @return the obj
	 * @author Daniel M. de Oliveira
	 */
	public Object getObject() {
		return obj;
	}

	/**
	 * Sets the obj.
	 *
	 * @param object the new obj
	 */
	public void setObject(Object object) {
		this.obj = object;
	}


	public String getDynamic_nondisclosure_limit() {
		return dynamic_nondisclosure_limit;
	}

	public void setDynamic_nondisclosure_limit(
			String dynamic_nondisclosure_limit) {
		this.dynamic_nondisclosure_limit = dynamic_nondisclosure_limit;
	}

	public Date getStatic_nondisclosure_limit() {
		return static_nondisclosure_limit;
	}

	public void setStatic_nondisclosure_limit(Date static_nondisclosure_limit) {
		this.static_nondisclosure_limit = static_nondisclosure_limit;
	}

	public String getContainer_extension() {
		return container_extension;
	}

	public void setContainer_extension(String container_extension) {
		this.container_extension = container_extension;
	}
}
