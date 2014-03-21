package de.uzk.hki.da.cb;

import java.io.InputStream;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import de.uzk.hki.da.metadata.XsltGenerator;
import de.uzk.hki.da.repository.RepositoryFacade;

/**
 * @author Sebastian Cuy
 */
public class CreateEDMAction extends AbstractAction {
	
	private RepositoryFacade repositoryFacade;
	private String choBaseUri;
	private String aggrBaseUri;
	private String localBaseUri;

	@Override
	boolean implementation() {
		if (repositoryFacade == null) 
			throw new RuntimeException("Repository facade object not set. Make sure the action is configured properly");
		
		String objectId = object.getIdentifier();
		
		try {
			
			logger.debug("Getting DC datastream for object: {}", objectId);
			
			InputStream dcStream = repositoryFacade.retrieveFile(objectId, "danrw", "DC");
			
			SAXBuilder builder = new SAXBuilder(false);
			Document doc = builder.build(dcStream);
			Element formatEl = doc.getRootElement().getChild("format",
					Namespace.getNamespace("http://purl.org/dc/elements/1.1/"));
			if (formatEl == null) {
				logger.warn("No format element found in DC. EDM cannot be created!");
				return true;
			}
			String packageType = formatEl.getTextNormalize();
			
			logger.debug("Read package type: {}", packageType);
			
			String xsltFile;
			if ("METS".equals(packageType)) {
				xsltFile = "mets-mods_to_edm.xsl";
			} else if ("EAD".equals(packageType)) {
				xsltFile = "ead_to_edm.xsl";
			} else if ("XMP".equals(packageType)) {
				xsltFile = "xmp_to_edm.xsl";
			} else if ("LIDO".equals(packageType)) {
				xsltFile = "lido_to_edm.xsl";
			} else {
				throw new RuntimeException("No conversion available for package type '" + packageType + "'. EDM can not be created.");
			}
			
			InputStream metadataStream = repositoryFacade.retrieveFile(objectId, "danrw", packageType);
			
			XsltGenerator edmGenerator = new XsltGenerator(
					"conf/xslt/edm/" + xsltFile, metadataStream);	
			edmGenerator.setParameter("urn", object.getUrn());
			edmGenerator.setParameter("cho-base-uri", choBaseUri + "/" + objectId);
			edmGenerator.setParameter("aggr-base-uri", aggrBaseUri + "/" + objectId);
			edmGenerator.setParameter("local-base-uri", localBaseUri);
			String edmResult = edmGenerator.generate();
			
			repositoryFacade.createMetadataFile(objectId, "danrw", "EDM", edmResult, "Object representation in Europeana Data Model", "application/rdf+xml");
			
			logger.info("Successfully created EDM datastream for object {}.", objectId);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return true;
	}

	@Override
	void rollback() throws Exception {
		// nothing to do		
	}

	public String getChoBaseUri() {
		return choBaseUri;
	}

	public void setChoBaseUri(String choBaseUri) {
		this.choBaseUri = choBaseUri;
	}

	public String getAggrBaseUri() {
		return aggrBaseUri;
	}

	public void setAggrBaseUri(String aggrBaseUri) {
		this.aggrBaseUri = aggrBaseUri;
	}

	public String getLocalBaseUri() {
		return localBaseUri;
	}

	public void setLocalBaseUri(String localBaseUri) {
		this.localBaseUri = localBaseUri;
	}

}
