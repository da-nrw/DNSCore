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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.globus.ftp.exception.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Converts a rights section which is embedded into a METS file to the native data model.
 * @author Sebastian Cuy
 * @author Daniel M. de Oliveira
 *
 */
public class RightsSectionMetsXmlReader {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(RightsSectionMetsXmlReader.class);
	
	/** The xslt input stream. */
	private InputStream xsltInputStream;
	
	/**
	 * Instantiates a new mets rights section xml reader.
	 */
	public RightsSectionMetsXmlReader() {
		xsltInputStream = this.getClass().getClass().getResourceAsStream("/darights_read.xsl");
	}
	
	/**
	 * Deserialize.
	 *
	 * @param uri the uri
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<RightsStatement> deserialize(String uri) throws IOException {
		throw new NotImplementedException();
	}

	/**
	 * Deserialize.
	 *
	 * @param file the file
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<RightsStatement> deserialize(File file) throws IOException {
		return deserialize(new FileReader(file));
	}

	/**
	 * Deserialize.
	 *
	 * @param source the source
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @author Sebastian Cuy
	 * From the old XsltXmlMetadataReader
	 */
	public List<RightsStatement> deserialize(Reader source) throws IOException {
		
		Source xmlSource = new StreamSource(source);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String tmpResult;
		try {
			transform(xmlSource,new StreamResult(baos));
			tmpResult = baos.toString("UTF-8");
			logger.debug("tmpResult: " + tmpResult);
		} catch (TransformerException e) {
			throw new IOException(e);
		}
		List<RightsStatement> result = readRawMetadata(new StringReader(tmpResult));
		logger.debug("Result: " + result);
		baos.close();
		return result;
	}

	
	/**
	 * From the old XsltXmlMetadataReader.
	 *
	 * @param xmlSource the xml source
	 * @param result the result
	 * @throws TransformerException the transformer exception
	 * @author Sebastian Cuy
	 */
	private void transform(Source xmlSource, StreamResult result) throws TransformerException {
		
		Source xsltSource = new StreamSource(xsltInputStream);
		TransformerFactory transFact = TransformerFactory.newInstance();
		Transformer trans = transFact.newTransformer(xsltSource);
		trans.transform(xmlSource, result);

	}
	
	/**
	 * From the original SimpleXmlMetadataReader.
	 *
	 * @param reader the reader
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @author Sebastian Cuy
	 */
	private List<RightsStatement> readRawMetadata(Reader reader) throws IOException{
		try {
			JAXBContext context = JAXBContext.newInstance(
					RightsContainer.class,
					RightsStatement.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return ((RightsContainer) unmarshaller.unmarshal(reader)).getRights();
		} catch (JAXBException e) {
			throw new IOException(e);
		}
	}
		
}
