/*
  DA-NRW Software Suite | SIP-Builder
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
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

package de.uzk.hki.da.sb;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Checks if the user input is complete and valid (e.g. if folder paths exist etc.)
 * 
 * @author Thomas Kleinke
 */
public class UserInputValidator {

	public enum Feedback { VALID,
					NO_SOURCE_PATH,
					NO_DESTINATION_PATH,
					SOURCE_PATH_DOES_NOT_EXIST,
					FOLDER_EQUALITY,
					SUBFOLDER,
					NON_DIRECTORY_FILES_EXIST,
					NO_COLLECTION_NAME,
					INVALID_COLLECTION_NAME,
					COLLECTION_ALREADY_EXISTS,
					INVALID_SIP_NAME,
					NO_DATE,
					INVALID_DATE };
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	
	private static final char[] illegalFilenameCharacters = { '/', '\\','\n', '\r', '\t', '\0', '\f', '`', '\"', '?', '*', '<', '>', '|', ':' };
	
	
	/**
	 * Checks if the paths chosen by the user exist and the folder contents are compliant
	 * with the chosen kind of SIP building
	 * 
	 * @param sourcePath The source path
	 * @param destinationPath The destination path
	 * @param kindOfSipBuilding The kind of SIP building chosen by the user
	 * @return The validation result as a Feedback enum
	 */
	public static Feedback checkPaths(String sourcePath, String destinationPath,
									  SIPFactory.KindOfSIPBuilding kindOfSipBuilding) {

		if (sourcePath == null || sourcePath.equals(""))
			return Feedback.NO_SOURCE_PATH;
		
		if (!new File(sourcePath).isDirectory())
			return Feedback.SOURCE_PATH_DOES_NOT_EXIST;

		if (destinationPath == null || destinationPath.equals(""))
			return Feedback.NO_DESTINATION_PATH;
		
		if (sourcePath.equals(destinationPath))
			return Feedback.FOLDER_EQUALITY;

		if (destinationPath.startsWith(sourcePath))
			return Feedback.SUBFOLDER;

		if (kindOfSipBuilding == SIPFactory.KindOfSIPBuilding.MULTIPLE_FOLDERS) {
			List<File> subFolders = Arrays.asList(new File(sourcePath).listFiles());
			for (File folder : subFolders) {
				if (!folder.isDirectory() && !folder.isHidden())
					return Feedback.NON_DIRECTORY_FILES_EXIST;
			}			
		}
		
		return Feedback.VALID;		
	}
	
	/**
	 * Checks if a collection with the given name can be created in the folder specified by the given destination path
	 * 
	 * @param collectionName The collection name
	 * @param destinationPath The destination path
	 * @return The validation result as a Feedback enum
	 */
	public static Feedback checkCollectionName(String collectionName, String destinationPath) {
		
		if (collectionName == null || collectionName.equals(""))
			return Feedback.NO_COLLECTION_NAME;
		
		for (char character : illegalFilenameCharacters) {
			if (collectionName.indexOf(character) != -1)
				return Feedback.INVALID_COLLECTION_NAME;		
		}
		
		if (new File(destinationPath + File.separator + collectionName).exists())
			return Feedback.COLLECTION_ALREADY_EXISTS;
		
		return Feedback.VALID;
	}
	
	/**
	 * Checks if the SIP name contains invalid characters
	 * 
	 * @param sipName The SIP name
	 * @return The validation result as a Feedback enum
	 */
	public static Feedback checkSipName(String sipName) {
		
		for (char character : illegalFilenameCharacters) {
			if (sipName.indexOf(character) != -1)
				return Feedback.INVALID_SIP_NAME;		
		}
	
		return Feedback.VALID;
	}

	/**
	 * Checks if the given date string is a valid date
	 * 
	 * @param date the date as a string
	 * @return The validation result as a Feedback enum
	 */
	public static Feedback checkDate(String date) {

		if (date == null || date.equals(""))
			return Feedback.NO_DATE;

		dateFormat.setLenient(false);

		try {
			dateFormat.parse(date);
		}
		catch(Exception exc){			 
			return Feedback.INVALID_DATE;
		}

		return Feedback.VALID;
	}
}
