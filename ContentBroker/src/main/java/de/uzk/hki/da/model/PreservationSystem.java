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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	@JoinColumn(name="admin_id",unique=true)
	private User admin;
	
	@OneToMany
	@JoinColumn(name="psystem_id")
	private List<ConversionRoutine> conversionRoutines = new ArrayList<ConversionRoutine>();
	
	@Transient
	private List<User> contractors = new ArrayList<User>();

	@Transient
	private Map<String,List<ConversionPolicy>> policiesMap;
	
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
	
	
	
	
	
	@SuppressWarnings("unused")
	public void initialize(CentralDatabaseDAO dao){
		policiesMap = new HashMap<String,List<ConversionPolicy>>();
		
		Session session = HibernateUtil.openSession();
		session.getTransaction().begin();
		
		User presenter = dao.getContractor(session, "PRESENTER");
		if (presenter==null) {
			session.close();
			throw new IllegalStateException("contractor PRESENTER not found in db");
		}
		
		User archive = dao.getContractor(session, "DEFAULT");
		if (archive==null) {
			session.close();
			throw new IllegalStateException("contractor DEFAULT not found in db");
		}
		
		
		contractors.add(archive);
		contractors.add(presenter);
		
		// to avoid lazy initialization issues
		for (ConversionPolicy p:archive.getConversion_policies());
		for (ConversionPolicy p:presenter.getConversion_policies());
		
		policiesMap.put(
				"DEFAULT",archive.getConversion_policies());
		policiesMap.put(
				"PRESENTER",presenter.getConversion_policies());
		session.close();
	}

	
	/**
	 * Matches the files format (specifically the puid) against the available
	 * ConversionPolicies and returns all matches. If a file has no evaluable file format information a warning
	 * will be logged.
	 *
	 * @param file the file
	 * @param contractor_short_name the contractor_short_name
	 * @return the result list. can be empty if no matching policies can be found. This might even be the case
	 * if there is no evaluable file format information in file. Can also be empty if there are no policies for
	 * a contractor. We do not consider it a special case if the contractor does not exists. In any case a warning is
	 * logged.
	 */
	public List<ConversionPolicy> getApplicablePolicies(DAFile file,String contractor_short_name) {
		if (file.getFormatPUID().isEmpty()){
			logger.warn("No FileFormat information available in DAFile: "+file.toString());
			return new ArrayList<ConversionPolicy>(); 
		}
		if (policiesMap.get(contractor_short_name)==null){
			logger.warn("no ConversionPolicies found for: "+contractor_short_name);
			return new ArrayList<ConversionPolicy>(); 
		}
		
		
		List<ConversionPolicy> result = new ArrayList<ConversionPolicy>();
		for (ConversionPolicy cp:policiesMap.get(contractor_short_name)){
			if (cp.getSource_format() == null) throw new RuntimeException("cp.getSourceFormat returned null.");
			if (cp.getSource_format().equals(file.getFormatPUID())){
				result.add(cp);
			}
		}
		return result;
	}
	
}
