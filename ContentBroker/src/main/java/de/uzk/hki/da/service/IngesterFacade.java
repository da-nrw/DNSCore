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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.fedorest.Fedora;
import de.uzk.hki.fedorest.FedoraException;


/**
 * The Class IngesterFacade.
 */
public class IngesterFacade {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(IngesterFacade.class);

	/** The Constant FILTER_FILES. */
	@SuppressWarnings("serial")
	private static final Set<String> FILTER_FILES = new HashSet<String>() {{
		add("premis.xml");
		add("premis.xml.tmp");
	}};

	/** The fedora. */
	private Fedora fedora;
	
	/** The label map. */
	private Map<String,String> labelMap;

	/**
	 * Instantiates a new ingester facade.
	 *
	 * @param fedora the fedora
	 */
	public IngesterFacade(Fedora fedora) {
		this.fedora = fedora;
	}
	
	/**
	 * Purge package if exists.
	 *
	 * @param urn the urn
	 * @param prefix the prefix
	 */
	public void purgePackageIfExists(String urn, String objectId, String prefix) {
		
		String pid = prefix + objectId;
		
		try {
			fedora.purgeObject().param("pid", pid).execute();
		} catch (FedoraException e) {
			// eat exception
		}
		
	}

	/**
	 * Ingest package.
	 *
	 * @param urn the urn
	 * @param packagePath the package path
	 * @param contractorShortName the contractor short name
	 * @param packageType the package type
	 * @param prefix the prefix
	 * @return true, if successful
	 * @throws IngestException the ingest exception
	 */
	public boolean ingestPackage(String urn, String objectId, String packagePath, String contractorShortName, String packageType, String prefix, String[] sets) throws IngestException {

		// check if dip exists
		File pack = new File(packagePath);
		if (!pack.exists()) {
			logger.info("Directory {} does not exist or is empty and will not be published", packagePath);
			return false;
		}

		// generate pid
		String pid = prefix + objectId;
		logger.debug("Generated pid {}",pid);

		// create object for package in fedora if it does not already exist
		// note: getObjectProfile throws an exception if object is not found
		try {
			fedora.getObjectProfile().param("pid", pid).execute();
		} catch (FedoraException e) {
			try {
				fedora.ingest().param("pid", pid).param("ownerId",contractorShortName)
				.param("label",urn).execute();
				logger.info("Successfully created object in Fedora. pid: {}", pid);
			} catch (FedoraException e2) {
				throw new IngestException("Failed to create object for package. ", e2);
			}
		}			
		
		// walk package and add files as datastreams recursively
		try {
			ingestDir(pack,pid,packagePath,packageType);
		} catch (IngestException e) {
			rollback(pid);
			throw e;
		}
		
		// add identifiers to DC datastream
		try {
			String no = pid.split(":")[1];
			String url = "http://www.danrw.de/objects/" + no;
			String result = fedora.getDatastreamDissemination()
					.param("pid", pid).param("dsID","DC").execute()
					.getContent();
			SAXBuilder builder = new SAXBuilder(false);
			Document doc = builder.build(new StringReader(result));
			doc.getRootElement().addContent(
					new Element("identifier","dc","http://purl.org/dc/elements/1.1/")
					.setText(urn));
			doc.getRootElement().addContent(
					new Element("identifier","dc","http://purl.org/dc/elements/1.1/")
					.setText(url));
			String content = new XMLOutputter().outputString(doc);
		    fedora.modifyDatastream().param("pid", pid).param("dsID", "DC").execute(content);
		    logger.info("Successfully added identifiers to DC datastream");
		} catch (Exception e) {
			throw new IngestException("Failed to create object for package "+packagePath+" in fedora",e);
		}
		
		// add RELS-EXT relationships
		try {

			// add urn as owl:sameAs
			fedora.addRelationship().param("pid", pid)
				.param("predicate", "http://www.w3.org/2002/07/owl#sameAs")
				.param("object", urn).execute();
			logger.debug("Added relationship: owl:sameAs " + urn);
			
			// add collection membership
			String collectionUri;
			if ("danrw-closed:".equals(prefix))
				collectionUri = "info:fedora/collection:closed";
			else
				collectionUri = "info:fedora/collection:open";
			fedora.addRelationship().param("pid", pid)
				.param("predicate", "info:fedora/fedora-system:def/relations-external#isMemberOfCollection")
				.param("object", collectionUri).execute();
			logger.debug("Added relationship: rels-ext:isMemberOfCollection " + collectionUri);
			
			// add oai identifier
			if (!"danrw-closed:".equals(prefix) && 
				!(	// don't add test packages to OAI-PMH
					// TODO move test contractors to config
					"TEST".equals(contractorShortName)
					|| "LVRInfoKom".equals(contractorShortName)
					|| "HBZ".equals(contractorShortName)
				)
			) {
				String oaiId = "oai:danrw.de:" + pid.split(":")[1];
				fedora.addRelationship().param("pid", pid)
					.param("predicate", "http://www.openarchives.org/OAI/2.0/identifier")
					.param("object", oaiId).execute();
				logger.debug("Added relationship: oai:identifier " + oaiId);
			}
			
			// add oai sets
			if (sets != null) for (String set : sets) {
				fedora.addRelationship().param("pid", pid)
					.param("predicate", "info:fedora/fedora-system:def/relations-external#isMemberOf")
					.param("object", "info:fedora/set:" + set).execute();
				logger.debug("Added relationship: rels-ext:isMemberOf info:fedora/set:" + set);
			}
			
		} catch (Exception e) {
			throw new IngestException("Failed to add relationships for package "+packagePath+" in fedora",e);
		}

		return true;
	}

	/**
	 * Rollback.
	 *
	 * @param pid the pid
	 * @throws IngestException the ingest exception
	 */
	private void rollback(String pid) throws IngestException {
		try {
			fedora.purgeObject().param("pid", pid).execute();
			logger.error("Rolled back ingest for pid {}",pid);
		} catch (FedoraException e) {
			throw new IngestException("Error while rolling back ingest",e);
		}		
	}

	/**
	 * Ingest dir.
	 *
	 * @param dir the dir
	 * @param pid the pid
	 * @param packagePath the package path
	 * @param packageType the package type
	 * @throws IngestException the ingest exception
	 */
	private void ingestDir(File dir, String pid, String packagePath, String packageType) throws IngestException {

		File files[] = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (FILTER_FILES.contains(name)) return false;
				else return true;
			}
		});

		if(files != null) {
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					ingestDir(files[i], pid, packagePath, packageType);
				} else {
					ingestFile(files[i], pid, packagePath, packageType);
				}
			}
		}

	}

	/**
	 * Ingest file.
	 *
	 * @param file the file
	 * @param pid the pid
	 * @param packagePath the package path
	 * @param packageType the package type
	 * @throws IngestException the ingest exception
	 */
	private void ingestFile(File file, String pid, String packagePath, String packageType) throws IngestException {
		
		// add Files as external content
		String controlGroup = "E";

		// Detect MIME-Type
		String mimeType = detectMimeType(file);
		
		// Generate datastream id
		String dsID = generateDSID(file.getAbsolutePath().replace(packagePath + "/",""));
		
		// special datastream ids for metadata files
		if (file.getName().equals("DC.xml")) {
			dsID = "DC";
			controlGroup = "X";
		} else if (FilenameUtils.getBaseName(file.getName()).equals(packageType)) {
			dsID = packageType;
		}

		// Ingest File as datastream
		addDatastream(pid, dsID, file, controlGroup, mimeType);
		
	}
	
	/**
	 * Detect mime type.
	 *
	 * @param file the file
	 * @return the string
	 * @throws IngestException the ingest exception
	 */
	private String detectMimeType(File file) throws IngestException {
		
		// TODO: read from premis when available
		
		String mimeType;
	    Tika tika = new Tika();
	    try {
	        mimeType = tika.detect(file);
	    }  catch (IOException e) {
	        throw new IngestException("Unable to open file for mime type detection", e);
	    }
		
		/*MimetypesFileTypeMap typeMap = new MimetypesFileTypeMap();
		String mimeType = typeMap.getContentType(file);*/
	    
		logger.debug("Detected MIME type {} for file {}",mimeType,file.getName());		
		return mimeType;
		
	}
	
	/**
	 * Adds the datastream.
	 *
	 * @param pid the pid
	 * @param dsID the ds id
	 * @param file the file
	 * @param controlGroup the control group
	 * @param mimeType the mime type
	 * @throws IngestException the ingest exception
	 */
	private void addDatastream(String pid, String dsID, File file, String controlGroup, String mimeType) throws IngestException {
		try {
			String label = file.getName();
			if (labelMap.containsKey(dsID))
				label = labelMap.get(dsID);
			if ("X".equals(controlGroup)) {
				fedora.addDatastream().param("pid", pid).param("dsID", dsID)
					.param("mimeType", mimeType).param("controlGroup", controlGroup)
					.param("dsLabel", file.getName()).execute(file);
			} else {
				String dsLocation = "file://" + file.getAbsolutePath(); 
				fedora.addDatastream().param("pid", pid).param("dsID", dsID)
					.param("mimeType", mimeType).param("controlGroup", controlGroup)
					.param("dsLabel", label)
					.param("dsLocation", dsLocation).execute();
			}
			logger.info("Successfully created datastream with dsID {} for file {}.",dsID,file.getName());
		} catch (FedoraException e) {
			throw new IngestException("Error while trying to add datastream for file "+file.getName(),e);
		}
	}

	/**
	 * Generate dsid.
	 *
	 * @param path the path
	 * @return the string
	 */
	public static String generateDSID(String path) {
		
		// replace slashes
		String dsID = path.replace("/", "-");
		
		// eliminate disallowed beginnings
		if (Character.isDigit(dsID.charAt(0))
				|| dsID.startsWith("xml")
				|| dsID.startsWith("XML")) {
			dsID = "_" + dsID;
		}
		
		// replace disallowed characters
		dsID = dsID.replaceAll("[^\\p{L}\\p{Digit}\\._-]","_");
		
		return dsID;
	}

	/**
	 * Gets the label map.
	 *
	 * @return the label map
	 */
	public Map<String,String> getLabelMap() {
		return labelMap;
	}

	/**
	 * Sets the label map.
	 *
	 * @param labelMap the label map
	 */
	public void setLabelMap(Map<String,String> labelMap) {
		this.labelMap = labelMap;
	}

	/**
	 * The Class IngestException.
	 */
	public static class IngestException extends Exception {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 2183506809417224233L;

		/**
		 * Instantiates a new ingest exception.
		 *
		 * @param msg the msg
		 * @param e the e
		 */
		public IngestException(String msg, Throwable e) {
			super(msg,e);
		}
		
	}

}
