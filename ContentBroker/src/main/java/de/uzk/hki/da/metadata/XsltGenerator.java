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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;


/**
 * Converts a metadata stream to EDM, based on 
 * 
 * 
 * The Class XsltGenerator.
 */
public class XsltGenerator {

	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(XsltGenerator.class);
	
	/** The Constant TRANSFORMER_FACTORY_CLASS. */
	private static final String TRANSFORMER_FACTORY_CLASS = "net.sf.saxon.TransformerFactoryImpl";

	/** The input stream. */
	private InputStream inputStream;
	
	/** The params. */
	private Map<String,String> params = new HashMap<String,String>();

	private Source xsltSource;

	private Transformer transformer;

	/**
	 * Instantiates a new xslt generator.
	 *
	 * @param xsltPath the xslt path to the edm mapping file
	 * @param inputStream the input stream of the source metadata file
	 * @throws FileNotFoundException 
	 * @throws TransformerConfigurationException 
	 */
	public XsltGenerator(String xsltPath, InputStream inputStream) throws FileNotFoundException, TransformerConfigurationException {
		if (!new File(xsltPath).exists())
			throw new FileNotFoundException();
		
		try {
			String theString = IOUtils.toString(inputStream, "UTF-8");
			this.inputStream = new ByteArrayInputStream(theString.getBytes());
			
			xsltSource = new StreamSource(new FileInputStream(xsltPath));

			TransformerFactory transFact = TransformerFactory.newInstance(TRANSFORMER_FACTORY_CLASS, null);
			transFact.setErrorListener(new CutomErrorListener());
			
			transformer = null;
			transformer = transFact.newTransformer(xsltSource);
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setErrorListener(new CutomErrorListener());
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * @return
	 * @throws TransformerException
	 * @throws IOException 
	 */
	public String generate() throws TransformerException, IOException {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setNamespaceAware(true);
		try {
			dbf.setFeature("http://xml.org/sax/features/namespaces", false);
			dbf.setFeature("http://xml.org/sax/features/validation", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		} catch (ParserConfigurationException e2) {
			throw new RuntimeException(e2);
		}
	    
	    DocumentBuilder parser;
	    Document doc=null;
		try {
			parser = dbf.newDocumentBuilder();
			doc = parser.parse(inputStream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Source xmlSource = new DOMSource(doc);
		
		for (String key : params.keySet()) {
			transformer.setParameter(key, params.get(key));
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		transformer.transform(xmlSource, new StreamResult(outputStream));
		
		try {
			return outputStream.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} finally {
		inputStream.close();
		outputStream.close();
		}
	}
	
	/**
	 * Sets the parameter.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public void setParameter(String name, String value) {
		params.put(name, value);
	}
	
	/**
	 * The listener interface for receiving cutomError events.
	 * The class that is interested in processing a cutomError
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addCutomErrorListener<code> method. When
	 * the cutomError event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see CutomErrorEvent
	 */
	private static class CutomErrorListener implements ErrorListener {
		
		/* (non-Javadoc)
		 * @see javax.xml.transform.ErrorListener#error(javax.xml.transform.TransformerException)
		 */
		@Override
		public void error(TransformerException e)
				throws TransformerException {
			throw e;
		}
		
		/* (non-Javadoc)
		 * @see javax.xml.transform.ErrorListener#fatalError(javax.xml.transform.TransformerException)
		 */
		@Override
		public void fatalError(TransformerException e)
				throws TransformerException {
			throw e;					
		}
		
		/* (non-Javadoc)
		 * @see javax.xml.transform.ErrorListener#warning(javax.xml.transform.TransformerException)
		 */
		@Override
		public void warning(TransformerException e)
				throws TransformerException {
			logger.warn("Uncritical exception when instantiating transformer",e);
		}
		
	}

}
