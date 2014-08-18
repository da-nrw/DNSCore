/*
  DA-NRW Software Suite | ContentBroker
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Represents the preservation system.
 * @author Daniel M. de Oliveira
 */
@Entity
@Table(name="psystem")
public class PSystem {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@OneToOne
	@JoinColumn(name="admin_id",unique=true)
	private User admin;
	
	@Column(name="min_repls")
	private Integer minRepls;

	@Column(name="sidecar_extensions")
	private String sidecarExtensions="";
	
	@Column(name="pres_server")
	private String presServer;

	@Column(name="urn_name_space")
	private String urnNameSpace;
	
	@Column(name="uris_file")
	private String urisFile;
	
	@Column(name="uris_cho")
	private String urisCho;
	
	@Column(name="uris_aggr")
	private String urisAggr;
	
	@Column(name="uris_local")
	private String urisLocal;
	
	@Column(name="open_collection_name")
	private String openCollectionName;
	
	@Column(name="closed_collection_name")
	private String closedCollectionName;

	// TODO refactor to admin of system
	@Column(name="email_from")
	private String emailFrom;
	
	public Integer getMinRepls() {
		return minRepls;
	}
	public void setMinRepls(Integer minRepls) {
		this.minRepls = minRepls;
	}
	public String getSidecarExtensions() {
		return sidecarExtensions;
	}
	public void setSidecarExtensions(String sidecarExtensions) {
		this.sidecarExtensions = sidecarExtensions;
	}
	public String getPresServer() {
		return presServer;
	}
	public void setPresServer(String presServer) {
		this.presServer = presServer;
	}
	public String getUrnNameSpace() {
		return urnNameSpace;
	}
	public void setUrnNameSpace(String urnNameSpace) {
		this.urnNameSpace = urnNameSpace;
	}
	public String getUrisFile() {
		return urisFile;
	}
	public void setUrisFile(String urisFile) {
		this.urisFile = urisFile;
	}
	public String getUrisCho() {
		return urisCho;
	}
	public void setUrisCho(String urisCho) {
		this.urisCho = urisCho;
	}
	public String getUrisAggr() {
		return urisAggr;
	}
	public void setUrisAggr(String urisAggr) {
		this.urisAggr = urisAggr;
	}
	public String getUrisLocal() {
		return urisLocal;
	}
	public void setUrisLocal(String urisLocal) {
		this.urisLocal = urisLocal;
	}
	public String getClosedCollectionName() {
		return closedCollectionName;
	}
	public void setClosedCollectionName(String closedCollectionName) {
		this.closedCollectionName = closedCollectionName;
	}
	public String getOpenCollectionName() {
		return openCollectionName;
	}
	public void setOpenCollectionName(String openCollectionName) {
		this.openCollectionName = openCollectionName;
	}
	public String getEmailFrom() {
		return emailFrom;
	}
	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public User getAdmin() {
		return admin;
	}
	public void setAdmin(User admin) {
		this.admin = admin;
	}

	
}
