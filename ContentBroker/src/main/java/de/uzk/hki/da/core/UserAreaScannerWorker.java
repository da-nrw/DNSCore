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

package de.uzk.hki.da.core;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.utilities.SimpleResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.utils.Utilities;


/**
 * Checks the TransferArea for collections of SIPs ("Lieferung").
 * This check is performed on the basis of a defined collection structure
 * (the SIPs are required to be placed inside of a BagIt-style folder).  
 * If a collection is ready, it automatically gets moved to the StagingArea
 * for further processing.
 * @author Daniel M. de Oliveira
 */
public class UserAreaScannerWorker {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(UserAreaScannerWorker.class);
	
	/** The contractor short names. */
	List<String> contractorShortNames = new ArrayList<String>();
	
	/**
	 * Data location from which the ContentBroker takes SIPs for 
	 * ingest.
	 */
	private String ingestAreaRootPath;
	
	/**	
	 * Data location which is used to exchange SIPs with clients.
	 * Beneath the root path there is a layer of folders named like
	 * the contractors' short names which in turn contain an incoming
	 * and and an outgoing directory.
	 */
	private String userAreaRootPath;
		
	
	
	/**
	 * Inits the.
	 */
	public void init(){
		if (!new File(userAreaRootPath).exists()) throw new RuntimeException("TransferArea doesn't exist.");
		
		logger.info("Scanning staging area for contractor folders");
		String children[] = new File(userAreaRootPath).list();

		for (int i=0;i<children.length;i++){
		
			logger.info(children[i]);
			contractorShortNames.add(children[i]);
		}
		
	}
	
	
	
	/**
	 * Checking for new collections in staging area.
	 */
	public void scheduleTask() {
		
		for (String contractorShortName:contractorShortNames) {
			
			try {
				moveCompletedCollections(
						userAreaRootPath+contractorShortName+"/incoming/",
						ingestAreaRootPath+contractorShortName+"/");
			} catch (IOException e) {
				logger.error("Ouch! Problem occured during checking for completed collections.");
			}
		}
	}


	
	/**
	 * Checks whether sourcePath contains
	 * collections which are bagit-complete. If so, the files get renamed and moved to targetPath.
	 * 
	 * The files inside a collection like this
	 * <pre>
	 * sourcePath/collection1/a.tgz
	 * /b.tgz
	 * /c.tgz
	 * </pre>
	 * 
	 * get moved to
	 * <pre>
	 * 
	 * targetPath/collection1%2fa.tgz
	 * targetPath/collection1%2fb.tgz
	 * targetPath/collection1%2fc.tgz
	 * </pre>
	 * 
	 * Note: The staging area protocol requires a "mv" of SIPs. "cp" must not be used!
	 *
	 * @param sourcePath the source path
	 * @param targetPath the target path
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @author Daniel M. de Oliveira
	 */
	void moveCompletedCollections(String sourcePath,String targetPath) throws IOException {
		sourcePath = Utilities.slashize(sourcePath);
		targetPath = Utilities.slashize(targetPath);
		
		List<String> finishedCollections = new ArrayList<String>();
		
		String children[] = new File (sourcePath).list();
		
		if (children == null)
			return;
		
		for (int i=0;i<children.length;i++){
			
			if (new File(sourcePath+"/"+children[i])
				.isDirectory()){

				BagFactory bagFactory = new BagFactory();
				Bag bag = bagFactory.createBag(new File(sourcePath+"/"+children[i]));
				SimpleResult completed = bag.verifyComplete();
				if (completed.isSuccess()) {
					
					SimpleResult valid = bag.verifyValid();
					if (valid.isSuccess()){
					
						finishedCollections.add(children[i]);
						logger.debug(sourcePath+"/"+children[i]+" is complete and valid. Its files will be moved one folder up.");
					}
				}
			}
		}
		
		// move the files of the completed collections
		for (String coll:finishedCollections){
			String collPath = sourcePath +"/"+coll;
			
			String collFiles[] = new File ( collPath + "/data/" ).list();
			for (int i=0;i<collFiles.length;i++){
				
				logger.debug(collPath +"/data/"+collFiles[i]+"-->"+targetPath +"%2F"+collFiles[i]);
				
				FileUtils.moveFile( 
						new File(collPath +"/data/"+collFiles[i]),
						new File(targetPath + coll +"%2F"+collFiles[i]));
			}
			FileUtils.deleteDirectory(new File(collPath));
		}
	}

	/**
	 * Gets the ingest area root path.
	 *
	 * @return the ingest area root path
	 */
	public String getIngestAreaRootPath() {
		return ingestAreaRootPath;
	}

	/**
	 * Sets the ingest area root path.
	 *
	 * @param stagingAreaRootPaskPath the new ingest area root path
	 */
	public void setIngestAreaRootPath(String stagingAreaRootPaskPath) {
		this.ingestAreaRootPath = Utilities.slashize(stagingAreaRootPaskPath);
	}

	/**
	 * Gets the user area root path.
	 *
	 * @return the user area root path
	 */
	public String getUserAreaRootPath() {
		return userAreaRootPath;
	}

	/**
	 * Sets the user area root path.
	 *
	 * @param transferAreaRootPath the new user area root path
	 */
	public void setUserAreaRootPath(String transferAreaRootPath) {
		this.userAreaRootPath = Utilities.slashize(transferAreaRootPath);
	}
	
}
