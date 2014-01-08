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

package de.uzk.hki.da.service;

import java.io.File;
import java.io.StringReader;
import java.util.Date;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.metadata.MetsRightsSectionXmlReader;
import de.uzk.hki.da.metadata.PremisXmlWriter;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.contract.MigrationRight;
import de.uzk.hki.da.model.contract.PublicationRight;
import de.uzk.hki.da.model.contract.RightsStatement;


/**
 * Identify package types generically. (Is it a mets or bagit type container,
 * which metadata are contained (ead, lido...)
 * 
 * @author Sebastian Cuy
 */
public class PremisCreator {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(PremisCreator.class);

	/**
	 * Creates the premis from mets.
	 *
	 * @param metsFilePath the mets file path
	 * @param premisFilePath the premis file path
	 * @param contractor the contractor
	 * @throws IdentifyPackageException the identify package exception
	 */
	public void createPremisFromMets(String metsFilePath, String premisFilePath,Contractor contractor) throws IdentifyPackageException {
		
		try {
		
		// read METS file and extract rights section
		SAXBuilder builder = new SAXBuilder(false);
		Document doc = builder.build(new File(metsFilePath));
		XPath xPath = XPath.newInstance("/mets:mets/mets:amdSec/mets:rightsMD/mets:mdWrap/mets:xmlData/da:rightsGranted");
		xPath.addNamespace("mets", "http://www.loc.gov/METS/");
		xPath.addNamespace("da", "http://www.danrw.de/contract/v1");
		Element rightsNode = (Element) xPath.selectSingleNode(doc);
		
		Object object = new Object();
		object.setContractor(contractor);

		// if no rights given construct empty PREMIS file
		if (rightsNode == null) {
			logger.info("no rights found in METS file, constructing empty PREMIS file");
			
		// else deserialize rights section
		} else {
			
			XMLOutputter out = new XMLOutputter();
			String rightsString = out.outputString(rightsNode);
			
			List<RightsStatement> rights= new MetsRightsSectionXmlReader()
					.deserialize(new StringReader(rightsString));
			
			// add start dates if necessary in order to create a valid premis file
			if (rights.get(0).getPublicationRights() != null) {
				for (PublicationRight p : rights.get(0).getPublicationRights()) {
					if (p != null && p.getStartDate() == null)
						p.setStartDate(new Date());
				}
			}
			MigrationRight m = rights.get(0).getMigrationRight();
			if (m != null && m.getStartDate() == null)
				m.setStartDate(new Date());
			
			object.setRights(rights.get(0));
		}
		
		
		object.getPackages().add(new Package());
		object.setIdentifier("null");
		
		// serialize rights section in premis
		new PremisXmlWriter().serialize(object, new File(premisFilePath));
		
		} catch (Exception e) {
			throw new IdentifyPackageException("Error while generating PREMIS file from METS rights section.",e);
		}
		
	}

	/**
	 * The Class IdentifyPackageException.
	 */
	public static class IdentifyPackageException extends Exception {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 8676596720652020447L;

		/**
		 * Instantiates a new identify package exception.
		 *
		 * @param msg the msg
		 * @param e the e
		 */
		public IdentifyPackageException(String msg, Throwable e) {
			super(msg,e);
		}
		
	}

}
