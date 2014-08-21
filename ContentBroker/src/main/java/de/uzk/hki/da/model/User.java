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

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


/**
 * The Class User. An institutional agent. Role under which the users 
 * which belong to an institution
 * interact with the system.
 * @author Daniel M. de Oliveira
 */
@Entity
@Table(name="users")
public class User{
	
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
	@Column(name="email_contact")
	private String emailAddress;
	
	private String username;
	private String password;
	private String description;
	   
    @ManyToMany(cascade=CascadeType.ALL)  
    @JoinTable(name="user_role",  
    joinColumns={@JoinColumn(name="user_id", referencedColumnName="id")},  
    inverseJoinColumns={@JoinColumn(name="role_id", referencedColumnName="id")})
    private Set<Role> roles; 
    
	private Boolean enabled = true;
	private Boolean accountexpired;
	private Boolean accountlocked;
	private Boolean passwordexpired;
	
	/**
	 * Gets the Roles.
	 *
	 * @return the roles
	 */
	public Set<Role> getRoles() {
		return this.roles;
	}
	
	/**
	 * Instantiates a new contractor.
	 */
	public User(){}
	
	/**
	 * Instantiates a new contractor.
	 *
	 * @param short_name the short_name
	 * @param forbidden_nodes the forbidden_nodes
	 * @param email_contact the email_contact
	 */
	public User(
			String short_name,
			String forbidden_nodes,
			String email_contact){
		this.short_name=short_name;
		this.forbidden_nodes=forbidden_nodes;
		this.emailAddress=email_contact;
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
	 * @param emailAddress the new email_contact
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * Gets the email_contact.
	 *
	 * @return the email_contact
	 */
	public String getEmailAddress() {
		return emailAddress;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		
		return "User["+short_name+","+forbidden_nodes+","+emailAddress+"]";
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
		User other = (User) o;
		return (this.short_name.equals(other.short_name));
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getAccountexpired() {
		return accountexpired;
	}

	public void setAccountexpired(Boolean accountexpired) {
		this.accountexpired = accountexpired;
	}

	public Boolean getAccountlocked() {
		return accountlocked;
	}

	public void setAccountlocked(Boolean accountlocked) {
		this.accountlocked = accountlocked;
	}

	public Boolean getPasswordexpired() {
		return passwordexpired;
	}

	public void setPasswordexpired(Boolean passwordexpired) {
		this.passwordexpired = passwordexpired;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
