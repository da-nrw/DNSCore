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

package de.uzk.hki.da.pkg;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class MetsConsistencyChecker.
 */
public class MetsConsistencyChecker implements ConsistencyChecker {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(MetsConsistencyChecker.class);	
	
	/** The package path. */
	String packagePath;
	
	/** The messages. */
	List<String> messages;

	/**
	 * Instantiates a new mets consistency checker.
	 *
	 * @param packagePath the package path
	 */
	public MetsConsistencyChecker(String packagePath) {
		this.packagePath = packagePath;
		messages = new ArrayList<String>();
	}
	
	/**
	 * Check package.
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 * @see MetsConsistencyChecker#checkPackageBasedOnFiles()
	 */
	public boolean checkPackage() throws Exception {
		return checkPackageBasedOnFiles();
	}
	
	/**
	 * Checks the package consistency based on actual files
	 * present in package the on the file system.
	 * 
	 * For every file in the packagePath, a checksum will be computed.
	 * Also a corresponding mets:FLocat element will be extracted and
	 * the expected checksum will be compared to the computed one.
	 *
	 * @return true if a corresponding FLocat element could be found for every
	 * file and the checksums matched, otherwise false
	 * @throws Exception the exception
	 */
	public boolean checkPackageBasedOnFiles() throws Exception {
		
		boolean result = true;
		
		Namespace metsNS = Namespace.getNamespace("mets", "http://www.loc.gov/METS/");
		Namespace xlinkNS = Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink");
		String metsPath = packagePath + "/export_mets.xml";
		
		SAXBuilder builder = new SAXBuilder(false);
		Document doc = builder.build(new File(metsPath));
		
		ExecutorService executor = Executors.newFixedThreadPool(8);
		List<FileChecksumVerifierThread> threads = new ArrayList<FileChecksumVerifierThread>();
		
		String absPackagePath = new File(packagePath).getAbsolutePath();
		
		for (File f:(List<File>) FileUtils.listFiles(new File(packagePath), null, true)){
			
			// skip the METS file itself
			if ("export_mets.xml".equals(f.getName())) {
				continue;
			}
			
			String relPath = f.getAbsolutePath().substring(absPackagePath.length()+1);
			
			logger.debug("Verifying file: {}", relPath);
			
			String xpathExpr = "//mets:file/mets:FLocat[@xlink:href='" + relPath + "']";
			XPath xpath = XPath.newInstance(xpathExpr);
			xpath.addNamespace(metsNS);
			xpath.addNamespace(xlinkNS);
			Element elemFLocat = (Element) xpath.selectSingleNode(doc);
			
			// check if METS contains FLocat element for file
			if (elemFLocat == null) {
				result = false;
				String msg = "Could not find FLocat element in METS metadata: " + relPath;
				logger.error(msg);
				messages.add(msg);
				continue;
			}
			
			Element elemFile = elemFLocat.getParentElement();
			
			String checksum = elemFile.getAttributeValue("CHECKSUM");
			String checksumType = elemFile.getAttributeValue("CHECKSUMTYPE");
			
			// check if required attributes are set
			if (checksum == null) {
				logger.warn("METS File Element in {} does not contain attribute CHECKSUM. File consistency can not be verified.", metsPath);
				continue;
			}
			if (checksumType == null) {
				logger.warn("METS File Element in {} does not contain attribute CHECKSUM TYPE. File consistency can not be verified.", metsPath);
				continue;
			}
			
			logger.debug("Checking with algorithm: {}", checksumType);
			
			// calculate and verify checksum
			checksumType = checksumType.replaceAll("-", "");
			try {
				MessageDigest algorithm = MessageDigest.getInstance(checksumType);
				threads.add(new FileChecksumVerifierThread(checksum, f, algorithm));
			} catch (NoSuchAlgorithmException e) {
				logger.warn("METS File Element in {} contains unknown CHECKSUM TYPE: {}. File consistency can not be verified.", metsPath, checksumType);
				continue;
			}

		}
		
		List<Future<ChecksumResult>> futures = executor.invokeAll(threads);
		
		for (Future<ChecksumResult> future : futures) {
			ChecksumResult cResult = future.get();
			if(!cResult.isSuccess()) {
				result = false;
				logger.error(cResult.getMessage());
				messages.add(cResult.getMessage());
			}
		}
		
		return result;
		
	}

	/**
	 * Checks the package consistency based on the File elements
	 * in the METS file.
	 * 
	 * This assumes that a file must exist for every File element,
	 * which is not the case for delta-packages.
	 *
	 * @return true, if a file could be found for every File element
	 * and the checksums matched, otherwise false
	 * @throws Exception the exception
	 */
	public boolean checkPackageBasedOnMets() throws Exception {
		
		boolean result = true;
	
		Namespace metsNS = Namespace.getNamespace("mets", "http://www.loc.gov/METS/");
		Namespace xlinkNS = Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink");
		String metsPath = packagePath + "/export_mets.xml";
		
		SAXBuilder builder = new SAXBuilder(false);
		Document doc = builder.build(new File(metsPath));
		XPath xpath = XPath.newInstance("//mets:file");
		xpath.addNamespace(metsNS);
		
		@SuppressWarnings("rawtypes")
		List nodes = xpath.selectNodes(doc);
		
		logger.debug("Found {} mets:file elements", nodes.size());
		
		ExecutorService executor = Executors.newFixedThreadPool(8);
		List<FileChecksumVerifierThread> threads = new ArrayList<FileChecksumVerifierThread>();
		
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = nodes.iterator(); iterator.hasNext();) {
			
			Element elem = (Element) iterator.next();
			String checksum = elem.getAttributeValue("CHECKSUM");
			String checksumType = elem.getAttributeValue("CHECKSUMTYPE");
			String path = elem.getChild("FLocat", metsNS).getAttributeValue("href", xlinkNS);
			
			logger.debug("Verifying file: {}", path);
			
			// check if required attributes are set
			if (checksum == null) {
				logger.warn("METS File Element in {} does not contain attribute CHECKSUM. File consistency can not be verified.", metsPath);
				continue;
			}
			if (checksumType == null) {
				logger.warn("METS File Element in {} does not contain attribute CHECKSUM TYPE. File consistency can not be verified.", metsPath);
				continue;
			}
			if (path == null) {
				logger.warn("METS File Element in {} does not contain path.", metsPath);
				continue;
			}
			
			// check if file exists at path
			File file = new File(packagePath + "/" + path);
			if (!file.exists()) {
				result = false;
				String msg = "Could not find file referenced in METS metadata: " + path;
				logger.error(msg);
				messages.add(msg);
				continue;
			}
			
			logger.debug("Checking with algorithm: {}", checksumType);
			
			// calculate and verify checksum
			checksumType = checksumType.replaceAll("-", "");
			try {
				MessageDigest algorithm = MessageDigest.getInstance(checksumType);
				threads.add(new FileChecksumVerifierThread(checksum, file, algorithm));
			} catch (NoSuchAlgorithmException e) {
				logger.warn("METS File Element in {} contains unknown CHECKSUM TYPE: {}. File consistency can not be verified.", metsPath, checksumType);
				continue;
			}
			
		}
		
		List<Future<ChecksumResult>> futures = executor.invokeAll(threads);
		
		for (Future<ChecksumResult> future : futures) {
			ChecksumResult cResult = future.get();
			if(!cResult.isSuccess()) {
				result = false;
				logger.error(cResult.getMessage());
				messages.add(cResult.getMessage());
			}
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see de.uzk.hki.da.utils.ConsistencyChecker#getMessages()
	 */
	public List<String> getMessages() {
		return messages;
	}
	
	/**
	 * Calculate hash.
	 *
	 * @param algorithm the algorithm
	 * @param file the file
	 * @return the string
	 * @throws Exception the exception
	 */
	private static String calculateHash(MessageDigest algorithm, File file) throws Exception {
		
		FileInputStream     fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        DigestInputStream   dis = new DigestInputStream(bis, algorithm);
        
        // read the file and update the hash calculation
        while (dis.read() != -1);

        // get the hash value as byte array
        byte[] hash = algorithm.digest();

        fis.close();
        bis.close();
        dis.close();
        
        return byteArray2Hex(hash);
        
	}
	
	/**
	 * Byte array2 hex.
	 *
	 * @param hash the hash
	 * @return the string
	 */
	private static String byteArray2Hex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        
        String result = formatter.toString();
        formatter.close();
        return result;
    }
	
	/**
	 * The Class FileChecksumVerifierThread.
	 */
	private static class FileChecksumVerifierThread implements Callable<ChecksumResult> {

		/** The expected checksum. */
		private String expectedChecksum;
		
		/** The file. */
		private File file;
		
		/** The algorithm. */
		private MessageDigest algorithm;
		
		/**
		 * Instantiates a new file checksum verifier thread.
		 *
		 * @param expectedChecksum the expected checksum
		 * @param file the file
		 * @param algorithm the algorithm
		 */
		public FileChecksumVerifierThread(String expectedChecksum, File file, MessageDigest algorithm) {
			this.expectedChecksum = expectedChecksum;
			this.file = file;
			this.algorithm = algorithm;
		}
		
		/* (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		public ChecksumResult call() throws Exception {
			String calcChecksum = calculateHash(algorithm, file);
			logger.debug("{} - Expected checksum:\t {}", file.getName(), expectedChecksum);
			logger.debug("{} - Calculated checksum:\t {}", file.getName(), calcChecksum);
			if (calcChecksum.equals(expectedChecksum)) {
				return new ChecksumResult(true, "");
			} else {
				String message = "Checksum validation failed for file: " + file.getName();
				return new ChecksumResult(false, message);
			}
		}
		
	}
	
	/**
	 * The Class ChecksumResult.
	 */
	private static class ChecksumResult {
		
		/** The success. */
		private boolean success;
		
		/** The message. */
		private String message;
		
		/**
		 * Instantiates a new checksum result.
		 *
		 * @param success the success
		 * @param message the message
		 */
		public ChecksumResult(boolean success, String message) {
			this.success = success;
			this.message = message;
		}
		
		/**
		 * Checks if is success.
		 *
		 * @return true, if is success
		 */
		public boolean isSuccess() {
			return success;
		}
		
		/**
		 * Gets the message.
		 *
		 * @return the message
		 */
		public String getMessage() {
			return message;
		}
		
	}

}
