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

/**
 * 
 */
package de.uzk.hki.da.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.db.CentralDatabaseDAO;
import de.uzk.hki.da.db.HibernateUtil;


/**
 * Manages ConversionPolicies and knows which ones 
 * to apply for certain file format / user combinations.
 * Acts like a repository for the ConversionPolicies.
 * @author Daniel M. de Oliveira
 *
 */
public class PreservationSystem {
	
	/** The Constant logger. */
	static final Logger logger = LoggerFactory.getLogger(PreservationSystem.class);
	
	/** The policies map. */
	private Map<String,List<ConversionPolicy>> policiesMap;
	
	/** The contractors. */
	private List<Contractor> contractors = new ArrayList<Contractor>();
	
	
	/**
	 * Instantiates a new preservation system.
	 *
	 * @param dao the dao
	 */
	@SuppressWarnings("unused")
	public PreservationSystem(CentralDatabaseDAO dao){
		policiesMap = new HashMap<String,List<ConversionPolicy>>();
		
		Session session = HibernateUtil.openSession();
		session.getTransaction().begin();
		
		Contractor presenter = dao.getContractor(session, "PRESENTER");
		if (presenter==null) {
			session.close();
			throw new IllegalStateException("contractor PRESENTER not found in db");
		}
		
		Contractor archive = dao.getContractor(session, "DEFAULT");
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
