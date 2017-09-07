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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.model.ConversionPolicy;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;

/**
 * 
 * @author trebunski
 *
 */
public class QualityLevelCheckAction extends AbstractAction {
/*
	private static final String OUTGOING = "outgoing";
	public static int PAUSE_DELAY = 20000;
	*/
/*	public QualityLevelCheckAction(){
		SUPPRESS_OBJECT_CONSISTENCY_CHECK=true;
		setKILLATEXIT(true);
	}*/
	
	private Comparator<Event> eventComparator=new Comparator<Event>(){
		@Override public int compare(Event o1, Event o2) {
			String f1=o1.getSource_file().getRep_name() + o1.getSource_file().getRelative_path();
			String f2=o2.getSource_file().getRep_name() + o2.getSource_file().getRelative_path();
			return f1.compareTo(f2);
		}};
	
	@Override
	public void checkConfiguration() {
	}
	

	@Override
	public void checkPreconditions() {
	}
	
	@Override
	public boolean implementation() {
		logger.debug("QualityLevelCheckAction called! ");
		int qualityLevel=-1;
		List<Event> events = o.getLatestPackage().getEvents();
		List<Event> validationFailEvents = new ArrayList<Event>();
		List<Event> conversionFailEvents = new ArrayList<Event>();
		
		List<DAFile> unrecognizedPUIDFiles = new ArrayList<DAFile>();
		List<DAFile> unknownPuidFile = new ArrayList<DAFile>();
		Set<String> supportedFormatsForLZA=new HashSet<String>();
		
		for(ConversionPolicy cp:this.preservationSystem.getConversion_policies())
			if(ConversionPolicy.FormatType.LZA.equals(cp.getFormat_type()))
				supportedFormatsForLZA.add(cp.getSource_format());
		for(DAFile df:o.getLatestPackage().getFiles()){
			System.out.println(df+" "+df.getFormatPUID());
			if(df.getFormatPUID().equals(C.UNRECOGNIZED_PUID))
				unrecognizedPUIDFiles.add(df);
			else if(!supportedFormatsForLZA.contains(df.getFormatPUID()))
				unknownPuidFile.add(df);
		}
		
		for(Event ev:events){
			System.out.println(ev+" "+ev.getSource_file()+" "+ev.getType());
		}
		o.getLatestPackage().getFiles().get(0).getFormatPUID();
		for (Event e : events) {
			if (e.getType().equals(C.EVENT_TYPE_QUALITY_FAULT_CONVERSION)) {
				conversionFailEvents.add(e);
			} else if (e.getType().equals(C.EVENT_TYPE_QUALITY_FAULT_VALIDATION)) {
				validationFailEvents.add(e);
			}
		}
		
		
		Collections.sort(conversionFailEvents, eventComparator);
		Collections.sort(validationFailEvents, eventComparator);
		if(validationFailEvents.isEmpty()&&conversionFailEvents.isEmpty()){
			if(!unrecognizedPUIDFiles.isEmpty() || !unknownPuidFile.isEmpty()){
				extendObject(C.QUALITYFLAG_LEVEL_4,createEvent(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_4,o.getLatestPackage().getFiles().get(0),"NO CRITICAL QUALITY_LEVEL EVENTS, BUT HAS UNSUPPORTED FORMATS , e.g. FILE: "+unrecognizedPUIDFiles.get(0).getRelative_path()));
				qualityLevel=C.QUALITYFLAG_LEVEL_4;
			}else{ // if(unknownPuidFile.isEmpty() && unrecognizedPUIDFiles.isEmpty()){
				extendObject(C.QUALITYFLAG_LEVEL_5,createEvent(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_5,o.getLatestPackage().getFiles().get(0),"NO CRITICAL QUALITY_LEVEL EVENTS"));
				qualityLevel=C.QUALITYFLAG_LEVEL_5;
			}
		}else if(!validationFailEvents.isEmpty()&&conversionFailEvents.isEmpty()){
			extendObject(C.QUALITYFLAG_LEVEL_3,createEvent(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_3,validationFailEvents.get(0).getSource_file(),generateQualityEventDetail("ONLY "+C.EVENT_TYPE_QUALITY_FAULT_VALIDATION+" EVENTS",validationFailEvents)));
			qualityLevel=C.QUALITYFLAG_LEVEL_3;
		}else if(validationFailEvents.isEmpty()&&!conversionFailEvents.isEmpty()){
			extendObject(C.QUALITYFLAG_LEVEL_2,createEvent(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_2,conversionFailEvents.get(0).getSource_file(),generateQualityEventDetail("ONLY "+C.EVENT_TYPE_QUALITY_FAULT_CONVERSION+" EVENTS",conversionFailEvents)));
			qualityLevel=C.QUALITYFLAG_LEVEL_2;
		}else{
			List<Event> commonConversionEvents=new ArrayList<Event>();
			Event[] validationEventsArray=new Event[validationFailEvents.size()];
			validationEventsArray=validationFailEvents.toArray(validationEventsArray);
			
			for(Event e:conversionFailEvents){
				int index=Arrays.binarySearch(validationEventsArray, e, eventComparator);
					if (index>=0)
						commonConversionEvents.add(validationEventsArray[index]);
			}
			if(commonConversionEvents.isEmpty()){//there is no validation and conversion events on same files
				conversionFailEvents.addAll(validationFailEvents);
				extendObject(C.QUALITYFLAG_LEVEL_2,createEvent(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_2,conversionFailEvents.get(0).getSource_file(),generateQualityEventDetail("CONVERSION AND VALIDATION QUALITY_LEVEL EVENTS ON DIFFERENT FILES",conversionFailEvents)));
				qualityLevel=C.QUALITYFLAG_LEVEL_2;
			}else{
				extendObject(C.QUALITYFLAG_LEVEL_1,createEvent(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_1,commonConversionEvents.get(0).getSource_file(),generateQualityEventDetail("CONVERSION AND VALIDATION QUALITY_LEVEL EVENTS ON SAME FILES",commonConversionEvents)));
				qualityLevel=C.QUALITYFLAG_LEVEL_1;
			}
		}
		
		int requiredIngestQualityPremis=getRequiredIngestLevelFromPremis();
		if(requiredIngestQualityPremis>0){
			if(qualityLevel<requiredIngestQualityPremis)
				throw new UserException(UserExceptionId.QUALITY_BELOW_REQUIRED, "Current Quality("+qualityLevel+") is below "+requiredIngestQualityPremis +" ");
		}else if(qualityLevel< this.o.getContractor().getRequiredIngestQuality())
			throw new UserException(UserExceptionId.QUALITY_BELOW_REQUIRED, "Current Quality("+qualityLevel+") is below "+this.o.getContractor().getRequiredIngestQuality() +" ");
		
		return true;

	}
	
	private int getRequiredIngestLevelFromPremis(){
		Object sipPREMISObject = CreatePremisAction.parsePremisFile(
				new File(Path.make(wa.dataPath(),o.getNameOfLatestBRep(),C.PREMIS).toString().replace("+b", "+a")));
		return sipPREMISObject.getRequiredIngestQLevel();
	}
	
	private String generateQualityEventDetail(String prefix, List<Event> eList) {
		StringBuilder sb = new StringBuilder(prefix);
		if (!eList.isEmpty()) {
			sb.append(": [");
			for (int i =0;i< eList.size();i++) {
				sb.append(eList.get(i).getSource_file().getRep_name()+"/"+eList.get(i).getSource_file().getRelative_path());
				if(i< eList.size()-1)
					sb.append(", ");
			}
			sb.append("]");
		}
		String msg=sb.toString();
		if(msg.length()>Event.MAX_DETAIL_STR_LEN)
			msg=msg.substring(0,Event.MAX_DETAIL_STR_LEN);
		
		return msg;
	}
	/*
	private void delay(){
		try {
			Thread.sleep(PAUSE_DELAY); // to prevent unnecessary small intervals when checking
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}*/
	private void extendObject(int qualityFlag,Event qualityEvent) {
		//o.setObject_state(100);
		o.setModifiedAt(new Date());
		//TODO: Object set Quality Level
		o.setQuality_flag(qualityFlag);
		o.getLatestPackage().getEvents().add(qualityEvent);
	}

	@Override
	public void rollback() throws Exception {
		// Do nothing.
	}
	
	
	private Event createEvent(String qualityLevel,DAFile srcFile,String qualityMessage) {
		
		Event qualityEvent = new Event();
		
		//virusEventElement.setIdentifier(o.getIdentifier() + "+" + o.getLatestPackage().getName());
		qualityEvent.setIdentifier(o.getIdentifier());
		//qualityEvent.setIdType(qualityLevel);
		qualityEvent.setSource_file(srcFile);
		qualityEvent.setType(qualityLevel);
		qualityEvent.setAgent_name(n.getName());
		qualityEvent.setAgent_type(C.AGENT_TYPE_NODE);
		qualityEvent.setDate(new Date());
		qualityEvent.setDetail(qualityMessage);
		
		return qualityEvent;
	}
}
