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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.FileUtils;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.ff.FileFormatFacade;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.ObjectPremisXmlReader;
import de.uzk.hki.da.model.ObjectPremisXmlWriter;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.PremisXmlValidator;
import de.uzk.hki.da.model.PublicationRight;

/**
 * 
 * Expects that below the objects data folder there is a directory jhove_temp which contains
 * xml files with the metadata extracted from jhove.
 * The metadata file names are md5 hashed.
 * Example:
 * <li>File: WorkAreaRootPath/work/csn/oid/data/repname/sub/a.jpg
 * <li>Jhove: WorkAreaRootPath/work/csn/oid/data/jhove_temp/repname/md5hashed(sub/a.jpg)
 * 
 * @author Thomas Kleinke
 * @author Daniel M. de Oliveira
 */
public class CreatePremisAction extends AbstractAction {

	private static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";

	private FileFormatFacade fileFormatFacade;
	
	private List<Event> addedEvents = new ArrayList<Event>();

	
	/** The writer. */
	private XMLStreamWriter writer;
	
	/** The output stream. */
	private FileOutputStream outputStream;
	
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		// Auto-generated method stub
	}

	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		// Auto-generated method stub
		
	}

	/**
	 * @throws FileNotFoundException if one or more of the jhove output files are not present.
	 */
	@Override
	public boolean implementation() throws IOException	{
		logger.debug("Listing all files attached to all packages of the object:");
		 for (Package pkg : object.getPackages()) 
		   		for (DAFile fi : pkg.getFiles())
			   		logger.debug(fi.toString());
		
		Object newPREMISObject = new Object();
		newPREMISObject.setTransientNodeRef(object.getTransientNodeRef());;
		newPREMISObject.setOrig_name(object.getOrig_name());
		newPREMISObject.setIdentifier(object.getIdentifier());
		newPREMISObject.setUrn(object.getUrn());
		newPREMISObject.setContractor(object.getContractor());
		
		Object sipPREMISObject = parseSipPremisFile(
				new File(Path.make(object.getPath("newest"),"premis.xml").toString().replace("+b", "+a")));
		
		if (sipPREMISObject.getPackages().size() > 0) {
			object.getLatestPackage().getEvents().addAll(sipPREMISObject.getPackages().get(0).getEvents());
			addedEvents.addAll(sipPREMISObject.getPackages().get(0).getEvents());
		}
		
		newPREMISObject.setRights(sipPREMISObject.getRights());
		newPREMISObject.getRights().setId(object.getIdentifier() + "#rights");
		newPREMISObject.getAgents().addAll(sipPREMISObject.getAgents());
		
		Event ingestEventElement = generateIngestEventElement();
		object.getLatestPackage().getEvents().add(ingestEventElement);
		addedEvents.add(ingestEventElement);
		
		newPREMISObject.getPackages().add(object.getLatestPackage());
		
		if (object.isDelta()){
		
			Object mainPREMISObject = parseOldPremisFile(
					Path.makeFile(object.getDataPath(),"premis_old.xml"));

			if (mainPREMISObject==null) throw new RuntimeException("mainPREMISObject is null");
			if (mainPREMISObject.getPackages()==null) throw new RuntimeException("mainPREMISObject.getPackages is null");
			if (mainPREMISObject.getPackages().size()==0) throw new RuntimeException("number of packages from old PREMIS expected to be not 0");
			
			// TODO refactor
			for (Package mainPREMISPackage : mainPREMISObject.getPackages()) {
				logger.debug("attaching "+mainPREMISPackage+" to temp object which waits for serialization into PREMIS");
				
				Package newPREMISPackage = new Package();
				newPREMISPackage.setId(object.getLatestPackage().getId());
				newPREMISPackage.setName(mainPREMISPackage.getName());
				newPREMISPackage.setContainerName(mainPREMISPackage.getContainerName());
				newPREMISPackage.setFiles(mainPREMISPackage.getFiles());
				newPREMISPackage.setEvents(mainPREMISPackage.getEvents());
				newPREMISObject.getPackages().add(newPREMISPackage);
				newPREMISObject.setTransientNodeRef(localNode);
				newPREMISObject.reattach();
			}
			newPREMISObject.getAgents().addAll(mainPREMISObject.getAgents());
		}
				
		checkConvertEvents(newPREMISObject);
	
		File newPREMISXml = Path.make( 
				object.getPath("newest"),"premis.xml").toFile();
		logger.trace("trying to write new Premis file at " + newPREMISXml.getAbsolutePath());
		new ObjectPremisXmlWriter().serialize(newPREMISObject, newPREMISXml);
		
		if (!PremisXmlValidator.validatePremisFile(newPREMISXml))
			throw new RuntimeException("PREMIS that has recently been created is not valid");
		logger.trace("Successfully created premis file");
		object.getLatestPackage().getFiles().add(new DAFile(object.getLatestPackage(),job.getRep_name()+"b","premis.xml"));
		
		for (Package p : newPREMISObject.getPackages()){
			logger.debug("pname:" + p.getName());
		}
		
		determineDisclosureLimits(newPREMISObject);
		deleteJhoveTempFiles();
		
		return true;
	}
	
	/**
	 * Saves file format information in ActionCommunicatorService for later storage in object db
	 * @author Thomas Kleinke
	 * @author Daniel M. de Oliveira
	 */
	private void determineDisclosureLimits(Object object) {
		
		Date static_nondisclosure_limit = null;
		String dynamic_nondisclosure_limit = null;
		if (object.getRights() != null && object.getRights().getPublicationRights() != null)
		{
			for (PublicationRight p : object.getRights().getPublicationRights())
			{
				if (p.getAudience().equals(PublicationRight.Audience.PUBLIC))
				{
					static_nondisclosure_limit = p.getStartDate();
					if (p.getLawID() != null)
						dynamic_nondisclosure_limit = p.getLawID().toString();
					break;
				}
			}
		}
		
		job.setDynamic_nondisclosure_limit(dynamic_nondisclosure_limit);
		job.setStatic_nondisclosure_limit(static_nondisclosure_limit);
	}
	
	private Event generateIngestEventElement() {
		
		Event ingestEventElement = new Event();
		ingestEventElement.setType("INGEST");
		ingestEventElement.setIdentifier(object.getIdentifier() + "+" + object.getLatestPackage().getName());
		ingestEventElement.setIdType(Event.IdType.INGEST_ID);
		ingestEventElement.setDate(new Date());
		ingestEventElement.setAgent_name(object.getContractor().getShort_name());
		ingestEventElement.setAgent_type("CONTRACTOR");
		return ingestEventElement;		
	}
	
	private Object parseSipPremisFile(File premisFile) {
		
		Object premisData;
		
		ObjectPremisXmlReader reader = new ObjectPremisXmlReader();
		try {
			premisData = reader.deserialize(premisFile);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't read file " + premisFile.getAbsolutePath(), e);
		} catch (ParseException pe){
			throw new RuntimeException("Error while parsing premis file", pe);
		}
		return premisData;
	}
	
	
	
	private Object parseOldPremisFile(File premisFile) {

		Object premisData;

		try {
			extractJhoveData(premisFile.getAbsolutePath(),
					new File("jhove").getAbsolutePath() +
					"/temp/" + job.getId());
		} catch (XMLStreamException e) {
			throw new RuntimeException("Couldn't extract jhove sections of file " + premisFile.getAbsolutePath(), e);
		}

		ObjectPremisXmlReader reader = new ObjectPremisXmlReader();
		
		
		try {
			premisData = reader.deserialize(premisFile);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't read file " + premisFile.getAbsolutePath(), e);
		} catch (ParseException pe){
			throw new RuntimeException("error while parsing premis file",pe);
		}

		return premisData;
	}

	/**
	 * @author Thomas Kleinke
	 * @param object
	 * @throws RuntimeException if rep b files without corresponding CONVERT/COPY/CREATE events exist 
	 */
	private void checkConvertEvents(Object object) {
		
		for (Package pkg : object.getPackages()) {
			for (DAFile f : pkg.getFiles()) {
				if (f.getRep_name().endsWith("b")) {
					boolean eventExists = false;
					for (Event e : pkg.getEvents()) {
						if (e.getType().equals("CONVERT") || e.getType().equals("COPY") || e.getType().equals("CREATE")
								&& e.getTarget_file() != null
								&& e.getTarget_file().getRelative_path().equals(f.getRelative_path())) {
							eventExists = true;
						}
					}
					if (!eventExists)
						throw new RuntimeException("No event found for file " + f.toRegularFile().getAbsolutePath());
				}
			}
		}		
	}
			
	private void deleteJhoveTempFiles() {
		File tempFolder = new File("jhove/temp/" + job.getId());
		if (tempFolder.exists())
		try {
			FileUtils.deleteDirectory(tempFolder);
		} catch (IOException e) {
			throw new RuntimeException("Failed to delete directory " + tempFolder);
		}
	}
		
	/**
	 * @author Thomas Kleinke
	 */
	@Override
	public void rollback() throws Exception {
		
		Path.make(object.getPath("newest"),"premis.xml").toFile().delete();
		
		File tempFolder = new File("jhove/temp/" + job.getId() + "/premis_output/");
		if (tempFolder.exists())
			FileUtils.deleteDirectory(tempFolder);
		
		object.getLatestPackage().getEvents().removeAll(addedEvents);
		
		job.setStatic_nondisclosure_limit(null);
		job.setDynamic_nondisclosure_limit(null);
	}

	public FileFormatFacade getFileFormatFacade() {
		return fileFormatFacade;
	}

	public void setFileFormatFacade(FileFormatFacade fff) {
		this.fileFormatFacade = fff;
	}
	
	/**
	 * Accepts a premis.xml file and creates a new xml file for each jhove section  
	 * 
	 * @author Thomas Kleinke
	 * Extract jhove data.
	 *
	 * @param premisFilePath the premis file path
	 * @param outputFolder the output folder
	 * @throws XMLStreamException the xML stream exception
	 */
	public void extractJhoveData(String premisFilePath, String outputFolder) throws XMLStreamException {
		
		
		outputFolder += "/premis_output/";
		
		FileInputStream inputStream = null;
		
		try {
			inputStream = new FileInputStream(premisFilePath);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Couldn't find file " + premisFilePath, e);
		}
		
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader streamReader = inputFactory.createXMLStreamReader(inputStream);
		
		boolean textElement = false;
		boolean jhoveSection = false;
		boolean objectIdentifierValue = false;
		int tab = 0;
		String fileId = "";
		
		while(streamReader.hasNext())
		{
		    int event = streamReader.next();
		    
		    switch (event)
		    {
       
		    case XMLStreamConstants.START_ELEMENT:
		    	
		    	if (streamReader.getLocalName().equals("jhove"))
	    		{
	    			jhoveSection = true;
	    			String outputFilePath = outputFolder + 
	    									fileId.replace('/', '_').replace('.', '_') + ".xml";
	    			if (!new File(outputFolder).exists())
	    				new File(outputFolder).mkdirs();
	    			writer = startNewDocument(outputFilePath);
	    		}
	    		
	    		if (streamReader.getLocalName().equals("objectIdentifierValue"))
	    			objectIdentifierValue = true;
		    	
		    	if (jhoveSection)
		    	{
		    	 	writer.writeDTD("\n");
		    	 	indent(tab);
		    	 	tab++;
		    	
		    	 	String prefix = streamReader.getPrefix();

		    	 	if (prefix != null && !prefix.equals(""))
		    	 	{
		    	 		writer.setPrefix(prefix, streamReader.getNamespaceURI());	    	
		    	 		writer.writeStartElement(streamReader.getNamespaceURI(), streamReader.getLocalName());
		    	 	}
		    	 	else
		    	 		writer.writeStartElement(streamReader.getLocalName());
		    	 	
		    	 	for (int i = 0; i < streamReader.getNamespaceCount(); i++)
		    	 		writer.writeNamespace(streamReader.getNamespacePrefix(i), streamReader.getNamespaceURI(i));
		    	 			    	 	    	 	
		    	 	for (int i = 0; i < streamReader.getAttributeCount(); i++)
		    	 	{
		    	 		QName qname = streamReader.getAttributeName(i);
		    	 		String attributeName = qname.getLocalPart();
		    	 		String attributePrefix = qname.getPrefix(); 
		    	 		if (attributePrefix != null && !attributePrefix.equals(""))
		    	 			attributeName = attributePrefix + ":" + attributeName;
		    		
		    	 		writer.writeAttribute(attributeName, streamReader.getAttributeValue(i));
		    	 	}
		    	}
		    				    	
		    	break;
		    	
		    case XMLStreamConstants.CHARACTERS:
		    	if (objectIdentifierValue)
		    	{
		    		fileId = streamReader.getText();
		    		objectIdentifierValue = false;
		    	}
		    	
		    	if(jhoveSection && !streamReader.isWhiteSpace())
		    	{
		    		writer.writeCharacters(streamReader.getText());
		    		textElement = true;
		    	}
		    	break;
		            
		    case XMLStreamConstants.END_ELEMENT:
		       	if (jhoveSection)
		       	{
		       		tab--;
		    	
		       		if (!textElement)
		       		{
		       			writer.writeDTD("\n");
		       			indent(tab);
		       		}
	    	
		       		writer.writeEndElement();
		       		textElement = false;
		    	
		       		if (streamReader.getLocalName().equals("jhove"))
		       		{
		       			jhoveSection = false;
		       			finalizeDocument();
		       		}
		       	}
		    	break;
		    	
		    case XMLStreamConstants.END_DOCUMENT:
		    	streamReader.close();
		    	try {
					inputStream.close();
				} catch (IOException e) {
					throw new RuntimeException("Failed to close input stream", e);
				}
		    	break;
		    	
		    default:
		    	break;
		    }
		}
	}
	
	/**
	 * Start new document.
	 *
	 * @param filePath the file path
	 * @return the xML stream writer
	 */
	private XMLStreamWriter startNewDocument(String filePath) {
		
		XMLStreamWriter newWriter;
		
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		try {
			outputStream = new FileOutputStream(filePath);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Failed to create FileOutputStream", e);
		}

		try {
			newWriter = outputFactory.createXMLStreamWriter(outputStream);

			newWriter.writeStartDocument("UTF-8", "1.0");
			newWriter.setPrefix("xsi", XSI_NS);
		} catch (Exception e) {
			throw new RuntimeException("Failed to create XMLStreamWriter", e);
		}
	    		
		return newWriter;
	}
	
	/**
	 * Finalize document.
	 */
	private void finalizeDocument() {
		
		try {
			writer.writeDTD("\n");
			writer.writeEndDocument();
			writer.close();
		} catch (XMLStreamException e) {
			throw new RuntimeException("Failed to finalize document", e);
		}
		writer = null;
		
		try {
			outputStream.close();
		} catch (IOException e) {
			throw new RuntimeException("Failed to close FileOutputStream", e);
		}
		outputStream = null;
	}
	
	/**
	 * Indent.
	 *
	 * @param tab the tab
	 * @throws XMLStreamException the xML stream exception
	 */
	private void indent(int tab) throws XMLStreamException {
		
		for (int i = 0; i < tab; i++)
			writer.writeDTD("    ");
	}

	
	
	
	

}
