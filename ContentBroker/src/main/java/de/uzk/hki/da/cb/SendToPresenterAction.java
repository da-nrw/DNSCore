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
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.hibernate.classic.Session;
import org.apache.tika.Tika;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.metadata.XepicurWriter;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.repository.RepositoryFacade;

/** 
 * This action implements the ingest into the presentation repository.
 * 
 * It creates the epicur metadata for URN/URL mapping and creates
 * one object and the in the collections "danrw" and "danrw-closed"
 * depending on the audiences it should be accessible to according
 * to the contract and sets the published flag in the database
 * accordingly.
 * 
 * The action also makes sure the metadata necessary for 
 * publication with OAI-PMH is present in the repository.
 * 
 * @author Sebastian Cuy
 */
/*
 * TODO refactor in a way that a single FOXML-File is created
 * and ingested based on the conversion events (and package type).
 * Set datastream labels from the events' source file.
*/
public class SendToPresenterAction extends AbstractAction {

	static final Logger logger = LoggerFactory.getLogger(SendToPresenterAction.class);
	
	private RepositoryFacade repositoryFacade;
	private Map<String,String> viewerUrls;
	private Set<String> fileFilter;
	private Map<String,String> labelMap;
	private Set<String> testContractors;
	
	SendToPresenterAction(){}

	@Override
	public boolean implementation() throws IOException {
		
		if (repositoryFacade == null) 
			throw new RuntimeException("Repository facade object not set. Make sure the action is configured properly");
		
		String dipPathPublic = new StringBuilder(localNode.getDipAreaRootPath())
			.append("public/").append(object.getContractor().getShort_name())
			.append("/").append(object.getIdentifier()).toString();
		logger.debug("generated dipPathPublic: {}", dipPathPublic);
		String dipPathInstitution = new StringBuilder(localNode.getDipAreaRootPath())
			.append("institution/").append(object.getContractor().getShort_name())
			.append("/").append(object.getIdentifier()).toString();
		logger.debug("generated dipPathInstitution: {}", dipPathInstitution);
		
		String packageType = getPackageTypeFromDC(dipPathPublic, dipPathInstitution);
		
		// build map that contains original filenames for labeling
		labelMap = new HashMap<String,String>();
		for (Event e:object.getLatestPackage().getEvents()) {			
			if (!"CONVERT".equals(e.getType())) continue;
			DAFile targetFile = e.getTarget_file();
			if (!targetFile.getRep_name().startsWith("dip")) continue;			
			DAFile sourceFile = e.getSource_file();
			labelMap.put(targetFile.getRelative_path(), sourceFile.getRelative_path());			
		}
		
		String urn = object.getUrn();

		int publishedFlag = 0;
		
		try {

			// delete existing packages before ingesting the new ones
			getRepositoryFacade().purgeObjectIfExists(object.getIdentifier(), "danrw:");
			getRepositoryFacade().purgeObjectIfExists(object.getIdentifier(), "danrw-closed:");
		
			if (new File(dipPathPublic).exists()) {				
				// write xepicur file for urn resolving
				XepicurWriter.createXepicur(object.getIdentifier(), packageType, viewerUrls.get(packageType), dipPathPublic);
				String[] sets = null;
				if (!object.ddbExcluded()) {
					sets = new String[]{ "ddb" };
				}
				// ingest package to public collection
				if (ingestPackage(urn, object.getIdentifier(), "danrw", dipPathPublic, object.getContractor().getShort_name(), packageType, sets))
					publishedFlag += 1;
			}
			if (new File(dipPathInstitution).exists()) {
				// write xepicur file for urn resolving
				XepicurWriter.createXepicur(object.getIdentifier(), packageType, viewerUrls.get(packageType), dipPathInstitution);
				// ingest package to closed collection
				if (ingestPackage(urn, object.getIdentifier(), "danrw-closed", dipPathInstitution, object.getContractor().getShort_name(), packageType, null))
					publishedFlag += 2;
			}
			
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}
		
		object.setPublished_flag(publishedFlag);
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.save(object);
		session.getTransaction().commit();
		session.close();
		logger.debug("Set published flag of object to '{}'", object.getPublished_flag());
		
		// if no public DIP is created EDM creation and ES indexing is skipped
		if (publishedFlag % 2 == 0) {
			setKILLATEXIT(true);
		}
		
		return true;
	}
	
	@Override
	public void rollback() {
		throw new NotImplementedException("No rollback implemented for this action");
	}

	public Map<String,String> getViewerUrls() {
		return viewerUrls;
	}

	public void setViewerUrls(Map<String,String> viewerUrls) {
		this.viewerUrls = viewerUrls;
	}

	/**
	 * Get the repository facade
	 * @return the repository facade
	 */
	public RepositoryFacade getRepositoryFacade() {
		return repositoryFacade;
	}
	
	/**
	 * Set the repository facade
	 * @param the repository facade
	 */
	public void setRepositoryFacade(RepositoryFacade repositoryFacade) {
		this.repositoryFacade = repositoryFacade;
	}
	
	/**
	 * Get the set of filenames to be filtered during ingest
	 * @return the set of filenames to be filtered during ingest
	 */
	public Set<String> getFileFilter() {
		return fileFilter;
	}
	
	/**
	 * Set the set of filenames to be filtered during ingest
	 * @param fileFilter the set of filenames to be filtered during ingest
	 */
	public void setFileFilter(Set<String> fileFilter) {
		this.fileFilter = fileFilter;
	}

	private String getPackageTypeFromDC(String dipPathPublic, String dipPathInstitution) {
		String packageType = null;
		File dcFile = new File(dipPathPublic + "/DC.xml");
		if (!dcFile.exists())
			dcFile = new File(dipPathInstitution + "/DC.xml");
		if (dcFile.exists()) {
			SAXBuilder builder = new SAXBuilder();
			Document doc;
			try {
				doc = builder.build(new FileReader(dcFile));
				Element formatEl = doc.getRootElement().getChild("format",
						Namespace.getNamespace("http://purl.org/dc/elements/1.1/"));
				if (formatEl == null) {
					logger.warn("No format element found in DC, unable to determine package type!");
				} else {
					packageType = formatEl.getTextNormalize();
				}
			} catch (Exception e) {
				logger.error("Error while parsing DC, unable to determine package type.", e);
			}
		} else {
			logger.warn("No DC file found, unable to determine package type!");
		}
		return packageType;
	}	
	
	private boolean ingestPackage(String urn, String objectId, String collection,
			String packagePath, String contractorShortName, String packageType,
			String[] sets) throws RepositoryException, IOException {
		
		// check if pip exists
		File pack = new File(packagePath);
		if (!pack.exists()) {
			throw new IOException("Directory " + packagePath +" does not exist");
		}

		// create object for package in fedora if it does not already exist
		if (!repositoryFacade.objectExists(objectId, collection)) {
			repositoryFacade.createObject(objectId, collection, contractorShortName);
		}			
		
		// walk package and add files as datastreams recursively
		ingestDir(objectId, collection, pack, packagePath, packageType);
		
		// add identifiers to DC datastream
		try {
			String url = "http://www.danrw.de/objects/" + objectId;
			InputStream result = repositoryFacade.retrieveFile(objectId, collection, "DC");
			SAXBuilder builder = new SAXBuilder(false);
			Document doc = builder.build(result);
			doc.getRootElement().addContent(
					new Element("identifier","dc","http://purl.org/dc/elements/1.1/")
					.setText(urn));
			doc.getRootElement().addContent(
					new Element("identifier","dc","http://purl.org/dc/elements/1.1/")
					.setText(url));
			String content = new XMLOutputter().outputString(doc);
			repositoryFacade.updateMetadataFile(objectId, collection, "DC", content, "DC.xml", "text/xml");
		    logger.info("Successfully added identifiers to DC datastream");
		} catch (Exception e) {
			throw new RepositoryException("Failed to create object for package "+packagePath+" in fedora",e);
		}
		
		// add RELS-EXT relationships
		try {

			// add urn as owl:sameAs
			repositoryFacade.addRelationship(objectId, collection, "http://www.w3.org/2002/07/owl#sameAs", urn);
			logger.debug("Added relationship: owl:sameAs " + urn);
			
			// add collection membership
			String collectionUri;
			if ("danrw-closed".equals(collection)) {
				collectionUri = "info:fedora/collection:closed";
			} else {
				collectionUri = "info:fedora/collection:open";
			}
			repositoryFacade.addRelationship(objectId, collection, "info:fedora/fedora-system:def/relations-external#isMemberOfCollection", collectionUri);
			logger.debug("Added relationship: rels-ext:isMemberOfCollection " + collectionUri);
			
			// add oai identifier
			if (!"danrw-closed:".equals(collection) && 
				// don't add test packages to OAI-PMH
				!testContractors.contains(contractorShortName)
			) {
				String oaiId = "oai:danrw.de:" + objectId;
				repositoryFacade.addRelationship(objectId, collection, "http://www.openarchives.org/OAI/2.0/identifier", oaiId);
				logger.debug("Added relationship: oai:identifier " + oaiId);
			}
			
			// add oai sets
			if (sets != null) for (String set : sets) {
				repositoryFacade.addRelationship(objectId, collection, "info:fedora/fedora-system:def/relations-external#isMemberOf", "info:fedora/set:" + set);
				logger.debug("Added relationship: rels-ext:isMemberOf info:fedora/set:" + set);
			}
			
		} catch (Exception e) {
			throw new RepositoryException("Failed to add relationships for package "+packagePath+" in fedora",e);
		}

		return true;
	}

	private void ingestDir(String objectId, String collection, File dir, String packagePath, String packageType) throws RepositoryException, IOException {

		File files[] = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (getFileFilter().contains(name)) return false;
				else return true;
			}
		});

		if(files != null) {
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					ingestDir(objectId, collection, files[i], packagePath, packageType);
				} else {
					ingestFile(objectId, collection, files[i], packagePath, packageType);
				}
			}
		}

	}	

	private boolean ingestFile(String objectId, String collection, File file, String packagePath, String packageType) throws RepositoryException, IOException {
		
		boolean isMetadataFile = false;

		// Detect MIME-Type
		String mimeType = detectMimeType(file);
		
		// Generate datastream id
		String relPath = file.getAbsolutePath().replace(packagePath + "/","");
		String fileId = repositoryFacade.generateFileId(relPath);
		
		// special file IDs for metadata files
		if (file.getName().equals("DC.xml")) {
			fileId = "DC";
			isMetadataFile = true;
			mimeType = "text/xml";
		} else if (file.getName().equals(packageType + ".xml")) {
			fileId = packageType;
			isMetadataFile = true;
		}

		String label = file.getName();
		if (labelMap.containsKey(fileId)) {
			label = labelMap.get(fileId);
		}
		if (isMetadataFile) {
			FileInputStream fileInputStream = new FileInputStream(file);
			String content = IOUtils.toString(fileInputStream, "UTF-8");
			fileInputStream.close();
			repositoryFacade.createMetadataFile(objectId, collection, fileId, content, label, mimeType);
		} else {
			repositoryFacade.ingestFile(objectId, collection, fileId, file, label, mimeType);
		}
		logger.info("Successfully created datastream with fileId {} for file {}.",fileId,file.getName());
		
		return true;
		
	}
	
	private String detectMimeType(File file) throws IOException {
		
		// TODO: read from premis when available
		
		String mimeType;
	    Tika tika = new Tika();
	    try {
	        mimeType = tika.detect(file);
	    }  catch (IOException e) {
	        throw new IOException("Unable to open file for mime type detection: " + file.getAbsolutePath(), e);
	    }
	    
		logger.debug("Detected MIME type {} for file {}",mimeType,file.getName());		
		return mimeType;
		
	}

	/**
	 * Get the set of contractors that are considered test users.
	 * Objects ingested by these users will not be published 
	 * in the OAI-PMH server.
	 * @return the set of test users
	 */
	public Set<String> getTestContractors() {
		return testContractors;
	}

	/**
	 * Set the set of contractors that are considered test users.
	 * Objects ingested by these users will not be published 
	 * in the OAI-PMH server.
	 * @param the set of test users
	 */
	public void setTestContractors(Set<String> testContractors) {
		this.testContractors = testContractors;
	}

}
