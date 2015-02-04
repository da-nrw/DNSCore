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

import static de.uzk.hki.da.core.C.WA_DIP;
import static de.uzk.hki.da.core.C.WA_INSTITUTION;
import static de.uzk.hki.da.core.C.WA_PIPS;
import static de.uzk.hki.da.core.C.WA_PUBLIC;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.io.FileUtils;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.ObjectPremisXmlReader;
import de.uzk.hki.da.model.PublicationRight.Audience;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.util.RelativePath;


/**
 * First moves the dip out of the package to another location.
 * Then evaluates contract rights for publication and trims down bitstreams
 * of PIP that aren't allowed to get published.
 * @author Daniel M. de Oliveira
 */
public class PrepareSendToPresenterAction extends AbstractAction {

	private static final String UNDERSCORE = "_";
	private static final String PREMIS_XML = "premis.xml";
	private DistributedConversionAdapter distributedConversionAdapter;
	private File publDir;
	private File instDir;
	
	/**
	 * @
	 */
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		if (distributedConversionAdapter==null) throw new ConfigurationException("distributedConversionAdapter not set");
	}


	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		// Auto-generated method stub
	}


	@Override
	public boolean implementation() throws IOException {
		
		String dipName = o.getContractor().getShort_name() + "/" + o.getIdentifier()+UNDERSCORE+o.getLatestPackage().getId();
		publDir = Path.makeFile(n.getWorkAreaRootPath(),WA_PIPS,WA_PUBLIC,dipName);
		instDir = Path.makeFile(n.getWorkAreaRootPath(),WA_PIPS,WA_INSTITUTION,dipName);
		
		logger.trace("Moving the dip content for presentation purposes out of the archival package.");
		copyPIPSforReplication();
		
		Object premisObject = readRightsFromPREMIS(o.getLatest(PREMIS_XML).toRegularFile());
		deleteNotAllowedDataStreams(premisObject,publDir,instDir);
		
		registerPIPSforReplication(dipName);
		return true;
	}


	private void deleteNotAllowedDataStreams(Object premisObject,File publDir,File instDir)
			throws IOException {
		if (premisObject==null) throw new IllegalArgumentException("Must not be null: premisObject");
		
		if (!premisObject.grantsRight("PUBLICATION")) {
			logger.info("Publication Right not granted. Will delete datastreams");
			if (publDir.exists()) FileUtils.deleteDirectory(publDir);
			if (instDir.exists()) FileUtils.deleteDirectory(instDir);
		} else
		if (!premisObject.grantsPublicationRight(Audience.PUBLIC)) {
			logger.info("Publication Right for audience public not granted. Will delete public datastreams.");
			if (publDir.exists()) FileUtils.deleteDirectory(publDir);
		} else
		if (!premisObject.grantsPublicationRight(Audience.INSTITUTION)) {
			logger.info("Publication Right for audience institution not granted. Will delete institution datastreams.");
			if (instDir.exists()) FileUtils.deleteDirectory(instDir);
		}
	}


	@Override
	public void rollback() throws Exception {
		if (publDir.exists()) FileUtils.deleteDirectory(publDir);
		if (instDir.exists()) FileUtils.deleteDirectory(instDir);
		logger.info("@Admin: You can safely roll back this job to status "+this.getStartStatus()+" now.");
	}


	/**
	 * @author Daniel M. de Oliveira
	 */
	static Object readRightsFromPREMIS(File premisXML) throws IOException {
		if (premisXML==null) throw new IllegalArgumentException("premisXML is null");
		if (! premisXML.exists()) throw new FileNotFoundException("Missing file or directory: "+premisXML);
		
		Object premisObject = null;
		try {

			premisObject = new ObjectPremisXmlReader().deserialize(
					premisXML);
			
		} catch (ParseException pe){
			throw new RuntimeException("error while parsing PREMIS-file",pe);
		}
		return premisObject;
	}


	/**
	 * @param dipName has the form [csn]/[oid]_[pkgid]
	 */
	private void registerPIPSforReplication(String dipName) {
		
		if (!publDir.exists())
			distributedConversionAdapter.create(new RelativePath("pips","public",dipName).toString());
		else
			distributedConversionAdapter.register(new RelativePath
				("pips","public",dipName).toString(),
				publDir.getAbsolutePath()
				);
		
		if (!instDir.exists())
			distributedConversionAdapter.create(new RelativePath("pips","institution",dipName).toString());
		else
			distributedConversionAdapter.register(new RelativePath(
				"pips","institution",dipName).toString(),
				instDir.getAbsolutePath()
				);
	}


	/**
	 * The xIP (in case PUBLIC right is set) contains converted files that are 
	 * meant only to be presented in a portal but not to be archived. So to prevent these files
	 * get into the archive we extract the derivate before we build a tar file around the xIP.
	 * 
	 * Moves 
	 * <li>from work/[csn]/[oid]/data/temp_pips/public -> dip/public/[csn]/[oid]
	 * <li>from work/[csn]/[oid]/data/temp_pips/institution -> dip/institution/[csn]/[oid]
	 * 
	 * @param dipName has the form urn_pkgid
	 * @throws IOException 
	 */
	private void copyPIPSforReplication() throws IOException {

		if (Path.makeFile(o.getDataPath(),WA_DIP,WA_PUBLIC).exists()){
			logger.info("Copying public datastreams to " + publDir.getAbsolutePath());
			if (publDir.exists()) FileUtils.deleteDirectory(publDir);
			FileUtils.copyDirectory(
					Path.make(o.getDataPath(),WA_DIP,WA_PUBLIC).toFile(), 
					publDir);
		}
		if (Path.makeFile(o.getDataPath(),WA_DIP,WA_INSTITUTION).exists()){
			logger.info("Copying institution datastreams to " + instDir);
			if (instDir.exists()) FileUtils.deleteDirectory(instDir);
			FileUtils.copyDirectory(
					Path.make(o.getDataPath(),WA_DIP,WA_PUBLIC).toFile(), 
					instDir);
		}
	}
	
	

	public DistributedConversionAdapter getDistributedConversionAdapter() {
		return distributedConversionAdapter;
	}


	public void setDistributedConversionAdapter(
			DistributedConversionAdapter distributedConversionAdapter) {
		this.distributedConversionAdapter = distributedConversionAdapter;
	}
}
