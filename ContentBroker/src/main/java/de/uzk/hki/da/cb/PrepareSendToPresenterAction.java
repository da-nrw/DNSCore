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

package de.uzk.hki.da.cb;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.io.FileUtils;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.metadata.PremisXmlReader;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.contract.PublicationRight.Audience;


/**
 * First moves the dip out of the package to another location.
 * Then evaluates contract rights for publication and trims down bitstreams
 * of PIP that aren't allowed to get be published.
 * @author Daniel M. de Oliveira
 */
public class PrepareSendToPresenterAction extends AbstractAction {

	private DistributedConversionAdapter distributedConversionAdapter;
	
	@Override
	boolean implementation() throws IOException {
		if (distributedConversionAdapter==null) throw new ConfigurationException("distributedConversionAdapter not set");
		object.reattach();
		
		
		String dipName = object.getContractor().getShort_name() + "/" + object.getIdentifier()+"_"+object.getLatestPackage().getId();
		logger.trace("Moving the dip content for presentation purposes out of the archival package.");
		extractDerivateForPresentation(dipName);
		
		
		Object premisObject = readRightsFromPREMIS();
		
		
		if (!premisObject.grantsRight("PUBLICATION")) {
			logger.info("PUBLICATION Right not granted. Will delete datastreams.");
			if (new File(localNode.getDipAreaRootPath()+"public/"+dipName).exists())
				FileUtils.deleteDirectory(
						new File(localNode.getDipAreaRootPath()+"public/"+dipName));
			if (new File(localNode.getDipAreaRootPath()+"institution/"+dipName).exists())
				FileUtils.deleteDirectory(
					new File(localNode.getDipAreaRootPath()+"institution/"+dipName));
		} else
		if (!premisObject.grantsPublicationRight(Audience.PUBLIC)) {
			logger.info("Rights for PUBLIC not granted. Will delete datastreams.");
			if (new File(localNode.getDipAreaRootPath()+"public/"+dipName).exists())
				FileUtils.deleteDirectory(
					new File(localNode.getDipAreaRootPath()+"public/"+dipName));
		} else
		if (!premisObject.grantsPublicationRight(Audience.INSTITUTION)) {
			logger.info("Rights for INSTITUTION not granted. Will delete datastreams.");
			if (new File(localNode.getDipAreaRootPath()+"/institution/"+dipName).exists())
				FileUtils.deleteDirectory(
					new File(localNode.getDipAreaRootPath()+"institution/"+dipName));
		}
		
		
		
		irodsRegisterStuffForReplication(dipName);
		distributedConversionAdapter.remove("fork/"+object.getContractor().getShort_name()+"/"
			+object.getIdentifier()+"/data/dip");
		
		return true;
	}


	/**
	 * @author Daniel M. de Oliveira
	 * @return
	 * @throws IOException
	 */
	private Object readRightsFromPREMIS() throws IOException {
		Object premisObject = null;
		try {
			PremisXmlReader reader = new PremisXmlReader();

			File premisFile = new File(object.getDataPath() +
					object.getNameOfNewestBRep() + "/premis.xml");
			
			premisObject = reader.deserialize(premisFile);
		} catch (ParseException pe){
			throw new RuntimeException("error while parsing PREMIS-file",pe);
		}
		return premisObject;
	}


	/**
	 * @author Daniel M. de Oliveira
	 * @param dipName has the form csn/urn_pkgid
	 */
	private void irodsRegisterStuffForReplication(String dipName) {
		
		String publPartialPath = "public/"+dipName;
		register(publPartialPath);
		String instPartialPath = "institution/"+dipName;
		register(instPartialPath);
	}


	/**
	 * @author Daniel M. de Oliveira
	 * @param pipPartialPath
	 */
	private void register(String pipPartialPath) {
		if (!new File(localNode.getDipAreaRootPath()+pipPartialPath).exists()){
			// So Fedora can in any case ingest an empty object as our interface (through SendToPresenterAction) requires.
			logger.info("Since the dip collection at "+pipPartialPath+" is empty create an empty collection");
			new File(localNode.getDipAreaRootPath()+pipPartialPath).mkdir();
			distributedConversionAdapter.create("dip/"+pipPartialPath);
		}
		distributedConversionAdapter.register(
				"dip/"+pipPartialPath, 
				localNode.getDipAreaRootPath()+pipPartialPath
				);
	}


	/**
	 * The xIP (in case PUBLIC right is set) contains converted files that are 
	 * meant only to be presented in a portal but not to be archived. So to prevent these files
	 * get into the archive we extract the derivate before we build a tar file around the xIP.
	 * 
	 * Moves 
	 * <li>from fork/csn/123/data/dip/public -> dip/public/TEST/objectIdentifier_123
	 * <li>from fork/csn/123/data/dip/institution -> dip/institution/TEST/objectIdentifier_123
	 * 
	 * @author Daniel M. de Oliveira
	 * @param dipName has the form urn_pkgid
	 * @throws IOException 
	 */
	private void extractDerivateForPresentation(String dipName) throws IOException {
		
		if (new File(object.getDataPath()+"dip/public").exists()){
			File publicDir = new File(localNode.getDipAreaRootPath()+"public/"+dipName);
			logger.info("Moving public datastreams to " + publicDir.getAbsolutePath());
			if (publicDir.exists()) FileUtils.deleteDirectory(publicDir);
			FileUtils.moveDirectory(
					new File(object.getDataPath()+"dip/public"), 
					publicDir);
		}
		if (new File(object.getDataPath()+"dip/institution").exists()){
			File instDir = new File(localNode.getDipAreaRootPath()+"institution/"+dipName);
			logger.info("Moving public datastreams to " + instDir);
			if (instDir.exists()) FileUtils.deleteDirectory(instDir);
			FileUtils.moveDirectory(
					new File(object.getDataPath()+"dip/institution"), 
					instDir);
		}
	}
	
	

	@Override
	void rollback() throws Exception {}


	public DistributedConversionAdapter getDistributedConversionAdapter() {
		return distributedConversionAdapter;
	}


	public void setDistributedConversionAdapter(
			DistributedConversionAdapter distributedConversionAdapter) {
		this.distributedConversionAdapter = distributedConversionAdapter;
	}
}
