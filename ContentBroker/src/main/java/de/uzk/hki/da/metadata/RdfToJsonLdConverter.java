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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.github.jsonldjava.core.JSONLD;
import com.github.jsonldjava.core.JSONLDProcessingError;
import com.github.jsonldjava.impl.JenaRDFParser;
import com.github.jsonldjava.utils.JSONUtils;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


/**
 * The Class RdfToJsonLdConverter.
 */
public class RdfToJsonLdConverter {
	
	/** The frame. */
	private Map<String, Object> frame;

	/**
	 * Instantiates a new rdf to json ld converter.
	 *
	 * @param frameFilePath the frame file path
	 */
	@SuppressWarnings("unchecked")
	public RdfToJsonLdConverter(String frameFilePath) {
		try {
			InputStream inputStream = new FileInputStream(frameFilePath);
			frame = (Map<String, Object>) JSONUtils.fromInputStream(inputStream, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize RdfToJsonLdConverter", e);
		} catch (ClassCastException e) {
			throw new RuntimeException("Could not initialize RdfToJsonLdConverter: Invalid frame file", e);
		}
	}

	/**
	 * Convert.
	 *
	 * @param content the content
	 * @return the map
	 * @throws JSONLDProcessingError the jSONLD processing error
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> convert(String content) throws JSONLDProcessingError,Exception {
		Object json=null;
		final Model modelResult = ModelFactory.createDefaultModel().read(
				new ByteArrayInputStream(content.getBytes()), "", "RDF/XML");
		final JenaRDFParser parser = new JenaRDFParser();
		json = JSONLD.fromRDF(modelResult, parser);
		json = JSONLD.frame(json, frame);
			
		return (Map<String,Object>) json;
	}

}
