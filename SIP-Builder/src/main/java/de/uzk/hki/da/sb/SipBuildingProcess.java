package de.uzk.hki.da.sb;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;


/**
 * The SIP building procedure is run in its own thread to prevent GUI freezing
 * 
 * @author Thomas Kleinke
 */
public class SipBuildingProcess extends Thread{

	boolean abortRequested = false;
	
//	PARAMS
	boolean alwaysOverwrite;
	boolean skippedFiles;	
	boolean createCollection;
	boolean ignoreZeroByteFiles;
	MessageWriter messageWriter;
	File collectionFolder;
	SIPFactory sf;
	ProgressManager progressManager;
	String sourcePath;
	String destinationPath;
	String collectionName;
	File listCreationTempFolder;
	Feedback returnCode;
	
	public SipBuildingProcess(MessageWriter mw, 
			boolean alwaysOverwrite, 
			boolean skippedFiles, 
			boolean createCollection,
			boolean ignoreZeroByteFiles,
			File collectionFolder, 
			SIPFactory sf, 
			ProgressManager pm, 
			String sourcePath, 
			String destinationPath, 
			String collectionName, 
			File listCreationTempFolder,
			Feedback returnCode){
		
		this.alwaysOverwrite = alwaysOverwrite;
		this.skippedFiles = skippedFiles;
		this.messageWriter = mw;
		this.createCollection = createCollection;
		this.collectionFolder = collectionFolder;
		this.sf = sf;
		this.progressManager = pm;
		this.sourcePath = sourcePath;
		this.destinationPath = destinationPath;
		this.collectionName = collectionName;
		this.listCreationTempFolder = listCreationTempFolder;
		this.ignoreZeroByteFiles = ignoreZeroByteFiles;
		this.returnCode = returnCode;
	}

	/**
	 * Creates one ore more SIPs as specified by the user
	 */
	public void run() {

		alwaysOverwrite = false;
		skippedFiles = false;				 
		messageWriter.resetZeroByteFiles();

		if (createCollection) {
			collectionFolder = new File(new File(destinationPath), collectionName);

			if (collectionFolder.exists()) {
				MessageWriter.UserInput answer =
						messageWriter.showCollectionOverwriteDialog("Eine Lieferung mit dem Namen \"" + collectionName + "\"" + 
								"existiert bereits.\n" +
								"Möchten Sie die bestehende Lieferung überschreiben?");

				switch (answer) {
				case YES:
					FileUtils.deleteQuietly(collectionFolder);
					break;
				case NO:
					progressManager.abort();
					return;
				default:
					break;
				}

			}

			new File(collectionFolder, "data").mkdirs();
			sf.setCollectionFolder(collectionFolder);
		}

		HashMap<File, String> folderListWithNames = sf.createFolderList(sourcePath);
		List<File> folderList = new ArrayList<File>();
		for(File f : folderListWithNames.keySet()) {
			folderList.add(f);
		}
		if (sf.initializeProgressManager(folderList, collectionName) != Feedback.SUCCESS) {
			messageWriter.showMessage("Das SIP konnte nicht erstellt werden.\n\n" +
					"Der angegebene Ordner existiert nicht mehr. ", JOptionPane.ERROR_MESSAGE);
			sf.abortSipBuilding();
			return;
		}

		int id = 0;
		for (File folder : folderListWithNames.keySet()) {
			returnCode = sf.buildSIP(id, folder, folderListWithNames.get(folder));
			sf.setReturnCode(returnCode);
			
			
			if (returnCode != Feedback.SUCCESS && returnCode != Feedback.DELETE_TEMP_FOLDER_WARNING)
				sf.abortSipBuilding();

			switch(returnCode) {
			case COPY_ERROR:
				messageWriter.showMessage("Das SIP \"" + folder.getName() + "\" konnte nicht erstellt werden.\n\n" +
						"Während des Kopiervorgangs ist ein Fehler aufgetreten.", JOptionPane.ERROR_MESSAGE);
				return;
			case ZERO_BYTES_ERROR:
				messageWriter.showZeroByteFileMessage();
				return;
			case PREMIS_ERROR:
				messageWriter.showMessage("Das SIP \"" + folder.getName() + "\" konnte nicht erstellt werden.\n\n" +
						"Während der Erstellung der Premis-Datei ist ein Fehler aufgetreten.", JOptionPane.ERROR_MESSAGE);
				return;
			case BAGIT_ERROR:
				messageWriter.showMessage("Das SIP \"" + folder.getName() + "\" konnte nicht erstellt werden.\n\n" +
						"Während der Erzeugung des Bags ist ein Fehler aufgetreten.", JOptionPane.ERROR_MESSAGE);
				return;
			case ARCHIVE_ERROR:
				messageWriter.showMessage("Das SIP \"" + folder.getName() + "\" konnte nicht erstellt werden.\n\n" +
						"Während der tgz-Archivierung ist ein Fehler aufgetreten.", JOptionPane.ERROR_MESSAGE);
				return;
			case DELETE_TEMP_FOLDER_WARNING:
				messageWriter.showMessage("Während der Bereinigung temporärer Daten ist ein Fehler aufgetreten.\n\n" +
						"Bitte löschen Sie nicht benötigte verbleibende Verzeichnisse\n" +
						"im Ordner \"" + destinationPath + "\" manuell.", JOptionPane.ERROR_MESSAGE);
				break;
			case MOVE_TO_COLLECTION_FOLDER_ERROR:
				messageWriter.showMessage("Das SIP \"" + folder.getName() + "\" konnte der Lieferung nicht hinzugefügt werden.", JOptionPane.ERROR_MESSAGE);
				return;
			case ABORT:
				return;
			default:
				break;
			}

			id++;
		}
		
		if (listCreationTempFolder != null && listCreationTempFolder.exists())
			FileUtils.deleteQuietly(listCreationTempFolder);

		if (createCollection) {
			progressManager.startJob(-1);
			if (sf.createBag(-1, collectionFolder) == Feedback.BAGIT_ERROR)
				messageWriter.showMessage("Die Lieferung \"" + collectionName + "\" konnte nicht erstellt werden.\n\n" +
						"Während der Erzeugung des Bags ist ein Fehler aufgetreten.", JOptionPane.ERROR_MESSAGE);
		}

		progressManager.createSuccessMessage(skippedFiles);

		if (ignoreZeroByteFiles && messageWriter.getZeroByteFiles().size() > 0) {
			String message = "WARNING: Found zero byte files:";
			for (String s : messageWriter.getZeroByteFiles()) {
				message += "\n";
				message += s;						 
			}
			messageWriter.showZeroByteFileMessage();
		}
	}

	public void abort() {
		abortRequested = true;
	}

	public boolean isAborted() {
		return abortRequested;
	}
}
