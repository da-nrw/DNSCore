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

package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


/**
 * Accepts a premis.xml file and creates a new xml file for each jhove section  
 * 
 * @author Thomas Kleinke
 */
public class PremisXmlJhoveExtractor {
	
	/** The Constant XSI_NS. */
	private static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
	
	/** The writer. */
	private XMLStreamWriter writer;
	
	/** The output stream. */
	private FileOutputStream outputStream;

	/**
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
