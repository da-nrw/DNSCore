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
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import nu.xom.Builder;
import nu.xom.NodeFactory;
import nu.xom.converters.DOMConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import de.uzk.hki.da.metadata.PremisXmlReaderNodeFactory;


/**
 * The Class XPathUtils.
 */
public class XPathUtils {

	/** The logger. */
	protected static Logger logger = LoggerFactory.getLogger(XPathUtils.class);
	
	/**
	 * Parses xml and returns the Document.
	 *
	 * @param filename the filename
	 * @return null if an error occured.
	 */
	public static Document parseDom(String filename){
		DocumentBuilderFactory domFactory = 
		DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); 
		DocumentBuilder builder=null;
		try {
			builder = domFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("ParserConfigurationException with {}",filename);
		}
		try {
			return builder.parse(filename);
		} catch (SAXException e) {
			logger.error("SAXException while parsing {}",filename);
		} catch (IOException e) {
			logger.error("IOException while parsing {}",filename);
		}
		return null;
	}
	
	/**
	 * Parses a premis.xml file and returns the Document; jhove sections are skipped
	 *
	 * @param filename the filename
	 * @return the document
	 * @throws Exception the exception
	 */
	public static Document parsePremisFile(String filename) throws Exception {
		XMLReader xmlReader = null;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			xmlReader = spf.newSAXParser().getXMLReader();
		} catch (Exception e) {
			throw new Exception("Error creating SAX parser", e);
		}
		xmlReader.setErrorHandler(new ErrorHandler(){

			@Override
			public void error(SAXParseException e) throws SAXException {
				throw new SAXException("Error while parsing premis file", e);
			}

			@Override
			public void fatalError(SAXParseException e) throws SAXException {
				throw new SAXException("Fatal error while parsing premis file", e);
			}

			@Override
			public void warning(SAXParseException e) throws SAXException {
				logger.warn("Warning while parsing premis file", e);
			}
		});
		
		NodeFactory nodeFactory = new PremisXmlReaderNodeFactory();
		Builder parser = new Builder(xmlReader, false, nodeFactory);
		
		FileReader reader = new FileReader(new File(filename));		
		nu.xom.Document doc = null;
		try {
			doc = parser.build(reader);
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			reader.close();
		}
		
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(false); 
		DocumentBuilder builder = domFactory.newDocumentBuilder();

		DOMImplementation implementation = builder.getDOMImplementation();
		
		return DOMConverter.convert(doc, implementation);
	}
	
	/**
	 * Evaluates the XPath Expression express and returns the textvalue the element contains.
	 *
	 * @param dom Document
	 * @param express xpath to the element (should not include the occurence of /text())
	 * @return the textvalue of the element
	 */
	public static String getXPathElementText(Document dom, String express) {
		
		try {
		
			XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(new NamespaceResolver());
			XPathExpression expr = xpath.compile(express);
			
			Object result = expr.evaluate(dom, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			
			if (nodes.getLength()==0) {
				logger.debug("getXPathElement - no results for "+express);
				return null;
			}
			if (nodes.getLength()>1) {
				logger.debug("getXPathElement - too many results for "+express);
				return null;
			}
			return nodes.item(0).getNodeValue();
			
		} catch (XPathException e) {
			logger.error("Error in XPath expression: " + express, e);
			return null;
		}
			
	}
	
	/**
	 * The Class NamespaceResolver.
	 */
	private static class NamespaceResolver implements NamespaceContext {
		
		/* (non-Javadoc)
		 * @see javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
		 */
		public String getNamespaceURI(String prefix) {
			if (prefix == null)
				throw new IllegalArgumentException("No prefix");
			else if (prefix.equals("premis"))
				return "info:lc/xmlns/premis-v2";
			else if (prefix.equals("contract"))
				return "http://www.danrw.de/contract/v1";
			
			return XMLConstants.NULL_NS_URI;
		}
		
		/* (non-Javadoc)
		 * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
		 */
		public String getPrefix(String namespaceURI) {
	        return null;
	    }

	    /* (non-Javadoc)
    	 * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
    	 */
    	@SuppressWarnings("rawtypes")
		public Iterator getPrefixes(String namespaceURI) {
	        return null;
	    }
	}
}
