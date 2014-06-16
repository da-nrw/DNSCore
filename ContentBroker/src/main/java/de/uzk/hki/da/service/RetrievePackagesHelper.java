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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.utils.ArchiveBuilder;
import de.uzk.hki.da.utils.ArchiveBuilderFactory;


/**
 * RetrievePackagesHelper.
 * @author Daniel M. de Oliveira
 * @author Jens Peters
 * @author Thomas Kleinke
 */
public class RetrievePackagesHelper {

	private GridFacade grid;
	
	public RetrievePackagesHelper(GridFacade grid){
		this.grid = grid;
	}
	
	/**
	 * Retrieves the packages of an object from the LZA storage system and merges their unpacked
	 * contents at the destined object location at workAreaRootPath/[csn]/[id] where they can be
	 * further processed. If [workAreaRootPath]/|csn]/[id] does not exist, it gets created. Note also
	 * that any existing files below [workAreaRootPath]/[csn]/[id] get overwritten in case of namespace
	 * collision with files contained within the retrieved packages. 
	 * 
	 * @param object the packages that get processed belong to this object. The object has packages, to which,
	 * as a side of the operation, new instances of DAFile will get attached.
	 * @param includeLastPackage if set to false, the method will load all but the last package.
	 * @throws IOException if at least one of the packages could not be retrieved from the grid.
	 * @author Daniel M. de Oliveira
	 */
	public void loadPackages(Object object,boolean includeLastPackage) throws IOException{
		
		if (grid==null) throw new IllegalStateException("grid not set");
		if (object==null) throw new IllegalArgumentException("corresponding Object is null");
		if (object.getPackages().isEmpty()) throw new IllegalArgumentException("Object does not contain any packages");
		if (!object.getPath().toFile().exists()) throw new IllegalArgumentException(object.getPath()+" does not exist");

		for (Package pkg : object.getPackages()) {
			
			if (!includeLastPackage)
				if (pkg==object.getLatestPackage()) continue;
			
			File retrievedPackage = retrieveSinglePackageFromGrid(object,pkg);
			List<DAFile> results = unpackExistingPackage(object,retrievedPackage);
			pkg.getFiles().addAll(results);
		}
	}
	
	
	
	
	/**
	 * Size of the object (but not including the newest package).
	 *
	 * @param obj the obj
	 * @param job the job
	 * @return the object size
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @author Jens Peters
	 * @author Daniel M. de Oliveira
	 */
	public long getObjectSize(Object obj,Job job) throws IOException {
		if (grid==null) throw new IllegalStateException("grid not set");
		
		if (obj == null) throw new IllegalArgumentException("corresponding Object is null");
	
		if (obj.getPackages().isEmpty())
			return 0;
	
		long objectSize = 0;
	
		List<Package> packs = obj.getPackages();
		for (Package pkg : packs) {
			if (pkg==obj.getLatestPackage()) continue; // don't count newest package
			
			String pn = pkg.getName();
	
			try {
				objectSize += grid.getFileSize( obj.getContractor().getShort_name() + "/" + obj.getIdentifier() +
						"/" + obj.getIdentifier() + ".pack_" + pn + ".tar");
			} catch (IOException e) {
				throw new RuntimeException("Failed to determine file size", e);
			}
		}
	
		return objectSize;
	}

	/**
	 * Any needed folder gets created. Any existing target file gets overwritten.
	 * @author Daniel M. de Oliveira
	 * @param object
	 * @param pkg
	 * @throws IOException
	 */
	private File retrieveSinglePackageFromGrid(Object object,Package pkg) throws IOException{
		String data_name =
				object.getContractor().getShort_name() + "/" + object.getIdentifier()
				+ "/" + object.getIdentifier() + ".pack_" + pkg.getName() + ".tar";
		
		if (!object.getDataPath().toFile().exists()) object.getDataPath().toFile().mkdirs();

		File targetDir=new File(object.getPath() + "/loadedAIPs");
		if (!targetDir.exists()) targetDir.mkdirs(); 
		File targetFile = new File(targetDir.getAbsolutePath() +"/"+object.getIdentifier() + ".pack_" + pkg.getName() + ".tar");
		if (targetFile.exists()) targetFile.delete(); 

		logger.debug("Retrieving from lza to temp resource: "+data_name);
		grid.get(targetFile,
				data_name);
		
		return targetFile;
	}
	
	

	/** The Constant logger. */
	static final Logger logger = LoggerFactory.getLogger(RetrievePackagesHelper.class);
	/** The Constant irodsZonePath. */


	/**
	 * Adds existing representations to the unpacked delta package.
	 *
	 * @param job the job
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @author Thomas Kleinke
	 * @author Daniel M. de Oliveira
	 */
	private List<DAFile> unpackExistingPackage(Object object,File container) throws IOException {
		
		List<DAFile> results = new ArrayList<DAFile>();
		
		String loadedAIPsPath = object.getPath()+"/loadedAIPs/";
		
		logger.debug("unpacking: " + container);			
		results = unpackArchiveAndMoveContents(
				container,
				loadedAIPsPath+"data");
		
		removeBagitFilesAndPremis(loadedAIPsPath);	
			
		object.getDataPath().toFile().mkdir();
		normalizeObject(object); // TODO really? for every package?
		FileUtils.deleteDirectory(new File(loadedAIPsPath));
		
		return results;
	}

	
	/**
	 * Unpacks one archive container and moves its content to targetPath.
	 * When unpacked, all files below [containerFirstLevelEntry]/data/ are content in this sense.
	 * <pre>Example:
	 * a/data/rep/abc.tif (from a.tar) -\> [targetPath]/rep/abc.tif</pre>
	 *  
	 * @param container
	 * @param targetPath
	 * @throws IOException
	 */
	private List<DAFile> unpackArchiveAndMoveContents(
			File container,
			String targetPath) throws IOException {
		
		List<DAFile> results = new ArrayList<DAFile>();
		
		File tempFolder = new File(targetPath + "Temp");		
		tempFolder.mkdir();
		
		try {
			ArchiveBuilder builder = ArchiveBuilderFactory.getArchiveBuilderForFile(container);
			builder.unarchiveFolder(container, 
					tempFolder);
		} catch (Exception e) {
			throw new IOException("Existing AIP \"" + container +
					"\" couldn't be unpacked to folder " + tempFolder, e);
		}
	
		String containerFirstLevelEntry[] = (tempFolder).list();
		File tempDataFolder = new File(tempFolder.getPath()+"/"+containerFirstLevelEntry[0]+"/data");
		if (!tempDataFolder.exists())
			throw new RuntimeException("unpacked package in Temp doesn't contain a data folder!");
	
		logger.debug("Listing representations inside temporary folder: ");
		File[] repFolders = tempDataFolder.listFiles();
		for (File rep : repFolders) {
			
			String repPartialPath = (rep.getPath()).replace(tempDataFolder.getPath()+"/", "");
			if (!rep.isDirectory()) continue;
				
		    for (File f : FileUtils.listFiles(rep,
			        TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)){
		    	results.add(new DAFile(null,repPartialPath,f.getPath().replace((tempDataFolder.getPath()+"/"+repPartialPath+"/"),"")));    
		    }
			    
			FileUtils.moveDirectoryToDirectory(rep, new File(targetPath), true);
		}
		
		FileUtils.deleteDirectory(tempFolder);
		
		return results;
	}

	/**
	 * At the beginning the objects data are located under:
	 * <pre>[workAreaRootPath]/[csn]/object-id/loadedAIPs/data</pre>
	 * After they should be at
	 * <pre>[workAreaRootPath]/[csn]/[id]/data</pre>
	 * @param object
	 * @throws IOException
	 */
	private void normalizeObject(Object object) throws IOException {
	
		logger.trace("normalizeObject: Moving unpacked representation folders from " + object.getPath() + "loadedAIPs to " + object.getPath());
		String dataPath = object.getPath() + "/loadedAIPs/data/";
		String dataPathContents[] = new File(dataPath).list();
		
		if (dataPathContents == null)
			throw new RuntimeException("Listing files in " + dataPath + " failed!");
		for (int i = 0; i < dataPathContents.length; i++)
		{		
			logger.debug("adding "+dataPathContents[i]+ " to target location");
			if (new File(dataPath + dataPathContents[i]).isDirectory())
				FileUtils.moveDirectoryToDirectory(
						new File(dataPath + dataPathContents[i]), 
						object.getDataPath().toFile(), false);
			else
				FileUtils.moveFileToDirectory(
						new File(dataPath + dataPathContents[i]), 
						object.getDataPath().toFile(), false );
		}
	}

	
	
	
	private void removeBagitFilesAndPremis(String pathToBagRoot){
		if (new File(pathToBagRoot+"bagit.txt").exists())
			new File(pathToBagRoot+"bagit.txt").delete();
		if (new File(pathToBagRoot+"bag-info.txt").exists())
			new File(pathToBagRoot+"bag-info.txt").delete();
		if (new File(pathToBagRoot+"manifest-md5.txt").exists())
			new File(pathToBagRoot+"manifest-md5.txt").delete();
		if (new File(pathToBagRoot+"tagmanifest-md5.txt").exists())
			new File(pathToBagRoot+"tagmanifest-md5.txt").delete();
	}
}
