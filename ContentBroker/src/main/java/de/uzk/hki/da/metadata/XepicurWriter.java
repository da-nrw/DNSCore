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
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;

/**
 * Generates a minimal xepicur XML file for URN publishing
 * @author Sebastian Cuy
 */
public class XepicurWriter {
	
	private static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
	private static final String XEPICUR_SCHEMA_LOCATION = "urn:nbn:de:1111-2004033116 http://www.persistent-identifier.de/xepicur/version1.0/xepicur.xsd";

	/**
	 * Creates a file named "epicur.xml" containing the urn and the
	 * corresponing url of the object in context of a viewer.
	 * @param objectId The internal id of the object
	 * @param packageType The metadata type this package is base on
	 * @param viewerUrl The prefix for the viewer to be used
	 * @param path The folder the XML will be created in
	 */
	public static void createXepicur(String objectId, String packageType, String viewerUrl, String path,String urn_prefix,String file_url) {
		
		Namespace xsi = Namespace.getNamespace("xsi", XSI_NAMESPACE);
		Namespace epicur = Namespace.getNamespace("urn:nbn:de:1111-2004033116");
		
		Element root = new Element("epicur", epicur);
		root.addNamespaceDeclaration(xsi);
		root.setAttribute("schemaLocation", XEPICUR_SCHEMA_LOCATION, xsi);
		
		Element admin = new Element("administrative_data", epicur);
		Element delivery = new Element("delivery", epicur);
		Element update = new Element("update_status", epicur);
		update.setAttribute("type", "urn_new");
		delivery.addContent(update);
		admin.addContent(delivery);
		root.addContent(admin);
		
		Element record = new Element("record", epicur);
		Element identifier = new Element("identifier", epicur);
		identifier.setAttribute("scheme", "urn:nbn:de");
		identifier.addContent(urn_prefix + "-" + objectId);
		record.addContent(identifier);
		Element resource = new Element("resource", epicur);
		identifier = new Element("identifier", epicur);
		identifier.setAttribute("scheme", "url");
		identifier.setAttribute("role", "primary");
		@SuppressWarnings("deprecation")
		String fileUrl = URLEncoder.encode(file_url + "/" + objectId + "/" + packageType);
		identifier.addContent(viewerUrl + fileUrl);
		resource.addContent(identifier);
		Element format = new Element("format", epicur);
		format.setAttribute("scheme", "imt");
		format.addContent("text/html");
		resource.addContent(format);
		record.addContent(resource);
		root.addContent(record);
		
		Document doc = new Document(root);
		XMLOutputter out = new XMLOutputter();
		try {
			FileWriter fw = new FileWriter(new File(path + "/epicur.xml"));
			out.output(doc, fw);
		} catch (IOException e) {
			throw new RuntimeException("Unable to write epicur XML!", e);
		}
		
	}
}
