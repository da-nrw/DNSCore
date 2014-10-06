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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.ff.IFileWithFileFormat;

/**
 * Represents the preservation system.
 * @author Daniel M. de Oliveira
 */
@Entity
@Table(name="preservation_system")
public class PreservationSystem {
	
	static final Logger logger = LoggerFactory.getLogger(PreservationSystem.class);
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@OneToOne
	@JoinColumn(name="admin_id",unique=true,nullable=false)
	private User admin;
	
	@OneToMany
	@JoinColumn(name="psystem_id")
	private List<ConversionRoutine> conversionRoutines = new ArrayList<ConversionRoutine>();
	
	@OneToMany
	@JoinColumn(name="psystem_id")
	private List<ConversionPolicy> conversion_policies = new ArrayList<ConversionPolicy>();
	
	@Transient
	private List<SecondStageScanPolicy> subformatIdentificationPolicies = new ArrayList<SecondStageScanPolicy>();
	
	@OneToMany
	@JoinColumn(name="psystem_id")
	private Set<Node> nodes = new HashSet<Node>();
	
	@Transient
	private List<User> contractors = new ArrayList<User>();

	@Transient
	private Map<String,List<ConversionPolicy>> policiesMap;
	
	@Column(name="min_repls",nullable=false)
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
	public List<ConversionRoutine> getConversionRoutines() {
		return conversionRoutines;
	}
	public void setConversionRoutines(List<ConversionRoutine> conversionRoutines) {
		this.conversionRoutines = conversionRoutines;
	}
	
	
	
	
	

	
	/**
	 * Matches the files format (specifically the puid) against the available
	 * ConversionPolicies and returns all matches. If a file has no evaluable file format information a warning
	 * will be logged.
	 *
	 * @param file the file
	 * @return the result list. can be empty if no matching policies can be found. This might even be the case
	 * if there is no evaluable file format information in file.
	 * @throws IllegalStateException 
	 */
	public List<ConversionPolicy> getApplicablePolicies(IFileWithFileFormat file,Boolean presentation) throws IllegalStateException {
		if (file==null) throw new IllegalStateException("DAFile file is null!");
		if (file.getFormatPUID()==null) throw new IllegalStateException("Format PUID is null!");
		if (file.getFormatPUID().isEmpty())throw new IllegalStateException("Format PUID is empty!");
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();

		session.refresh(this);

		// circumvent lazy initialization issues
		
		List<ConversionPolicy> result = new ArrayList<ConversionPolicy>();
		for (ConversionPolicy p:conversion_policies){
			if ((p.getSource_format().equals(file.getFormatPUID()))&&(presentation.equals(p.isPresentation()))){
				result.add(p);
			}
		}
		session.close();
		
		return result;
	}
	
	
	
	public List<ConversionPolicy> getConversion_policies() {
		return conversion_policies;
	}
	public void setConversion_policies(List<ConversionPolicy> conversion_policies) {
		this.conversion_policies = conversion_policies;
	}
	public Set<Node> getNodes() {
		return nodes;
	}
	public void setNodes(Set<Node> nodes) {
		this.nodes = nodes;
	}
	public List<SecondStageScanPolicy> getSubformatIdentificationPolicies() {
		return subformatIdentificationPolicies;
	}
	public void setSubformatIdentificationPolicies(
			List<SecondStageScanPolicy> subformatIdentificationPolicies) {
		this.subformatIdentificationPolicies = subformatIdentificationPolicies;
	}
	
}
