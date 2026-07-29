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

import static de.uzk.hki.da.cb.RestructureAction.makeRepOfSIPContent;
import static de.uzk.hki.da.cb.RestructureAction.revertToSIPContent;
import static de.uzk.hki.da.utils.StringUtilities.isNotSet;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.Path;


/**
 * Resets job in ingest workflow back to the start status of the ingest workflow.
 * Suited to reset jobs from between 12x and 36x, which is a portion of the ingest workflow (beans-workflow.ingest.xml).
 * Removes all artifacts generated during the ingest workflow and puts the contents of only the newest a representation back
 * to the data folder which is by definition similar to the state of the unpacked SIP.
 * 
 * @author Thomas Kleinke
 * @author Daniel M. de Oliveira
 */
public class RestartIngestWorkflowAction extends AbstractAction {

	private static final String A = "a";
	private static final String UNDERSCORE = "_";

	public RestartIngestWorkflowAction(){SUPPRESS_OBJECT_CONSISTENCY_CHECK=true;}

	@Override
	public void checkConfiguration() {
	}
	

	@Override
	public void checkPreconditions() {
	}
	
	@Override
	public boolean implementation() throws IOException {

		if (!o.isDelta())
			o.setUrn(null);
		
		Path dP = wa.dataPath();
		String repName = evaLatestARep();
		if (repName != null) {
			logger.debug("found rep: " + repName + "will revert to sip content");
			Path oP = wa.objectPath();
			revertToSIPContent(oP, dP, repName);
			deleteTemporaryPIPs();
//		} else if (ingestContainerStillExists()) {
//			logger.debug("ingestContainerStillExists. will revert to ingest container");
//			revertToIngestContainer();
		} else if (dP.toFile().exists()) {
			logger.debug("dataPath exists. will restart restructure action");
		} else {
			throw new IllegalArgumentException(" unable to examine state before restart");
		}
		
		clearNonpersistentObjectProperties(o);
		j.getConversion_instructions().clear();
		return true;
	}
	
	protected boolean ingestContainerStillExists() {
		Path path = Path.make(
				n.getIngestAreaRootPath(),
				o.getContractor().getShort_name(),
				o.getLatestPackage().getContainerName());
		File file = path.toFile();
		java.nio.file.Path patho;
		
		BasicFileAttributes attrs;
		try {
			patho = Paths.get(file.getCanonicalPath());
		    attrs = Files.readAttributes(patho, BasicFileAttributes.class);
			FileTime cr = attrs.creationTime();
			FileTime md = attrs.lastModifiedTime();
			FileTime ac = attrs.lastAccessTime();
			logger.debug("Cr:" + cr + " Md:" + md + " Ac:" + ac);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean exists = file.exists();
		return exists;
	}
	
	protected void revertToIngestContainer() {
		File waFile = wa.sipFile();
		if (waFile.isDirectory()) {
			try {
				FolderUtils.deleteDirectorySafe(waFile);
			} catch (IOException e) {
			}
		} else {
			waFile.delete();
		}

		Path oP = wa.objectPath();
		File oPFile = oP.toFile();
		try {
			FolderUtils.deleteDirectorySafe(oPFile);
		} catch (IOException e) {
		}
		setEndStatus(C.WORKFLOW_STATUS_START___INGEST_UNPACK_ACTION);
	}
	
	String evaLatestARep() {
		FilenameFilter fileNameFilter = new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		    	if ( name.length() > 20
		    	  && Character.isDigit(name.charAt(0))
				  && Character.isDigit(name.charAt(1))
				  && Character.isDigit(name.charAt(2))
				  && Character.isDigit(name.charAt(3))
				  && name.charAt(4) == '_'
				  && Character.isDigit(name.charAt(5))
				  && Character.isDigit(name.charAt(6))
				  && name.charAt(7) == '_'
				  && Character.isDigit(name.charAt(8))
				  && Character.isDigit(name.charAt(9))
				  && name.charAt(10) == '+'
				  && Character.isDigit(name.charAt(11))
				  && Character.isDigit(name.charAt(12))
				  && name.charAt(13) == '_'
				  && Character.isDigit(name.charAt(14))
				  && Character.isDigit(name.charAt(15))
				  && name.charAt(16) == '_'
				  && Character.isDigit(name.charAt(17))
				  && Character.isDigit(name.charAt(18))
				  && name.charAt(19) == '+'
				  && name.charAt(20) == 'a'){
			    	return true;
		    	}
		    	return false;
		    };
	    };
	
		File dataDir = wa.dataPath().toFile();
		File[] reptiles = dataDir.listFiles(fileNameFilter);
		
		if (reptiles == null || reptiles.length == 0) {
			return null;
		}
		
		String turtleName = reptiles[0].getName();
		
		for (int iii=1; iii<reptiles.length; iii++) {
			String krokoName = reptiles[iii].getName();
			int cmpRet = krokoName.compareTo(turtleName);
			if (cmpRet > 0) {
				turtleName = krokoName; 
			}

		}
		turtleName = turtleName.substring(0, 20);
		
		return turtleName;
		}
	
	@Override
	public void rollback() throws Exception {
		if (isNotSet(j.getRep_name())) throw new IllegalStateException("Rep name not set.");
		
		if (	thereIsNoARepresentation()
				&&(wa.dataPath().toFile().exists())
				&&dataIsOnlySubfolderOfObject()) {
			
			makeRepOfSIPContent(wa.objectPath(), wa.dataPath(), j.getRep_name());
		} 
		else {
			throw new RuntimeException("Rollback not possible.");
		}
	}

	
	
	private boolean dataIsOnlySubfolderOfObject() {
		String subfolders[] = wa.objectPath().toFile().list();
		if (subfolders.length!=1) return false;
		if (!subfolders[0].equals(WorkArea.DATA)) return false;
		return true;
	}


	private boolean thereIsNoARepresentation() {
		return (!Path.makeFile(wa.dataPath(),j.getRep_name()+A).exists());
	}


	/**
	 * @author Thomas Kleinke
	 */
	private void deleteTemporaryPIPs() throws IOException {
		
		if (makePIPSourceFolder(WorkArea.PUBLIC).exists())
			FolderUtils.deleteDirectorySafe(makePIPSourceFolder(WorkArea.PUBLIC));
		if (makePIPSourceFolder(WorkArea.WA_INSTITUTION).exists())
			FolderUtils.deleteDirectorySafe(makePIPSourceFolder(WorkArea.WA_INSTITUTION));
	}
	
	private File makePIPSourceFolder(String pipType) {
		return Path.makeFile(n.getWorkAreaRootPath(),WorkArea.PIPS,pipType,o.getContractor().getShort_name(),o.getIdentifier()+UNDERSCORE+o.getLatestPackage().getId());
	}
}
