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

package de.uzk.hki.da.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.commons.io.FileUtils;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import de.uzk.hki.da.main.SIPBuilder;
import de.uzk.hki.da.sb.Logger;
import de.uzk.hki.da.sb.MessageWriter;
import de.uzk.hki.da.sb.SIPFactory;
import de.uzk.hki.da.sb.UserInputValidator;
import de.uzk.hki.da.sb.MessageWriter.UserInput;
import de.uzk.hki.da.sb.SIPFactory.Feedback;
import de.uzk.hki.da.sb.SIPFactory.KindOfSIPBuilding;
import de.uzk.hki.da.utils.Utilities;

/**
 * Runs the SIP-Builder in CLI mode
 * 
 * @author Thomas Kleinke
 */
public class Cli {
	
	private String confFolderPath;
	private String[] args;
	private SIPFactory sipFactory;
	private Logger logger;
	
	private boolean alwaysOverwrite = false;
	
	private File fileListFile = null;
	private File sipListFile = null;
	
	
	public Cli(String confFolderPath, String dataFolderPath, String[] args) {
		this.confFolderPath = confFolderPath;
		this.args = args;
		logger = new Logger(dataFolderPath);
		sipFactory = new SIPFactory();
		sipFactory.setLogger(logger);
	}

	/**
	 * Starts the SIP building process in CLI mode
	 * 
	 * @return The return value (one of the values defined in SIPFactory.Feedback)
	 */
	public int start() {
		
		Feedback returnValue;
		
    	if ((returnValue = configureSipFactory()) != Feedback.SUCCESS)
    		return returnValue.toInt();
    	
    	if ((returnValue = checkConfiguration()) != Feedback.SUCCESS)
    		return returnValue.toInt();

    	if ((returnValue = copyFilesFromList()) != Feedback.SUCCESS)
    		return returnValue.toInt();
    	
    	if ((returnValue = checkSourceFolder()) != Feedback.SUCCESS)
    		return returnValue.toInt();
    	
    	sipFactory.startSIPBuilding();
    	
    	do { 
    		try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				break;
			}
    	} while (sipFactory.isWorking());
    	
    	return sipFactory.getReturnCode().toInt();
	}
	
	/**
	 * Sets the values in the SIPFactory depending on the arguments passed to the SIP-Builder
	 * 
	 * @return The method result as a Feedback enum
	 */
	private Feedback configureSipFactory() {
		
		boolean contractRightsLoaded = false;
		CliMessageWriter messageWriter = new CliMessageWriter();
		
		// Default settings
    	sipFactory.setKindofSIPBuilding(SIPFactory.KindOfSIPBuilding.MULTIPLE_FOLDERS);
    	sipFactory.setCreateCollection(false);
    	sipFactory.setIgnoreZeroByteFiles(true);
    	sipFactory.setCompress(true);
    	sipFactory.setProgressManager(new CliProgressManager());
 	
    	// User settings
    	for (String arg : args) {
    		
    		if (arg.equals("-help") || arg.equals("-h")) {
    			showHelp();
    			return Feedback.EXIT_AFTER_HELP;
    		}
    		
    		if (arg.startsWith("-source")) {
    			sipFactory.setSourcePath(extractParameter(arg));
    			continue;
    		}
    		
    		if (arg.startsWith("-filelist")) {
    			fileListFile = new File(extractParameter(arg));
    			if (!fileListFile.exists()) {
    				System.out.println("Die Datei " + fileListFile.getAbsolutePath() +
    								   " konnte nicht gefunden werden.");
    				return Feedback.FILELIST_NOT_FOUND;
    			}
    			continue;
    		}
    		
    		if (arg.startsWith("-siplist")) {
    			sipListFile = new File(extractParameter(arg));
    			if (!sipListFile.exists()) {
    				System.out.println("Die Datei " + sipListFile.getAbsolutePath() +
    								   " konnte nicht gefunden werden.");
    				return Feedback.SIPLIST_NOT_FOUND;
    			}
    			continue;
    		}
    		
    		if (arg.startsWith("-destination")) {
    			sipFactory.setDestinationPath(extractParameter(arg));
    			continue;
    		}
    		
    		if (arg.startsWith("-premis")) {
    			File premisFile = new File(extractParameter(arg));
    			if (!premisFile.exists()) {
    				System.out.println("Die Datei " + premisFile.getAbsolutePath() +
    								   " konnte nicht gefunden werden.");
    				return Feedback.PREMIS_FILE_NOT_FOUND;
    			}
    			sipFactory.setRightsSourcePremisFile(premisFile);
    			continue;
    		}
    		
    		if (arg.startsWith("-rights")) {
    			File contractRightsFile = new File(extractParameter(arg));
    			if (!contractRightsFile.exists()) {
    				System.out.println("Die Datei " + contractRightsFile.getAbsolutePath() +
    								   " konnte nicht gefunden werden.");
    				return Feedback.RIGHTS_FILE_NOT_FOUND;
    			}
    			try {
					sipFactory.getContractRights().loadContractRightsFromFile(contractRightsFile);
				} catch (Exception e) {
					System.out.println("Die Rechteeinstellungen aus der Datei " +
									   contractRightsFile.getAbsolutePath() + " konnten nicht gelesen werden.");
					logger.log("ERROR: Failed to load contract rights from file " + contractRightsFile.getAbsolutePath(), e);
					return Feedback.RIGHTS_FILE_READ_ERROR;
				}
				contractRightsLoaded = true;
				continue;
    		}
    		
    		if (arg.equals("-single")) {
    			sipFactory.setKindofSIPBuilding(SIPFactory.KindOfSIPBuilding.SINGLE_FOLDER);
    			continue;
    		}
     		else if (arg.startsWith("-single")) {
    			sipFactory.setKindofSIPBuilding(SIPFactory.KindOfSIPBuilding.SINGLE_FOLDER);
    			String name = extractParameter(arg);
    			if (name != null)
    				sipFactory.setName(name);
    			continue;
    		}    			
    		
    		if (arg.startsWith("-collection")) {
    			sipFactory.setCreateCollection(true);
    			sipFactory.setCollectionName(extractParameter(arg));
    			continue;
    		}
    		
    		if (arg.equals("-alwaysOverwrite")) {
    			messageWriter.setStandardAnswer(MessageWriter.UserInput.YES);
    			alwaysOverwrite = true;
    			continue;
    		}
    		
    		if (arg.startsWith("-ignoreExtensions")) {
    			sipFactory.setForbiddenFileExtensions(extractParameters(arg));
    			continue;
    		}
    		
    		if (arg.equals("-noCompression")) {
    			sipFactory.setCompress(false);
    			continue;
    		}
    		
    		if (arg.equals("-default") || arg.equals("-multiple") || arg.equals("-neverOverwrite") || arg.equals("-compression"))
    			continue;
    		
    		System.out.println(arg + " ist kein gültiger Parameter. Starten Sie den SipBuilder mit dem Parameter " +
    		                   "-help, um eine Liste aller möglichen Parameter anzuzeigen.");
    		return Feedback.INVALID_PARAMETER;
    	}
    	
    	sipFactory.setMessageWriter(messageWriter);
    	
    	if (!contractRightsLoaded)
    		try {
    			sipFactory.getContractRights().loadContractRightsFromFile(new File(confFolderPath + File.separator + "standardRights.xml"));
    		} catch (Exception e) {
    			System.out.println("Die Standardrechte konnten nicht aus der Datei \"" + confFolderPath + File.separator +
    							   "standardRights.xml\" geladen werden.");
    			return Feedback.STANDARD_RIGHTS_FILE_READ_ERROR;
    		}
    	
    	return Feedback.SUCCESS;
	}
	
	/**
	 * Checks if the SIPFactory's configuration settings are valid
	 * 
	 * @return The method result as a Feedback enum
	 */
	private Feedback checkConfiguration() {
		
		if (sipFactory.getCreateCollection() &&
				sipFactory.getKindofSIPBuilding() == SIPFactory.KindOfSIPBuilding.SINGLE_FOLDER)
		{
			System.out.println("Die Parameter -single und -collection sind nicht miteinander kompatibel. Lieferungen können nur " +
							   "bei Erstellung mehrerer SIPs angelegt werden.");
    		return Feedback.INVALID_PARAMETER_COMBINATION;
		}
		
		if (sipFactory.getDestinationPath() == null || sipFactory.getDestinationPath().equals("")) {
    		System.out.println("Bitte geben Sie einen Zielordner an.");
    		return Feedback.NO_DESTINATION_FOLDER;
		}
    		
    	UserInputValidator.Feedback feedback;
    		
    	if (sipFactory.getCreateCollection()) {
    		feedback = UserInputValidator.checkCollectionName(sipFactory.getCollectionName(), sipFactory.getDestinationPath());

    		switch(feedback) {
    		case NO_COLLECTION_NAME:
    			System.out.println("Bitte geben Sie den gewünschten Namen der Lieferung an.");
    			return Feedback.NO_COLLECTION_NAME;

    		case INVALID_COLLECTION_NAME:
    			System.out.println("Der von Ihnen gewählte Lieferungsname enthält Zeichen, die auf manchen Betriebssystemen für " +
    							   "die Benennung von Dateien nicht erlaubt sind.");
    			return Feedback.INVALID_COLLECTION_NAME;

    		case COLLECTION_ALREADY_EXISTS:
    			if (alwaysOverwrite) {
    				FileUtils.deleteQuietly(new File(new File(sipFactory.getDestinationPath()),
    						sipFactory.getCollectionName()));
    			} else {
    				System.out.println("Im Zielverzeichnis existiert bereits eine Lieferung namens \"" + sipFactory.getCollectionName() + "\". " + 
    								   "Bitte wählen Sie einen anderen Namen oder starten Sie den SIP-Builder mit dem Parameter -alwaysOverwrite, " +
    								   "um die bestehende Lieferung zu überschreiben.");
    				return Feedback.COLLECTION_ALREADY_EXISTS;
    			}    			

    		default:
    			break;
    		}
    	}
    	
    	if (sipFactory.getName() != null && !sipFactory.getName().equals("")) {
    		feedback = UserInputValidator.checkSipName(sipFactory.getName());
    		
    		switch(feedback) {
    		case INVALID_SIP_NAME:
    			System.out.println("Der von Ihnen gewählte SIP-Name enthält Zeichen, die auf manchen Betriebssystemen für\n" +
    					"die Benennung von Dateien nicht erlaubt sind.");
    			return Feedback.INVALID_SIP_NAME;
    			
    		default:
    			break;
    		}
    	}
    	
    	return Feedback.SUCCESS;
	}
	
	/**
	 * Copies the files listed in a file list or SIP list to a single directory
	 * 
	 * @return The method result as a Feedback enum
	 */
	private Feedback copyFilesFromList() {
		
		if (fileListFile != null && sipListFile != null) {
			System.out.println("Die Parameter -filelist und -siplist sind nicht miteinander kompatibel.");
			return Feedback.INVALID_PARAMETER_COMBINATION;    		
		}
		
    	if (fileListFile != null) {
    		if (sipFactory.getName() == null) {
    			System.out.println("Bitte geben Sie mithilfe des Parameters -single=\"[Name]\" einen Namen für das SIP an, wenn Sie das SIP " +
    		                       "per Dateiliste erstellen möchten.");
    			return Feedback.NO_SIP_NAME;    		
    		}
    		
    		System.out.println("\nTrage Dateien aus der Dateiliste zusammen...");
			String sourcePath = copySipContentToFolder(fileListFile);
			if (sourcePath.equals(""))
				return Feedback.FILELIST_READ_ERROR;
			else {   		
				sipFactory.setSourcePath(sourcePath);
				sipFactory.setListCreationTempFolder(new File(sourcePath).getParentFile());
			}
    	}
    	
    	if (sipListFile != null) {
    		if (sipFactory.getKindofSIPBuilding() != SIPFactory.KindOfSIPBuilding.MULTIPLE_FOLDERS) {
    			System.out.println("Die Parameter -single und -siplist sind nicht miteinander kompatibel. Bitte wählen Sie bei Verwendung einer " +
    							   "SIP-Liste den Parameter -multiple.");
    			return Feedback.INVALID_PARAMETER_COMBINATION;
    		}
    		
    		System.out.println("\nTrage Dateien aus der SIP-Liste zusammen...");
			String sourcePath = copySipListContentToFolder(sipListFile);
			if (sourcePath.equals(""))
				return Feedback.SIPLIST_READ_ERROR;
			else {   		
				sipFactory.setSourcePath(sourcePath);
				sipFactory.setListCreationTempFolder(new File(sourcePath));
			}
    	}
    	
    	return Feedback.SUCCESS;		
	}
	
	/**
	 * Checks if the source folder chosen by the user is a valid source folder
	 * 
	 * @return The method result as a Feedback enum
	 */
	private Feedback checkSourceFolder() {
		
		UserInputValidator.Feedback feedback = UserInputValidator.checkPaths(sipFactory.getSourcePath(),
    			sipFactory.getDestinationPath(), sipFactory.getKindofSIPBuilding());

    	switch(feedback) {
    	case NO_SOURCE_PATH:
    		System.out.println("Bitte geben Sie einen Quellordner an.");
    		return Feedback.NO_SOURCE_FOLDER;

    	case SOURCE_PATH_DOES_NOT_EXIST:
    		System.out.println("Der von Ihnen angegebene Quellordner existiert nicht.");
    		return Feedback.SOURCE_FOLDER_NOT_FOUND;	

    	case FOLDER_EQUALITY:
    		System.out.println("Bitte stellen Sie sicher, dass Quell- und Zielordner nicht identisch sind.");
    		return Feedback.SOURCE_AND_DESTINATION_IDENTITY;

    	case SUBFOLDER:
    		System.out.println("Bitte stellen Sie sicher, dass der Zielordner kein Unterordner des Quellordners ist. ");
    		return Feedback.DESTINATION_FOLDER_IS_SUBFOLDER_OF_SOURCE_FOLDER;

    	case NON_DIRECTORY_FILES_EXIST:
    		System.out.println("Das von Ihnen angegebene Quellverzeichnis enthält Dateien, die keine Ordner sind. " +
    				           "Bitte überprüfen Sie, ob Sie die richtige Einstellung zur SIP-Generierung gewählt haben.");
    		return Feedback.SOURCE_FOLDER_CONTAINS_NON_DIRECTORY_FILES;

    	default:
    		break;		    			
    	}
		
		return Feedback.SUCCESS;
	}

	/**
	 * Determines the parameter value of an argument
	 * 
	 * @param arg The argument
	 * @return The parameter value
	 */
    private String extractParameter(String arg) {
    	
    	int index = arg.indexOf('=');
    	
    	if (index == -1)
    		return null;
    	
    	String parameter = arg.substring(index + 1);
    	if (parameter.startsWith("\""))
    		parameter = parameter.substring(1);
    	if (parameter.endsWith("\""))
    		parameter = parameter.substring(0, parameter.length() - 2);
    	
    	return parameter;
    }
    
    /**
	 * Determines the parameter values of an argument
	 * 
	 * @param arg The argument
	 * @return The parameter values as a string list
	 */
    private List<String> extractParameters(String arg) {
    	
    	List<String> parameters = new ArrayList<String>();
    	
    	int startIndex = arg.indexOf('=');
    	
    	if (startIndex == -1)
    		return null;
    	
    	int stopIndex = arg.indexOf(';', startIndex);

    	while (stopIndex != -1) {
    		String parameter = arg.substring(startIndex + 1, stopIndex);
    		if (parameter.startsWith("\""))
    			parameter = parameter.substring(1);
    		if (parameter.endsWith("\""))
    			parameter = parameter.substring(0, parameter.length() - 2);
    		if (!parameter.equals(""))
    			parameters.add(parameter);
    		
    		startIndex = stopIndex;
    		stopIndex = arg.indexOf(';', startIndex + 1);
    	}
    	
    	if (startIndex + 1 < arg.length()) {
    		String parameter = arg.substring(startIndex + 1);
    		if (parameter != null && !parameter.equals(""))
    			parameters.add(parameter);
    	}
    	
    	return parameters;    	
    }

    /**
     * Copies the files listed in a file list to a single directory
     * 
     * @param fileListFile The file list file
     * @return The path to the directory containing the files
     */
    private String copySipContentToFolder(File fileListFile) {

    	CliProgressManager progressManager = new CliProgressManager();
    	
    	String tempFolderName = getTempFolderName();
    	
    	String fileList = "";
    	try {
    		fileList = Utilities.readFile(fileListFile);
    	} catch (Exception e) {
    		logger.log("ERROR: Failed to read file " + fileListFile.getAbsolutePath(), e);
    		System.out.println("Die Datei " + fileListFile.getAbsolutePath() + " konnte nicht gelesen werden.");
    		return "";
    	}
    	
    	fileList = fileList.replace("\r", "");
    	if (!fileList.endsWith("\n"))
    			fileList += "\n";
    	
    	long files = 0;
    	for (int i = 0; i < fileList.length(); i++) {
    		if (fileList.charAt(i) == '\n')
    			files++;
    	}
    	progressManager.setTotalSize(files);

    	File tempDirectory = new File(tempFolderName + File.separator + sipFactory.getName());
    	tempDirectory.mkdirs();

    	while (true) {
    		int index = fileList.indexOf('\n');

    		if (index >= 0) {
    			String filepath = fileList.substring(0, index);
    			fileList = fileList.substring(index + 1);

    			File file = new File(filepath);
    			if (!file.exists()) {
    				logger.log("ERROR: File " + file.getAbsolutePath() + " is referenced in filelist, " +
	    						   "but does not exist");
    				System.out.println("\nDie in der Dateiliste angegebene Datei " + file.getAbsolutePath() + " existiert nicht.");
    				FileUtils.deleteQuietly(tempDirectory);
    				return "";
    			}

    			try {
    				if (file.isDirectory())
    					FileUtils.copyDirectoryToDirectory(file, tempDirectory);
    				else
    					FileUtils.copyFileToDirectory(file, tempDirectory);
    				progressManager.copyFilesFromListProgress();
    			} catch (IOException e) {
    				logger.log("ERROR: Failed to copy file " + file.getAbsolutePath() + " to folder " + tempDirectory.getAbsolutePath(), e);
    				System.out.println("\nDie in der Dateiliste angegebene Datei " + file.getAbsolutePath() + " konnte nicht kopiert werden.");
    				FileUtils.deleteQuietly(tempDirectory);
    				return "";
    			}
    		}
    		else
    			break;
    	}

    	return (tempDirectory.getAbsolutePath());    	
    }
    
    /**
     * Copies the files listed in a SIP list to a single directory
     * 
     * @param fileListFile The SIP list file
     * @return The path to the directory containing the files
     */
    private String copySipListContentToFolder(File sipListFile) {

    	CliProgressManager progressManager = new CliProgressManager();
    	
    	String tempFolderName = getTempFolderName();
    	
    	XMLReader xmlReader = null;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			xmlReader = spf.newSAXParser().getXMLReader();
		} catch (Exception e) {
			logger.log("ERROR: Failed to create SAX parser", e);
			System.out.println("Fehler beim Einlesen der SIP-Liste: SAX-Parser konnte nicht erstellt werden.");
			return "";
		}
		xmlReader.setErrorHandler(new ErrorHandler(){

			@Override
			public void error(SAXParseException e) throws SAXException {
				throw new SAXException("Beim Einlesen der SIP-Liste ist ein Fehler aufgetreten.", e);
			}

			@Override
			public void fatalError(SAXParseException e) throws SAXException {
				throw new SAXException("Beim Einlesen der SIP-Liste ist ein schwerer Fehler aufgetreten.", e);
			}

			@Override
			public void warning(SAXParseException e) throws SAXException {
				logger.log("WARNING: Warning while parsing siplist", e);
				System.out.println("\nWarnung:\n" + e.getMessage());
			}
		});

		InputStream inputStream;
		try {
			inputStream = new FileInputStream(sipListFile);
			
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			Builder parser = new Builder(xmlReader);
			Document doc = parser.build(reader);
			reader.close();
			
			Element root = doc.getRootElement();
			Elements sipElements = root.getChildElements("sip");
			
			long files = 0;
			for (int i = 0; i < sipElements.size(); i++) {
				Elements fileElements = sipElements.get(i).getChildElements("file");
				if (fileElements != null)
					files+= fileElements.size();				
			}
			progressManager.setTotalSize(files);
			
			for (int i = 0; i < sipElements.size(); i++) {
				Element sipElement = sipElements.get(i);
				String sipName = sipElement.getAttributeValue("name");
				
				File tempDirectory = new File(tempFolderName + File.separator + sipName);
				if (tempDirectory.exists()) {
					FileUtils.deleteQuietly(new File(tempFolderName));	
					System.out.println("\nDie SIP-Liste enthält mehrere SIPs mit dem Namen " + sipName + ". " +
									   "Bitte vergeben Sie für jedes SIP einen eigenen Namen.");
					return "";
				}
				tempDirectory.mkdirs();
				
				Elements fileElements = sipElement.getChildElements("file");
				
				for (int j = 0; j < fileElements.size(); j++) {
					Element fileElement = fileElements.get(j);
					String filepath = fileElement.getValue();
					
					File file = new File(filepath);    			
	    			if (!file.exists()) {
	    				logger.log("ERROR: File " + file.getAbsolutePath() + " is referenced in siplist, " +
	    						   "but does not exist");
	    				System.out.println("\nDie in der SIP-Liste angegebene Datei " + file.getAbsolutePath() +
	    						" existiert nicht.");
	    				FileUtils.deleteQuietly(new File(tempFolderName));
	    				return "";
	    			}

	    			try {
	    				if (file.isDirectory())
	    					FileUtils.copyDirectoryToDirectory(file, tempDirectory);
	    				else
	    					FileUtils.copyFileToDirectory(file, tempDirectory);
	    				progressManager.copyFilesFromListProgress();
	    			} catch (IOException e) {
	    				logger.log("ERROR: Failed to copy file " + file.getAbsolutePath() + " to folder " +
	    						   tempDirectory.getAbsolutePath(), e);
	    				System.out.println("\nDie in der SIP-Liste angegebene Datei " + file.getAbsolutePath() +
	    						" konnte nicht kopiert werden.");
	    				FileUtils.deleteQuietly(new File(tempFolderName));
	    				return "";
	    			}					
				}				
			}			
		} catch (Exception e) {
			logger.log("ERROR: Failed to read siplist " + sipListFile.getAbsolutePath(), e);
			System.out.println("\nBeim Lesen der SIP-Liste ist ein Fehler aufgetreten. ");
			return "";
		}
		   	
    	return (new File(tempFolderName).getAbsolutePath());    	
    }
    
    /**
     * Returns a name for a temp folder that is not already existing
     * 
     * @return The temp folder name
     */
    private String getTempFolderName() {
		
    	String destinationPath = sipFactory.getDestinationPath();
    	
		if (!new File(destinationPath + File.separator + "temp").exists())
			return destinationPath + File.separator + "temp";
		
		String tempFolderName;
		int i = 0;
		do {
			tempFolderName = "temp_" + i++;						
		} while (new File(destinationPath + File.separator + tempFolderName).exists());
	
		return destinationPath + File.separator + tempFolderName;
	}
    
    /**
     * Displays an overview of the possible arguments
     */
    private void showHelp() {
    	
		System.out.println("");
		System.out.println("");
		System.out.println(SIPBuilder.getProperties().getProperty("ARCHIVE_NAME") + " SIP-Builder v" + Utilities.getSipBuilderVersion());
		System.out.println("");
		System.out.println("Aufruf: java -jar SipBuilder.jar [-source | -filelist | -siplist] -destination [Weitere Optionen]");
		System.out.println("");
		System.out.println("Mögliche Optionen:");
		System.out.println("   -source=\"[Pfad]\"          Angabe eines Quellordners, aus dem die SIPs erstellt werden sollen");
		System.out.println("   -filelist=\"[Pfad]\"        Angabe einer Textdatei, die die Pfade zu den Dateien enthält, aus denen das SIP erstellt werden soll");
		System.out.println("   -siplist=\"[Pfad]\"         Angabe einer XML-Datei, die die Pfade zu den Dateien enthält, aus denen SIPs erstellt werden sollen");
		System.out.println("");
		System.out.println("   -destination=\"[Pfad]\"     Angabe des Zielordners, in dem die SIPs erstellt werden sollen");
		System.out.println("");
		System.out.println("   -default                  Standardrechte verwenden (Standard)");
		System.out.println("   -premis=\"[Pfad]\"          Angabe der Contract Rights durch eine Premis-Datei");
		System.out.println("   -rights=\"[Pfad]\"          Angabe der Contract Rights durch eine vormals per SIP-Builder erstellte Rechte-Datei");
		System.out.println("");
		System.out.println("   -multiple                 Mehrere SIPs aus Unterordnern des Quellordners erstellen (Standard)");
		System.out.println("   -single                   Einzelnes SIP aus dem Quellordner erstellen");
		System.out.println("   -single=\"[Name]\"          Einzelnes SIP mit dem angegebenen Namen aus dem Quellordner erstellen");
		System.out.println("");
		System.out.println("   -collection=\"[Name]\"      SIPs zu einer Lieferung bündeln");
		System.out.println("");
		System.out.println("   -compression              SIPs als komprimierte tgz-Files erstellen (Standard)");
		System.out.println("   -noCompression            SIPs als unkomprimierte tar-Files erstellen");
		System.out.println("");
		System.out.println("   -neverOverwrite           SIPs nicht erstellen, wenn sich im Zielordner bereits ein SIP gleichen Namens befindet (Standard)");
		System.out.println("   -alwaysOverwrite          Bereits existierende SIPs/Lieferungen gleichen Namens im Zielordner ohne Nachfrage überschreiben");
		System.out.println("");
		System.out.println("   -ignoreExtensions");
		System.out.println("       =\"ext1;ext2;ext3...\"  Dateien mit den angegebenen Dateiendungen nicht ins SIP aufnehmen");
		System.out.println("");
		System.out.println("   -help                     Diese Hilfe anzeigen");
    }
	
}
