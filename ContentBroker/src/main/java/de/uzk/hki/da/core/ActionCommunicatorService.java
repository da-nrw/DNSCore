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

package de.uzk.hki.da.core;

import java.io.*;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * The Class ActionCommunicatorService.
 *
 * @author Christian Weitz
 * @author Thomas Kleinke
 */

public class ActionCommunicatorService {

	/** The data object map. */
	public Map<Integer, Map<String, Object>> dataObjectMap;
	
	/** The Constant logger. */
	static final Logger logger = LoggerFactory.getLogger(ActionCommunicatorService.class);
	
	/** The recovery file name. */
	final private String recoveryFileName = "actionCommunicatorService.recovery";
	
	private boolean isSerializing = false;
	
	/**
	 * Instantiates a new action communicator service.
	 */
	public ActionCommunicatorService() {
		
		if (new File(recoveryFileName).exists())
			deserialize();
		else
			dataObjectMap = new HashMap<Integer, Map<String, Object>>();
	}
	
	/**
	 * Serialize.
	 */
	public synchronized void serialize() {
		
		isSerializing = true;
		
		ObjectOutputStream output = null;
		try {
			output = new ObjectOutputStream
					(new FileOutputStream(recoveryFileName));
			output.writeObject(dataObjectMap);
			
		} catch (IOException e) {
			throw new RuntimeException("Couldn't serialize dataObjectMap!",e );
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				throw new RuntimeException("Failed to close output stream.", e);
			}		
		}
		
		isSerializing = false;
	}
	
	/**
	 * Deserialize.
	 */
	@SuppressWarnings("unchecked")
	public void deserialize() {
		
		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream
					(new FileInputStream(recoveryFileName));
			Object obj = input.readObject();
			if (obj instanceof HashMap)
				dataObjectMap = (HashMap<Integer, Map<String, Object>>) obj;
			else 
				throw new IOException();			
			
		} catch (Exception e) {
			throw new RuntimeException("Couldn't deserialize dataObjectMap!", e);
		} finally {
			try {
				if (input!=null) input.close();
			} catch (IOException e) {
				throw new RuntimeException("Failed to close input stream.", e);
			}
		}
		
		if (new File(recoveryFileName).exists())
			new File(recoveryFileName).delete();
	}
	
	/**
	 * Adds a data object to the data object map.
	 *
	 * @param jobId the job id
	 * @param dataObjectId the data object id
	 * @param dataObject the data object
	 */
	public void addDataObject(int jobId, String dataObjectId, Object dataObject) {
		
		waitWhileSerializing();
		
		Map<String, Object> map = dataObjectMap.get(jobId);
		
		if (map == null)
			map = new HashMap<String, Object>();
				
		map.put(dataObjectId, dataObject);
		dataObjectMap.put(jobId, map);
		logger.debug("Added data object \"" + dataObjectId + "\" for job id " + jobId + ".");
	}
	
	/**
	 * Retrieves data object for given job id and data object id
	 * and removes it from the data object map.
	 * @author Thomas Kleinke
	 * @author Daniel M. de Oliveira
	 *
	 * @param jobId the job id
	 * @param dataObjectId the data object id
	 * @return returns null if no data object is found
	 */
	public Object extractDataObject(int jobId, String dataObjectId) {
		
		waitWhileSerializing();
		
		Object obj = getDataObject(jobId, dataObjectId);
		if (obj!=null)
			removeDataObject(jobId, dataObjectId);
		
		return obj;		
	}
	
	/**
	 * Retrieves data object for given job id and data object id without
	 * removing it from the data object map.
	 *
	 * @param jobId the job id
	 * @param dataObjectId the data object id
	 * @return returns null if no data object is found
	 */
	
	public Object getDataObject(int jobId, String dataObjectId) {
		
		waitWhileSerializing();
		
		Map<String, Object> map = dataObjectMap.get(jobId);
		
		if (map == null)
			return null;
		else
			return map.get(dataObjectId);
	}
	
	/**
	 * Removes serialized ActionCommunicatorService.recovery file
	 */
	public void purgeActionCommunicatorService() {
		
		waitWhileSerializing();
		dataObjectMap = new HashMap<Integer, Map<String, Object>>();
		serialize();
	}
	
	/**
	 * Removes data object for given job id and data object id from the
	 * data object map.
	 *
	 * @param jobId the job id
	 * @param dataObjectId the data object id
	 * @return returns false if data object wasn't found
	 */
	public boolean removeDataObject(int jobId, String dataObjectId) {
		
		waitWhileSerializing();
		
		Map<String, Object> map = dataObjectMap.get(jobId);
		
		if (map != null && map.remove(dataObjectId) != null)
		{
			if (map.size() == 0)
				dataObjectMap.remove(jobId);
			
			logger.debug("Removed data object \"" + dataObjectId + "\" for job id " + jobId + ".");
			return true;
		}
		else
		{
			logger.debug("Failed to remove data object \"" + dataObjectId + "\" for job id " + jobId + ".");
			return false;
		}
	}
	
	private void waitWhileSerializing() {
		
		while (isSerializing) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.warn(e.getMessage() + e.getStackTrace());
			}
		}
	}
}
