package de.uzk.hki.da.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.apache.tika.Tika;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.AddDatastream;
import com.yourmediashelf.fedora.client.request.AddRelationship;
import com.yourmediashelf.fedora.client.request.GetDatastreamDissemination;
import com.yourmediashelf.fedora.client.request.GetObjectProfile;
import com.yourmediashelf.fedora.client.request.Ingest;
import com.yourmediashelf.fedora.client.request.ModifyDatastream;
import com.yourmediashelf.fedora.client.request.PurgeObject;

public class Fedora3RepositoryFacade implements RepositoryFacade {
	
	private static Logger logger = LoggerFactory.getLogger(Fedora3RepositoryFacade.class);
	
	private FedoraClient fedora;
	private Map<String,String> labelMap;
	private Set<String> fileFilter;
	
	/**
	 * Instantiates a new fedora 3 repository facade.
	 * @param fedora the fedora client
	 */
	public Fedora3RepositoryFacade(FedoraClient fedora) {
		this.fedora = fedora;
	}

	@Override
	public Map<String, String> getLabelMap() {
		return labelMap;
	}

	@Override
	public void setLabelMap(Map<String, String> labelMap) {
		this.labelMap = labelMap;
	}

	@Override
	public Set<String> getFileFilter() {
		return fileFilter;
	}

	@Override
	public void setFileFilter(Set<String> fileFilter) {
		this.fileFilter = fileFilter;
	}

	@Override
	public boolean purgePackageIfExists(String objectId, String prefix)
			throws RepositoryException {
		String pid = prefix + ":" + objectId;
		if (!objectExists(pid)) return false;
		try {
			new PurgeObject(pid).execute(fedora);
		} catch (FedoraClientException e) {
			throw new RepositoryException("Unable to purge package " + pid, e);
		}
		return true;
	}

	@Override
	public boolean ingestPackage(String urn, String objectId,
			String packagePath, String contractorShortName, String packageType,
			String prefix, String[] sets) throws RepositoryException, IOException {

		String pid = prefix + objectId;
		logger.debug("Generated pid {}",pid);
		
		// check if pip exists
		File pack = new File(packagePath);
		if (!pack.exists()) {
			throw new IOException("Directory " + packagePath +" does not exist");
		}

		// create object for package in fedora if it does not already exist
		if (!objectExists(pid)) {
			try {
				new Ingest(pid).ownerId(contractorShortName).label(urn).execute();
				logger.info("Successfully created object in Fedora. pid: {}", pid);
			} catch (FedoraClientException e) {
				throw new RepositoryException("Failed to create object for package. ", e);
			}
		}			
		
		// walk package and add files as datastreams recursively
		try {
			ingestDir(pack,pid,packagePath,packageType);
		} catch (RepositoryException e) {
			rollback(pid);
			throw e;
		} catch (IOException e) {
			rollback(pid);
			throw e;
		}
		
		// add identifiers to DC datastream
		try {
			String no = pid.split(":")[1];
			String url = "http://www.danrw.de/objects/" + no;
			InputStream result = retrieveFile(pid, "DC");
			SAXBuilder builder = new SAXBuilder(false);
			Document doc = builder.build(result);
			doc.getRootElement().addContent(
					new Element("identifier","dc","http://purl.org/dc/elements/1.1/")
					.setText(urn));
			doc.getRootElement().addContent(
					new Element("identifier","dc","http://purl.org/dc/elements/1.1/")
					.setText(url));
			String content = new XMLOutputter().outputString(doc);
			new ModifyDatastream(pid, "DC").content(content).execute(fedora);
		    logger.info("Successfully added identifiers to DC datastream");
		} catch (Exception e) {
			throw new RepositoryException("Failed to create object for package "+packagePath+" in fedora",e);
		}
		
		// add RELS-EXT relationships
		try {

			// add urn as owl:sameAs
			addRel(pid, "http://www.w3.org/2002/07/owl#sameAs", urn);
			logger.debug("Added relationship: owl:sameAs " + urn);
			
			// add collection membership
			String collectionUri;
			if ("danrw-closed:".equals(prefix)) {
				collectionUri = "info:fedora/collection:closed";
			} else {
				collectionUri = "info:fedora/collection:open";
			}
			addRel(pid, "info:fedora/fedora-system:def/relations-external#isMemberOfCollection", collectionUri);
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
				addRel(pid, "http://www.openarchives.org/OAI/2.0/identifier", oaiId);
				logger.debug("Added relationship: oai:identifier " + oaiId);
			}
			
			// add oai sets
			if (sets != null) for (String set : sets) {
				addRel(pid, "info:fedora/fedora-system:def/relations-external#isMemberOf", "info:fedora/set:" + set);
				logger.debug("Added relationship: rels-ext:isMemberOf info:fedora/set:" + set);
			}
			
		} catch (Exception e) {
			throw new RepositoryException("Failed to add relationships for package "+packagePath+" in fedora",e);
		}

		return true;
	}
	
	@Override
	public boolean ingestFile(String pid, File file, String relPath) throws RepositoryException, IOException {
		
		// add Files as external content
		String controlGroup = "E";

		// Detect MIME-Type
		String mimeType = detectMimeType(file);
		
		// Generate datastream id
		String dsID = generateDSID(relPath);
		
		// special datastream ids for metadata files
		if (file.getName().equals("DC.xml")) {
			dsID = "DC";
			controlGroup = "X";
		}

		// Ingest File as datastream
		try {
			String label = file.getName();
			if (labelMap.containsKey(dsID))
				label = labelMap.get(dsID);
			if ("X".equals(controlGroup)) {
				FileInputStream fileInputStream = new FileInputStream(file);
				new AddDatastream(pid, dsID).mimeType(mimeType)
					.controlGroup(controlGroup).dsLabel(label)
					.content(fileInputStream)
					.execute(fedora);
				fileInputStream.close();
			} else {
				String dsLocation = "file://" + file.getAbsolutePath();
				new AddDatastream(pid, dsID).mimeType(mimeType)
					.controlGroup(controlGroup).dsLabel(label)
					.dsLocation(dsLocation).execute(fedora);
			}
			logger.info("Successfully created datastream with dsID {} for file {}.",dsID,file.getName());
		} catch (FedoraClientException e) {
			throw new RepositoryException("Error while trying to add datastream for file "+file.getName(),e);
		}
		
		return true;
		
	}
	
	@Override
	public boolean createMetadataFile(String pid, String dsId, String content, String label, String mimeType) throws RepositoryException {
		try {
			new AddDatastream(pid, dsId).mimeType(mimeType)
				.controlGroup("X").dsLabel(label)
				.content(content).execute(fedora);
			return true;
		} catch(FedoraClientException e) {
			throw new RepositoryException("Unable to create metadata file: " + dsId, e);
		}
	}
	
	/**
	 * Generate a datastream id from a file path
	 * @param path the path to the file
	 * @return the generated ds id
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

	@Override
	public InputStream retrieveFile(String pid, String fileId)
			throws RepositoryException {
		try {
			return new GetDatastreamDissemination(pid, "DC")
				.execute(fedora).getEntityInputStream();
		} catch (FedoraClientException e) {
			throw new RepositoryException("Failed to retrieve datastream: " + fileId, e);
		}
	}
	
	private boolean objectExists(String pid) throws RepositoryException {
		try {
			new GetObjectProfile(pid).execute(fedora);
		} catch (FedoraClientException e) {
			if (e.getStatus() == 404) {
				// object does not exist and does not need to be purged
				return false;
			} else {
				throw new RepositoryException("Failed to check if package exists", e);
			}
		}
		return true;
	}
	
	private void rollback(String pid) throws RepositoryException {
		try {
			new PurgeObject(pid).execute();
			logger.warn("Rolled back ingest for pid {}", pid);
		} catch (FedoraClientException e) {
			throw new RepositoryException("Error while rolling back ingest", e);
		}		
	}

	private void ingestDir(File dir, String pid, String packagePath, String packageType) throws RepositoryException, IOException {

		File files[] = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (getFileFilter().contains(name)) return false;
				else return true;
			}
		});

		if(files != null) {
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					ingestDir(files[i], pid, packagePath, packageType);
				} else {
					String relPath = files[i].getAbsolutePath().replace(packagePath + "/","");
					ingestFile(pid, files[i], relPath);
				}
			}
		}

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
	
	private void addRel(String pid, String predicate, String object) throws FedoraClientException {
		new AddRelationship("info:fedora/" + pid)
			.predicate(predicate)
			.object(object).execute();
	}

}
