package de.uzk.hki.da.cb;

import java.io.StringReader;

import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import de.uzk.hki.da.metadata.XsltGenerator;
import de.uzk.hki.fedorest.Fedora;
import de.uzk.hki.fedorest.FedoraResult;

/**
 * @author Sebastian Cuy
 */
public class CreateEDMAction extends AbstractAction {
	
	private Fedora fedora;
	private String choBaseUri;
	private String aggrBaseUri;
	private String localBaseUri;

	@Override
	boolean implementation() {
		if (fedora == null) 
			throw new RuntimeException("Fedora object not set. Make sure the action is configured properly");
		
		String id = object.getIdentifier().replace("+", ":");
		String pid = "danrw:" + id;
		
		try {
			
			logger.debug("Getting DC datastream for pid: {}", pid);
			
			FedoraResult result = fedora.getDatastreamDissemination()
					.param("pid", pid)
					.param("dsID", "DC")
					.execute();
			
			SAXBuilder builder = new SAXBuilder(false);
			Document doc = builder.build(new StringReader(result.getContent()));
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
			
			FedoraResult xmlResult = fedora.getDatastreamDissemination()
					.param("pid", pid)
					.param("dsID", packageType)
					.execute();
			XsltGenerator edmGenerator = new XsltGenerator(
					"conf/xslt/edm/" + xsltFile,
					IOUtils.toInputStream(xmlResult.getContent()));	
			edmGenerator.setParameter("urn", object.getUrn());
			edmGenerator.setParameter("cho-base-uri", choBaseUri + "/" + id);
			edmGenerator.setParameter("aggr-base-uri", aggrBaseUri + "/" + id);
			edmGenerator.setParameter("local-base-uri", localBaseUri);
			String edmResult = edmGenerator.generate().toString();
			
			fedora.addDatastream().param("pid", pid).param("dsID", "EDM")
					.param("mimeType", "application/rdf+xml").param("controlGroup", "X")
					.param("dsLabel", "Object representation in Europeana Data Model")
					.execute(edmResult);
			logger.info("Successfully created EDM datastream for pid {}.", pid);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return true;
	}

	@Override
	void rollback() throws Exception {
		// nothing to do		
	}
	
	public Fedora getFedora() {
		return fedora;
	}

	public void setFedora(Fedora fedora) {
		this.fedora = fedora;
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
