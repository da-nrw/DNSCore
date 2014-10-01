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

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for displaying info messages and user dialogs
 * 
 * @author Thomas Kleinke
 */
public abstract class MessageWriter {
	
	public enum UserInput { YES, NO, ALWAYS_OVERWRITE };
	
	protected List<String> zeroByteFiles = new ArrayList<String>();

	/**
	 * Displays a message
	 * 
	 * @param message The message to display
	 */
	abstract public void showMessage(String message);
	
	/**
	 *  Displays a message of the given message type
	 *  
	 * @param message The message to display
	 * @param type The message type (one of the message types in JOptionPane)
	 */
	abstract public void showMessage(String message, int type);
	
	/**
	 * Lets the user decide if a certain file will be overwritten or not
	 * 
	 * @param message The message to display
	 * @return The user input as a UserInput enum
	 */
	abstract public UserInput showOverwriteDialog(String message);
	
	/**
	 * Lets the user decide if an already existing collection will be overwritten or not
	 * 
	 * @param message The message to display
	 * @return The user input as a UserInput enum
	 */
	abstract public UserInput showCollectionOverwriteDialog(String message); 
	
	/**
	 * Displays a message that informs the user about which zero byte files were found
	 */
	abstract public void showZeroByteFileMessage();
	
	/**
	 * Adds a file name to the list of zero byte files that were found
	 * @param zeroByteFile The file name to add
	 */
	public void addZeroByteFile(String zeroByteFile) {
		zeroByteFiles.add(zeroByteFile);
	}
	
	/**
	 * Clears the list of zero byte files that were found
	 */
	public void resetZeroByteFiles() {
		zeroByteFiles.clear();
	}
	
	/**
	 * @return The list of zero byte files that were found
	 */
	public List<String> getZeroByteFiles() {
		return zeroByteFiles;
	}
}
