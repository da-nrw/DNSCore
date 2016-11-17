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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.utils.C;

/**
 * 
 * @author Jens Peters
 * Deletes objects already retrieved by Contentbroker and read by user (via daweb) 
 * on the given outgoing path
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
		List<Event> events = o.getLatestPackage().getEvents();
		List<Event> validationEvents = new ArrayList<Event>();
		List<Event> conversionEvents = new ArrayList<Event>();
		for (Event e : events) {
			if (e.getType().equals(C.EVENT_TYPE_QUALITY_FAULT_CONVERSION)) {
				conversionEvents.add(e);
			} else if (e.getType().equals(C.EVENT_TYPE_QUALITY_FAULT_VALIDATION)) {
				validationEvents.add(e);
			}
		}
		
		
		Collections.sort(conversionEvents, eventComparator);
		Collections.sort(validationEvents, eventComparator);
		if(validationEvents.isEmpty()&&conversionEvents.isEmpty()){
			extendObject(C.QUALITYFLAG_LEVEL_4,createEvent(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_4,"NO CRITICAL QUALITY_LEVEL EVENTS"));
			
		}else if(!validationEvents.isEmpty()&&conversionEvents.isEmpty()){
			extendObject(C.QUALITYFLAG_LEVEL_3,createEvent(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_3,generateQualityEventDetail("ONLY VALIDATION QUALITY_LEVEL EVENTS",validationEvents)));
		}else if(validationEvents.isEmpty()&&!conversionEvents.isEmpty()){
			extendObject(C.QUALITYFLAG_LEVEL_2,createEvent(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_2,generateQualityEventDetail("ONLY CONVERSION QUALITY_LEVEL EVENTS",conversionEvents)));
		}else{
			List<Event> commonConversionEvents=new ArrayList<Event>();
			Event[] validationEventsArray=new Event[validationEvents.size()];
			validationEventsArray=validationEvents.toArray(validationEventsArray);
			
			for(Event e:conversionEvents){
				int index=Arrays.binarySearch(validationEventsArray, e, eventComparator);
					if (index>=0)
						commonConversionEvents.add(validationEventsArray[index]);
			}
			if(commonConversionEvents.isEmpty()){//there is no validation and conversion events on same files
				conversionEvents.addAll(validationEvents);
				extendObject(C.QUALITYFLAG_LEVEL_2,createEvent(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_2,generateQualityEventDetail("CONVERSION AND VALIDATION QUALITY_LEVEL EVENTS ON DIFFERENT FILES",conversionEvents)));
			}else{
				extendObject(C.QUALITYFLAG_LEVEL_1,createEvent(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_1,generateQualityEventDetail("CONVERSION AND VALIDATION QUALITY_LEVEL EVENTS ON SAME FILES",commonConversionEvents)));
			}
		}
		
		
		return true;

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
		if(msg.length()>1000)
			msg=msg.substring(0,1000);
		
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
		o.setDate_modified(String.valueOf(new Date().getTime()));
		//TODO: Object set Quality Level
		o.setQuality_flag(qualityFlag);
		o.getLatestPackage().getEvents().add(qualityEvent);
	}

	@Override
	public void rollback() throws Exception {
		// Do nothing.
	}
	
	
	private Event createEvent(String qualityLevel,String qualityMessage) {
		
		Event qualityEvent = new Event();
		
		//virusEventElement.setIdentifier(o.getIdentifier() + "+" + o.getLatestPackage().getName());
		qualityEvent.setIdentifier(o.getIdentifier());
		//qualityEvent.setIdType(qualityLevel);
		qualityEvent.setType(qualityLevel);
		qualityEvent.setAgent_name(n.getName());
		qualityEvent.setAgent_type(C.AGENT_TYPE_NODE);
		qualityEvent.setDate(new Date());
		qualityEvent.setDetail(qualityMessage);
		
		return qualityEvent;
	}
}
