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

package de.uzk.hki.da.cb;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.metadata.XepicurWriter;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.service.IngesterFacade;
import de.uzk.hki.da.service.IngesterFacade.IngestException;
import de.uzk.hki.fedorest.Fedora;

/** 
 * TODO refactor in a way that a single FOXML-File is created
 * and ingested based on the conversion events (and package type).
 * Set datastream labels from the events' source file.
 * @author Sebastian Cuy
 */
public class SendToPresenterAction extends AbstractAction {

	static final Logger logger = LoggerFactory.getLogger(SendToPresenterAction.class);
	
	private Fedora fedora;
	private Map<String,String> viewerUrls;
	
	SendToPresenterAction(){}

	@Override
	public boolean implementation() throws IOException {
		
		String dipPathPublic = new StringBuilder(localNode.getDipAreaRootPath())
			.append("public/").append(object.getContractor().getShort_name())
			.append("/").append(object.getIdentifier()).toString();
		logger.debug("generated dipPathPublic: {}", dipPathPublic);
		String dipPathInstitution = new StringBuilder(localNode.getDipAreaRootPath())
			.append("institution/").append(object.getContractor().getShort_name())
			.append("/").append(object.getIdentifier()).toString();
		logger.debug("generated dipPathInstitution: {}", dipPathInstitution);
		
		String packageType = getPackageTypeFromDC(dipPathPublic, dipPathInstitution);
		
		// build map that contains original filenames for labeling in Fedora
		Map<String,String> labelMap = new HashMap<String,String>();
		for (Event e:object.getLatestPackage().getEvents()) {			
			if (!"CONVERT".equals(e.getType())) continue;
			DAFile targetFile = e.getTarget_file();
			if (!targetFile.getRep_name().startsWith("dip")) continue;			
			DAFile sourceFile = e.getSource_file();
			labelMap.put(targetFile.getRelative_path(), sourceFile.getRelative_path());			
		}
		
		IngesterFacade ingester = new IngesterFacade(getFedora());
		ingester.setLabelMap(labelMap);
		String urn = object.getUrn();
		int publishedFlag = 0;
		try {
			if (new File(dipPathPublic).exists()) {				
				// write xepicur file for urn resolving
				XepicurWriter.createXepicur(object.getIdentifier(), packageType, viewerUrls.get(packageType), dipPathPublic);
				String[] sets = null;
				if (!object.ddbExcluded()) {
					sets = new String[]{ "ddb" };
				}
				ingester.purgePackageIfExists(urn, object.getIdentifier(), "danrw:"); // in case of delta
				if (ingester.ingestPackage(urn, object.getIdentifier(), dipPathPublic, object.getContractor().getShort_name(), packageType, "danrw:", sets))
					publishedFlag += 1;
			}
			if (new File(dipPathInstitution).exists()) {
				// write xepicur file for urn resolving
				XepicurWriter.createXepicur(object.getIdentifier(), packageType, viewerUrls.get(packageType), dipPathInstitution);
				ingester.purgePackageIfExists(urn, object.getIdentifier(), "danrw-closed:"); // in case of delta
				if (ingester.ingestPackage(urn, object.getIdentifier(), dipPathInstitution, object.getContractor().getShort_name(), packageType, "danrw-closed:", null))
					publishedFlag += 2;
			}
			
		} catch (IngestException e) {
			throw new RuntimeException(e);
		}
		object.setPublished_flag(publishedFlag);
		logger.debug("Set published flag of object to '{}'", object.getPublished_flag());
		
		// if no public DIP is created EDM creation and ES indexing is skipped
		if (publishedFlag % 2 == 0) {
			setKILLATEXIT(true);
		}
		
		return true;
	}

	

	/**
	 * @param dipPathPublic
	 * @param dipPathInstitution
	 * @return
	 */
	private String getPackageTypeFromDC(String dipPathPublic, String dipPathInstitution) {
		String packageType = null;
		File dcFile = new File(dipPathPublic + "/DC.xml");
		if (!dcFile.exists())
			dcFile = new File(dipPathInstitution + "/DC.xml");
		if (dcFile.exists()) {
			SAXBuilder builder = new SAXBuilder();
			Document doc;
			try {
				doc = builder.build(new FileReader(dcFile));
				Element formatEl = doc.getRootElement().getChild("format",
						Namespace.getNamespace("http://purl.org/dc/elements/1.1/"));
				if (formatEl == null) {
					logger.warn("No format element found in DC, unable to determine package type!");
				} else {
					packageType = formatEl.getTextNormalize();
				}
			} catch (Exception e) {
				logger.error("Error while parsing DC, unable to determine package type.", e);
			}
		} else {
			logger.warn("No DC file found, unable to determine package type!");
		}
		return packageType;
	}

	
	
	
	@Override
	public void rollback() {
		throw new NotImplementedException("No rollback implemented for this action");
	}

	
	
	public Fedora getFedora() {
		return fedora;
	}

	public void setFedora(Fedora fedora) {
		this.fedora = fedora;
	}

	public Map<String,String> getViewerUrls() {
		return viewerUrls;
	}

	public void setViewerUrls(Map<String,String> viewerUrls) {
		this.viewerUrls = viewerUrls;
	}

}
