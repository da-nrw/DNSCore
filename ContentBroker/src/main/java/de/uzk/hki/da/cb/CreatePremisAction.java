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
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;

import de.uzk.hki.da.format.JhoveScanService;
import de.uzk.hki.da.metadata.PremisXmlJhoveExtractor;
import de.uzk.hki.da.metadata.PremisXmlReader;
import de.uzk.hki.da.metadata.PremisXmlValidator;
import de.uzk.hki.da.metadata.PremisXmlWriter;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.PublicationRight;
import de.uzk.hki.da.utils.Path;

/**
 * 
 * @author Thomas Kleinke
 * @author Daniel M. de Oliveira
 */
public class CreatePremisAction extends AbstractAction {

	private JhoveScanService jhoveScanService;
	
	private List<Event> addedEvents = new ArrayList<Event>();

	@Override
	public boolean implementation() throws IOException	{
		logger.debug("Listing all files attached to all packages of the object:");
		 for (Package pkg : object.getPackages()) 
		   		for (DAFile fi : pkg.getFiles())
			   		logger.debug(fi.toString());
		
		Object newPREMISObject = new Object();
		newPREMISObject.setOrig_name(object.getOrig_name());
		newPREMISObject.setIdentifier(object.getIdentifier());
		newPREMISObject.setUrn(object.getUrn());
		newPREMISObject.setContractor(object.getContractor());
		
		Object sipPREMISObject = parseSipPremisFile(
				Path.makeFile(object.getDataPath(),object.getNameOfNewestARep(),"premis.xml"));
		
		if (sipPREMISObject.getPackages().size() > 0) {
			object.getLatestPackage().getEvents().addAll(sipPREMISObject.getPackages().get(0).getEvents());
			addedEvents.addAll(sipPREMISObject.getPackages().get(0).getEvents());
		}
		
		newPREMISObject.setRights(sipPREMISObject.getRights());
		newPREMISObject.getRights().setId(object.getIdentifier() + "#rights");
		newPREMISObject.getAgents().addAll(sipPREMISObject.getAgents());
		
		Event ingestEventElement = generateIngestEventElement();
		object.getLatestPackage().getEvents().add(ingestEventElement);
		addedEvents.add(ingestEventElement);
		
		newPREMISObject.getPackages().add(object.getLatestPackage());
		
		if (object.isDelta()){
		
			Object mainPREMISObject = parseOldPremisFile(
					Path.makeFile(object.getDataPath(),"premis_old.xml"));

			if (mainPREMISObject==null) throw new RuntimeException("mainPREMISObject is null");
			if (mainPREMISObject.getPackages()==null) throw new RuntimeException("mainPREMISObject.getPackages is null");
			if (mainPREMISObject.getPackages().size()==0) throw new RuntimeException("number of packages from old PREMIS expected to be not 0");
			
			// TODO refactor
			for (Package mainPREMISPackage : mainPREMISObject.getPackages()) {
				logger.debug("attaching "+mainPREMISPackage+" to temp object which waits for serialization into PREMIS");
				
				Package newPREMISPackage = new Package();
				newPREMISPackage.setId(object.getLatestPackage().getId());
				newPREMISPackage.setName(mainPREMISPackage.getName());
				newPREMISPackage.setContainerName(mainPREMISPackage.getContainerName());
				newPREMISPackage.setFiles(mainPREMISPackage.getFiles());
				newPREMISPackage.setEvents(mainPREMISPackage.getEvents());
				newPREMISObject.getPackages().add(newPREMISPackage);
				newPREMISObject.setTransientNodeRef(localNode);
				newPREMISObject.reattach();
			}
			newPREMISObject.getAgents().addAll(mainPREMISObject.getAgents());
		}
				
		checkConvertEvents(newPREMISObject);
	
		File newPREMISXml = Path.make(object.getDataPath(), 
				object.getNameOfNewestBRep(),"premis.xml").toFile();
		logger.trace("trying to write new Premis file at " + newPREMISXml.getAbsolutePath());
		new PremisXmlWriter().serialize(newPREMISObject, newPREMISXml);
		
		if (!PremisXmlValidator.validatePremisFile(newPREMISXml))
			throw new RuntimeException("PREMIS that has recently been created is not valid");
		logger.trace("Successfully created premis file");
		object.getLatestPackage().getFiles().add(new DAFile(object.getLatestPackage(),job.getRep_name()+"b","premis.xml"));
		
		for (Package p : newPREMISObject.getPackages()){
			logger.debug("pname:" + p.getName());
		}
		
		determineDisclosureLimits(newPREMISObject);
		deleteJhoveTempFiles();
		
		return true;
	}
	
	/**
	 * Saves file format information in ActionCommunicatorService for later storage in object db
	 * @author Thomas Kleinke
	 * @author Daniel M. de Oliveira
	 */
	private void determineDisclosureLimits(Object object) {
		
		Date static_nondisclosure_limit = null;
		String dynamic_nondisclosure_limit = null;
		if (object.getRights() != null && object.getRights().getPublicationRights() != null)
		{
			for (PublicationRight p : object.getRights().getPublicationRights())
			{
				if (p.getAudience().equals(PublicationRight.Audience.PUBLIC))
				{
					static_nondisclosure_limit = p.getStartDate();
					if (p.getLawID() != null)
						dynamic_nondisclosure_limit = p.getLawID().toString();
					break;
				}
			}
		}
		
		job.setDynamic_nondisclosure_limit(dynamic_nondisclosure_limit);
		job.setStatic_nondisclosure_limit(static_nondisclosure_limit);
	}
	
	private Event generateIngestEventElement() {
		
		Event ingestEventElement = new Event();
		ingestEventElement.setType("INGEST");
		ingestEventElement.setIdentifier(object.getIdentifier() + "+" + object.getLatestPackage().getName());
		ingestEventElement.setIdType(Event.IdType.INGEST_ID);
		ingestEventElement.setDate(new Date());
		ingestEventElement.setAgent_name(object.getContractor().getShort_name());
		ingestEventElement.setAgent_type("CONTRACTOR");
		return ingestEventElement;		
	}
	
	private Object parseSipPremisFile(File premisFile) {
		
		Object premisData;
		
		PremisXmlReader reader = new PremisXmlReader();
		try {
			premisData = reader.deserialize(premisFile);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't read file " + premisFile.getAbsolutePath(), e);
		} catch (ParseException pe){
			throw new RuntimeException("Error while parsing premis file", pe);
		}
		return premisData;
	}
	
	
	
	private Object parseOldPremisFile(File premisFile) {

		Object premisData;

		PremisXmlJhoveExtractor jhoveExtractor = new PremisXmlJhoveExtractor();
		try {
			jhoveExtractor.extractJhoveData(premisFile.getAbsolutePath(),
					new File(jhoveScanService.getJhoveFolder()).getAbsolutePath() +
					"/temp/" + job.getId());
		} catch (XMLStreamException e) {
			throw new RuntimeException("Couldn't extract jhove sections of file " + premisFile.getAbsolutePath(), e);
		}

		PremisXmlReader reader = new PremisXmlReader();
		reader.setJhoveTempFolder(new File(jhoveScanService.getJhoveFolder()).getAbsolutePath() + 
				"/temp/" + job.getId());
		try {
			premisData = reader.deserialize(premisFile);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't read file " + premisFile.getAbsolutePath(), e);
		} catch (ParseException pe){
			throw new RuntimeException("error while parsing premis file",pe);
		}

		return premisData;
	}

	/**
	 * @author Thomas Kleinke
	 * @param object
	 * @throws RuntimeException if rep b files without corresponding CONVERT/COPY/CREATE events exist 
	 */
	private void checkConvertEvents(Object object) {
		
		for (Package pkg : object.getPackages()) {
			for (DAFile f : pkg.getFiles()) {
				if (f.getRep_name().endsWith("b")) {
					boolean eventExists = false;
					for (Event e : pkg.getEvents()) {
						if (e.getType().equals("CONVERT") || e.getType().equals("COPY") || e.getType().equals("CREATE")
								&& e.getTarget_file() != null
								&& e.getTarget_file().getRelative_path().equals(f.getRelative_path())) {
							eventExists = true;
						}
					}
					if (!eventExists)
						throw new RuntimeException("No event found for file " + f.toRegularFile().getAbsolutePath());
				}
			}
		}		
	}
			
	private void deleteJhoveTempFiles() {
		File tempFolder = new File(jhoveScanService.getJhoveFolder() + "/temp/" + job.getId());
		if (tempFolder.exists())
		try {
			FileUtils.deleteDirectory(tempFolder);
		} catch (IOException e) {
			throw new RuntimeException("Failed to delete directory " + tempFolder);
		}
	}
		
	/**
	 * @author Thomas Kleinke
	 */
	@Override
	void rollback() throws Exception {
		
		Path.make(object.getDataPath(),object.getNameOfNewestBRep(),"premis.xml").toFile().delete();
		
		File tempFolder = new File(jhoveScanService.getJhoveFolder() + "/temp/" + job.getId() + "/premis_output/");
		if (tempFolder.exists())
			FileUtils.deleteDirectory(tempFolder);
		
		object.getLatestPackage().getEvents().removeAll(addedEvents);
		
		job.setStatic_nondisclosure_limit(null);
		job.setDynamic_nondisclosure_limit(null);
	}

	public void setJhoveScanService(JhoveScanService jhoveScanService) {
		this.jhoveScanService = jhoveScanService;
	}

}
