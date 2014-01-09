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
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.archivers.ArchiveBuilder;
import de.uzk.hki.da.archivers.ArchiveBuilderFactory;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;


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
	 * @param object the packages that get processed belong to this object.
	 * @param includeLastPackage if set to false, the method will load all but the last package.
	 * @throws IOException if at least one of the packages could not be retrieved from the grid.
	 * @author Daniel M. de Oliveira
	 */
	public void loadPackages(Object object,boolean includeLastPackage) throws IOException{
		
		if (grid==null) throw new IllegalStateException("grid not set");
		if (object==null) throw new IllegalArgumentException("corresponding Object is null");
		if (object.getPackages().isEmpty()) throw new IllegalArgumentException("Object does not contain any packages");

		
		logger.trace("Retrieving packages...");
		for (Package pkg : object.getPackages()) {
			
			if (!includeLastPackage)
				if (pkg==object.getLatestPackage()) continue;
			
			retrieveSinglePackageFromGrid(object,pkg);
		}
		unpackExistingPackages(object);
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
				objectSize += grid.getFileSize(irodsZonePath + "aip/" + obj.getContractor().getShort_name() + "/" + obj.getIdentifier() +
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
	private void retrieveSinglePackageFromGrid(Object object,Package pkg) throws IOException{
		String data_name = "/aip/"
				+ object.getContractor().getShort_name() + "/" + object.getIdentifier()
				+ "/" + object.getIdentifier() + ".pack_" + pkg.getName() + ".tar";
		
		if (!new File(object.getDataPath()).exists()) new File(object.getDataPath()).mkdirs();

		File targetDir=new File(object.getPath() + "/loadedAIPs");
		if (targetDir.exists()) targetDir.mkdir(); 
		File targetFile = new File(targetDir.getAbsolutePath() +"/"+object.getIdentifier() + ".pack_" + pkg.getName() + ".tar");
		if (targetFile.exists()) targetFile.delete(); 

		logger.debug("Retrieving from lza to temp resource: "+data_name);
		grid.get(targetFile,
				data_name);
	}
	
	

	/** The Constant logger. */
	static final Logger logger = LoggerFactory.getLogger(RetrievePackagesHelper.class);
	/** The Constant irodsZonePath. */
	static final String irodsZonePath = "/da-nrw/";


	/**
	 * Adds existing representations to the unpacked delta package.
	 *
	 * @param job the job
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @author Thomas Kleinke
	 * @author Daniel M. de Oliveira
	 */
	private void unpackExistingPackages(Object object) throws IOException {
		
		String loadedAIPsPath = object.getPath()+"loadedAIPs/";
		
		if (!new File(object.getPath()).exists()) throw new IOException(object.getPath()+" does not exist");
		String archives[] = new File(loadedAIPsPath).list();
		if (archives==null||archives.length==0) 
			throw new RuntimeException("temporary folder "+loadedAIPsPath+" does not contain any packages to unpack");
		Arrays.sort(archives);

		for (int i = 0; i < archives.length; i++)
		{
			String archivePath = loadedAIPsPath + archives[i];
			logger.debug("unpacking: " + archivePath);			
			unpackArchiveAndMoveContents(
					archivePath,
					object.getPath(),
					loadedAIPsPath+"data");
			removeBagitFilesAndPremis(loadedAIPsPath);	
		}

		new File(object.getDataPath()).mkdir();
		normalizeObject(object);
		FileUtils.deleteDirectory(new File(loadedAIPsPath));	
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
		String dataPath = object.getPath() + "loadedAIPs/data/";
		String dataPathContents[] = new File(dataPath).list();
		
		if (dataPathContents == null)
			throw new RuntimeException("Listing files in " + dataPath + " failed!");
		for (int i = 0; i < dataPathContents.length; i++)
		{		
			logger.debug("adding "+dataPathContents[i]+ " to target location");
			if (new File(dataPath + dataPathContents[i]).isDirectory())
				FileUtils.moveDirectoryToDirectory(
						new File(dataPath + dataPathContents[i]), 
						new File(object.getDataPath()), false);
			else
				FileUtils.moveFileToDirectory(
						new File(dataPath + dataPathContents[i]), 
						new File(object.getDataPath()), false );
		}
	}

	
	
	
	private void unpackArchiveAndMoveContents(
			String archivePath,
			String objectPath,
			String targetDataPath) throws IOException {
		
		File tempFolder = new File(objectPath + "Temp");		
		tempFolder.mkdir();
		
		try {
			ArchiveBuilder builder = ArchiveBuilderFactory.getArchiveBuilderForFile(new File(archivePath));
			builder.unarchiveFolder(new File(archivePath), 
					tempFolder);
		} catch (Exception e) {
			throw new IOException("Existing AIP \"" + archivePath +
					"\" couldn't be unpacked to folder " + tempFolder, e);
		}

		String subfolder[] = (new File(objectPath+"Temp")).list();
		File tempDataFolder = new File(objectPath+"Temp/"+subfolder[0]+"/data");
		if (!tempDataFolder.exists())
			throw new RuntimeException("unpacked package in Temp doesn't contain a data folder!");

		File[] repFolders = tempDataFolder.listFiles();
		for (File f : repFolders) {
			logger.debug(f.getAbsolutePath());
			if (f.isFile())
				FileUtils.moveFileToDirectory(f, new File(targetDataPath), true);
			if (f.isDirectory())
				FileUtils.moveDirectoryToDirectory(f, new File(targetDataPath), true);

		}
		FileUtils.deleteDirectory(tempFolder);
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
		if (new File(pathToBagRoot+"data/premis.xml").exists())
			new File(pathToBagRoot+"data/premis.xml").delete();
	}
}
