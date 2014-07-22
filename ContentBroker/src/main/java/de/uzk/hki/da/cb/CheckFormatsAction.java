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

import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.format.FormatScanService;
import de.uzk.hki.da.format.JhoveScanService;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.utils.CommaSeparatedList;

/**
 * @author Daniel M. de Oliveira
 */
public class CheckFormatsAction extends AbstractAction {


	private String sidecarExtensions;

	private FormatScanService formatScanService;

	private JhoveScanService jhoveScanService;

	@Override
	boolean implementation() throws FileNotFoundException, IOException {
		
		List<DAFile> allFiles = new ArrayList<DAFile>();
		for (Package p:object.getPackages()){
				allFiles.addAll(p.getFiles());
				
		}
		allFiles = getFormatScanService().identify(allFiles);
		
		for (DAFile f:allFiles){
			if (f.getFormatPUID()==null) throw new RuntimeException("file \""+f+"\" has no format puid");
		}
		attachJhoveInfoToAllFiles(allFiles);
		

		List<DAFile> newestFiles = object.getNewestFilesFromAllRepresentations(sidecarExtensions);
		
		Set<String> mostRecentFormats = new HashSet<String>();
		Set<String> mostRecentSecondaryAttributes = new HashSet<String>();
		
		for (DAFile f:newestFiles){
			mostRecentFormats.add(f.getFormatPUID());
			if (!f.getFormatSecondaryAttribute().isEmpty())
				mostRecentSecondaryAttributes.add(f.getFormatSecondaryAttribute());
			if (f.getFormatSecondaryAttribute()==null||f.getFormatSecondaryAttribute().isEmpty()) continue;
		}
		
		// TODO remove. send via communicator. this should not be saved to object this early. 
		object.setMost_recent_formats(new CommaSeparatedList(new ArrayList<String>(mostRecentFormats)).toString());
		object.setMostRecentSecondaryAttributes(new CommaSeparatedList(new ArrayList<String>(mostRecentSecondaryAttributes)).toString());
		object.setOriginal_formats(
				new CommaSeparatedList(new ArrayList<String>(getPUIDsForAllRepAFiles(allFiles))).toString() // hack necessary again?
						);
		
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
			String jhoveOut = jhoveScanService.extract(f.toRegularFile(), job.getId());

			f.setPathToJhoveOutput(jhoveOut);
			logger.debug("Path to jhove output for file \""+f+"\": " + jhoveOut);
		}
	}

	@Override
	void rollback() throws Exception {
		throw new NotImplementedException("No rollback implemented for this action");
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
