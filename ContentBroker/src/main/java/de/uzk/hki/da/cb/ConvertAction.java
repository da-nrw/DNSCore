/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln
  Copyright (C) 2015 LVRInfoKom
  Landschaftsverband Rheinland

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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.convert.ConverterService;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.format.KnownFormatCmdLineErrors;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Document;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.ObjectPremisXmlReader;
import de.uzk.hki.da.model.RightsStatement;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.utils.Path;



/**
 * 
 * Performs format conversions based on the 
 * ConversionInstructions generated in a previous action. 
 * 
 * @author Daniel M. de Oliveira
 * @author Thomas Kleinke
 * @author Polina Gubaidullina
 * @author jens Peters
 * 
 */
public class ConvertAction extends AbstractAction {
	
	private static final String PREMIS = "premis.xml";
	private DistributedConversionAdapter distributedConversionAdapter;
	private List<Event> events;
	private KnownFormatCmdLineErrors knownFormatCmdLineErrors;
	
	public ConvertAction(){}
	
	
	@Override
	public void checkConfiguration() {
		if (distributedConversionAdapter==null) throw new ConfigurationException("distributedConversionAdapter");
	}
	

	@Override
	public void checkPreconditions() {
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean implementation() throws IOException {
		
		if (j.getConversion_instructions().size()==0) {
			logger.warn("No Conversion Instruction has be found for job.");
			return true;
		}
		
		// The publication related ConversionStrategies rely on the information from the contract.
		o.setRights(getObjectRights()); 
		try{
		events = 
			new ConverterService(o.getLatestPackage().isPruneExceptions(),knownFormatCmdLineErrors).convertBatch(wa,o,new ArrayList(j.getConversion_instructions()));
		}catch(UserException e){
			j.getConversion_instructions().clear();
			throw e;			
		}catch(RuntimeException e){
			logger.debug("RuntimeException: "+e.getStackTrace()[0]+"\n"+e);
		}
		listFiles(o);
		extendObject(o,events);
		j.getConversion_instructions().clear();
		/*if (o.getQualityLevelRecheck_flag()) {
			emptyLatestARepresentation(wa.objectPath(), j.getRep_name());
			emptyLatestARepresentationInDb(o, j.getRep_name());
			//o.setQualityLevelRecheck_flag(false);
		}*/
		return true;
	}


	@Override
	public void rollback() throws IOException {
		
		if (events == null || events.isEmpty()) return; 
		
		for (Event e : events) {
			wa.toFile(e.getTarget_file()).delete();
		}
		
		revertObject(o, events);
	}

	


	public DistributedConversionAdapter getDistributedConversionAdapter() {
		return distributedConversionAdapter;
	}

	public void setDistributedConversionAdapter(
			DistributedConversionAdapter distributedConversionAdapter) {
		this.distributedConversionAdapter = distributedConversionAdapter;
	}

	/**
	 * Extracts the information from the events generated during the conversion batch
	 * and translates it into a proper object model structure. 
	 */
	private void extendObject(Object o, List<Event> events) {
		for (Event e : events) {
			o.getLatestPackage().getFiles().add(e.getTarget_file());
			if (e.getTarget_file()==null) {
				logger.debug("target file is null");
				continue;
			}
			addDAFileToDocument(o,e.getTarget_file());

			o.getLatestPackage().getEvents().add(e);
		}
	}
	
	/**
	 * Reverts the effects of extendObject.
	 */
	private void revertObject(Object o,List<Event> events) {

		for (Event e : events) {

			o.getLatestPackage().getFiles().remove(e.getTarget_file());
			if (e.getTarget_file()==null) {
				logger.debug("target file is null");
				continue;
			}
			removeDAFileFromDocument(o,e.getTarget_file());
			
			o.getLatestPackage().getEvents().remove(e);
		}
	}
	
	
	
	
	private void removeDAFileFromDocument(Object o,DAFile file) {
		Document doc = o.getDocument(FilenameUtils.removeExtension(file.getRelative_path()));
		if(doc==null) {
			throw new IllegalStateException("Cannot add new dafile to document "+FilenameUtils.removeExtension(file.getRelative_path())+".");
		}else {
			logger.debug("Remove from document "+file);
			boolean removed=doc.removeDAFile(file);
			if (!removed) logger.warn("Remove from document not succesful for dafile "+file);
		}
	}


	private void addDAFileToDocument(Object o,DAFile file) {
		Document doc = o.getDocument(FilenameUtils.removeExtension(file.getRelative_path()));
		if(doc==null) {
			throw new IllegalStateException("Cannot add new dafile to document "+FilenameUtils.removeExtension(file.getRelative_path())+"."); 
		}else {
			doc.addDAFile(file);
		}
	}


	private Object parsePremisToMetadata(String pathToPremis) throws IOException {
		Object o = null;
		try {
			o = new ObjectPremisXmlReader()
				.deserialize(new File(pathToPremis));
		} catch (ParseException e) {
			throw new RuntimeException("error while parsing premis file",e);
		}
		return o;
	}


	/**
	 * @throws IOException
	 */
	private RightsStatement getObjectRights() throws IOException {
		Object premisObject = parsePremisToMetadata(wa.toFile(o.getLatest(PREMIS)).getAbsolutePath());
		return premisObject.getRights();
	}


	private void listFiles(Object o) {
		logger.debug("listing file instances attached to latest package");
		for (DAFile f:o.getLatestPackage().getFiles()){
			logger.debug(f.toString());
		}
	}
	
	
	/**
	 * Used for unit testing only. 
	 * @param events
	 */
	void setEvents(List<Event> events) {
		this.events=events;
	}


	public KnownFormatCmdLineErrors getKnownFormatCmdLineErrors() {
		return knownFormatCmdLineErrors;
	}


	public void setKnownFormatCmdLineErrors(KnownFormatCmdLineErrors knownFormatCmdLineErrors) {
		this.knownFormatCmdLineErrors = knownFormatCmdLineErrors;
	}
	
	static void emptyLatestARepresentationInDb(de.uzk.hki.da.model.Object o, String repName) {
	    if (o == null) return;

	    final String rep = repName + "a";

	    de.uzk.hki.da.model.Package pkg = o.getLatestPackage();
	    if (pkg == null || pkg.getFiles() == null) return;

	    // 1) IDs sammeln, die gelöscht werden sollen (nur rep+a, nicht .xml)
	    java.util.Set<Integer> removedIds = new java.util.HashSet<>();
	    for (DAFile f : pkg.getFiles()) {
	        if (f == null) continue;
	        if (!rep.equals(f.getRep_name())) continue;
	        String rel = f.getRelative_path();
	        if (rel == null) continue;
	        if (!rel.toLowerCase().endsWith(".xml")) {
	            removedIds.add(f.getId());
	        }
	    }
	    if (removedIds.isEmpty()) return;

	    // 2) Events bereinigen, die auf zu löschende DAFile(s) zeigen
	    //    (sonst bleibt Referenz auf deleted object im Graph)
	    if (pkg.getEvents() != null) {
	        pkg.getEvents().removeIf(ev -> {
	            if (ev == null) return false;
	            DAFile src = ev.getSource_file();
	            DAFile tgt = ev.getTarget_file();
	            return (src != null && removedIds.contains(src.getId()))
	                || (tgt != null && removedIds.contains(tgt.getId()));
	        });
	    }

	    // 3) Jetzt die DAFile(s) aus dem Package entfernen (orphanRemoval => delete)
	    pkg.getFiles().removeIf(f -> f != null && removedIds.contains(f.getId()));

	    
	}
	static void emptyLatestARepresentation(Path objectPath, String repName) {
	    File aReprDataPath = Path.make(objectPath, WorkArea.DATA, repName + "a").toFile();
	    java.nio.file.Path dir = aReprDataPath.toPath();

	    DirectoryStream.Filter<java.nio.file.Path> filter = p ->
	            Files.isRegularFile(p) &&
	            !p.getFileName().toString().endsWith(".xml");

	    try (DirectoryStream<java.nio.file.Path> stream = Files.newDirectoryStream(dir, filter)) {
	        for (java.nio.file.Path file : stream) {
	            Files.delete(file);
	        }
	    } catch (IOException e) {
	        throw new RuntimeException("Couldn't empty newest A-Repr folder " + repName + "a", e);
	    }
	}
	
}
