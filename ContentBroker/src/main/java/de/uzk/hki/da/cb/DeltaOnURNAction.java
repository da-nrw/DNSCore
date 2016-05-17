package de.uzk.hki.da.cb;

import static de.uzk.hki.da.utils.C.ERROR_MSG_DURING_FILE_FORMAT_IDENTIFICATION;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.PreconditionsNotMetException;
import de.uzk.hki.da.core.SubsystemNotAvailableException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.format.FileFormatException;
import de.uzk.hki.da.format.FileFormatFacade;
import de.uzk.hki.da.format.FileWithFileFormat;
import de.uzk.hki.da.format.FileWithFileFormatImpl;
import de.uzk.hki.da.metadata.MetsMetadataStructure;
import de.uzk.hki.da.model.Document;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.ObjectPremisXmlReader;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.StringUtilities;

public class DeltaOnURNAction extends AbstractAction {

	private int timeOut = 20000;

	private static final String PREMIS_XML = "premis.xml";
	private FileFormatFacade fileFormatFacade;

	@Override
	public void checkConfiguration() {
	}

	@Override
	public void checkPreconditions() {
		File file = premisFile();
		if (!file.exists()) {
			throw new PreconditionsNotMetException("Must exist: " + PREMIS_XML);
		}
	}

	@Override
	public void rollback() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean implementation() throws FileNotFoundException, IOException, UserException, RepositoryException, JDOMException, ParserConfigurationException, SAXException,
			SubsystemNotAvailableException {
		if (Boolean.FALSE.equals(o.getContractor().isDeltaOnUrn())){
			logger.debug("DeltaOnUrn of User is FALSE");
			return true;
		}
		
		if (o.isDelta()) {
			logger.debug("Object is allready Delta");
			return true;
		}

		String newUrn = getUrn();
		if (newUrn == null) {
			logger.debug("No URN specified, no Delta assumed");
			return true;
		}

		boolean ret = false;
		Session session = openSession();
		try {
			ret = deltaOnUrn(session, newUrn);
		} finally {
			session.close();
		}
		return ret;
	};

	public boolean deltaOnUrn(Session session, String newUrn) throws FileNotFoundException, IOException, UserException, RepositoryException, JDOMException, ParserConfigurationException,
			SAXException, SubsystemNotAvailableException {
		String queryStr = "select o from Object o where o.urn = ?1 and user_id = ?2";
		Query query = session.createQuery(queryStr);


		query.setParameter("1", newUrn);
		query.setParameter("2", this.o.getContractor().getId());

		Transaction transi = session.beginTransaction();

		@SuppressWarnings("unchecked")
		List<Object> obbis = query.list();
		if (obbis.size() == 0) {
			logger.debug("No old object with URN: " + newUrn + " found, no Delta assumed. ");
			return true;
		}

		if (obbis.size() > 1) {
			throw new RuntimeException("More than one Object with same urn found.");
		}

		Object oldObject = obbis.get(0);
		logger.debug("Object '" + oldObject.getOrig_name()  + "' :" + oldObject.getIdentifier()
				+ " with same URN '"+ oldObject.getUrn() + "' found");

		while(oldObject.getObject_state() != Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow)
		{
			logger.debug("Waiting for '" + oldObject.getOrig_name() + "' :" + oldObject.getIdentifier() 
					+ " getting in archived state");
			this.delay();
			session.refresh(oldObject);
		}

		oldObject.setObject_state(Object.ObjectStatus.InWorkflow);
		session.update(oldObject);
		session.flush();
		
		List<de.uzk.hki.da.model.Package> packs = o.getPackages();
		if (packs.size() > 1) {
			throw new RuntimeException("More than one Package of a new Object found");
		}

		int max = 0;
		for (de.uzk.hki.da.model.Package pack : oldObject.getPackages()){
			max = Math.max(Integer.parseInt(pack.getName()), max);	
		}
		
		de.uzk.hki.da.model.Package myPack = packs.get(0);
		de.uzk.hki.da.model.Package deltaPack = new Package();
		
		deltaPack.setName(Integer.toString(max  + 1));
		deltaPack.setContainerName(myPack.getContainerName());
		
		j.setObject(oldObject);
		
		o.getPackages().remove(myPack);
		logger.debug("Old Object now contains " + o.getPackages().size() + " Packages");
		
		session.delete(myPack);
		session.update(j);
		session.update(o);
		
		oldObject.getPackages().add(deltaPack);
		logger.debug("Found Object now contains " + oldObject.getPackages().size() + " Packages");
		for (de.uzk.hki.da.model.Package ppp : oldObject.getPackages()){
			logger.debug("Found Object contains Package: " + ppp.getContainerName());
		}
		
		session.update(oldObject);
		
		this.DELETEOBJECT = true;
		
		File oldFile = wa.objectPath().toFile();
		File newFile = wa.objectPathFor(oldObject).toFile();
		
		oldFile.renameTo(newFile);
		
		transi.commit();

		return true;
	};

	private String getUrn() throws FileNotFoundException, IOException, JDOMException {
		File file = premisFile();

		String urn = extractURNFromPremisFile(file);
		logger.debug("URN from premis: " + urn);
		if (StringUtilities.isSet(urn)) {
			return urn;
		}

		File metsFile = searchMetsFile();
		if (metsFile == null) {
			return null;
		}

		List<Document> dummyDocs = new ArrayList<Document>();
		MetsMetadataStructure mms = new MetsMetadataStructure(wa.dataPath(), metsFile, dummyDocs);

		urn = mms.getUrn();
		logger.debug("URN from mets: " + urn);
		return urn;
	}

	private File searchMetsFile() throws FileNotFoundException, IOException {
		File baseDir = wa.dataPath().toFile();
		File[] files = baseDir.listFiles();

		List<FileWithFileFormatImpl> filesToScan = new ArrayList<FileWithFileFormatImpl>();
		for (int fff = 0; fff < files.length; fff++) {
			File fily = files[fff];
			if (!fily.isDirectory()) {
				FileWithFileFormatImpl fileToScan = new FileWithFileFormatImpl();
				String fileName = fily.getName();
				fileToScan.setPath(new RelativePath(fileName));
				filesToScan.add(fileToScan);
			}
		}

		List<FileWithFileFormat> scannedFiles = null;
		try {
			scannedFiles = fileFormatFacade.identify(wa.dataPath(), filesToScan, true);
		} catch (FileFormatException e) {
			throw new RuntimeException(ERROR_MSG_DURING_FILE_FORMAT_IDENTIFICATION, e);
		}

		for (int fff = 0; fff < scannedFiles.size(); fff++) {
			FileWithFileFormat fileWF = scannedFiles.get(fff);
			String subF = fileWF.getSubformatIdentifier();
			// String fPuid = fileWF.getFormatPUID();

			if (C.SUBFORMAT_IDENTIFIER_METS.equals(subF)) {
				return fileWF.getPath().toFile();
			}
		}

		return null;
	}

	private File premisFile() {
		File file = Path.make(wa.dataPath(), PREMIS_XML).toFile();
		return file;
	}

	private static final String extractURNFromPremisFile(File premisFile) {

		Object premisObject = null;
		try {
			premisObject = new ObjectPremisXmlReader().deserialize(premisFile);
			if (premisObject == null)
				throw new Exception("Premis object must not be null after deserialization.");
		} catch (Exception e) {
			// Deserializing the PREMIS file is already checked in
			// unpack-action, where a
			// user error gets thrown. So we consider this here a
			// merely technical error.
			throw new RuntimeException("Couldn't deserialize: " + premisFile, e);
		}
		return premisObject.getUrn();
	}

	public FileFormatFacade getFileFormatFacade() {
		return fileFormatFacade;
	}

	public void setFileFormatFacade(FileFormatFacade fileFormatFacade) {
		this.fileFormatFacade = fileFormatFacade;
	}

	private void delay(){
		try {
			Thread.sleep(timeOut); // to prevent unnecessary small intervals when checking
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
