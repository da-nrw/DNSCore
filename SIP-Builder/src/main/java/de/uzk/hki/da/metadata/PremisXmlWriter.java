/*
  DA-NRW Software Suite | SIP-Builder
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
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

package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import de.uzk.hki.da.main.SIPBuilder;
import de.uzk.hki.da.sb.SIPFactory;
import de.uzk.hki.da.utils.Utilities;

/**
 * Provides methods for XML file creation (premis.xml and Contract-XML)
 * 
 * @author Thomas Kleinke
 */
public class PremisXmlWriter {

	private static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private static final SimpleDateFormat dayDateFormat = new SimpleDateFormat("yyyy-MM-dd'T00:00:00.000+01:00'");
	
	private XMLStreamWriter writer = null;

	
	/**
	 * Creates a new premis.xml file; the rights settings are taken from the SIPFactory
	 * 
	 * @param sip The active SIPFactory
	 * @param f The premis file to create
	 * @param packageName The package name
	 * @throws Exception
	 */
	public void createPremisFile(SIPFactory sip, File f, String packageName) throws Exception {
		
		createPremisFile(sip, f, null, packageName);
	}
	
	/**
	 * Creates a new premis.xml; the rights settings are taken from the SIPFactory or optionally from an existing premis.xml file
	 * 
	 * @param sip The active SIPFactory
	 * @param f The premis file to create
	 * @param rightsSourcePremisFile The premis file from which the rights settings are taken (null if the rights settings are
	 * taken from the SIPFactory)
	 * @param packageName The package name
	 * @throws Exception
	 */
	public void createPremisFile(SIPFactory sip, File f, File rightsSourcePremisFile, String packageName) throws Exception {
		
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		FileOutputStream outputStream = new FileOutputStream(f);		

		try {
	    		writer = outputFactory.createXMLStreamWriter(outputStream, "UTF-8");
			} catch (XMLStreamException e) {
				throw new IOException("Failed to create XMLStreamWriter", e);
			}
	    
	    try {	    
		   	  writer.writeStartDocument("UTF-8", "1.0");
		   	  writer.setPrefix("xsi", XSI_NS);
			  
		   	  createOpenElement("premis", 0);
		   	  		createAttribute("xmlns", "info:lc/xmlns/premis-v2");
		   	  		createAttribute("xmlns:premis", "info:lc/xmlns/premis-v2");
		   	  		createAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		   	  		createAttribute(XSI_NS, "schemaLocation", "info:lc/xmlns/premis-v2 http://www.loc.gov/standards/premis/v2/premis-v2-2.xsd");
		   	  		createAttribute("version", "2.2");
		   	  				   	
		   	  		generateObjectElement(packageName);
		   	  		generateEventElement(packageName);
		   	  		generateAgentElement();
		   	  	if (rightsSourcePremisFile == null)
		   	  		generateRightsElement(sip.getContractRights());
		   	  	else
		   	  		copyRightsElementFromPremisFile(rightsSourcePremisFile);
		   	  createCloseElement(0);

		   	  writer.writeDTD("\n");
		   	  writer.writeEndDocument();
		   	  writer.close();
		   	  outputStream.close();
		    
	    } catch (XMLStreamException e) {
	    	throw new IOException("Failed to serialize premis.xml", e);
	    }

	}
	
	/**
	 * Writes the user's contract rights settings to an XML file
	 * 
	 * @param contractRights The contracts rights to serialize
	 * @param f The contracts rights file
	 * @throws Exception
	 */
	public void createContractRightsFile(ContractRights contractRights, File f) throws Exception {

		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		FileOutputStream outputStream = new FileOutputStream(f);		

		try {
			writer = outputFactory.createXMLStreamWriter(outputStream, "UTF-8");
		} catch (XMLStreamException e) {
			throw new IOException("Failed to create XMLStreamWriter", e);
		}

		try {	    
			writer.writeStartDocument("UTF-8", "1.0");

			createOpenElement("contractRights", 0);
				createOpenElement("institutionRights", 1);
					generatePublicationRightsElement(contractRights.getInstitutionRights());
				createCloseElement(1);
				createOpenElement("publicRights", 1);
					generatePublicationRightsElement(contractRights.getPublicRights());
				createCloseElement(1);
				createOpenElement("conversionRights", 1);
					createTextElement("condition", contractRights.getConversionCondition().toString(), 2);
				createCloseElement(1);
				createTextElement("ddbExclusion", String.valueOf(contractRights.getDdbExclusion()), 1);
			createCloseElement(0);

			writer.writeDTD("\n");
			writer.writeEndDocument();
			writer.close();
			outputStream.close();

	    } catch (XMLStreamException e) {
	    	throw new IOException("Failed to serialize contract rights", e);
		}
	
	}
	
	/**
	 * Writes the publication rights to a contract rights XML file
	 * 
	 * @param pubRights The publication rights to serialize
	 * @throws Exception
	 */
	private void generatePublicationRightsElement(PublicationRights pubRights) throws Exception {
		
		createTextElement("allowed", String.valueOf(pubRights.getAllowPublication()), 2);
		createTextElement("tempPublication", String.valueOf(pubRights.getTempPublication()), 2);
		createTextElement("lawPublication", String.valueOf(pubRights.getLawPublication()), 2);
		if (pubRights.getStartDate() != null)
			createTextElement("startDate", formatDate(pubRights.getStartDate(), dayDateFormat), 2);
		if (pubRights.getLaw() != null)
			createTextElement("law", pubRights.getLaw().toString(), 2);
		
			createOpenElement("restrictions", 2);
				createTextElement("textRestriction", String.valueOf(pubRights.getTextRestriction()), 3);
				createTextElement("imageRestriction", String.valueOf(pubRights.getImageRestriction()), 3);
				createTextElement("imageRestrictionText", String.valueOf(pubRights.getImageRestrictionText()), 3);
				createTextElement("audioRestriction", String.valueOf(pubRights.getAudioRestriction()), 3);
				createTextElement("videoRestriction", String.valueOf(pubRights.getVideoRestriction()), 3);
				createTextElement("videoDurationRestriction", String.valueOf(pubRights.getVideoDurationRestriction()), 3);
				if (pubRights.getPages() != null && !pubRights.getPages().equals(""))
					createTextElement("pages", pubRights.getPages(), 3);
				if (pubRights.getImageWidth() != null && !pubRights.getImageWidth().equals(""))
					createTextElement("imageWidth", pubRights.getImageWidth(), 3);
				if (pubRights.getImageHeight() != null && !pubRights.getImageHeight().equals(""))
					createTextElement("imageHeight", pubRights.getImageHeight(), 3);
				if (pubRights.getFooterText() != null && !pubRights.getFooterText().equals(""))
					createTextElement("footerText", pubRights.getFooterText(), 3);
				if (pubRights.getImageTextType() != null)
					createTextElement("imageTextType", pubRights.getImageTextType().toString(), 3);
				if (pubRights.getWatermarkOpacity() != null)
					createTextElement("watermarkOpacity", pubRights.getWatermarkOpacity(), 3);
				if (pubRights.getWatermarkSize() != null)
					createTextElement("watermarkSize", pubRights.getWatermarkSize(), 3);
				if (pubRights.getAudioDuration() != null && !pubRights.getAudioDuration().equals(""))
					createTextElement("audioDuration", pubRights.getAudioDuration(), 3);
				if (pubRights.getVideoSize() != null && !pubRights.getVideoSize().equals(""))
					createTextElement("videoSize", pubRights.getVideoSize(), 3);
				if (pubRights.getVideoDuration() != null && !pubRights.getVideoDuration().equals(""))
					createTextElement("videoDuration", pubRights.getVideoDuration(), 3);
			createCloseElement(2);
	}
	
	/**
	 * Creates a premis object element for the package
	 * 
	 * @param packageName The package name
	 * @throws XMLStreamException
	 */
	private void generateObjectElement(String packageName) throws XMLStreamException {
		
		createOpenElement("object", 1);
		createAttribute(XSI_NS, "type", "representation");
			createOpenElement("objectIdentifier", 2);
				createTextElement("objectIdentifierType", "PACKAGE_NAME", 3);
				createTextElement("objectIdentifierValue", packageName, 3);
			createCloseElement(2);
		createCloseElement(1);		
	}
	
	/**
	 * Creates a premis event element for the SIP creation process
	 * 
	 * @param packageName The package name
	 * @throws XMLStreamException
	 */
	private void generateEventElement(String packageName) throws XMLStreamException {
		
		Date creationDate = new Date();
		
		createOpenElement("event", 1);
			createOpenElement("eventIdentifier", 2);
				createTextElement("eventIdentifierType", "SIP_CREATION_ID", 3);	
				createTextElement("eventIdentifierValue", "Sip_Creation_" + formatDate(creationDate, dateFormat), 3);
			createCloseElement(2);
			createTextElement("eventType", "SIP_CREATION", 2);
			createTextElement("eventDateTime", formatDate(creationDate, dateFormat), 2);
			createOpenElement("linkingAgentIdentifier", 2);
				createTextElement("linkingAgentIdentifierType", "APPLICATION_NAME", 3);
				createTextElement("linkingAgentIdentifierValue", SIPBuilder.getProperties().getProperty("ARCHIVE_NAME") + " SIP-Builder " + Utilities.getSipBuilderVersion(), 3);
			createCloseElement(2);
			createOpenElement("linkingObjectIdentifier", 2);
				createTextElement("linkingObjectIdentifierType", "PACKAGE_NAME", 3);
				createTextElement("linkingObjectIdentifierValue", packageName, 3);
			createCloseElement(2);
		createCloseElement(1);
	}
	
	/**
	 * Creates an agent element for the SIP-Builder
	 * 
	 * @throws XMLStreamException
	 */
	private void generateAgentElement() throws XMLStreamException {
		
		createOpenElement("agent", 1);
			createOpenElement("agentIdentifier", 2);
				createTextElement("agentIdentifierType", "APPLICATION_NAME", 3);
				createTextElement("agentIdentifierValue", SIPBuilder.getProperties().getProperty("ARCHIVE_NAME") + " SIP-Builder " + Utilities.getSipBuilderVersion(), 3);
			createCloseElement(2);
			createTextElement("agentType", "APPLICATION", 2);
		createCloseElement(1);		
	}

	/**
	 * Creates a rights element representing the user's contract rights settings
	 * 
	 * @param contractRights The contract rights settings to serialize
	 * @throws XMLStreamException
	 */
	private void generateRightsElement(ContractRights contractRights) throws XMLStreamException {
		
		PublicationRights publicRights = contractRights.getPublicRights();
		PublicationRights institutionRights = contractRights.getInstitutionRights();
		
		createOpenElement("rights", 1);
		
			
			// Rights Statement
		
			createOpenElement("rightsStatement", 2);
				createOpenElement("rightsStatementIdentifier", 3);
					createTextElement("rightsStatementIdentifierType", "rightsid", 4);
					createTextElement("rightsStatementIdentifierValue", "", 4);
				createCloseElement(3);
				createTextElement("rightsBasis", "license", 3);
		
			// Publication Public			
			if (publicRights.getAllowPublication())
			{
				createOpenElement("rightsGranted", 3);
					createTextElement("act", "PUBLICATION_PUBLIC",  4);
					createTextElement("restriction", "see rightsExtension", 4);
					createOpenElement("termOfGrant", 4);
					if (publicRights.getTempPublication())
						createTextElement("startDate", formatDate(publicRights.getStartDate(), dayDateFormat), 5);
					else
						createTextElement("startDate", formatDate(new Date(), dayDateFormat), 5);
					createCloseElement(4);
				createCloseElement(3);					
			}
			
			// Publication Institution		
			if (institutionRights.getAllowPublication())
			{
				createOpenElement("rightsGranted", 3);
					createTextElement("act", "PUBLICATION_INSTITUTION",  4);
					createTextElement("restriction", "see rightsExtension", 4);
					createOpenElement("termOfGrant", 4);
					if (institutionRights.getTempPublication())
						createTextElement("startDate", formatDate(institutionRights.getStartDate(), dayDateFormat), 5);
					else
						createTextElement("startDate", formatDate(new Date(), dayDateFormat), 5);
					createCloseElement(4);
				createCloseElement(3);					
			}
			
			// Migration
				createOpenElement("rightsGranted", 3);
					createTextElement("act", "MIGRATION",  4);
					createTextElement("restriction", "see rightsExtension", 4);
					createOpenElement("termOfGrant", 4);
					createTextElement("startDate", formatDate(new Date(), dayDateFormat), 5);
					createCloseElement(4);
				createCloseElement(3);		
			
			createCloseElement(2);
			
			
			// Rights Extension
			
			createOpenElement("rightsExtension", 2);
				createOpenElement("rightsGranted", 3);
				createAttribute("xmlns", "http://www.danrw.de/contract/v1");
				createAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				createAttribute("xsi:schemaLocation", "http://www.danrw.de/contract/v1 http://www.danrw.de/schemas/contract/v1/danrw-contract-1.xsd");
					// Migration Right
					createOpenElement("migrationRight", 4);
						createTextElement("condition", contractRights.getConversionCondition().toString(), 5);
					createCloseElement(4);
			
					// Publication Right Public
					if (publicRights.getAllowPublication())
					{
						createOpenElement("publicationRight", 4);
							createTextElement("audience", "PUBLIC", 5);
							
						if (publicRights.getTempPublication())
							createTextElement("startDate", formatDate(publicRights.getStartDate(), dayDateFormat), 6);
						else
							createTextElement("startDate", formatDate(new Date(), dayDateFormat), 6);
							
						if (publicRights.getLawPublication())
							createTextElement("lawID", publicRights.getLaw().toString(), 6);
						
						boolean publicImageRestrictionText = false;
						if (publicRights.getImageRestrictionText() &&
								publicRights.getFooterText() != null && 
								!publicRights.getFooterText().equals(""))
							publicImageRestrictionText = true;
						
						if (publicRights.getAudioRestriction() ||
							publicRights.getImageRestriction() ||
							publicRights.getTextRestriction() ||
							publicRights.getVideoRestriction() ||
							publicRights.getVideoDurationRestriction() ||
							publicImageRestrictionText)
						{					
							createOpenElement("restrictions", 5);
							
							if (publicRights.getAudioRestriction())
							{
								createOpenElement("restrictAudio", 6);
									createTextElement("duration", String.valueOf(publicRights.getAudioDuration()), 7);
								createCloseElement(6);
							}
							
							if (publicRights.getImageRestriction() || publicImageRestrictionText)
							{
								createOpenElement("restrictImage", 6);
								
								if (publicRights.getImageRestriction()) {
									createTextElement("width", publicRights.getImageWidth(), 7);	
									createTextElement("height", publicRights.getImageHeight(), 7);
								}
									
								if (publicImageRestrictionText) {
									if (publicRights.getImageTextType() == PublicationRights.TextType.footer)
										createTextElement("footerText", publicRights.getFooterText(), 7);
									else {
										createOpenElement("watermark", 7);
											createTextElement("watermarkString", publicRights.getFooterText(), 8);
											createTextElement("pointSize", publicRights.getWatermarkSize(), 8);
											createTextElement("position", publicRights.getImageTextType().toString(), 8);
											createTextElement("opacity", publicRights.getWatermarkOpacity(), 8);
										createCloseElement(7);											
									}
								}
									
								createCloseElement(6);
							}

							if (publicRights.getTextRestriction())
							{
								createOpenElement("restrictText", 6);

								String pages = publicRights.parsePages();
								if (pages != "") {
									createTextElement("certainPages", pages, 7);
								}
								createCloseElement(6);
							}

							if (publicRights.getVideoRestriction() || publicRights.getVideoDurationRestriction())
							{
								createOpenElement("restrictVideo", 6);
								if (publicRights.getVideoRestriction())
									createTextElement("height", publicRights.getVideoSize(), 7);
								if (publicRights.getVideoDurationRestriction())
									createTextElement("duration", String.valueOf(publicRights.getVideoDuration()), 7);

								createCloseElement(6);
							}
			
							createCloseElement(5);
					}
					else
							createEmptyElement("restrictions", 5);
							
						createCloseElement(4);
					}
					
					// Publication Right Institution					
					if (institutionRights.getAllowPublication())
					{
						createOpenElement("publicationRight", 4);
							createTextElement("audience", "INSTITUTION", 5);
							
						if (institutionRights.getTempPublication())
							createTextElement("startDate", formatDate(institutionRights.getStartDate(), dayDateFormat), 6);
						else
							createTextElement("startDate", formatDate(new Date(), dayDateFormat), 6);
							
						if (institutionRights.getLawPublication())
							createTextElement("lawID", institutionRights.getLaw().toString(), 6);
						
						boolean institutionImageRestrictionText = false;
						if (institutionRights.getImageRestrictionText() &&
								institutionRights.getFooterText() != null && 
								!institutionRights.getFooterText().equals(""))
							institutionImageRestrictionText = true;
						
						if (institutionRights.getAudioRestriction() ||
							institutionRights.getImageRestriction() ||
							institutionRights.getTextRestriction() ||
							institutionRights.getVideoRestriction() ||
							institutionRights.getVideoDurationRestriction() ||
							institutionImageRestrictionText)
						{					
							createOpenElement("restrictions", 5);
							
							if (institutionRights.getAudioRestriction())
							{
								createOpenElement("restrictAudio", 6);
									createTextElement("duration", String.valueOf(institutionRights.getAudioDuration()), 7);
								createCloseElement(6);
							}
							
							if (institutionRights.getImageRestriction() || institutionImageRestrictionText)
							{
								createOpenElement("restrictImage", 6);
								
								if (institutionRights.getImageRestriction()) {
									createTextElement("width", institutionRights.getImageWidth(), 7);	
									createTextElement("height", institutionRights.getImageHeight(), 7);
								}
									
								if (institutionImageRestrictionText) {
									if (institutionRights.getImageTextType() == PublicationRights.TextType.footer)
										createTextElement("footerText", institutionRights.getFooterText(), 7);
									else {
										createOpenElement("watermark", 7);
											createTextElement("watermarkString", institutionRights.getFooterText(), 8);
											createTextElement("pointSize", institutionRights.getWatermarkSize(), 8);
											createTextElement("position", institutionRights.getImageTextType().toString(), 8);
											createTextElement("opacity", institutionRights.getWatermarkOpacity(), 8);
										createCloseElement(7);											
									}
								}
									
								createCloseElement(6);
							}

							if (institutionRights.getTextRestriction())
							{
								createOpenElement("restrictText", 6);

								String pages = institutionRights.parsePages();
								if (pages != "") {
									createTextElement("certainPages", pages, 7);
								}
								createCloseElement(6);
							}

							if (institutionRights.getVideoRestriction() || institutionRights.getVideoDurationRestriction())
							{
								createOpenElement("restrictVideo", 6);
								if (institutionRights.getVideoRestriction())
									createTextElement("height", institutionRights.getVideoSize(), 7);
								if (institutionRights.getVideoDurationRestriction())
									createTextElement("duration", String.valueOf(institutionRights.getVideoDuration()), 7);

								createCloseElement(6);
							}
			
							createCloseElement(5);
					}
					else
							createEmptyElement("restrictions", 5);
							
						createCloseElement(4);
					}
				
					// DDB exclusion option
					if (publicRights.getAllowPublication() && contractRights.getDdbExclusion())
						createEmptyElement("DDBexclusion", 4);
			
				createCloseElement(3);
			createCloseElement(2);
			
		createCloseElement(1);
	}
	
	/**
	 * Copies the contract rights settings from an existing premis file to the newly created premis file
	 * 
	 * @param rightsSourcePremisFile - The premis file from which the rights settings are taken
	 * @throws XMLStreamException
	 */
	private void copyRightsElementFromPremisFile(File rightsSourcePremisFile) throws XMLStreamException {

		FileInputStream inputStream = null;

		try {
			inputStream = new FileInputStream(rightsSourcePremisFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Couldn't find file " + rightsSourcePremisFile.getAbsolutePath(), e);
		}

		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader streamReader = inputFactory.createXMLStreamReader(inputStream);

		boolean textElement = false;
		boolean inRightsElement = false;
		int tab = 1;
		
		String name = null;
		String text = null;
		
		while(streamReader.hasNext())
		{
			int event = streamReader.next();

			switch (event)
			{

			case XMLStreamConstants.START_ELEMENT:
				if (streamReader.getLocalName().equals("rights"))
					inRightsElement = true;

				if (inRightsElement) {

					if (name != null) {
						writer.writeStartElement(name);
						if (name.equals("rightsExtension")) {
							createAttribute("xmlns", "http://www.danrw.de/contract/v1");
							createAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
							createAttribute("xsi:schemaLocation", "http://www.danrw.de/contract/v1 http://www.danrw.de/schemas/contract/v1/danrw-contract-1.xsd");
						}

						name = null;
						text = null;
					}

					writer.writeDTD("\n");
					indent(tab);
					tab++;

					name = streamReader.getLocalName();
				}	
				break;

			case XMLStreamConstants.CHARACTERS:
				if(inRightsElement && !streamReader.isWhiteSpace())
				{
					text = streamReader.getText();
					textElement = true;
				}
				break;

			case XMLStreamConstants.END_ELEMENT:
				if (inRightsElement) {
					
					if (!textElement && streamReader.getLocalName().equals(name)) {
						writer.writeEmptyElement(name);
						
						name = null;
						text = null;
						tab--;
						break;
					}
					else if (textElement) {
						writer.writeStartElement(streamReader.getLocalName());	
						writer.writeCharacters(text);
						
						name = null;
						text = null;
					}
					
					tab--;

					if (!textElement)
					{
						writer.writeDTD("\n");
						indent(tab);
					}

					writer.writeEndElement();
					textElement = false;
				}

				if (streamReader.getLocalName().equals("rights"))
					inRightsElement = false;
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
	
	private String formatDate(Date date, SimpleDateFormat format) {
		format.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
		String dateString = format.format(date);
		if (dateString.endsWith("00") && !dateString.endsWith(":00"))
			dateString = dateString.substring(0, dateString.length() - 2) + ":00";
		return dateString;
	}
	
	private void indent(int tab) throws XMLStreamException {
		
		for (int i = 0; i < tab; i++)
			writer.writeDTD("    ");
	}
	
	private void createOpenElement(String namespace, String name, int tab) throws XMLStreamException {
		
		writer.writeDTD("\n");
		indent(tab);
		
		if (namespace.equals(""))
			writer.writeStartElement(name);
		else
			writer.writeStartElement(namespace, name);	
	}
	
	private void createOpenElement(String name, int tab) throws XMLStreamException {
		
		createOpenElement("", name, tab);
	}
	
	private void createCloseElement(int tab) throws XMLStreamException {
		
		writer.writeDTD("\n");
		indent(tab);
		
		writer.writeEndElement();
	}
	
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
	
	private void createTextElement(String name, String text, int tab) throws XMLStreamException  {
		
		createTextElement("", name, text, tab);
	}
	
	private void createEmptyElement(String name, int tab) throws XMLStreamException {
		
		writer.writeDTD("\n");
		indent(tab);
		
		writer.writeEmptyElement(name);		
	}
	
	private void createAttribute(String namespace, String name, String value) throws XMLStreamException {
		
		if (namespace.equals(""))
			writer.writeAttribute(name, value);
		else
			writer.writeAttribute(namespace, name, value);		
	}
	
	private void createAttribute(String name, String value) throws XMLStreamException {
		
		createAttribute("", name, value);
	}
}
