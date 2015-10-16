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

package de.uzk.hki.da.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.uzk.hki.da.metadata.ContractRights.ConversionCondition;
import de.uzk.hki.da.metadata.EadParser;
import de.uzk.hki.da.metadata.LidoParser;
import de.uzk.hki.da.metadata.MetsParser;
import de.uzk.hki.da.metadata.PublicationRights.Law;
import de.uzk.hki.da.metadata.PublicationRights.TextType;
import de.uzk.hki.da.sb.MessageWriter;
import de.uzk.hki.da.sb.SIPFactory.KindOfSIPBuilding;

/**
 * Contains several utility methods and the SIP-Builder version
 * 
 * @author Thomas Kleinke
 * @author Polina Gubaidullina
 */
public class Utilities {
	
	private static final String sipBuilderVersion = "0.6.5-p1";
	private static Logger logger = Logger.getLogger(Utilities.class );
	/**
	 * String to enum translation method
	 * 
	 * @param kindofSIPBuildingName The string to translate into an enum
	 * @return The enum corresponding to the given string
	 */
	public static KindOfSIPBuilding translateKindOfSIPBuilding(String kindofSIPBuildingName) {
		if (kindofSIPBuildingName.equals(C.KIND_OF_SIPBUILDING_MULTIPLE))
			return KindOfSIPBuilding.MULTIPLE_FOLDERS;
		else if (kindofSIPBuildingName.equals(C.KIND_OF_SIPBUILDING_SINGLE))
			return KindOfSIPBuilding.SINGLE_FOLDER;
		else if (kindofSIPBuildingName.equals(C.KIND_OF_SIPBUILDING_NESTED))
			return KindOfSIPBuilding.NESTED_FOLDERS;
		
		return null;
	}
	
	/**
	 * String to enum translation method
	 * 
	 * @param conversionConditionName The string to translate into an enum
	 * @return The enum corresponding to the given string
	 */
	public static ConversionCondition translateConversionCondition(String conversionConditionName) {

		if (conversionConditionName.equals("Keine"))
			return ConversionCondition.NONE;
		else if (conversionConditionName.equals("Über Migration informieren"))
			return ConversionCondition.NOTIFY;
		else if (conversionConditionName.equals("Zustimmung für Migration einholen"))
			return ConversionCondition.CONFIRM;
		
		return null;
	}
	
	/**
	 * Enum to string translation method
	 * 
	 * @param conversionConditionEnum The enum to translate into a string
	 * @return The string corresponding to the given enum
	 */
	public static String translateConversionCondition(ConversionCondition conversionConditionEnum) {

		if (conversionConditionEnum == ConversionCondition.NONE)
			return "Keine";
		else if (conversionConditionEnum == ConversionCondition.NOTIFY)
			return "Über Migration informieren";
		else if (conversionConditionEnum == ConversionCondition.CONFIRM)
			return "Zustimmung für Migration einholen";
		
		return null;
	}
	
	/**
	 * String to enum translation method
	 * 
	 * @param textType The string to translate into an enum
	 * @return The enum corresponding to the given string
	 */
	public static TextType translateTextType(String textType) {

		if (textType.equals("Fußzeile"))
			return TextType.footer;
		else if (textType.equals("Wasserzeichen (oben)"))
			return TextType.north;
		else if (textType.equals("Wasserzeichen (mittig)"))
			return TextType.center;
		else if (textType.equals("Wasserzeichen (unten)"))
			return TextType.south;
		
		return null;
	}
	
	/**
	 * Enum to string translation method
	 * 
	 * @param textType The enum to translate into a string
	 * @return The string corresponding to the given enum
	 */
	public static String translateTextType(TextType textType) {

		if (textType == TextType.footer)
			return "Fußzeile";
		else if (textType == TextType.north)
			return "Wasserzeichen (oben)";
		else if (textType == TextType.center)
			return "Wasserzeichen (mittig)";
		else if (textType == TextType.south)
			return "Wasserzeichen (unten)";
		
		return null;
	}
	
	/**
	 * Enum to string translation method
	 * 
	 * @param textType The enum to translate into a string
	 * @return The string corresponding to the given enum
	 */
	public static String translateTextTypePosition(TextType textType) {

		if (textType == TextType.footer)
			return "Fußzeile";
		else if (textType == TextType.north)
			return "oben";
		else if (textType == TextType.center)
			return "mittig";
		else if (textType == TextType.south)
			return "unten";
		
		return null;
	}	
	
	/**
	 * String to enum translation method
	 * 
	 * @param lawName The string to translate into an enum
	 * @return The enum corresponding to the given string
	 */
	public static Law translateLaw(String lawName) {

		if (lawName.equals("ePflicht"))
			return Law.EPFLICHT;
		else if (lawName.equals("UrhG DE"))
			return Law.URHG_DE;
		
		return null;
	}
	
	/**
	 * Enum to string translation method
	 * 
	 * @param lawEnum The enum to translate into a string
	 * @return The string corresponding to the given enum
	 */
	public static String translateLaw(Law lawEnum) {

		if (lawEnum == Law.EPFLICHT)
			return "ePflicht";
		else if (lawEnum == Law.URHG_DE)
			return "UrhG DE";
		
		return null;
	}
	
	/**
	 * Checks if zero byte files exist in the given folder 
	 * 
	 * @param folder The folder to check
	 * @param sipName The SIP name
	 * @param messageWriter The message writer
	 * @return true if zero byte files exist inside the folder, false otherwise
	 */
	public static boolean checkForZeroByteFiles(File folder, String sipName, MessageWriter messageWriter) {
		
		Collection<File> files = FileUtils.listFiles(folder, null, true);
		
		for (File file : files) {
			if (file.length() == 0) {
				String zeroByteFileEntry = file.getName() + " (" + sipName + ")";
				messageWriter.addZeroByteFile(zeroByteFileEntry);
			}				
		}
		
		if (messageWriter.getZeroByteFiles().size() > 0)
			return true;
		else
			return false;		
	}
	
	/**
	 * Deserializes the given file and returns its content as a string
	 * 
	 * @param file The file to read
	 * @return The file content as a string
	 * @throws Exception
	 */
	public static String readFile(File file) throws Exception {

		Reader reader;
		try {
			reader = new FileReader(file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			throw new Exception("Couldn't create reader", e);
		}
		String text = "";

		try {
			for (int c; (c = reader.read() ) != -1; )
				text += (char) c;

			reader.close();								

		} catch (IOException e) {
			throw new Exception("Couldn't read file " + file.getAbsolutePath(), e);
		}

		return text;		
	}

	/**
	 * Writes the given text string into a file
	 * 
	 * @param output File The file to write into
	 * @param text The text to write into the file
	 * @throws Exception
	 */
	public static void writeFile(File outputFile, String text) throws Exception {

		Writer writer;
		try {
			writer = new FileWriter(outputFile);
		} catch (IOException e) {
			throw new Exception("Couldn't create writer", e);
		}

		try {
			writer.write(text);
			writer.close();

		} catch (IOException e) {
			throw new Exception("Couldn't write to file " + outputFile.getAbsolutePath(), e);
		}		
	}
	
	public static String getSipBuilderVersion() {
		return sipBuilderVersion;
	}
	
	public static String getSipBuilderShortVersion() {
		
		int index = sipBuilderVersion.indexOf('-');
		if (index == -1)
			return sipBuilderVersion;
		
		return sipBuilderVersion.substring(0, index);		
	}

	public static void validateFileReferencesInMetadata(File metadataFile, String metadataType) throws Exception {
		logger.info("Checking references in metadata file "+metadataFile);
		List<String> references = new ArrayList<String>();
		List<String> wrongRefs = new ArrayList<String>();
		if(metadataType.equals(C.CB_PACKAGETYPE_METS)) {
			references = new MetsParser(XMLUtils.getDocumentFromXMLFile(metadataFile)).getReferences();
		} else if(metadataType.equals(C.CB_PACKAGETYPE_LIDO)) {
			references = new LidoParser(XMLUtils.getDocumentFromXMLFile(metadataFile)).getReferences();
		} else if(metadataType.equals(C.CB_PACKAGETYPE_EAD)) {
			references = new EadParser(XMLUtils.getDocumentFromXMLFile(metadataFile)).getReferences();
		}
		for (String s : references) {
			if(!fileReferenceInXmlIsValid(s, metadataFile)) {
				wrongRefs.add(s);
			}
		}
		if(!wrongRefs.isEmpty()) {
			String msg = "Die Metadatendatei "+metadataFile+" enthält falsche Referenzen."
					+ "\nFolgende Dateien konnten nicht gefunden werden: \n"+wrongRefs;
			throw new Error(msg);
		}
	}
	
	private static boolean fileReferenceInXmlIsValid(String reference, File metadataFile) throws IOException {
		boolean isValid = false;
		File referencedFile = new File(metadataFile.getParentFile(), reference);
		logger.debug("Check reference "+reference+" in metadata file "+metadataFile);
		if(referencedFile.exists()) {
			logger.debug("File "+referencedFile.getAbsolutePath()+" exists.");
			File sipMain = new File(metadataFile.getParentFile(), "");
			logger.debug("Check if file is inside a sip canidate "+sipMain);
			if(referencedFile.getCanonicalPath().startsWith(sipMain.getCanonicalPath())) {
				logger.debug("File "+referencedFile+" is inside a sip");
				isValid = true;
			}
		}	
		return isValid;
	}
}
