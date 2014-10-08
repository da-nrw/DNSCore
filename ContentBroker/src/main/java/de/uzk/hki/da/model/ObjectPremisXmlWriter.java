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

package de.uzk.hki.da.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.utils.MD5Checksum;


/**
 * Maps an object of our own native data model format to a premis file.
 *  
 * @author Sebastian Cuy
 * @author Thomas Kleinke
 * @author Daniel M. de Oliveira
 */
public class ObjectPremisXmlWriter {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(ObjectPremisXmlWriter.class);
	
	/** The Constant dateFormat. */
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	// Each PREMIS-Object describing a file has an mdSec element for the jhove output which needs an id. Simple Counter.
	/** The jhove md sec id counter. */
	private int jhoveMDSecIdCounter = 0; 
	
	/** The writer. */
	private XMLStreamWriter writer = null;

	/** The agents. */
	private Set<Agent> agents = null;
	
	/**
	 * Note: On its way writing the PREMIS file all the previously created jhove files get integrated into the resulting file. This
	 * can't be done outside and passed as object attributes because it can be potentially very big and cause memory issues.
	 *
	 * @param object the object
	 * @param f the f
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void serialize(Object object, File f) throws IOException {
		if ((object.getPackages()==null)||(object.getPackages().isEmpty())) throw new IllegalStateException("No Packages set");
		if (object.getIdentifier()==null) throw new IllegalStateException("object identifier is null");
		if (object.getContractor()==null) throw new IllegalStateException("object has no contractor");
		if (object.getOrig_name()==null) throw new IllegalStateException("object has no orig name");
		
		agents = new HashSet<Agent>();
		
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		FileOutputStream outputStream = new FileOutputStream(f);		

		try {
	    	writer = outputFactory.createXMLStreamWriter(outputStream, C.ENCODING_UTF_8);
			} catch (XMLStreamException e) {
			throw new IOException("Failed to create XMLStreamWriter", e);
			}
	    
	    try {	    
			writer.writeStartDocument(C.ENCODING_UTF_8, "1.0");
			writer.setPrefix("xsi", C.XSI_NS);
			  
			createOpenElement("premis", 0);
					createAttribute(C.XSI_NS, "schemaLocation", "info:lc/xmlns/premis-v2 http://www.loc.gov/standards/premis/v2/premis-v2-2.xsd");
			  		createAttribute("version", "2.2");
					createAttribute("xmlns", "info:lc/xmlns/premis-v2");
					createAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			
			createObjectElement(object.getIdentifier(), object.getUrn(), object.getOrig_name());
		   	
			for (Package pkg : object.getPackages())
				createPackageElement(object, pkg);
		   	
		   	// generate files
			for (Package pkg : object.getPackages()) 
				for (DAFile fi : pkg.getFiles())
					createFileElement(fi, object);
		   	
			generateEvents(object);
						
			for (Agent a : agents)
				createAgentElement(object, a);
			
			createOpenElement("rights", 1);
			generateRightsStatementElement(object.getRights());
			createOpenElement("rightsExtension", 2);
			generateRightsGrantedElement(object,object.getRights());
			
			createCloseElement(2);
			createCloseElement(1);
	
		   	createCloseElement(0);
	    
		   	writer.writeDTD("\n");
		   	writer.writeEndDocument();
			writer.close();
			outputStream.close();
		    
	    } catch (XMLStreamException e) {
	    	throw new RuntimeException("Failed to serialize premis.xml", e);
		}
	
	}


	/**
	 * @param object
	 * @throws XMLStreamException
	 */
	private void generateEvents(Object object) throws XMLStreamException {
		
		for (Package pkg : object.getPackages())
		  for (Event e : pkg.getEvents()){
			if (e.getType().toUpperCase().equals(C.EVENT_TYPE_CONVERT)
					|| e.getType().toUpperCase().equals(C.EVENT_TYPE_COPY)
					|| e.getType().toUpperCase().equals(C.EVENT_TYPE_CREATE)){
				logger.debug("Serializing convert event: "+e);
				createConvertEventElement(e);
			}else{
				logger.debug("Serializing package event:"+e);
				createPackageEventElement(object, pkg, e);
			}
		}
	}
	
	
	/**
	 * Creates the object element.
	 *
	 * @param objectIdentifier the object identifier
	 * @param urn the urn
	 * @throws XMLStreamException the xML stream exception
	 */
	private void createObjectElement(String objectIdentifier, String urn, String orig_name) throws XMLStreamException{
		
		createOpenElement("object", 1);
		createAttribute(C.XSI_NS, "type", "representation");
			createOpenElement("objectIdentifier", 2);
				createTextElement("objectIdentifierType", "OBJECT_BUSINESS_KEY", 3);
				createTextElement("objectIdentifierValue", objectIdentifier, 3);
			createCloseElement(2);
			createOpenElement("objectIdentifier", 2);
				createTextElement("objectIdentifierType", "URN", 3);
				createTextElement("objectIdentifierValue", urn, 3);
			createCloseElement(2);
			createTextElement("originalName",orig_name, 2);
		createCloseElement(1);
	}

	
	/**
	 * Creates the file element.
	 *
	 * @param f the f
	 * @param object the object
	 * @throws XMLStreamException the xML stream exception
	 * @author Daniel M. de Oliveira
	 * @author Thomas Kleinke
	 * @throws FileNotFoundException 
	 */
	private void createFileElement(DAFile f, Object object) throws XMLStreamException, FileNotFoundException{
		
		logger.debug("Start serializing file \"" + f.toString() + "\" as object element to PREMIS");
		
		createOpenElement("object", 1);
		createAttribute(C.XSI_NS, "type", "file");
		createOpenElement("objectIdentifier", 2);
			createTextElement("objectIdentifierType", "FILE_PATH", 3);
			createTextElement("objectIdentifierValue", f.getRep_name()+"/"+f.getRelative_path(), 3);
		createCloseElement(2);
		
		createOpenElement("objectCharacteristics", 2);
		createTextElement("compositionLevel", "0", 3);
		
		String chksum = null;
		try {
			chksum = MD5Checksum.getMD5checksumForLocalFile(f.toRegularFile());
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		createOpenElement("fixity", 3);
			createTextElement("messageDigestAlgorithm", "MD5", 4);
			createTextElement("messageDigest", chksum, 4);
			createTextElement("messageDigestOriginator", "ContentBroker", 4);
		
		createCloseElement(3);
		createTextElement("size", Integer.toString((int) FileUtils.sizeOf(f.toRegularFile())), 3);
		
		createOpenElement("format", 3);			
			createOpenElement("formatRegistry", 4);
				createTextElement("formatRegistryName", "PRONOM", 5);
				createTextElement("formatRegistryKey", f.getFormatPUID(), 5);
				createTextElement("formatRegistryRole", "specification", 5);
			createCloseElement(4);
		createCloseElement(3);

		
		createOpenElement("objectCharacteristicsExtension", 3);
			createOpenElement("mdSec", 4);
			createAttribute("ID", "_" + jhoveMDSecIdCounter); jhoveMDSecIdCounter++;
				createOpenElement("mdWrap", 5);
				createAttribute("MDTYPE", "OTHER");
				createAttribute("OTHERMDTYPE", "JHOVE");
					createOpenElement("xmlData", 6);
						System.out.println(DigestUtils.md5Hex(f.getRelative_path()));
						integrateJhoveData(Path.make(object.getDataPath(),"jhove_temp",f.getRep_name(),
								DigestUtils.md5Hex(f.getRelative_path())).toString(), 7);
					createCloseElement(6);
				createCloseElement(5);
			createCloseElement(4);
		createCloseElement(3);
		
		createCloseElement(2);// close objectCharacteristics
		
		String originalName = null;
		for (Event e : f.getPackage().getEvents()) {
			if (e.getType().toUpperCase().equals(C.EVENT_TYPE_CONVERT)
					&& e.getTarget_file().getRelative_path().equals(f.getRelative_path()))
				originalName = FilenameUtils.getName(e.getSource_file().getRelative_path());
		}

		if (originalName == null)
			originalName = FilenameUtils.getName(f.getRelative_path());
		
		createTextElement("originalName", originalName, 2);
		
		createOpenElement("storage", 2);
			createOpenElement("contentLocation", 3);
				createTextElement("contentLocationType", "FILE_PATH", 4);
				createTextElement("contentLocationValue", f.getRep_name()+"/"+f.getRelative_path(), 4);
			createCloseElement(3);
		createCloseElement(2);
		
		createOpenElement("relationship", 2);
			createTextElement("relationshipType", "structural", 3);
			createTextElement("relationshipSubType", "is included in", 3);
			createOpenElement("relatedObjectIdentification", 3);
				createTextElement("relatedObjectIdentifierType", "PACKAGE_NAME", 4);
				createTextElement("relatedObjectIdentifierValue",
						object.getIdentifier() + ".pack_" + f.getPackage().getName() + ".tar", 4);
			createCloseElement(3);
		createCloseElement(2);
		
		createCloseElement(1);
	}
	
	/**
	 * Creates the convert event element.
	 *
	 * @param e the e
	 * @throws XMLStreamException the xML stream exception
	 */
	private void createConvertEventElement(Event e) throws XMLStreamException{
		createOpenElement("event", 1);
		createOpenElement("eventIdentifier", 2);
			createTextElement("eventIdentifierType", "TARGET_FILE_PATH", 3);
			createTextElement("eventIdentifierValue", e.getTarget_file().getRep_name() + "/" + e.getTarget_file().getRelative_path(), 3);
		createCloseElement(2);
		createTextElement("eventType", e.getType(), 2);
		if (e.getDate() != null)
			createTextElement("eventDateTime", formatDate(e.getDate()), 2);
		
		if (e.getDetail() != null)
			createTextElement("eventDetail", e.getDetail(), 2);

		if (e.getOutcome() != null)
		{
			createOpenElement("eventOutcomeInformation", 2);
				createTextElement("eventOutcome", e.getOutcome(), 3);
			createCloseElement(2);
		}

		Agent a = new Agent();
		a.setType("NODE");
		a.setName(e.getAgent_name());
		a.setLongName(e.getAgent_long_name());
		agents.add(a);
		
		createOpenElement("linkingAgentIdentifier", 2);
		createTextElement("linkingAgentIdentifierType", a.getIdentifierType(), 3);
		createTextElement("linkingAgentIdentifierValue", a.getName(), 3);
		createCloseElement(2);
		
		if (e.getType().equals(C.EVENT_TYPE_CONVERT) || e.getType().equals(C.EVENT_TYPE_COPY)) {
			createOpenElement("linkingObjectIdentifier", 2);
			createTextElement("linkingObjectIdentifierType", "FILE_PATH", 3);
			createTextElement("linkingObjectIdentifierValue", e.getSource_file().getRep_name() + "/" + e.getSource_file().getRelative_path(), 3);
			createTextElement("linkingObjectRole", "source", 3);
			createCloseElement(2);
		}
		
		createOpenElement("linkingObjectIdentifier", 2);
		createTextElement("linkingObjectIdentifierType", "FILE_PATH", 3);
		createTextElement("linkingObjectIdentifierValue", e.getTarget_file().getRep_name() + "/" + e.getTarget_file().getRelative_path(), 3);
		createTextElement("linkingObjectRole", "outcome", 3);
		createCloseElement(2);

		createCloseElement(1);
	}
	
	
	/**
	 * Creates the package event element.
	 *
	 * @param object the object
	 * @param pkg the pkg
	 * @param event the event
	 * @throws XMLStreamException the XML stream exception
	 * @author Daniel M. de Oliveira
	 * @author Sebastian Cuy
	 */
	private void createPackageEventElement(Object object,Package pkg,Event event) throws XMLStreamException {
		
		logger.debug("Start serializing event /" + event.getIdType().toString()+"/"+event.getIdentifier());
		
		createOpenElement("event", 1);
			createOpenElement("eventIdentifier", 2);
				createTextElement("eventIdentifierType", event.getIdType().toString(), 3);
				createTextElement("eventIdentifierValue", event.getIdentifier(), 3);
			createCloseElement(2);
			createTextElement("eventType", event.getType().toString(), 2);
			if (event.getDate() != null)
				createTextElement("eventDateTime", formatDate(event.getDate()), 2);
		
		if (event.getDetail() != null)
			createTextElement("eventDetail", event.getDetail(), 2);
	
		if (event.getOutcome() != null)
		{
			createOpenElement("eventOutcomeInformation", 2);
				createTextElement("eventOutcome", event.getOutcome(), 3);
			createCloseElement(2);
		}
		
		createOpenElement("linkingAgentIdentifier", 2);
		
		Agent a = new Agent();
		a.setType(event.getAgent_type());
		a.setName(event.getAgent_name());
		agents.add(a);
		
		createTextElement("linkingAgentIdentifierType", a.getIdentifierType(), 3);
		createTextElement("linkingAgentIdentifierValue", a.getName(), 3);
		createCloseElement(2);	

		createOpenElement("linkingObjectIdentifier", 2);
		createTextElement("linkingObjectIdentifierType", "PACKAGE_NAME", 3);
		createTextElement("linkingObjectIdentifierValue", object.getIdentifier()+".pack_"+pkg.getName()+".tar", 3);
		createCloseElement(2);	
		
		createCloseElement(1);
		
		logger.trace("Serialized event " + event.getId());		
	}

	/**
	 * Creates the package element.
	 *
	 * @param o the o
	 * @param pkg the pkg
	 * @throws XMLStreamException the xML stream exception
	 */
	private void createPackageElement(Object o, Package pkg) throws XMLStreamException{
		createOpenElement("object", 1);
		createAttribute(C.XSI_NS, "type", "representation");
		createOpenElement("objectIdentifier", 2);
			createTextElement("objectIdentifierType", "PACKAGE_NAME", 3);
			createTextElement("objectIdentifierValue", o.getIdentifier()+".pack_"+pkg.getName()+".tar", 3);
		createCloseElement(2);
		createTextElement("originalName", pkg.getContainerName(), 2);
		createCloseElement(1);
	}
	
	/**
	 * Creates the agent element.
	 *
	 * @param o the o
	 * @param agent the agent
	 * @throws XMLStreamException the xML stream exception
	 */
	private void createAgentElement(Object o, Agent agent) throws XMLStreamException {
		
		logger.debug("Start serializing agent " + agent.getName());
		
		for (Agent a : o.getAgents()) {
			if (a.getName().equals(agent.getName()) && a.getType().equals(agent.getType()))
				agent = a;
		}
		
		createOpenElement("agent", 1);
			createOpenElement("agentIdentifier", 2);
				createTextElement("agentIdentifierType", agent.getIdentifierType(), 3);
				createTextElement("agentIdentifierValue", agent.getName(), 3);
			createCloseElement(2);
			if (agent.getLongName() != null && !agent.getLongName().equals(""))
				createTextElement("agentName", agent.getLongName(), 2);
			createTextElement("agentType", agent.getType(), 2);
		createCloseElement(1);
	}

	/**
	 * Generate rights statement element.
	 *
	 * @param right the right
	 * @throws XMLStreamException the xML stream exception
	 * @author Sebastian Cuy
	 * @author Thomas Kleinke
	 */
	private void generateRightsStatementElement(RightsStatement right) throws XMLStreamException {
		
		logger.trace("Start serializing rights section");
		
		createOpenElement("rightsStatement", 2);
			createOpenElement("rightsStatementIdentifier", 3);
				createTextElement("rightsStatementIdentifierType", "rightsid", 4);
				createTextElement("rightsStatementIdentifierValue", right.getId(), 4);
			createCloseElement(3);
			createTextElement("rightsBasis", "license", 3);
		
		if (right.getPublicationRights() != null 
				&& !right.getPublicationRights().isEmpty()) {
			for (PublicationRight pubRight : right.getPublicationRights()) {
				createOpenElement("rightsGranted", 3);
					createTextElement("act", "PUBLICATION" + "_" + pubRight.getAudience().toString(), 4);
					createTextElement("restriction", "see rightsExtension", 4);
					if (pubRight.getStartDate() != null) {
						createOpenElement("termOfGrant", 4);
						createTextElement("startDate", formatDate(pubRight.getStartDate()), 5);
						createCloseElement(4);
					}
				createCloseElement(3);					
			}
		}
		
		if (right.getMigrationRight() != null) {
			createOpenElement("rightsGranted", 3);
				createTextElement("act", "MIGRATION", 4);
				createTextElement("restriction", "see rightsExtension", 4);
				if (right.getMigrationRight().getStartDate() != null) {
					createOpenElement("termOfGrant", 4);
					createTextElement("startDate", formatDate(right.getMigrationRight().getStartDate()), 5);
					createCloseElement(4);
				}
			createCloseElement(3);
		}
		
		createCloseElement(2);
		
		logger.trace("Serialized rights section");		
	}

	/**
	 * Generate rights granted element.
	 *
	 * @param right the right
	 * @throws XMLStreamException the xML stream exception
	 * @author Sebastian Cuy
	 * @author Thomas Kleinke
	 */
	private void generateRightsGrantedElement(Object object,RightsStatement right) throws XMLStreamException {
		
		logger.trace("Start serializing rights granted element");
		
		createOpenElement("rightsGranted", 3);
		createAttribute("xmlns", C.CONTRACT_V1_URL);
		createAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		createAttribute("xsi:schemaLocation", C.CONTRACT_V1_URL+" "+C.CONTRACT_V1_SCHEMA_LOCATION);
		
		if (right.getMigrationRight() != null) {
			createOpenElement("migrationRight", 4);
				createTextElement("condition", right.getMigrationRight().getCondition().toString(), 5);
			createCloseElement(4);
		}
		
		if (right.getPublicationRights() != null
				&& !right.getPublicationRights().isEmpty()) {
			for (PublicationRight pubRight : right.getPublicationRights()) {
				
				createOpenElement("publicationRight", 4);
					createTextElement("audience", pubRight.getAudience().toString(), 5);
					
					if (pubRight.getStartDate() != null)
						createTextElement("startDate", formatDate(pubRight.getStartDate()), 5);
					
					if (pubRight.getLawID() != null && !pubRight.getLawID().equals(""))
						createTextElement("lawID", pubRight.getLawID(), 5);
					
					if (pubRight.getAudioRestriction() != null ||
						pubRight.getImageRestriction() != null ||
						pubRight.getTextRestriction() != null ||
						pubRight.getVideoRestriction() != null)
					{					
						createOpenElement("restrictions", 5);
						
						if (pubRight.getImageRestriction() != null) {
							ImageRestriction imageRestriction = pubRight.getImageRestriction();
							createOpenElement("restrictImage", 6);
							if (imageRestriction.getWidth() != null && !imageRestriction.getWidth().equals(""))
								createTextElement("width", imageRestriction.getWidth(), 7);
							if (imageRestriction.getHeight() != null && !imageRestriction.getHeight().equals(""))
								createTextElement("height", imageRestriction.getHeight(), 7);
							if (imageRestriction.getFooterText() != null && !imageRestriction.getFooterText().equals(""))
								createTextElement("footerText", imageRestriction.getFooterText(), 7);
							if (imageRestriction.getWatermarkString() != null && !imageRestriction.getWatermarkString().equals("")) {
								createOpenElement("watermark", 7);
									createTextElement("watermarkString", imageRestriction.getWatermarkString(), 8);
									createTextElement("pointSize", imageRestriction.getWatermarkPointSize(), 8);
									createTextElement("position", imageRestriction.getWatermarkPosition(), 8);
									createTextElement("opacity", imageRestriction.getWatermarkOpacity(), 8);
								createCloseElement(7);
							}
							createCloseElement(6);
						}
						if (pubRight.getVideoRestriction() != null) {
							VideoRestriction videoRestriction = pubRight.getVideoRestriction();
							createOpenElement("restrictVideo", 6);
							if (videoRestriction.getWidth() != null && !videoRestriction.getWidth().equals(""))
								createTextElement("width", videoRestriction.getWidth(), 7);
							if (videoRestriction.getHeight() != null && !videoRestriction.getHeight().equals(""))
								createTextElement("height", videoRestriction.getHeight(), 7);
							if (videoRestriction.getDuration() != null)
								createTextElement("duration", Integer.toString(videoRestriction.getDuration()), 7);								
							createCloseElement(6);
						}
						if (pubRight.getAudioRestriction() != null) {
							createOpenElement("restrictAudio", 6);
								createTextElement("duration", Integer.toString(pubRight.getAudioRestriction().getDuration()), 7);
							createCloseElement(6);
						}				
						if (pubRight.getTextRestriction() != null) {					
							createOpenElement("restrictText", 6);
							if (pubRight.getTextRestriction().getPages() != null)
								createTextElement("numberOfPages", Integer.toString(pubRight.getTextRestriction().getPages()), 7);
								
							int[] certainPages = pubRight.getTextRestriction().getCertainPages();
							if (certainPages != null && certainPages.length > 0) {
								StringBuilder sb = new StringBuilder(Integer.toString(certainPages[0]));
								for (int i = 1; i < certainPages.length; i++) {
									sb.append(" ").append(certainPages[i]);
								}
								createTextElement("certainPages", sb.toString(), 7);
							}
							createCloseElement(6);
						}
						
					createCloseElement(5);
				}
				else
					createEmptyElement("restrictions", 5);
						
				createCloseElement(4);
			}
		}
		
		if (object.ddbExcluded())
			createEmptyElement("DDBexclusion", 4);
		
		createCloseElement(3);
		
		logger.trace("Serialized rights granted element");	
	}
	
	/**
	 * Integrate jhove data.
	 *
	 * @param jhoveFilePath the jhove file path
	 * @param tab the tab
	 * @throws XMLStreamException the xML stream exception
	 * @author Thomas Kleinke
	 * @throws FileNotFoundException 
	 */
	private void integrateJhoveData(String jhoveFilePath, int tab) throws XMLStreamException, FileNotFoundException {
		File jhoveFile = new File(jhoveFilePath);
		if (!jhoveFile.exists()) throw new FileNotFoundException("file does not exist. "+jhoveFile);
		
		FileInputStream inputStream = null;
		
		inputStream = new FileInputStream(jhoveFile);
		
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader streamReader = inputFactory.createXMLStreamReader(inputStream);
		
		boolean textElement = false;
		
		while(streamReader.hasNext())
		{
		    int event = streamReader.next();
		    
		    switch (event)
		    {
	
		    case XMLStreamConstants.START_ELEMENT:
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
		    				    	
		    	break;
		    	
		    case XMLStreamConstants.CHARACTERS:
		    	if(!streamReader.isWhiteSpace())
		    	{
		    		writer.writeCharacters(streamReader.getText());
		    		textElement = true;
		    	}
		    	break;
		            
		    case XMLStreamConstants.END_ELEMENT:
		    	tab--;
		    	
		    	if (!textElement)
		    	{
		    		writer.writeDTD("\n");
		    		indent(tab);
		    	}
	    	
		    	writer.writeEndElement();
		    	textElement = false;
		    	break;
		    	
		    default:
		    	break;
		    }
		}
		
		streamReader.close();
    	try {
			inputStream.close();
		} catch (IOException e) {
			throw new RuntimeException("Failed to close input stream", e);
		}
	}
	
	private String formatDate(Date date) {
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
		String dateString = dateFormat.format(date);
		if (dateString.endsWith("00"))
			dateString = dateString.substring(0, dateString.length() - 2) + ":00";
		return dateString;
	}

	/**
	 * Creates the open element.
	 *
	 * @param namespace the namespace
	 * @param name the name
	 * @param tab the tab
	 * @throws XMLStreamException the xML stream exception
	 */
	private void createOpenElement(String namespace, String name, int tab) throws XMLStreamException {
		
		writer.writeDTD("\n");
		indent(tab);
		
		if (namespace.equals(""))
			writer.writeStartElement(name);
		else
			writer.writeStartElement(namespace, name);	
	}
	
	/**
	 * Creates the open element.
	 *
	 * @param name the name
	 * @param tab the tab
	 * @throws XMLStreamException the xML stream exception
	 */
	private void createOpenElement(String name, int tab) throws XMLStreamException {
		
		createOpenElement("", name, tab);
	}
	
	/**
	 * Creates the close element.
	 *
	 * @param tab the tab
	 * @throws XMLStreamException the xML stream exception
	 */
	private void createCloseElement(int tab) throws XMLStreamException {
		
		writer.writeDTD("\n");
		indent(tab);
		
		writer.writeEndElement();
	}
	
	/**
	 * Creates the text element.
	 *
	 * @param namespace the namespace
	 * @param name the name
	 * @param text the text
	 * @param tab the tab
	 * @throws XMLStreamException the xML stream exception
	 */
	private void createTextElement(String namespace, String name, String text, int tab) throws XMLStreamException {
		
		writer.writeDTD("\n");
		indent(tab);
		
		if (namespace.equals(""))
			writer.writeStartElement(name);
		else
			writer.writeStartElement(namespace, name);
		
		writer.writeCharacters(text);
		writer.writeEndElement();	
	}
	
	/**
	 * Creates the text element.
	 *
	 * @param name the name
	 * @param text the text
	 * @param tab the tab
	 * @throws XMLStreamException the xML stream exception
	 */
	private void createTextElement(String name, String text, int tab) throws XMLStreamException  {
		
		createTextElement("", name, text, tab);
	}
	
	/**
	 * Creates the empty element.
	 *
	 * @param name the name
	 * @param tab the tab
	 * @throws XMLStreamException the xML stream exception
	 */
	private void createEmptyElement(String name, int tab) throws XMLStreamException {
		
		writer.writeDTD("\n");
		indent(tab);
		
		writer.writeEmptyElement(name);		
	}
	
	/**
	 * Creates the attribute.
	 *
	 * @param namespace the namespace
	 * @param name the name
	 * @param value the value
	 * @throws XMLStreamException the xML stream exception
	 */
	private void createAttribute(String namespace, String name, String value) throws XMLStreamException {
		
		if (namespace.equals(""))
			writer.writeAttribute(name, value);
		else
			writer.writeAttribute(namespace, name, value);		
	}
	
	/**
	 * Creates the attribute.
	 *
	 * @param name the name
	 * @param value the value
	 * @throws XMLStreamException the xML stream exception
	 */
	private void createAttribute(String name, String value) throws XMLStreamException {
		
		createAttribute("", name, value);
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
