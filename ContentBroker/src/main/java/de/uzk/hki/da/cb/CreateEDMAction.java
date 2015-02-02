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

import static de.uzk.hki.da.core.C.EDM_FOR_ES_INDEX_METADATA_STREAM_ID;
import static de.uzk.hki.da.core.C.EDM_XSLT_METADATA_STREAM_ID;
import static de.uzk.hki.da.core.C.FILE_EXTENSION_XML;
import static de.uzk.hki.da.core.C.WA_PIPS;
import static de.uzk.hki.da.core.C.WA_PUBLIC;
import static de.uzk.hki.da.utils.Utilities.isNotSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.metadata.EadMetsMetadataStructure;
import de.uzk.hki.da.metadata.LidoMetadataStructure;
import de.uzk.hki.da.metadata.MetadataStructure;
import de.uzk.hki.da.metadata.MetsMetadataStructure;
import de.uzk.hki.da.metadata.XMPMetadataStructure;
import de.uzk.hki.da.metadata.XsltEDMGenerator;
import de.uzk.hki.da.model.Document;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.repository.RepositoryFacade;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.util.Path;

/**
 * This action transforms the primary metadata of an
 * object into EDM/RDF and adds the result into
 * the presentation repository.
 * 
 * The transformation is done with XSLT, the mappings
 * can be found in "conf/xslt/edm/".
 * 
 * @author Sebastian Cuy
 * @author Daniel M. de Oliveira
 */
public class CreateEDMAction extends AbstractAction {
	
	private RepositoryFacade repositoryFacade;
	private Map<String,String> edmMappings;
	private File edmXSLTDestinationFile = null;
	private File edmIndexDestinationFile = null;

	/**
	 * @
	 */
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		if (repositoryFacade == null) 
			throw new ConfigurationException("Repository facade object not set. Make sure the action is configured properly");
	}



	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		if (isNotSet(preservationSystem.getUrisCho())) 
			throw new IllegalStateException("missing choBaseUri");
		if (isNotSet(preservationSystem.getUrisAggr())) 
			throw new IllegalStateException("missing aggrBaseUri");
		if (isNotSet(preservationSystem.getUrisLocal())) 
			throw new IllegalStateException("localBaseUri not set");
		if (edmMappings == null)
			throw new IllegalStateException("edmMappings not set.");
		for (String filePath:edmMappings.values())
			if (!new File(filePath).exists())
				throw new IllegalStateException("mapping file "+filePath+" does not exist");
		if (isNotSet(o.getPackage_type()))
			throw new IllegalStateException("missing package type");
	}



	@Override
	public boolean implementation() throws IOException, RepositoryException, JDOMException, ParserConfigurationException, SAXException {
		
		String xsltTransformationFile = getEdmMappings().get(o.getPackage_type());
		if (xsltTransformationFile == null)
			throw new RuntimeException("No mapping for package type: '" + o.getPackage_type());
		if (! new File(xsltTransformationFile).exists())
			throw new FileNotFoundException("Missing file: "+xsltTransformationFile);

		
		File metadataSourceFile = makeMetadataFile(o.getPackage_type(),WA_PUBLIC);
		if (!metadataSourceFile.exists())
			throw new RuntimeException("Missing file in public PIP: "+o.getPackage_type()+FILE_EXTENSION_XML);

		edmXSLTDestinationFile = generateEdmUsingXslt(xsltTransformationFile, metadataSourceFile);
		putToRepository(edmXSLTDestinationFile);
		
		edmIndexDestinationFile = generateEDM(metadataSourceFile);
		putToRepository(edmIndexDestinationFile);
		
		return true;
	}
	
	
	
	private File makeMetadataFile(String packageType,String pipType) {
		return Path.makeFile(n.getWorkAreaRootPath(),WA_PIPS,pipType,o.getContractor().getShort_name(),o.getIdentifier(),packageType+FILE_EXTENSION_XML);
	}
	
	
	private File generateEdmUsingXslt(String xsltTransformationFile,File metadataSourceFile) throws FileNotFoundException {
		
		File edm = makeMetadataFile(EDM_XSLT_METADATA_STREAM_ID,WA_PUBLIC); 
		
		String edmResult = generateEDM(o.getIdentifier(), xsltTransformationFile, new FileInputStream(metadataSourceFile));
		PrintWriter out = null;
		try {
			out = new PrintWriter(edm);
			out.println(edmResult);}
		finally {
			out.close();
		}
		return edm;
	}
	
	private File generateEDM(File metadataSourceFile) throws JDOMException, IOException, ParserConfigurationException, SAXException {
		
		File edm = makeMetadataFile(EDM_FOR_ES_INDEX_METADATA_STREAM_ID,WA_PUBLIC); 
		
		String packageType = o.getPackage_type();
		List<Document> documents = o.getDocuments();
		
		MetadataStructure ms = null;
		
		if(packageType.equals("EAD")) {
			ms = new EadMetsMetadataStructure(metadataSourceFile, documents);
		} else if(packageType.equals("METS")) {
			ms = new MetsMetadataStructure(metadataSourceFile, documents);
		} else if(packageType.equals("LIDO")) {
			ms = new LidoMetadataStructure(metadataSourceFile, documents);
		} else if(packageType.equals("XMP")) {
			ms = new XMPMetadataStructure(metadataSourceFile, documents);
		}
		
		ms.toEDM(ms.getIndexInfo(), edm);
		return edm;
	}
	

	
	private void putToRepository(File file) throws RepositoryException, IOException {
		repositoryFacade.ingestFile(o.getIdentifier(), preservationSystem.getOpenCollectionName(), 
				EDM_FOR_ES_INDEX_METADATA_STREAM_ID+FILE_EXTENSION_XML, file, 
				"Object representation in Europeana Data Model", "application/rdf+xml");
	}

	
	
	@Override
	public void rollback() throws Exception {
		if ((edmXSLTDestinationFile!=null)&&(edmXSLTDestinationFile.exists())) 
			edmXSLTDestinationFile.delete();
		if ((edmIndexDestinationFile!=null)&&(edmIndexDestinationFile.exists())) 
			edmIndexDestinationFile.delete();
	}



	private String generateEDM(String objectId, String xsltFile,
			InputStream metadataStream) throws FileNotFoundException {
		
		XsltEDMGenerator edmGenerator=null;
		try {
			edmGenerator = new XsltEDMGenerator(xsltFile, metadataStream);
		} catch (TransformerConfigurationException e1) {
			throw new RuntimeException(e1);
		}	
		edmGenerator.setParameter("urn", o.getUrn());
		edmGenerator.setParameter("cho-base-uri", preservationSystem.getUrisCho() + "/" + objectId);
		edmGenerator.setParameter("aggr-base-uri", preservationSystem.getUrisAggr() + "/" + objectId);
		edmGenerator.setParameter("local-base-uri", preservationSystem.getUrisLocal());
		String edmResult=null;
		try {
			edmResult = edmGenerator.generate();
		} catch (TransformerException e1) {
			throw new RuntimeException(e1);
		}
		return edmResult;
	}
	
	
	/**
	 * Gets the map that describes which XSLTs should be
	 * used to convert Metadata to EDM.
	 * @return a map, keys represent metadata formats,
	 * 	values the path to the XSLT file
	 */
	public Map<String,String> getEdmMappings() {
		return edmMappings;
	}

	/**
	 * Sets the map that describes which XSLTs should be
	 * used to convert Metadata to EDM.
	 * @param a map, keys represent metadata formats,
	 * 	values the path to the XSLT file
	 */
	public void setEdmMappings(Map<String,String> edmMappings) {
		this.edmMappings = edmMappings;
	}

	/**
	 * Get the repository implementation
	 * @return the repository implementation
	 */
	public RepositoryFacade getRepositoryFacade() {
		return repositoryFacade;
	}
	
	/**
	 * Set the repository implementation
	 * @param repositoryFacade the repository implementation
	 */
	public void setRepositoryFacade(RepositoryFacade repositoryFacade) {
		this.repositoryFacade = repositoryFacade;
	}

}
