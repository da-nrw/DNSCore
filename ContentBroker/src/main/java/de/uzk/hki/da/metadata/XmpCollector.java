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
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FilenameUtils;
import org.apache.xerces.parsers.SAXParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.uzk.hki.da.model.DAFile;


/**
 * The Class XmpCollector.
 */
public class XmpCollector {

	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(XmpCollector.class);
	
	/**
	 * Collect.
	 *
	 * @param xmpFiles list of xmp files to collect
	 * @param targetFile the target file
	 */
	public static void collect(List<DAFile> xmpFiles, File targetFile) {
		
		Model model = ModelFactory.createDefaultModel();
				
		for (DAFile dafile : xmpFiles) {
			
			File file = dafile.toRegularFile();
			
			logger.debug("collecting XMP file {}", file.getAbsolutePath());
			
			StringWriter xmpWriter = new StringWriter();
			
			try {
				// preprocess xmp in order to make it RDF/XML compatible	
				BufferedReader reader = new BufferedReader(new FileReader(file));
				BufferedWriter writer = new BufferedWriter(xmpWriter);

				String currentLine;
				while((currentLine = reader.readLine()) != null) {
				    String trimmedLine = currentLine.trim();
				    if(trimmedLine.startsWith("<?xpacket")) continue;
				    if(trimmedLine.contains("x:xmpmeta")) continue;
				    writer.write(currentLine);
				}
				reader.close();
				writer.close();
				
			} catch (Exception e) {
				throw new RuntimeException("Unable to preprocess XMP file: " + file.getAbsolutePath(), e);
			}
			
			final String baseName = FilenameUtils.removeExtension(file.getName());
			String[] list = file.getParentFile().list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					String baseName2 = FilenameUtils.removeExtension(name);
					if (baseName.equals(baseName2) && !name.toLowerCase().endsWith(".xmp")) return true;
					else return false;
				}
			});
			if (list.length > 1) {
				logger.warn("More than one matching file for sidecar file {}. Skipping ...", file.getName());
				continue;
			} else if (list.length < 1) {
				logger.warn("No matching file for sidecar file {}. Skipping ...", file.getName());
				continue;
			}
			logger.debug("found matching file {}", list[0]);
			
			
			logger.debug("read XMP with matching file as base name");
			
//			StringReader test = new StringReader(xmpWriter.toString().trim());
//			logger.debug("new Stringreader");
//			logger.debug("File name: "+list[0]);
//			logger.debug(xmpWriter.toString().trim().replaceFirst("^([\\W]+)<","<"));
//			InputStream in = new ByteArrayInputStream(xmpWriter.toString().trim().replaceFirst("^([\\W]+)<","<").getBytes(Charsets.UTF_8));
//			logger.debug("Create inputStream");
//			SAXParser sp = new SAXParser();
//			try {
//				sp.parse(xmpWriter.toString().trim().replaceFirst("^([\\W]+)<","<"));
//			} catch (SAXException e) {
//				logger.debug("huhu");
//				e.printStackTrace();
//			} catch (IOException e) {
//				logger.debug("hehe");
//				e.printStackTrace();
//			}
//			model.read(in,"http://www.danrw.de/temp/" + list[0]);

//			// use "http://www.danrw.de/temp/" as a pseudo base URI in order to allow relative resource URIs
			model.read(new StringReader(xmpWriter.toString().trim().replaceFirst("^([\\W]+)<","<")),"http://www.danrw.de/temp/" + list[0]);
//			logger.debug("Set preudo URI http://www.danrw.de/temp/ in RDF file");
		}
		
		try {
			FileOutputStream targetStream = new FileOutputStream(targetFile);
			model.write(targetStream, "RDF/XML-ABBREV", "http://www.danrw.de/temp/");
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not write XMP collection file: " 
					+ targetFile.getAbsolutePath());
		}
		
	}

}
