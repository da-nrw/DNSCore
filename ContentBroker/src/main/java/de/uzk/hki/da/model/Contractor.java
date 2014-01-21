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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The Class Contractor. An institutional agent. Role under which the users 
 * which belong to an institution
 * interact with the system.
 * @author Daniel M. de Oliveira
 */
@Entity
@Table(name="contractors")
public class Contractor{
	
	
	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	/** 
	 * The short_name. A unique idenfier for an institutional agent.
	 * In Javadoc comments troughout the source code base often refered to as csn (contractor short name).
	 **/
	private String short_name;
	
	/** The forbidden_nodes. */
	private String forbidden_nodes;
	
	/** The email_contact. */
	private String email_contact;
	
	private Integer admin;
	
	/** The conversion_policies. */
	@OneToMany
	@JoinColumn(name="contractor_id")
	private List<ConversionPolicy> conversion_policies = new ArrayList<ConversionPolicy>();
	
	
	/**
	 * Instantiates a new contractor.
	 */
	public Contractor(){}
	
	/**
	 * Instantiates a new contractor.
	 *
	 * @param short_name the short_name
	 * @param forbidden_nodes the forbidden_nodes
	 * @param email_contact the email_contact
	 */
	public Contractor(
			String short_name,
			String forbidden_nodes,
			String email_contact){
		this.short_name=short_name;
		this.forbidden_nodes=forbidden_nodes;
		this.email_contact=email_contact;
	}
	
	
	/**
	 * Sets the short_name.
	 *
	 * @param short_name the new short_name
	 */
	public void setShort_name(String short_name) {
		this.short_name = short_name;
	}

	/**
	 * Gets the short_name.
	 *
	 * @return the short_name
	 */
	public String getShort_name() {
		return short_name;
	}

	/**
	 * Sets the forbidden_nodes.
	 *
	 * @param forbidden_nodes the new forbidden_nodes
	 */
	public void setForbidden_nodes(String forbidden_nodes) {
		this.forbidden_nodes = forbidden_nodes;
	}

	/**
	 * Gets the forbidden_nodes.
	 *
	 * @return the forbidden_nodes
	 */
	public String getForbidden_nodes() {
		return forbidden_nodes;
	}


	/**
	 * Sets the email_contact.
	 *
	 * @param email_contact the new email_contact
	 */
	public void setEmail_contact(String email_contact) {
		this.email_contact = email_contact;
	}

	/**
	 * Gets the email_contact.
	 *
	 * @return the email_contact
	 */
	public String getEmail_contact() {
		return email_contact;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		
		return "Contractor["+short_name+","+forbidden_nodes+","+email_contact+"]";
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode(){
		return short_name.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(java.lang.Object o){
		Contractor other = (Contractor) o;
		return (this.short_name.equals(other.short_name));
	}

	/**
	 * Gets the conversion_policies.
	 *
	 * @return the conversion_policies
	 */
	public List<ConversionPolicy> getConversion_policies() {
		return conversion_policies;
	}

	/**
	 * Sets the conversion_policies.
	 *
	 * @param conversion_policies the new conversion_policies
	 */
	public void setConversion_policies(List<ConversionPolicy> conversion_policies) {
		this.conversion_policies = conversion_policies;
	}

	/**
	 * @param admin the admin to set
	 */
	public void setAdmin(int admin) {
		this.admin = admin;
	}

	/**
	 * @return the admin
	 */
	public int getAdmin() {
		return admin;
	}
}
