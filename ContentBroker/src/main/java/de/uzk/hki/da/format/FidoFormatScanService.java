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

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.SecondStageScanPolicy;




/**
 * Provides funcionalities for adding file format information to DAFile objects.
 *
 * @author Daniel M. de Oliveira
 */
public class FidoFormatScanService implements FormatScanService {
	
	private static final Logger logger = LoggerFactory.getLogger(FidoFormatScanService.class);
	
	private PronomFormatIdentifierWrapper pronomFormatIdentifier;
	
	/** The format second attribute identifiers. */
	private Set<CLIFormatIdentifier> formatSecondAttributeIdentifiers = new HashSet<CLIFormatIdentifier>();
	
	/** The second stage scans. */
	private List<SecondStageScanPolicy> secondStageScans;

	/**
	 * Instantiates a new format scan service.
	 *
	 * @param ops the ops
	 */
	public FidoFormatScanService(CentralDatabaseDAO dao){
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
	@Override
	public List<FileWithFileFormat> identify(List<FileWithFileFormat> files) throws FileNotFoundException {
//		for (FileWithFileFormat f:files){
//			if (!f.toRegularFile().exists()) throw new FileNotFoundException("file "+f.toRegularFile().getPath()+" doesn't exist");
//		}	
		for (FileWithFileFormat f:files){
			f.setFormatPUID(getPronomFormatIdentifier().getPuidForFile(f));
			logger.debug(f+" has puid "+f.getFormatPUID()+". Now searching if second stage scan policy is applicable");
			
			for (SecondStageScanPolicy scan:secondStageScans){
				if (!f.getFormatPUID().equals(scan.getPUID())) continue;
				logger.trace("Policy found: "+scan);
				
				for (CLIFormatIdentifier additionalIdentifier:formatSecondAttributeIdentifiers){
					if (!additionalIdentifier.getConversionScript().getName().equals(scan.getFormatIdentifierScriptName())) continue;
					logger.trace("found an identifier with identification script \""+scan.getFormatIdentifierScriptName()+"\" required by policy");	
					
					// TODO resolve
					Set<String> resultList = additionalIdentifier.identify(f);
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
	 * Gets the pronom format identifier.
	 *
	 * @return the pronom format identifier
	 */
	public PronomFormatIdentifierWrapper getPronomFormatIdentifier() {
		return pronomFormatIdentifier;
	}

	/**
	 * Sets the pronom format identifier.
	 *
	 * @param pronomFormatIdentifier the new pronom format identifier
	 */
	public void setPronomFormatIdentifier(PronomFormatIdentifierWrapper pronomFormatIdentifier) {
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
