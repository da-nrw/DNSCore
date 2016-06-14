/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2015 LVRInfoKom
  Landschaftsverband Rheinland

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


package de.uzk.hki.da.format;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.FormatMapping;
import de.uzk.hki.da.model.JHoveParameterMapping;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.IOTimeoutException;
import de.uzk.hki.da.utils.ProcessInformation;
import de.uzk.hki.da.utils.StringUtilities;

/**
 * 
 * @author Daniel M. de Oliveira
 * @author Eugen Trebunski
 *
 */
public class JhoveMetadataExtractor implements MetadataExtractor {

	private static final Logger logger = LoggerFactory.getLogger(JhoveMetadataExtractor.class);
	private List<JHoveParameterMapping> possibleOptions=null;
	private List<FormatMapping> pronomMimetypeList=null;
	
	/**
	 * {@link #pronomMimetypeList} can be long, binary comparator is useful to achieve better performance O(log(N)) instead of O(N)
	 */
	static Comparator<FormatMapping> formatMappingComparator=new Comparator<FormatMapping>(){
		@Override public int compare(FormatMapping o1, FormatMapping o2) {
			return o1.getPuid().compareToIgnoreCase(o2.getPuid());
		}};
	
	
	private static final int _6_MINUTES = 360000; // ms
	private static final String JHOVE_CONF = "conf/jhove.conf";
	private static final long JHOVE_TIMEOUT = _6_MINUTES;
	private static final String jhoveFolder = "jhove";
	private static final String JHOVE_BIN = "jhove";
	private static final String SHELL = "/bin/sh";
	
	private CommandLineConnector cli;
	
	private boolean connectabilityProved=false;
	
	
	/**
	 * Scans a file with jhove and extracts technical metadata to a xml file.
	 * Tries it a second time if the first time fails.
	 * The jhove result will be validated and can cause JHoveValidationException if jhove report invalidity.
	 * 
	 * @throws ConnectionException
	 *             when timeout limit reached two times.
	 * @throws IOException
	 */
	public void extract(File file, File extractedMetadata, String expectedPUID)
			throws ConnectionException, IOException {

		if (!connectabilityProved)
			throw new IllegalStateException("Make sure you run isExecutable first.");

		if (cli == null)
			throw new IllegalStateException("cli not set");
		if (!file.exists())
			throw new FileNotFoundException("Missing file or directory: " + file);
		if (!extractedMetadata.getParentFile().exists())
			throw new IllegalArgumentException("ParentFolder " + extractedMetadata.getParentFile() + " must exist in order to create " + extractedMetadata);

		int retval = 0;
		String mimeType=getMimeTypeForPronom(expectedPUID);
		String typeOptions = getJHoveOptionForMimeType(mimeType);
		try {
			retval = execCMD(jhoveCmd(extractedMetadata, makeFilePath(file), typeOptions));
		} catch (IOTimeoutException timeout) {
			logger.warn(timeout.getMessage());
			retval = 1;
		} catch (IOException e) {
			logger.warn(e.getMessage());
			retval = 1;
		}
		if (retval != 0) {
			logger.info("Problem during extracting technical metadata. Will retry without parsing the whole file.");

			retval = 0;
			try {
				retval = execCMD(jhoveCmdSkipWholeFileParsing(extractedMetadata , makeFilePath(file), typeOptions));
			} catch (IOTimeoutException timeout) {
				throw new ConnectionException("Second call to JHOVE ended with possible timeout (the 2nd time already).", timeout);
			} catch (IOException e) {
				throw new IOException("Second call to JHOVE ended with IOError (the 2nd time already).", e);
			}
		}
		if (retval != 0){
			throw new ConnectionException("Recieved not null return value from jhove.");
		}
		
		JhoveResult jhResult=null;
		try {
			jhResult = JhoveResult.parseJHoveXML(extractedMetadata.getAbsolutePath());
		} catch (Exception e) {
			logger.error("JHove outputfile(" + extractedMetadata.getAbsolutePath() + ") not interpretable: " + e.getMessage());
			//throw new JHoveValidationException("JHove Output(" + extractedMetadata.getAbsolutePath() + ") not interpretable: " + e.getMessage(), e);
		}

		/*
		 * The JHove result is not taken in count because it often alert about errorneus file, but in fact these file can be processed/converted.
		 * Second bug in jhove is: jhove can't handle good enough files containing whitespaces in their names (jira: DANRW-1415)
		 */
		if (jhResult==null || !jhResult.isValid()){
			logger.warn("JHove say " + file + " (PUID: "+expectedPUID+" MIMEType:"+mimeType+" JHove Parameter:"+typeOptions+") is not valid: " + (jhResult==null?"null":jhResult));
			//throw new JHoveValidationException("JHove say " + file + " (PUID: "+expectedPUID+" MIMEType:"+mimeType+" JHove Parameter:"+typeOptions+") is not valid: " + (jhResult==null?"null":jhResult));
		}
	}
	
	


	private String makeFilePath(File file) {
		String filePath;
		filePath=file.getAbsolutePath();
		if (StringUtilities.checkForWhitespace(filePath))
			filePath = "\"" + filePath + "\"";
		return filePath;
	}


	private String[] jhoveCmd(File extractedMetadata, String filePath, String typeOptions) {
		return new String[] {
                SHELL, JHOVE_BIN, "-c", JHOVE_CONF, "-h", "XML",typeOptions,
                filePath, "-o", extractedMetadata.getAbsolutePath() };
	}


	private String[] jhoveCmdSkipWholeFileParsing(File extractedMetadata, String filePath, String typeOptions) {
		return new String[] {
                SHELL, JHOVE_BIN, "-c", JHOVE_CONF, "-h", "XML", typeOptions,
                "-s", // skip parsing of the whole file
                filePath, "-o", extractedMetadata.getAbsolutePath() };
	}
	
	
	private int execCMD(String cmd[]) throws ConnectionException, IOException {
		ProcessInformation pi=null;
		pi = cli.runCmdSynchronously(cmd,
                new File(jhoveFolder),JHOVE_TIMEOUT);
		if (pi==null) {
			throw new ConnectionException("Call to JHOVE terminated with empty ProcessInformation");
		}
		if (pi.getExitValue()!=0) {
			logger.debug("StdOut from jhove cmd: "+pi.getStdOut());
			logger.debug("StdErr from jhove cmd: "+pi.getStdErr());
		}
		return pi.getExitValue();
	}

	
	@Override
	public boolean isConnectable() {
		
		System.out.print("INFO: CHECKING - "+this.getClass().getName()+".isConnectable() ....");
		ProcessInformation pi=null;
		try {
			pi = cli.runCmdSynchronously(new String[] {
			        "/bin/sh", "jhove", "-c", JHOVE_CONF, "--version" },
			        new File(jhoveFolder),JHOVE_TIMEOUT);
		} catch (IOException e) {
			return false;
		}
		if (pi.getStdOut().split("\\(Rel")[0].equals("Jhove ")){
			System.out.println(" .... OK");
			connectabilityProved=true;
			return true;
		}else {
			System.out.println(" .... FAIL");
			return false;
		}
	}
	

	public CommandLineConnector getCli() {
		return cli;
	}


	public void setCli(CommandLineConnector cli) {
		this.cli = cli;
	}

	
	/**
	 * This method returns additional type specific jhove parameter for specific mime type. If no no parameter for given mime type exists it will return empty string.
	 * 
	 * 
	 * @author Eugen Trebunski
	 * @param puid
	 * @return mimetype
	 */
	String getJHoveOptionForMimeType(String mimeType){
		mimeType=mimeType.trim();
		if(mimeType.isEmpty())
			return "";
		List<JHoveParameterMapping> mappingsConf=getJhoveParameterMappingTable();
		for(JHoveParameterMapping iterMapping:mappingsConf)
			if(iterMapping.getMime_type().equalsIgnoreCase(mimeType))
				return iterMapping.getMap_prameter();
		return "";
	}
	
	/**
	 * Get the actual table of jhove parameter mapping (mimetype to jhove-parameter), which is readed by hibernate and saved in {@link #possibleOptions}-Datastructure. 
	 * After first call of this method and after {@link #possibleOptions}-Datastructure is initialized, only the {@link #possibleOptions}
	 * will be return without use of hibernate.
	 * 
	 * @author Eugen Trebunski
	 * @return {@link #possibleOptions}-Datastructure
	 */
	private List<JHoveParameterMapping> getJhoveParameterMappingTable() {
		synchronized (this) {
			if (possibleOptions != null)
				return possibleOptions;
			Session session = HibernateUtil.openSession();
			session.beginTransaction();
			// List<Job> l = null;

			possibleOptions = session.createQuery("FROM JHoveParameterMapping").setReadOnly(true).list();
			session.close();
			return possibleOptions;
		}
	}
	
	/**
	 * This method returns the mime type for specific pronom id. If no FormatMapping-Object  for given pronom id exists, it will return empty string.
	 * This method use quicksearch to find the right FormatMapping very fast.
	 * 
	 * 
	 * @author Eugen Trebunski
	 * @param puid
	 * @return mimetype
	 */
	private String getMimeTypeForPronom(String puid) {
		puid = puid.trim();
		if (puid.isEmpty())
			return "";
		FormatMapping searchFor = new FormatMapping();
		searchFor.setPuid(puid);
		int indexOfSearchFor = Collections.binarySearch(getPronomTable(), searchFor, formatMappingComparator);
		if (indexOfSearchFor < 0) {
			logger.warn("Given pronom id(" + puid + ") is not in database");
			// throw new RuntimeException("Given pronom id("+puid+") is not in database");
			return "";
		}
		return getPronomTable().get(indexOfSearchFor).getMime_type();
	}
	
	/**
	 * Get the actual table of prnonom id's, which is readed by hibernate and saved in {@link #pronomMimetypeList}-Datastructure. 
	 * After first call of this method and after {@link #pronomMimetypeList}-Datastructure is initialized, only the {@link #pronomMimetypeList}
	 * will be return without use of hibernate.
	 * <br/>
	 * After {@link #pronomMimetypeList}-Datastructure is initialized by hibernate, it will be sorted before return, to allow fast searching in future accesses by using quicksearch.
	 *
	 * @author Eugen Trebunski
	 * @return {@link #pronomMimetypeList}-Datastructure
	 */
	private List<FormatMapping>  getPronomTable() {
		synchronized (this) {
		
		if(pronomMimetypeList!=null)
			return pronomMimetypeList;
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		//List<Job> l = null;
	
		pronomMimetypeList = session.createQuery("FROM FormatMapping")
							.setReadOnly(true).list();
		session.close();

		Collections.sort(pronomMimetypeList, formatMappingComparator);
		}
		return pronomMimetypeList;
	}

	public List<JHoveParameterMapping> getPossibleOptions() {
		return possibleOptions;
	}
	public void setPossibleOptions(List<JHoveParameterMapping> possibleOptions) {
		this.possibleOptions = possibleOptions;
	}
	public List<FormatMapping> getPronomMimetypeList() {
		return pronomMimetypeList;
	}
	public void setPronomMimetypeList(List<FormatMapping> pronomMimetypeList) {
		this.pronomMimetypeList = pronomMimetypeList;
	}
}
