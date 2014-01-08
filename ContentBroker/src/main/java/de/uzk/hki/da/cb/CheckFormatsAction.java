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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.format.FormatScanService;
import de.uzk.hki.da.format.JhoveScanService;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.utils.CommaSeparatedList;

public class CheckFormatsAction extends AbstractAction {

	static final Logger logger = LoggerFactory
			.getLogger(CheckFormatsAction.class);

	private String sidecarExtensions;

	private FormatScanService formatScanService;

	private JhoveScanService jhoveScanService;

	@Override
	boolean implementation() throws FileNotFoundException, IOException {
		object.reattach();
		
		logger.debug("listing file instances attached to latest package");
		for (DAFile f:object.getLatestPackage().getFiles()){
			logger.debug(f.toString());
		}
		
		
		
		List<DAFile> unattachedFileInstances = object.getAllFiles(); // XXX Hack. These instances are not attached.
		unattachedFileInstances = getFormatScanService().identify(unattachedFileInstances); // = is for mock during testing
		for (DAFile unattachedFileInstance:unattachedFileInstances){
			logger.debug("unattachedFileInstance: "+unattachedFileInstance+" puid["+unattachedFileInstance.getFormatPUID()+"]/secondaryAttribute/["+unattachedFileInstance.getFormatSecondaryAttribute()+"]");

			// XXX Hack since the files are not attached search through all file instances that are attached 
			// to the object and copy the file information into the attached dafile instances.
			for (Package pkg:object.getPackages()){
				for (DAFile objectfile:pkg.getFiles()){
					
					if (objectfile.equals(unattachedFileInstance)){ // is attached to object
						logger.debug("will update attached instance ");
						objectfile.setFormatPUID(unattachedFileInstance.getFormatPUID());
						objectfile.setFormatSecondaryAttribute(unattachedFileInstance.getFormatSecondaryAttribute());
					}
				}
			}
		}

		List<DAFile> newestFiles = object.getNewestFilesFromAllRepresentations(sidecarExtensions);
		Set<String> mostRecentFormats = new HashSet<String>();
		Set<String> mostRecentSecondaryAttributes = new HashSet<String>();
		
		for (DAFile f:newestFiles){
			
			// XXX same hack again
			for (DAFile af:unattachedFileInstances){
				if (af.equals(f)){
					f.setFormatPUID(af.getFormatPUID());
					f.setFormatSecondaryAttribute(af.getFormatSecondaryAttribute());
				}
			}
			
			if (f.getFormatPUID()==null) throw new RuntimeException("file \""+f+"\" has no format puid");
			mostRecentFormats.add(f.getFormatPUID());
			if (!f.getFormatSecondaryAttribute().isEmpty())
				mostRecentSecondaryAttributes.add(f.getFormatSecondaryAttribute());
			if (f.getFormatSecondaryAttribute()==null||f.getFormatSecondaryAttribute().isEmpty()) continue;
		}
		
		// TODO remove. send via communicator. this should not be saved to object this early. 
		object.setMost_recent_formats(new CommaSeparatedList(new ArrayList<String>(mostRecentFormats)).toString());
		object.setMostRecentSecondaryAttributes(new CommaSeparatedList(new ArrayList<String>(mostRecentSecondaryAttributes)).toString());
		object.setOriginal_formats(
				new CommaSeparatedList(new ArrayList<String>(getPUIDsForAllRepAFiles(unattachedFileInstances))).toString() // hack necessary again?
				                                                                                           // error check for puid is existent
						);
		
		attachJhoveInfoToAllFiles(object.getLatestPackage().getFiles());
		
		return true;
	}

	/**
	 * @return
	 */
	private Set<String> getPUIDsForAllRepAFiles(List<DAFile> allFiles) {
		Set<String> originalFormatsSet = new HashSet<String>();
		for (DAFile f : allFiles) {
			if (f.getRep_name().endsWith("a"))
				originalFormatsSet.add(f.getFormatPUID());
		}
		return originalFormatsSet;
	}

	/**
	 * Scans every file in files with jhove and sets the pathToJhoveOutput
	 * property accordingly
	 * 
	 * @param files
	 * @throws IOException 
	 */
	private void attachJhoveInfoToAllFiles(List<DAFile> files) throws IOException {
		for (DAFile f : files) {
			String jhoveOut = jhoveScanService.extract(f, job.getId());

			f.setPathToJhoveOutput(jhoveOut);
			logger.debug("Path to jhove output for file \""+f+"\": " + jhoveOut);
		}
	}

	@Override
	void rollback() throws Exception {
	}

	public FormatScanService getFormatScanService() {
		return formatScanService;
	}

	public void setFormatScanService(FormatScanService formatScanService) {
		this.formatScanService = formatScanService;
	}

	public JhoveScanService getJhoveScanService() {
		return jhoveScanService;
	}

	public void setJhoveScanService(JhoveScanService jhoveScanService) {
		this.jhoveScanService = jhoveScanService;
	}

	public void setSidecarExtensions(String sidecarExtensions) {
		this.sidecarExtensions = sidecarExtensions;
	}

	public String getSidecarExtensions() {
		return sidecarExtensions;
	}

}
