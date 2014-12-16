package de.uzk.hki.da.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeStampLogging {
	
	protected Logger logger = LoggerFactory.getLogger( this.getClass().getName() );
	
	public void log(String objectId, String actionName, long duration) {
		logger.info(getCurrentDate()+";"+objectId+";"+actionName+";"+duration);
	}
	
	public String getCurrentDate() {
		SimpleDateFormat formatter = new SimpleDateFormat( 
                "yyyy.MM.dd - HH:mm:ss:ms"); 
	    Date currentTime = new Date(); 
	    return formatter.format(currentTime);
	}
	
}
