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

package de.uzk.hki.da.format;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.SecondStageScanPolicy;




/**
 * Provides funcionalities for adding file format information to DAFile objects.
 *
 * @author Daniel M. de Oliveira
 */
public class FormatScanService {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(FormatScanService.class);
	
	/** The pronom format identifier. */
	private CLIFormatIdentifier pronomFormatIdentifier;
	
	/** The format second attribute identifiers. */
	private Set<CLIFormatIdentifier> formatSecondAttributeIdentifiers = new HashSet<CLIFormatIdentifier>();
	
	/** The second stage scans. */
	private List<SecondStageScanPolicy> secondStageScans;

	/**
	 * Instantiates a new format scan service.
	 *
	 * @param ops the ops
	 */
	public FormatScanService(CentralDatabaseDAO dao){
		Session session = HibernateUtil.openSession();
		secondStageScans = 
				dao.getSecondStageScanPolicies(session);
		session.close();
		
		logger.debug("Listing policies for second stage scan:");
		for (SecondStageScanPolicy scan:secondStageScans){
			logger.debug(scan.toString());
		}
	}
	
	
	/**
	 * Iterates over files and determines PUIDs and, if necessary, codec or compression related information.
	 * This means, that if the method runs successfully (without throwing exceptions) each file_format
	 * property is set to a PRONOM-PUID. Furthermore, if the file format is marked for second stage scanning
	 * (via configuration in the second_stage_scans table), the format_second_attribute of the dafile object is also set.
	 * In such cases the attribute has a proper value, which means it contains one of the values listed in the
	 * expected_values column of the row for the puid in question.
	 *
	 * @param files the files
	 * @return files. return value needed for mockup usage in unit tests.
	 * @throws FileNotFoundException if any of the files can not be found on the file system.
	 * @author Daniel M. de Oliveira
	 */
	public List<DAFile> identify(List<DAFile> files) throws FileNotFoundException {
		for (DAFile f:files){
			if (!f.toRegularFile().exists()) throw new FileNotFoundException("file "+f.toRegularFile().getPath()+" doesn't exist");
		}	
		for (DAFile f:files){
			f.setFormatPUID(identify(f.toRegularFile()));
			logger.trace(f+" has puid "+f.getFormatPUID()+". Now searching if second stage scan policy is applicable");
			
			for (SecondStageScanPolicy scan:secondStageScans){
				if (!f.getFormatPUID().equals(scan.getPUID())) continue;
				logger.trace("Policy found: "+scan);
				
				for (CLIFormatIdentifier additionalIdentifier:formatSecondAttributeIdentifiers){
					if (!additionalIdentifier.getConversionScript().getName().equals(scan.getFormatIdentifierScriptName())) continue;
					logger.trace("found an identifier with identification script \""+scan.getFormatIdentifierScriptName()+"\" required by policy");	
					
					// TODO resolve
					Set<String> resultList = additionalIdentifier.identify(f.toRegularFile());
					String result="";
					for (String r:resultList){
						result=r;
					}
					
					if (!scan.getAllowedValues().contains(result))
						throw new RuntimeException("result \""+result+"\" not part of allowed values \""+scan.getAllowedValues()+"\" for policy");
					
					f.setFormatSecondaryAttribute(result);
					break;
				}
				// TODO resolve possibly multiple values. For now, just take the last result
			}
		}
	
		return files;
	}
	
	
	
	
	
	/**
	 * Identifies a file format of a file. The file format is encoded as PRONOM puid.
	 * 
	 * TODO inline method and then refactor whole class and break down in smaller pieces
	 *
	 * @param file the file
	 * @return <li>UNDEFINED if no output from identifier.
	 * <li> puid, if only one format puid comes from the identifier.
	 * <li> the last puid from the output of the identifier, if there are more than one.
	 */
	private String identify(File file) {

		Set<String> fileFormats = getPronomFormatIdentifier().identify(file);
		if (fileFormats.isEmpty()){
			logger.warn("Identified format for file: \""+file.getName()+"\" is declared UNDEFINED due to the missing result of the" +
					"used identifier. Try testing this manually.");
			return "UNDEFINED";
		}
		
		// TODO for now return the last one
		String result="";
		for (String r:fileFormats){
			result=r;
		}
		return result;
	}
	

	/**
	 * Gets the pronom format identifier.
	 *
	 * @return the pronom format identifier
	 */
	public CLIFormatIdentifier getPronomFormatIdentifier() {
		return pronomFormatIdentifier;
	}

	/**
	 * Sets the pronom format identifier.
	 *
	 * @param pronomFormatIdentifier the new pronom format identifier
	 */
	public void setPronomFormatIdentifier(CLIFormatIdentifier pronomFormatIdentifier) {
		this.pronomFormatIdentifier = pronomFormatIdentifier;
	}



	/**
	 * The format identifiers which can identify codecs or compression algorithms.
	 *
	 * @return the format second attribute identifiers
	 */
	public Set<CLIFormatIdentifier> getFormatSecondAttributeIdentifiers() {
		return formatSecondAttributeIdentifiers;
	}




	/**
	 * Sets the format secondary attribute identifiers.
	 *
	 * @param formatSecondAttributeIdentifiers the new format secondary attribute identifiers
	 */
	public void setFormatSecondaryAttributeIdentifiers(
			Set<CLIFormatIdentifier> formatSecondAttributeIdentifiers) {
		this.formatSecondAttributeIdentifiers = formatSecondAttributeIdentifiers;
	}
}
