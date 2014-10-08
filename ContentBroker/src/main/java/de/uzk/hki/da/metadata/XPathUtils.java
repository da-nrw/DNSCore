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

import java.io.IOException;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.uzk.hki.da.core.C;


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
		@Override
		public String getNamespaceURI(String prefix) {
			if (prefix == null)
				throw new IllegalArgumentException("No prefix");
			else if (prefix.equals("premis"))
				return "info:lc/xmlns/premis-v2";
			else if (prefix.equals("contract"))
				return C.CONTRACT_V1_URL;
			
			return XMLConstants.NULL_NS_URI;
		}
		
		/* (non-Javadoc)
		 * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
		 */
		@Override
		public String getPrefix(String namespaceURI) {
	        return null;
	    }

	    /* (non-Javadoc)
    	 * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
    	 */
    	@Override
		@SuppressWarnings("rawtypes")
		public Iterator getPrefixes(String namespaceURI) {
	        return null;
	    }
	}
}
