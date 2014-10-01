/*
  DA NRW Software Suite | SIP-Builder
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

package de.uzk.hki.da.gui;

import java.awt.Component;
import java.awt.Image;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import de.uzk.hki.da.main.SIPBuilder;
import de.uzk.hki.da.sb.MessageWriter;
import de.uzk.hki.da.sb.MessageWriter.UserInput;

/**
 * A specialized message writer responsible for displaying info messages and dialogs in GUI mode
 * 
 * @author Thomas Kleinke
 */
class GuiMessageWriter extends MessageWriter {

	private Component gui;
	private Image iconImage;

	/**
	 * Shows a message box
	 * 
	 * @param message The message to display
	 */
	@Override
	public void showMessage(String message) {
		showMessage(message, JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * Shows a message box of the given message type
	 * 
	 * @param message The message to display
	 * @param type The message type (one of the message types in JOptionPane)
	 */
	@Override
	public void showMessage(String message, int type) {

		if (type == JOptionPane.PLAIN_MESSAGE)
			JOptionPane.showMessageDialog(gui, message, SIPBuilder.getProperties().getProperty("ARCHIVE_NAME") + " SIP-Builder", type, new ImageIcon(iconImage));
		else
			JOptionPane.showMessageDialog(gui, message, SIPBuilder.getProperties().getProperty("ARCHIVE_NAME") + " SIP-Builder", type);
	}

	/**
	 * Lets the user decide if a certain file will be overwritten or not
	 * 
	 * @param message The message to display
	 * @return The user input as a UserInput enum
	 */
	@Override
	public UserInput showOverwriteDialog(String message) {

		String[] options = new String[] {"Ja", "Nein", "Alle überschreiben" };
		int answer = JOptionPane.showOptionDialog(gui, message, SIPBuilder.getProperties().getProperty("ARCHIVE_NAME") + " SIP-Builder", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(iconImage), options, null);

		switch (answer) {
		case 0:
			return UserInput.YES;
		case 1:
			return UserInput.NO;
		case 2:
			return UserInput.ALWAYS_OVERWRITE;
		default:
			return UserInput.NO;
		}
	}
	
	/**
	 *  Lets the user decide if an already existing collection will be overwritten or not
	 * 
	 * @param message The message to display
	 * @return The user input as a UserInput enum
	 */
	@Override
	public UserInput showCollectionOverwriteDialog(String message) {

		String[] options = new String[] {"Ja", "Nein" };
		int answer = JOptionPane.showOptionDialog(gui, message, SIPBuilder.getProperties().getProperty("ARCHIVE_NAME") + " SIP-Builder", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(iconImage), options, null);

		switch (answer) {
		case 0:
			return UserInput.YES;
		case 1:
			return UserInput.NO;
		default:
			return UserInput.NO;
		}
	}
	
	/**
	 * Displays a message that informs the user about which zero byte files were found
	 */
	public void showZeroByteFileMessage() {
		
		Collections.sort(zeroByteFiles);
		
		String sipName = zeroByteFiles.get(0).substring(
				zeroByteFiles.get(0).lastIndexOf('(') + 1,
				zeroByteFiles.get(0).lastIndexOf(')'));
		
		String message = "";
		if (zeroByteFiles.size() == 1) {
			message = "Bei der Erstellung des SIPs " + sipName + " wurde eine Datei\nder Größe 0 Byte gefunden:\n\n" 
					+ zeroByteFiles.get(0).substring(0, zeroByteFiles.get(0).lastIndexOf('(')) + "\n\n" +					
					"Bitte überprüfen Sie die Datei.";		
		}
		else if (zeroByteFiles.size() < 11) {
			message = "Bei der Erstellung des SIPs " + sipName + " wurden Dateien\nder Größe 0 Byte gefunden:\n\n";
			for (String s : zeroByteFiles) {
				message += s.substring(0, s.lastIndexOf('('));
				message += "\n";
			}				
			message += "\nBitte überprüfen Sie die Dateien.";			
		} else {
			message = "Bei der Erstellung des SIPs " + sipName + " wurden Dateien\nder Größe 0 Byte gefunden:\n\n";
			for (int i = 0; i < 10; i++) {
				message += zeroByteFiles.get(i).substring(0, zeroByteFiles.get(i).lastIndexOf('('));
				message += "\n";
			}				
			message += "und ";
			message += String.valueOf(zeroByteFiles.size() - 10);
			message += " weitere Dateien.\n\n";
			message += "Bitte überprüfen Sie Ihre Daten.";
		}
		
		JOptionPane.showMessageDialog(gui, message, SIPBuilder.getProperties().getProperty("ARCHIVE_NAME") + " SIP-Builder", JOptionPane.ERROR_MESSAGE);
	}

	public void setGui(Component gui) {
		this.gui = gui;
	}

	public void setIconImage(Image iconImage) {
		this.iconImage = iconImage;
	}	
}
