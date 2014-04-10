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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.input.BOMInputStream;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.utils.XMLUtils;


/**
 * Provides several functions that deal with metadata
 * changes that are necessary after file conversions.
 * @author Sebastian Cuy
 */
public class UpdateMetadataService {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(UpdateMetadataService.class);
	
	/** The namespaces. */
	private Map<String,String> namespaces;
	
	/** The xpaths to urls. */
	private Map<String,String> xpathsToUrls = new HashMap<String,String>();
	
	
	
	/**

	 * Update paths in a packages metadata.
	 *
	 * @param pkg the current package
	 * @param packageType the metadata type of the package
	 * @param metadataFilePath the metadata file path
	 * @param repName the representation affected
	 * @param absUrlPrefix a prefix for generating absolute URLs, can be null
	 */
	public void updatePathsInMetadata(
			Package pkg,
			String packageType,
			String metadataFilePath,
			String repName,
			String absUrlPrefix) {
		
		if (absUrlPrefix == null) absUrlPrefix = "";
		
		// collect paths to be replaced in map
		Map<String,String> replacements = new HashMap<String,String>();
		for (Event e:pkg.getEvents()) {
			
			if (!"CONVERT".equals(e.getType())) continue;

			DAFile targetFile = e.getTarget_file();
			if (!targetFile.getRep_name().equals(repName)) continue;			
			DAFile sourceFile = e.getSource_file();
			replacements.put(sourceFile.getRelative_path(), absUrlPrefix + targetFile.getRelative_path());
			
		}
		
		logger.debug("Replacements: {}", replacements);
		
		// replace paths in elements denoted by xpath
		String xPathPath = xpathsToUrls.get(packageType);
		logger.debug("xPathPath: "+xPathPath);
		updatePathsInFile(pkg, repName, metadataFilePath, xPathPath, replacements);			
		
		// special treatment for EAD packages with METS files
		if ("EAD".equals(packageType)) {
			
			try {
				
				Map<String,String> metsReplacements = new HashMap<String,String>();
			
				SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
				File metadataFile = new File(pkg.getTransientBackRefToObject().getDataPath() + repName + "/" + metadataFilePath);

				FileInputStream fileInputStream = new FileInputStream(metadataFile);
				BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
				
				Document doc = builder.build(bomInputStream);
				
				XPath xPath = XPath.newInstance(xPathPath);
				for (String prefix : namespaces.keySet()) {
					xPath.addNamespace(prefix, namespaces.get(prefix));
				}
				@SuppressWarnings("rawtypes")
				List nodes = xPath.selectNodes(doc);
				if (nodes.size() == 0) {
					logger.warn("XPath expression did not match any Element. No paths will be updated!");
				}
				
				for (Object node : nodes) {
					Attribute attr = (Attribute) node;
					String value = attr.getValue();
					if (value.endsWith(".xml")) {
						updatePathsInFile(pkg, repName, value, xpathsToUrls.get("METS"), replacements);
						metsReplacements.put(value, absUrlPrefix + value);
					}
				}
				updatePathsInFile(pkg, repName, metadataFilePath, xPathPath, metsReplacements);	
			
			} catch(Exception err) {
				throw new UserException(UserExceptionId.REPLACE_URLS_IN_METADATA_ERROR,
						"Could not replace file URLs in XML metadata.", metadataFilePath, err);
			}
			
		}
		
	}
	
	/**
	 * Update paths in file.
	 *
	 * @param pkg the pkg
	 * @param repName the rep name
	 * @param metadataFilePath the metadata file path
	 * @param xPathPath the x path path
	 * @param replacements the replacements
	 */
	private void updatePathsInFile(
			Package pkg,
			String repName,
			String metadataFilePath,
			String xPathPath,
			Map<String,String> replacements
	) {
		try {

			SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
			File metadataFile = new File(pkg.getTransientBackRefToObject().getDataPath() + repName + "/" + metadataFilePath);
			
			FileInputStream fileInputStream = new FileInputStream(metadataFile);
			BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);

			Document doc = builder.build(bomInputStream);
			
			XPath xPath = XPath.newInstance(xPathPath);
			for (String prefix : namespaces.keySet()) {
				xPath.addNamespace(prefix, namespaces.get(prefix));
			}
			@SuppressWarnings("rawtypes")
			List nodes = xPath.selectNodes(doc);
			if (nodes.size() == 0) {
				logger.warn("XPath expression did not match any Element. No paths will be updated!");
			}
			for (Object node : nodes) {
				logger.debug("Found node: {}", node);
				if (node instanceof Attribute) {
					Attribute attr = (Attribute) node;
					String value = attr.getValue();
					if (replacements.containsKey(value)) {
						attr.setValue(replacements.get(value));
					}
				} else if (node instanceof Element) {
					Element elem = (Element) node;
					String value = elem.getText();
					if (replacements.containsKey(value)) {
						elem.setText(replacements.get(value));
					}
				}
			}
			
			XMLOutputter outputter = new XMLOutputter();
			outputter.setFormat(Format.getPrettyFormat());
			outputter.output(doc, new FileWriter(metadataFile));
			
		} catch (Exception err) {
			throw new UserException(UserExceptionId.REPLACE_URLS_IN_METADATA_ERROR,
					"Could not replace file URLs in XML metadata.", metadataFilePath, err);
		}
		
	}

	/**
	 * Gets the xpaths to urls.
	 *
	 * @return the xpaths to urls
	 */
	public Map<String,String> getXpathsToUrls() {
		return xpathsToUrls;
	}

	/**
	 * Sets the xpaths to urls.
	 *
	 * @param xpathsToUrls the xpaths to urls
	 */
	public void setXpathsToUrls(Map<String,String> xpathsToUrls) {
		this.xpathsToUrls = xpathsToUrls;
	}

	/**
	 * Gets the namespaces.
	 *
	 * @return the namespaces
	 */
	public Map<String,String> getNamespaces() {
		return namespaces;
	}

	/**
	 * Sets the namespaces.
	 *
	 * @param namespaces the namespaces
	 */
	public void setNamespaces(Map<String,String> namespaces) {
		this.namespaces = namespaces;
	}

}
