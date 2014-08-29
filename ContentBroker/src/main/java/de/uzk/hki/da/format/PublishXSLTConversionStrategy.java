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

package de.uzk.hki.da.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.SimplifiedCommandLineConnector;


/**
 * The Class PublishXSLTConversionStrategy.
 *
 * @author Sebastian Cuy
 * @author Daniel M. de Oliveira
 */
public class PublishXSLTConversionStrategy implements ConversionStrategy {

	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(PublishXSLTConversionStrategy.class);
	
	/** The transformer. */
	private Transformer transformer;

	/** The object. */
	private Object object = null;

	/** The stylesheet. */
	private String stylesheet;

	/**
	 * Convert file.
	 *
	 * @param ci the ci
	 * @return the list
	 * @throws FileNotFoundException the file not found exception
	 * @throws IllegalStateException the illegal state exception
	 * @author Daniel M. de Oliveira
	 * @author Sebastian Cuy
	 */
	@Override
	public List<Event> convertFile(ConversionInstruction ci) 
			throws FileNotFoundException,
			IllegalStateException{
		if (object==null) throw new IllegalStateException("Object not set");
		if (object.getIdentifier()==null||object.getIdentifier().equals("")) throw new IllegalStateException("object.getIdentifier() - returns no valid value");
		
		String objectId = object.getIdentifier().substring(object.getIdentifier().indexOf("-")+1);
		logger.debug("objectId: " + objectId);
		
		Source xmlSource = createXMLSource(ci.getSource_file().toRegularFile());
		
		String targetFileName = FilenameUtils.removeExtension( 
				FilenameUtils.getName(ci.getSource_file().toRegularFile().getAbsolutePath()));
		if (!ci.getConversion_routine().getName().endsWith("_paths-for-presenter")) 
			targetFileName += "_" + ci.getConversion_routine().getName();
		
		List<Event> results = new ArrayList<Event>();
		DAFile publFile = new DAFile(object.getLatestPackage(),"dip/public",ci.getTarget_folder() + "/" + targetFileName + ".xml");
		DAFile instFile = new DAFile(object.getLatestPackage(),"dip/institution",ci.getTarget_folder() + "/" + targetFileName + ".xml");

		new File(object.getDataPath()+"/dip/public/"+ci.getTarget_folder()).mkdirs();
		new File(object.getDataPath()+"/dip/institution/"+ci.getTarget_folder()).mkdirs();

		logger.debug("Will transform {} to {}", ci.getSource_file(), publFile);
		logger.debug("Will transform {} to {}", ci.getSource_file(), instFile);
		try {
			if (objectId != null) transformer.setParameter("object-id", objectId);
			
			transformer.transform(xmlSource, new StreamResult(publFile.toRegularFile().getAbsolutePath()));
			transformer.transform(xmlSource, new StreamResult(instFile.toRegularFile().getAbsolutePath()));
			
		} catch (TransformerException e) {
			throw new RuntimeException("Error while transforming xml.", e);
		}
		
		
		Event e1 = new Event();
		e1.setType("CONVERT");
		e1.setDetail(stylesheet);
		e1.setSource_file(ci.getSource_file());
		e1.setTarget_file(publFile);
		e1.setDate(new Date());
		
		Event e2 = new Event();
		e2.setType("CONVERT");
		e2.setDetail(stylesheet);
		e2.setSource_file(ci.getSource_file());
		e2.setTarget_file(instFile);
		e2.setDate(new Date());
		
		results.add(e1);
		results.add(e2);
		return results;
	}

	
	/**
	 * Sets the stylesheet.
	 *
	 * @param stylesheet the new stylesheet
	 * @author Sebastian Cuy
	 */
	public void setStylesheet(String stylesheet) {
		this.stylesheet=stylesheet;
		try {
			InputStream xsltInputStream = new FileInputStream(stylesheet);
			Source xsltSource = new StreamSource(xsltInputStream);
			TransformerFactory transFact = TransformerFactory.newInstance();
			transFact.setErrorListener(new CutomErrorListener());
			transformer = transFact.newTransformer(xsltSource);
			transformer.setErrorListener(new CutomErrorListener());
			xsltInputStream.close();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Creates the xml source.
	 *
	 * @param file the file
	 * @return the source
	 */
	private Source createXMLSource(File file){
		// disable validation in order to prevent url resolution of DTDs etc.
		SAXParserFactory spf = SAXParserFactory.newInstance();
	    spf.setNamespaceAware(true);
	    spf.setValidating(false);
	    try {
	    	spf.setFeature("http://xml.org/sax/features/validation", false);
	    	spf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
	    	spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
	    	spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
	    	spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
	    } catch (Exception e) {
	    	logger.warn(e.getMessage());
	    }
	    XMLReader reader;
	    try {
	    	reader = spf.newSAXParser().getXMLReader();
		} catch (Exception e) {
			throw new IllegalStateException("Unable to create SAXParser", e);
		}
		
		return new SAXSource(reader, new InputSource(file.getAbsolutePath()));
	}	
	
	
	
	
	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setParam(java.lang.String)
	 */
	@Override
	public void setParam(String param) {
		logger.debug("setting param {}", param);
		setStylesheet(param);
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
	 * @author Sebastian Cuy
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


	
	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setObject(de.uzk.hki.da.model.Object)
	 */
	@Override
	public void setObject(Object object){
		this.object = object;
	}

	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setCLIConnector(de.uzk.hki.da.convert.CLIConnector)
	 */
	@Override
	public void setCLIConnector(SimplifiedCommandLineConnector cliConnector) {}

}
