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
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.service.Mail;
import de.uzk.hki.da.utils.ArchiveBuilder;
import de.uzk.hki.da.utils.ArchiveBuilderFactory;
import de.uzk.hki.da.utils.BagitUtils;
import de.uzk.hki.da.utils.Path;


/**
 * Retrieves Packages from the DataGrid.
 * Does a MD5 Check and copies DIP to the outgoing folder of User. 
 * @author Jens Peters
 * @author Daniel M. de Oliveira
 * @author Thomas Kleinke
 */

public class RetrievalAction extends AbstractAction {
	
	private String sidecarExtensions;
	private DistributedConversionAdapter distributedConversionAdapter;
	
	public RetrievalAction(){}
	
	@Override
	protected
	boolean implementation() {
		
		ArchiveBuilder builder = ArchiveBuilderFactory.getArchiveBuilderForFile(new File(".tar"));

		String tempFolder = Path.make(localNode.getWorkAreaRootPath(),
				object.getContractor().getShort_name(), object.getIdentifier(), object.getIdentifier()) + "/";
		
		new File(tempFolder).mkdir();
		File premisFile = Path.makeFile(object.getDataPath(),object.getNameOfNewestBRep(),"/premis.xml");
		
		if (premisFile.exists())
		{
			File dest = new File(tempFolder + "data/premis.xml");
			try {
				FileUtils.copyFile(premisFile, dest);
			} catch (IOException e) {
				throw new UserException(UserExceptionId.RETRIEVAL_ERROR, "Couldn't copy file " + premisFile.getAbsolutePath() + " to " + dest.getAbsolutePath(), e);
			}
		} else
			throw new UserException(UserExceptionId.RETRIEVAL_ERROR, "Invalid AIP: No premis file in folder " + object.getDataPath() + object.getNameOfNewestBRep());
		
		copySurfaceRepresentation(object,tempFolder);
		
		logger.trace("Building BagIt");
		BagitUtils.buildBagit(tempFolder);
		
		// Repacking
		Path newTar = Path.make(localNode.getUserAreaRootPath(),object.getContractor().getShort_name(),"outgoing",object.getIdentifier() + ".tar");
		logger.debug("Building tar at " + newTar);
		try {
			builder.archiveFolder(new File(tempFolder),
								  newTar.toFile(), true);

		} catch (Exception e) {
			throw new UserException(UserExceptionId.RETRIEVAL_ERROR, "Tar couldn't be packed", e);
		} 
		
		try {
			FileUtils.deleteDirectory(new File(tempFolder));
		} catch (Exception e) {
			throw new UserException(UserExceptionId.RETRIEVAL_ERROR, "Error while deleting temp folder", e);
		}
		
		String relativePackagePath = object.getContractor().getShort_name() + "/" + object.getIdentifier() + "/";
		File packageFolder = Path.makeFile(localNode.getWorkAreaRootPath(),relativePackagePath);
		
		try {
			FileUtils.deleteDirectory(packageFolder);
		} catch (IOException e) {
			throw new UserException(UserExceptionId.RETRIEVAL_ERROR, "Couldn't delete folder " + packageFolder.getAbsolutePath(), e);
		}
		
		distributedConversionAdapter.remove("work/" + relativePackagePath.replaceAll("/$", "")); // replace all -> iRODS doesn't like trailing slashes
		
		emailReport(object.getContractor().getEmail_contact(),object.getIdentifier(),object.getContractor().getShort_name());
		return true;
	}

	
	
	
	
	
	
	/**
	 * @param destinationFolder
	 * @throws RuntimeException
	 */
	private void copySurfaceRepresentation(Object o, String destinationFolder)
			throws RuntimeException {
		
		List<DAFile> files = o.getNewestFilesFromAllRepresentations(sidecarExtensions);
		for (DAFile f : files)
		{
			if (!f.toRegularFile().getName().equals("premis.xml"))
			{
				File dest = new File(destinationFolder + "data/" + f.getRelative_path());
				logger.info("file will be part of dip: "+dest.getAbsolutePath());
				String destFolder = dest.getAbsolutePath().substring(0, dest.getAbsolutePath().lastIndexOf("/"));

				new File(destFolder).mkdirs();

				try {
					FileUtils.copyFile(f.toRegularFile(), dest);
				} catch (IOException e) {
					throw new UserException(UserExceptionId.RETRIEVAL_ERROR, "Couldn't copy file " + f.toRegularFile().getAbsolutePath() + " to folder " + destFolder, e);
				}
			}
		}
	}
	
	
	
	
	private void emailReport(String email,String objectIdentifier,String csn){
		
		String subject = "Retrieval Report für " + objectIdentifier;
		String msg = "Ihr angefordertes Objekt mit dem Namen \""+ objectIdentifier + "\" wurde unter Ihrem Outgoing Ordner unter " 
				+ csn + "/outgoing/ abgelegt und steht jetzt"
				+ " zum Retrieval bereit!\n\n";
		if (email != null) {
			try {
				Mail.sendAMail(getSystemFromEmailAdress(),email, subject, msg);
			} catch (MessagingException e) {
				logger.error("Sending email retrieval reciept for " + objectIdentifier + "failed", e);
			}
		} else {
			logger.warn(csn + " has no valid Email address!");
		}
	}
	
	@Override
	void rollback() {
		throw new NotImplementedException("No rollback implemented for this action");
	}
	
	public void setSidecarExtensions(String sidecarExtensions) {
		this.sidecarExtensions = sidecarExtensions;
	}

	public String getSidecarExtensions() {
		return sidecarExtensions;
	}


	public DistributedConversionAdapter getDistributedConversionAdapter() {
		return distributedConversionAdapter;
	}

	public void setDistributedConversionAdapter(
			DistributedConversionAdapter distributedConversionAdapter) {
		this.distributedConversionAdapter = distributedConversionAdapter;
	}
}
