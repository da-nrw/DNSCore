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

import static de.uzk.hki.da.utils.C.EDM_FOR_ES_INDEX_METADATA_STREAM_ID;
import static de.uzk.hki.da.utils.C.EDM_XSLT_METADATA_STREAM_ID;
import static de.uzk.hki.da.utils.C.FILE_EXTENSION_XML;
import static de.uzk.hki.da.utils.StringUtilities.isNotSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.PreconditionsNotMetException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.metadata.EadMetsMetadataStructure;
import de.uzk.hki.da.metadata.LidoMetadataStructure;
import de.uzk.hki.da.metadata.MetadataStructure;
import de.uzk.hki.da.metadata.MetsMetadataStructure;
import de.uzk.hki.da.metadata.XsltGenerator;
import de.uzk.hki.da.model.Document;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.repository.RepositoryFacade;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;

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

	@Override
	public void checkConfiguration() {
		if (repositoryFacade == null) throw new ConfigurationException("repositoryFacade");
	}
	

	@Override
	public void checkPreconditions() {
		if (edmMappings == null)
			throw new PreconditionsNotMetException("edmMappings not set.");
		for (String filePath:edmMappings.values())
			if (!new File(filePath).exists())
				throw new PreconditionsNotMetException("mapping file "+filePath+" does not exist");
		if (isNotSet(o.getPackage_type()))
			throw new PreconditionsNotMetException("missing package type");
	}


	@Override
	public boolean implementation() throws IOException, RepositoryException, JDOMException, ParserConfigurationException, SAXException {
		
		String xsltTransformationFile = getEdmMappings().get(o.getPackage_type());
		if (xsltTransformationFile == null)
			throw new RuntimeException("No mapping for package type: '" + o.getPackage_type());
		if (! new File(xsltTransformationFile).exists())
			throw new FileNotFoundException("Missing file: "+xsltTransformationFile);

		
		File metadataSourceFile = getWa().pipMetadataFile(WorkArea.PUBLIC,o.getPackage_type());
		if (!metadataSourceFile.exists())
			throw new RuntimeException("Missing file in public PIP: "+o.getPackage_type()+FILE_EXTENSION_XML);

		edmXSLTDestinationFile = generateEdmUsingXslt(xsltTransformationFile, new File(o.getPackage_type()+FILE_EXTENSION_XML), EDM_XSLT_METADATA_STREAM_ID);
		putToRepository(edmXSLTDestinationFile); //this file will be overwriten by the next call of putToRepository(...)
		
		File metadataFile= new RelativePath(o.getPackage_type()+FILE_EXTENSION_XML).toFile();
		edmIndexDestinationFile = serializeEDM(xsltTransformationFile, metadataFile);
		putToRepository(edmIndexDestinationFile);
		
		return true;
	}
	
	private File generateEdmUsingXslt(String xsltTransformationFile,File metadataSourceFile, String edmId) throws IOException {
		
		File edm = getWa().pipMetadataFile(WorkArea.PUBLIC, edmId);
		PrintWriter out = null;
		FileInputStream fis = new FileInputStream(Path.makeFile(wa.pipFolder(WorkArea.PUBLIC),metadataSourceFile.getPath()));
		String edmResult = generateEDM(o.getIdentifier(), xsltTransformationFile, fis);
		try {
			out = new PrintWriter(edm);
			out.println(edmResult);
		}
		finally {
			out.close();
			fis.close();
		}
		return edm;
	}
	
	private File serializeEDM(String xsltTransformationFile, File metadataFile) throws JDOMException, IOException, ParserConfigurationException, SAXException {
		
		String packageType = o.getPackage_type();
		MetadataStructure ms = null;
		File edm = null;
		
		try {
			if(packageType.equals("EAD") || packageType.equals("METS") || packageType.equals("LIDO")) {
				edm = getWa().pipMetadataFile(WorkArea.PUBLIC,EDM_FOR_ES_INDEX_METADATA_STREAM_ID);
				List<Document> documents = o.getDocuments();
				
				if(packageType.equals("EAD")) {
					ms = new EadMetsMetadataStructure(wa.pipFolder(WorkArea.PUBLIC),metadataFile, documents);
				} else if(packageType.equals("METS")) {
					ms = new MetsMetadataStructure(wa.pipFolder(WorkArea.PUBLIC),metadataFile, documents);
				} else if(packageType.equals("LIDO")) {
					ms = new LidoMetadataStructure(wa.pipFolder(WorkArea.PUBLIC),metadataFile, documents);
				}
				ms.toEDM(ms.getIndexInfo(o.getIdentifier()), edm, preservationSystem, o.getIdentifier(), o.getUrn());
			} else if(packageType.equals("XMP")) {
				edm = generateEdmUsingXslt(xsltTransformationFile, metadataFile, EDM_FOR_ES_INDEX_METADATA_STREAM_ID);
			} else {
				throw new UserException(null, "Unbekanntes Metadatenformat.");
			}
			return edm;
		} catch (Exception e) {
			throw new RuntimeException("Unable to serialize EDM!",e);
		}
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
			InputStream metadataStream) throws FileNotFoundException, UnsupportedEncodingException {		
		
		XsltGenerator edmGenerator=null;
		try {
			edmGenerator = new XsltGenerator(xsltFile, metadataStream);
			edmGenerator.setParameter("urn", o.getUrn());
			edmGenerator.setParameter("cho-base-uri", preservationSystem.getUrisCho() + "/" + objectId);
			edmGenerator.setParameter("aggr-base-uri", preservationSystem.getUrisAggr() + "/" + objectId);
			edmGenerator.setParameter("local-base-uri", preservationSystem.getUrisLocal());
		} catch (TransformerConfigurationException e1) {
			throw new RuntimeException(e1);
		}	
		String edmResult=null;
		try {
			edmResult = edmGenerator.generate();
		} catch (TransformerException e1) {
			throw new RuntimeException(e1);
		} catch (IOException e) {
			throw new RuntimeException(e);
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
