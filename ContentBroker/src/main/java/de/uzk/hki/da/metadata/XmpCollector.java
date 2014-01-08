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
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.StringReader;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


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
	 * @param folder the folder
	 * @param targetFile the target file
	 */
	public static void collect(File folder, File targetFile) {
		
		Model model = ModelFactory.createDefaultModel();
		
		File[] files = folder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.getName().toLowerCase().endsWith(".xmp")) return true;
				return false;
			}
		});
		if (files == null) {
			throw new RuntimeException("Unable to open directory: " + folder.getAbsolutePath());
		}
		for (File file : files) {
			logger.debug("found xmp file {}", file.getAbsolutePath());
			
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
			
			final String baseName = file.getName().substring(0, file.getName().length()-3);
			String[] list = folder.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					String baseName2 = name.substring(0, name.length()-3);
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
			// read XMP with matching file as base name
			// use "http://www.danrw.de/temp/" as a pseudo base URI in order to allow relative resource URIs
			model.read(new StringReader(xmpWriter.toString()),"http://www.danrw.de/temp/" + list[0]);
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
