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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Session;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.ff.FileFormatException;
import de.uzk.hki.da.ff.FileFormatFacade;
import de.uzk.hki.da.ff.IFileWithFileFormat;
import de.uzk.hki.da.ff.ISubformatIdentificationPolicy;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.SecondStageScanPolicy;
import de.uzk.hki.da.utils.CommaSeparatedList;

/**
 * Creates metadata files from extracted jhove output and puts them to the folder jhove_temp
 * below the objects data folder, which can be used in following actions. 
 * The metadata files are named by a md5 hash.
 * <br><br>
 * Example:
 * <ul>
 * <li>File: WorkAreaRootPath/work/csn/oid/data/repname/sub/a.jpg
 * <li>Jhove: WorkAreaRootPath/work/csn/oid/data/jhove_temp/repname/md5hashed(sub/a.jpg)
 * </ul>
 * 
 * @author Daniel M. de Oliveira
 */
public class CheckFormatsAction extends AbstractAction {


	private FileFormatFacade fileFormatFacade;


	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		// Auto-generated method stub
	}

	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		// Auto-generated method stub
		
	}

	@Override
	public boolean implementation() throws FileNotFoundException, IOException {
		
		List<IFileWithFileFormat> allFiles = new ArrayList<IFileWithFileFormat>();
		List<DAFile> allDAFiles = new ArrayList<DAFile>();
		for (Package p:object.getPackages()){
				allFiles.addAll(p.getFiles());
				allDAFiles.addAll(p.getFiles());
		}
		
		Session session = HibernateUtil.openSession();
		List<SecondStageScanPolicy> policies = 
				preservationSystem.getSubformatIdentificationPolicies();
		session.close();
		
		List<ISubformatIdentificationPolicy> polys = new ArrayList<ISubformatIdentificationPolicy>();
		for (SecondStageScanPolicy s:policies)
			polys.add((ISubformatIdentificationPolicy) s);
		getFileFormatFacade().setSubformatIdentificationPolicies(polys);

		try {
			allFiles = getFileFormatFacade().identify(allFiles);
		} catch (FileFormatException e) {
			throw new RuntimeException(C.ERROR_MSG_DURING_FILE_FORMAT_IDENTIFICATION,e);
		}
		
		for (IFileWithFileFormat f:allFiles){
			if (f.getFormatPUID()==null) throw new RuntimeException("file \""+f+"\" has no format puid");
		}
		attachJhoveInfoToAllFiles(allDAFiles);
		
		List<DAFile> newestFiles = object.getNewestFilesFromAllRepresentations(preservationSystem.getSidecarExtensions());
		
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
				new CommaSeparatedList(new ArrayList<String>(getPUIDsForAllRepAFiles(allDAFiles))).toString() // hack necessary again?
						);
		
		return true;
	}

	@Override
	public void rollback() throws Exception {
		throw new NotImplementedException("No rollback implemented for this action");
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
			// dir
			String dir = Path.make(object.getDataPath(),"jhove_temp",f.getRep_name()).toString();
			String fileName = DigestUtils.md5Hex(f.getRelative_path());
			
			if (!new File(dir).exists()) new File(dir).mkdirs();
			
			File target = Path.makeFile(dir,fileName);
			logger.debug("will write jhove output to: "+target);
			fileFormatFacade.extract(f.toRegularFile(), target);
		}
	}

	public FileFormatFacade getFileFormatFacade() {
		return fileFormatFacade;
	}

	public void setFileFormatFacade(FileFormatFacade fileFormatFacade) {
		this.fileFormatFacade = fileFormatFacade;
	}
}
