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

package de.uzk.hki.da.grid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.IRODSAccount.AuthScheme;
import org.irods.jargon.core.connection.IRODSCommands;
import org.irods.jargon.core.connection.SettableJargonProperties;
import org.irods.jargon.core.connection.auth.AuthResponse;
import org.irods.jargon.core.exception.DataNotFoundException;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.packinstr.TransferOptions;
import org.irods.jargon.core.pub.CollectionAO;
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO;
import org.irods.jargon.core.pub.DataAOHelper;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.EnvironmentalInfoAO;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSAccessObjectFactoryImpl;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.IRODSFileSystemAO;
import org.irods.jargon.core.pub.IRODSGenQueryExecutor;
import org.irods.jargon.core.pub.ResourceAO;
import org.irods.jargon.core.pub.RuleProcessingAO;
import org.irods.jargon.core.pub.RuleProcessingAO.RuleProcessingType;
import org.irods.jargon.core.pub.RuleProcessingAOImpl;
import org.irods.jargon.core.pub.domain.AvuData;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.pub.domain.DelayedRuleExecution;
import org.irods.jargon.core.pub.domain.Resource;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.query.IRODSGenQueryBuilder;
import org.irods.jargon.core.query.IRODSGenQueryFromBuilder;
import org.irods.jargon.core.query.IRODSQueryResultSetInterface;
import org.irods.jargon.core.query.MetaDataAndDomainData;
import org.irods.jargon.core.query.QueryConditionOperators;
import org.irods.jargon.core.query.RodsGenQueryEnum;
import org.irods.jargon.core.rule.IRODSRuleExecResult;
import org.irods.jargon.core.rule.IRODSRuleParameter;
import org.irods.jargon.core.transfer.DefaultTransferControlBlock;
import org.irods.jargon.core.transfer.TransferControlBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import de.uzk.hki.da.utils.PasswordUtils;




/**
 * The IrodsSystemConnector.
 * Centralized Access Class to functions of the iRODS DataGrid. 
 * Connects to an existing iRODS Data Server in Client mode and provides
 * set of methods needed to work with an iRODS Server. 
 * 
 * Most of the methods being provided here could be used by having just a clean installation 
 * of iRODS Server.  
 * 
 * @author Jens Peters
 *
 */
public class IrodsSystemConnector {
	
	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(IrodsSystemConnector.class);

	/** The irods account. */
	private IRODSAccount irodsAccount;
	
	/** The irods file system. */
	private IRODSFileSystem irodsFileSystem;
	
	/** The irods commands. */
	private IRODSCommands irodsCommands;
	
	
	/** The host. */
	private String host;
	
	/** The set pam mode. */
	private boolean setPamMode = false;
	
	/** The key store. */
	private String keyStore ;
	
	/** The trust store. */
	private String trustStore;
	
	/** The key store password. */
	private String keyStorePassword;
	
	/** The zone. */
	private String zone;
	
	/** The default storage. */
	private String defaultStorage;

	/** The port. */
	private int port = 1247;
	
	/**
	 * Inits the iRODS DataGrid System Connection via Jargon.
	 *
	 * @param user the user
	 * @param password the password
	 * @param host the host
	 * @param zone the zone
	 * @param defaultStorage the default storage
	 * @author Jens Peters
	 */
	public IrodsSystemConnector(String user, String password, String host, String zone,
			String defaultStorage) {

		this.setHost(host);
		this.setZone(zone);
		this.setDefaultStorage(defaultStorage);
		this.irodsAccount = new IRODSAccount(host, this.getPort(), user, PasswordUtils.decryptPassword(password),
				"/" + zone + "/", zone, defaultStorage);
		
	}
	
	/**
	 * Instantiates a new irods system connector.
	 *
	 * @param user the user
	 * @param password the password
	 */
	public IrodsSystemConnector(String user, String password) {
		this.irodsAccount = new IRODSAccount(this.getHost(),this.getPort(), user, PasswordUtils.decryptPassword(password),
				"/" + this.getZone() + "/", this.getZone(), this.getDefaultStorage());
	}
	
	/**
	 * Gets the default storage.
	 *
	 * @return the default storage
	 */
	public String getDefaultStorage() {
		return defaultStorage;
	}

	/**
	 * Sets the default storage.
	 *
	 * @param defaultStorage the new default storage
	 */
	public void setDefaultStorage(String defaultStorage) {
		this.defaultStorage = defaultStorage;
	}

	/**
	 * Gets the host.
	 *
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Sets the host.
	 *
	 * @param host the new host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port.
	 *
	 * @param port the new port
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * Gets the zone.
	 *
	 * @return the zone
	 */
	public String getZone() {
		return zone;
	}

	/**
	 * Sets the zone.
	 *
	 * @param zone the new zone
	 */
	public void setZone(String zone) {
		this.zone = zone;
	}

	/**
	 * Gets the gen query executor.
	 *
	 * @return the gen query executor
	 */
	private IRODSGenQueryExecutor getGenQueryExecutor() {
		IRODSGenQueryExecutor iqex = null;
		try {
			return this.getIRODSAccessObjectFactory()
					.getIRODSGenQueryExecutor(irodsAccount);
		} catch (JargonException e) {
			logger.error("error getting AccessObjectFactory: "+ e.getMessage());
		}
		return iqex;
		
	}
	

	/**
	 * Returns the connection state to the the iRODS DataGrid.
	 *
	 * @return true, if is connected
	 * @author Jens Peters
	 */
	public boolean isConnected() {
		if (irodsCommands==null) return false;
		return irodsCommands.isConnected();
	
	}
	
	

	
	/**
	 * Connect to the the iRODS DataGrid.
	 *
	 * @return true, if successful
	 * @author Jens Peters
	 */
	public boolean connect() {
			logger.debug("Establishing connection to the iRODS DataGrid now!");
			try {
				irodsFileSystem = IRODSFileSystem.instance();
				
				if (setPamMode) {
					System.setProperty("javax.net.ssl.keyStore", keyStore);
					System.setProperty("javax.net.ssl.keyStorePassword",  PasswordUtils.decryptPassword(keyStorePassword));
					System.setProperty("javax.net.ssl.trustStore", trustStore);
					System.setProperty("javax.net.ssl.trustStorePassword", PasswordUtils.decryptPassword(keyStorePassword));
					logger.debug("PAM Auth activated !");
					
					// pre 3.3 iRODS needs to set the Socket flush, jp 
					SettableJargonProperties jargonProperties = new SettableJargonProperties();
					jargonProperties.setForcePamFlush(true);
					irodsFileSystem.getIrodsSession().setJargonProperties(jargonProperties);
					
					
					irodsAccount.setAuthenticationScheme(AuthScheme.PAM);	
					EnvironmentalInfoAO environmentalInfoAO = irodsFileSystem
					.getIRODSAccessObjectFactory().getEnvironmentalInfoAO(
							irodsAccount);
					
					AuthResponse authResponse = environmentalInfoAO.getIRODSProtocol()
							.getAuthResponse();
					irodsAccount = authResponse.getAuthenticatedIRODSAccount();
					logger.debug("PAM Auth response is " + authResponse.isSuccessful());
				} 
				
				irodsCommands = irodsFileSystem.getIrodsSession().currentConnection(irodsAccount);
				boolean ret = irodsCommands.isConnected();
				
				logger.debug("Connection state: " + ret);
				return ret;
			} catch (JargonException e) {
				logger.error("Could not reconnect to the iRODS Data Grid server called: " + irodsAccount.getHost() +" recieved " +e.getUnderlyingIRODSExceptionCode() + " caused by " +e.getCause());
				return false;
			}
	}
	
	/**
	 * Logoff. Logs off the iRODS DataGrid
	 * @author Jens Peters
	 * eats all exceptions. should be called to clean up ressources at the iRODS Server 
	 */
	public void logoff() {
		
		irodsFileSystem.closeAndEatExceptions(irodsAccount);
	}

	/**
	 * Gets the iRODS access object factory.
	 *
	 * @return the iRODS access object factory
	 * @author Jens Peters
	 */
	public IRODSAccessObjectFactory getIRODSAccessObjectFactory() {
		if (irodsFileSystem == null) {
			throw new IrodsRuntimeException(
					"No IRODSFileSystem set, cannot obtain the IRODSAccessObjectFactory : connection down? connect first!");
		}
		try {
			return irodsFileSystem.getIRODSAccessObjectFactory();
		} catch (JargonException ex) {
			logger.error("exception getting IRODSAccessObjectFactory");
			throw new IrodsRuntimeException(
					"exception getting IRODSAccessObjectFactory : "
							+ ex.getUnderlyingIRODSExceptionCode(), ex);
		}
	}

	/**
	 * Gets the datobject Access Object.
	 *
	 * @return the datobject ao
	 * @throws IrodsRuntimeException the irods runtime exception
	 * @author Jens Peters
	 */
	public DataObjectAO getDataObjectAO() throws IrodsRuntimeException {
		if (irodsFileSystem == null){
			throw new IrodsRuntimeException(
					"No IRODSFileSystem set, cannot obtain the DataObjectAO");
		}
		if (!isConnected()) {
			connect();
		}
		
		try {
			IRODSAccessObjectFactory accessObjectFactory = getIRODSAccessObjectFactory();
			return accessObjectFactory.getDataObjectAO(irodsAccount);

		} catch (IrodsRuntimeException e) {
			logger.error("exception getting DataObjectAO");
			throw new IrodsRuntimeException("exception getting DataObjectAO", e);
		} catch (JargonException e) {
			logger.error("exception getting DataObjectAO");
			throw new IrodsRuntimeException("exception getting DataObjectAO: "
					+ e.getUnderlyingIRODSExceptionCode(), e);
		}

	}
	

	
	/**
	 * Gets data transfer operations object.
	 *
	 * @return the data transfer operations object
	 * @author Sebastian Cuy
	 */
	public DataTransferOperations getDataTransferOperations() {
		
		if (irodsFileSystem == null){
			throw new IrodsRuntimeException(
					"No IRODSFileSystem set, cannot obtain the DataObjectAO");
		}
		if (!isConnected()) {
			connect();
		}
		
		try {
			IRODSAccessObjectFactory accessObjectFactory = getIRODSAccessObjectFactory();
			return accessObjectFactory.getDataTransferOperations(irodsAccount);

		} catch (IrodsRuntimeException e) {
			logger.error("exception getting DataObjectAO");
			throw new IrodsRuntimeException("exception getting DataObjectAO", e);
		} catch (JargonException e) {
			logger.error("exception getting DataObjectAO");
			throw new IrodsRuntimeException("exception getting DataObjectAO: "
					+ e.getUnderlyingIRODSExceptionCode(), e);
		}
		
	}
	
	
	/**
	 * Checks whether collection exists in logical file space.
	 *
	 * @param path logical iRODS path
	 * @return true if file exists, false otherwise.
	 * @author Daniel M. de Oliveira
	 */
	public boolean collectionExists(String path){
		IRODSAccessObjectFactory accessObjectFactory = getIRODSAccessObjectFactory();
		CollectionAO ao;
		try {
			ao = accessObjectFactory.getCollectionAO(irodsAccount);
			ao.findByAbsolutePath(path);
		} catch (DataNotFoundException e){
			logger.debug("Collection doesn't exist: "+path);
			return false;
		} catch (FileNotFoundException e){
			logger.debug("Collection doesn't exist: "+path);
			return false;
		}  catch (JargonException e) {
			throw new IrodsRuntimeException("JargonException "
					+ e.getUnderlyingIRODSExceptionCode(), e);
		}
		logger.debug("Collection exists: "+path);
		return true;
	}
	
	/**
	 * Checks whether file exists in logical file space.
	 *
	 * @param path logical iRODS path
	 * @return true if file exist, false otherwise.
	 * @author Jens Peters
	 */
	public boolean fileExists(String path){
		
		boolean ret = false;
		try {
			IRODSAccessObjectFactory accessObjectFactory = IRODSAccessObjectFactoryImpl
			.instance(irodsFileSystem.getIrodsSession());
			IRODSFileFactory irodsFileFactory = accessObjectFactory
			.getIRODSFileFactory(irodsAccount);
			IRODSFile irodsFile = irodsFileFactory
			.instanceIRODSFile(path);
			ret = irodsFile.exists();
			} catch (JargonException e) {
			throw new IrodsRuntimeException("JargonException "
					+ e.getUnderlyingIRODSExceptionCode(), e);
		}
		logger.debug("File exists: "+path + " " + ret);
		return ret;
	}
	

	/**
	 * Gets the resource AccessObject.
	 *
	 * @return the resource ao
	 * @throws IrodsRuntimeException the irods runtime exception
	 * @author Jens Peters
	 */
	public ResourceAO getResourceAO() throws IrodsRuntimeException {
		if (irodsFileSystem == null) {
			throw new IrodsRuntimeException(
					"No IRODSFileSystem set, cannot obtain the DataObjectAO - not connected anymore?");
		}
		if (!isConnected()) {
			connect();
		}
		try {
			IRODSAccessObjectFactory accessObjectFactory = getIRODSAccessObjectFactory();
			return accessObjectFactory.getResourceAO(irodsAccount);

		} catch (IrodsRuntimeException e) {
			logger.error("exception getting ResourceAO");
			throw new IrodsRuntimeException("exception getting ResourceAO", e);
		} catch (JargonException e) {
			logger.error("exception getting ResourceAO");
			throw new IrodsRuntimeException("exception getting ResourceAO: "
					+ e.getUnderlyingIRODSExceptionCode(), e);
		}

	}
	
	

	/**
	 * Execute rule the given rule. Returns the iRODS Server result Object.
	 *
	 * @param rule the rule
	 * @param getParamName the get param name
	 * @return the string
	 * @author Jens Peters
	 */
	public String executeRule(String rule, String getParamName) {
		try {
			if (!isConnected()) {
				connect();
			}
			IRODSAccessObjectFactory accessObjectFactory = getIRODSAccessObjectFactory();
			RuleProcessingAO ruleProcessingAO;
			ruleProcessingAO = accessObjectFactory
					.getRuleProcessingAO(irodsAccount);
			IRODSRuleExecResult result = ruleProcessingAO.executeRule(rule);

			logger.debug("Executing Rule: " + rule);
			if (getParamName != null && !getParamName.equals("")) {
				if (result.getOutputParameterResults()
						.containsKey(getParamName)) {
					return result.getOutputParameterResults().get(getParamName)
							.getResultObject().toString();
				}

			}
			@SuppressWarnings("rawtypes")
			Iterator it = result.getOutputParameterResults().entrySet()
					.iterator();
			while (it.hasNext()) {

				@SuppressWarnings("rawtypes")
				Map.Entry pairs = (Map.Entry) it.next();
				logger.debug("Recieved form irods: " + pairs.getKey() + " - "
						+ pairs.getValue());
			}
		} catch (JargonException e) {
			logger.error("Error while executing Rule: " + rule);
			throw new IrodsRuntimeException("Error in Excecute Rule: " + rule
					+ " " + e.getUnderlyingIRODSExceptionCode(), e);

		}
		return "";
	}
	
	/**
	 * Returns the corresponding RuleProcessing AO
	 *  @author Jens Peters
	 */
	public RuleProcessingAO getRuleProcessingAO() {
		RuleProcessingAO ruleProcessingAO = null;
		
		try {
			if (!isConnected()) {
				connect();
			}
			IRODSAccessObjectFactory accessObjectFactory = getIRODSAccessObjectFactory();
			ruleProcessingAO = accessObjectFactory
					.getRuleProcessingAO(irodsAccount);
			
			return ruleProcessingAO;
			
			} catch (JargonException e) {
			logger.error("Error while getting Rule Processing AO");
			throw new IrodsRuntimeException("Error while getting Rule Processing AO"
					+ " " + e.getUnderlyingIRODSExceptionCode(), e);

		}
	}
	
	/**
	 * Exceutes rule from the given file, populated with parameters 
	 * 
	 * @param ruleToExec
	 * @param parms
	 * @return
	 * @throws IOException
	 * @author Jens Peters
	 */
	public String executeRuleFromFile(File ruleToExec , Map<String, String> parms) throws IOException {
		
		List<IRODSRuleParameter> inputparameters = new ArrayList<IRODSRuleParameter>();
		Iterator<Map.Entry<String, String>> entries = parms.entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry<String, String> entry = entries.next();
			IRODSRuleParameter iparm = new IRODSRuleParameter(entry.getKey(),entry.getValue());
			inputparameters.add(iparm);
		}
		
		String execOut = "";
		IRODSRuleExecResult result;	
		StringBuilder contents = new StringBuilder();
	    
		BufferedReader input =  new BufferedReader(new FileReader(ruleToExec));
	      try {
	        String line = null; //not declared within while loop
	        while (( line = input.readLine()) != null){
	          contents.append(line);
	          contents.append("\n");
	        }
	      } catch (Exception e) {
	    	  throw new IOException();
	    } finally {
	    	input.close();
	    }
	      
		
		try {
			
			result = getRuleProcessingAO().executeRule(
					contents.toString(),  inputparameters,
					RuleProcessingType.INTERNAL);
			execOut = result.getOutputParameterResults()
				.get(RuleProcessingAOImpl.RULE_EXEC_OUT).getResultObject()
				.toString();
		} catch (JargonException e) {

			throw new IrodsRuntimeException("Error while executing Rule " + ruleToExec.getAbsolutePath()
					+ " " + e.getUnderlyingIRODSExceptionCode(), e);
		
		}
		return execOut;
		
	}
	
	/**
	 * Gets the iRODS file factory.
	 *
	 * @return the iRODS file factory
	 * @author Jens Peters
	 */
	public IRODSFileFactory getIRODSFileFactory() {
		if (irodsFileSystem == null) {
			throw new IrodsRuntimeException(
					"No IRODSFileSystem set, cannot obtain the IRODSAccessObjectFactory");
		}
		if (!isConnected()) {
			connect();
		}
		if (irodsAccount == null) {
			throw new IrodsRuntimeException(
					"No IRODSAccount set, cannot obtain the IRODSAccessObjectFactory");
		}
		try {
			return irodsFileSystem.getIRODSFileFactory(irodsAccount);
		} catch (JargonException ex) {
			logger.error("Exception getting iRODS file factory");
			throw new IrodsRuntimeException(
					"Exception getting iRODS file factory: "
							+ ex.getUnderlyingIRODSExceptionCode(), ex);
		}

	}
	
	/**
	 * Gets the Vault Path for logical file on a Resource named rname.
	 *
	 * @param rname the rname
	 * @return The resource vault path for the given rname
	 * @author Jens Peters
	 */
	public String getVaultPathForRescName(String rname) {
		if (!isConnected()) {
			connect();
		}
		List<Resource> o = null;
		StringBuilder sb = new StringBuilder();
		sb.append(RodsGenQueryEnum.COL_R_RESC_NAME.getName());
		sb.append(" = '");
		sb.append(rname);
		sb.append("'");
		
		try {
			o = getResourceAO().findWhere(sb.toString());
			if (o.isEmpty()) {
				logger.warn("Could not determine vault path for resc name " + rname);
				logger.debug("used query: " + sb);
				return null;
			}
			
			return o.get(0).getVaultPath();
		} catch (Exception e) {
			throw new RuntimeException("Could not determine vault path for resc name " + rname, e);
		}
		
	}
	
	/**
	 * Gets the Resource Location for Resource named rname.
	 *
	 * @param rname the rname
	 * @return The resource location (server name) for the given rname
	 * @author Jens Peters
	 */
	public String getRescLocForRescName(String rname) {
		if (!isConnected()) {
			connect();
		}
		
		
		List<Resource> o = null;
		StringBuilder sb = new StringBuilder();
		sb.append(RodsGenQueryEnum.COL_R_RESC_NAME.getName());
		sb.append(" = '");
		sb.append(rname);
		sb.append("'");
		
		try {
			o = getResourceAO().findWhere(sb.toString());
			if (o.isEmpty()) {
				logger.warn("Could not determine RescLoc path for resc name " + rname);
				logger.debug("used query: " + sb);
				return null;
			}
			
			return o.get(0).getLocation();
		} catch (Exception e) {
			throw new RuntimeException("Could not determine RescLoc for resc name " + rname, e);
		}
		
	}

	
	/**
	 * Register file.
	 *
	 * @param logicalPath the logical path
	 * @param physicalPath the physical path
	 * @param workingResource the working resource
	 * @author daniel
	 */
	public void registerFile(String logicalPath, File physicalPath,String workingResource){
		logger.trace("registerFile");
		
		String rule = "register||msiPhyPathReg("
				+ logicalPath+ ","
				+ workingResource + ","
				+ physicalPath + ","
				+ "null" + ","
				+ "*stat)|nop\n"
				+ "null\n*result";
		
		logger.debug("Rule: "+rule);
		String result = executeRule(rule,
				"*result");
		logger.debug("Result: "+result);
	}
	
	/**
	 * Registers all files under physicalPackagePath that are not yet registered under
	 * logicalPath.
	 *
	 * @param logicalPath the logical irods path to which the physical path should be registered
	 * @param physicalPackage the path to the files on the file system
	 * @param workingResource the working resource
	 * @author Daniel M. de Oliveira
	 */
	public void registerFilesInCollection(String logicalPath, File physicalPackage,
			String workingResource) {
		logger.trace("registerFilesInCollection: "  + physicalPackage.getAbsolutePath() + " as " + logicalPath);
		
		String rule = "register||msiPhyPathReg("
				+ logicalPath+ ","
				+ workingResource + ","
				+ physicalPackage + ","
				+ "collection" + ","
				+ "*stat)|nop\n"
				+ "null\nruleExecOut";
		
		logger.debug("Rule: "+rule);
		String result = executeRule(rule,
				"*result");
		logger.debug("Result: "+result);
	}

	
	/**
	 * Renames a logical dataObj.
	 *
	 * @param sourceFullPath the source full path
	 * @param destFullPath the dest full path
	 * @return result The output from the actual grid operation
	 * @author Daniel M. de Oliveira & Jens Peters
	 */
	public void renameDataObject(String sourceFullPath,String destFullPath){
		//throw new UnsupportedOperationException("Do not use this function");
		logger.trace("renameDataObj "+ sourceFullPath + " to " +destFullPath);
		IRODSFile irodsFile;
		try {
			IRODSAccessObjectFactory accessObjectFactory = IRODSAccessObjectFactoryImpl
			.instance(irodsFileSystem.getIrodsSession());
	IRODSFileFactory irodsFileFactory = accessObjectFactory
			.getIRODSFileFactory(irodsAccount);
	
			irodsFile = irodsFileFactory
			.instanceIRODSFile(sourceFullPath);
			IRODSFile irodsRenameFile = irodsFileFactory
			.instanceIRODSFile(destFullPath);

			irodsFile.renameTo(irodsRenameFile);
		} catch (JargonException e) {
			throw new RuntimeException("Could not rename Dataobject " + sourceFullPath + " to " + destFullPath +". IRODS said to us: " + e.getUnderlyingIRODSExceptionCode(), e);
		}
	}
	
	/**
	 * Renames a logical irods collection. This is not suited for renaming DataObjects. Use renameDataObj() instead.
	 *
	 * @param address the address
	 * @return the file size
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @author  Jens Peters
	 */
	public long getFileSize(String address) throws IOException {
		logger.trace("getFileSize " +address);
		CollectionAndDataObjectListAndSearchAO listAndSearchAO;
		try {
			listAndSearchAO = irodsFileSystem
					.getIRODSAccessObjectFactory()
					.getCollectionAndDataObjectListAndSearchAO(irodsAccount);
		CollectionAndDataObjectListingEntry entry = listAndSearchAO
				.getCollectionAndDataObjectListingEntryAtGivenAbsolutePath(address);
		return entry.getDataSize();
		
		} catch (JargonException e) {
			throw new RuntimeException("Could not determine Dataobject size at address " +address, e);
		}
	}
	
	/**
	 * Renames a logical irods collection. This is not suited for renaming DataObjects. Use renameDataObj() instead.
	 *
	 * @param sourceCollection the source collection
	 * @param destCollection the dest collection
	 * @author Daniel M. de Oliveira & Jens Peters
	 */
	public void renameCollection(String sourceCollection, String destCollection) {
		logger.trace("renameCollection "+ sourceCollection + " to " +destCollection);
		try {	
		
			IRODSFile irodsFile;
			irodsFile = getIRODSFileFactory()
				.instanceIRODSFile(sourceCollection);
			IRODSFile irodsRenameFile = getIRODSFileFactory()
				.instanceIRODSFile(destCollection);
			IRODSAccessObjectFactory accessObjectFactory = IRODSAccessObjectFactoryImpl
				.instance( irodsFileSystem.getIrodsSession());
			
			IRODSFileSystemAO fileSystemAO = accessObjectFactory
				.getIRODSFileSystemAO(irodsAccount);
			fileSystemAO.renameDirectory(irodsFile, irodsRenameFile);
		} catch (JargonException e) {
			throw new RuntimeException("Could not rename Dataobject " + sourceCollection + " to " + destCollection +". IRODS said to us: " + e.getUnderlyingIRODSExceptionCode(), e);
		}
	}

	/**
	 * Removes a logical collection from the grid.
	 * The logical file entry is removed even if the physical file doesn't exist anymore.
	 *
	 * @param collName the coll name
	 * @author Daniel M. de Oliveira
	 */
	public void removeCollection(String collName){
		String rule = "removeCollection||msiRmColl("
				+ collName +
				",\"forceFlag=\",*trash)|nop\nnull\n*out";
		logger.debug("executing rule: "+rule);
		executeRule(rule,
				"*repName");
	}

	/**
	 * Removes a logical collection from the grid.
	 * The logical file entry is removed even if the physical file doesn't exist anymore.
	 *
	 * @param collName the coll name
	 * @author Jens Peters
	 */
	public void removeCollectionAndEatException(String collName){
		
		try {
			removeCollection(collName);
		} catch (IrodsRuntimeException e ) {
			logger.debug("catched and not raised Exception while removing collection : "+collName + " " +e.getMessage());
		}
		
	}

	/**
	 * Removes a logical file from the grid.
	 * The logical file entry is removed even if the physical file
	 * doesn't exist anymore.
	 *
	 * @param logicalPathToFile the logical path to file
	 * @author daniel
	 */
	public void removeFile( String logicalPathToFile ){
		String rule = "removeObject||msiDataObjUnlink("
				+ "\"objPath=" + logicalPathToFile + "++++forceFlag=\",*trash)|nop\nnull\n*out";
		executeRule(rule,
				"*result");
	}
	
	/**
	 * Removes a logical file from the grid.
	 * The logical file entry is removed even if the physical file
	 * doesn't exist anymore.
	 *
	 * @param logicalPathToFile the logical path to file
	 * @author daniel
	 */
	public void removeFileAndEatException( String logicalPathToFile ){
		
		try {
			
			String rule = 
					"removeObject||msiDataObjUnlink(" + "\"objPath="
					+ logicalPathToFile
					+ "++++forceFlag=\",*trash)|nop\nnull\n*out";
			
			executeRule(rule, "*result");
			
		} catch (Exception e) {
			
			logger.warn(
					"removeFileAndEatException ate Exception while trying to delete \"{}\".",
					logicalPathToFile);
		}
	}
	
	
	
	/**
	 * Created a logical collection in the grid.
	 *
	 * @param collName the coll name
	 * @author Daniel M. de Oliveira
	 */
	public void createCollection(String collName){
		String coll = collName;
		if (coll.endsWith("/")) coll = coll.substring(0,coll.length()-1);
		String rule = 
				"collCreate||"+
				
				"msiCollCreate("+coll+",1,*junk)|nop\nnull\n*result";
				
		logger.debug("Rule: "+rule);
		String result = executeRule(rule,
				"*result");
		logger.debug("Result: "+result);
	}
	
	
	
	/**
	 * Gets the resources from group.
	 *
	 * @param resGroupName the res group name
	 * @return the resources from group
	 * @author Jens Peters
	 */
	public List<Resource> getAllRessourcesFromGroup(String resGroupName) {
		List<Resource> res = null;
		try {
			ResourceAO rao = getResourceAO();
			StringBuilder sb = new StringBuilder();
			sb.append(RodsGenQueryEnum.COL_RESC_GROUP_NAME.getName());
			sb.append(" = '");
			sb.append(resGroupName);
			sb.append("' ");
			res = rao.findWhere(sb.toString());
		} catch (JargonException e) {
					logger.error("IrodsSystemConnector getRessourcesFromGroup: Error in getting Ressources from  "
					+ resGroupName);
		}
		return res;

	}
	
	/**
	 * checks if DAO is on given resource.
	 *
	 * @param collection the collection
	 * @param filename the filename
	 * @param rescName the resc name
	 * @return the resources from group
	 * @author Jens Peters
	 */
	public  boolean isObjectAvailableOnResource(String collection, String filename, String rescName) {
		List<DataObject> dao =  getReplicationsForFile(collection, filename, rescName);
		if (dao.isEmpty()) return false;
		return true;

	}
	
	/**
	 * Gets the resources from group.
	 *
	 * @param resGroupName the res group name
	 * @return the resources from group
	 * @author Jens Peters
	 */
	public List<Resource> getRessourcesFromGroup( String resGroupName) {
		List<Resource> lzares = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(RodsGenQueryEnum.COL_RESC_GROUP_NAME.getName());
			sb.append(" = '");
			sb.append(resGroupName);
			sb.append("' ");	
			lzares = getResourceAO().findWhere(sb.toString());
		} catch (JargonException e) {
			
			logger.error("IrodsSystemConnector getRessourcesFromGroup: Error in getting Ressources from  "
					+ resGroupName + " " + e.getMessage());
		}
		return lzares;
	}
	
/**
 * adds AVU MEtadata to DAO
 * Attention: adds a NEW AVU Metadata named name, every time it is called!.
 *
 * @param data_name the data_name
 * @param name the name
 * @param value the value
 * @author: Jens Peters
 */
	
	public void addAVUMetadataDataObject(String data_name, String name, String value) {
			
		AvuData avu = new AvuData();
		avu.setAttribute(name);
		avu.setValue(value);
		try {
			getDataObjectAO().addAVUMetadata(data_name, avu);
		} catch (JargonException e) {
			logger.error("Error adding AVU Metadata to Object named " + data_name +" . Name: " + name + " Value: " + value + " "+ e.getMessage());  
			throw new IrodsRuntimeException("Error adding AVU Metadata to Object named " + data_name +" . Name: " + name + " Value: " + value + " "+ e.getMessage(),e);
		}
	}
	
	/**
	 * saves AVU MEtadata to DAO
	 * modifys if AVU Data already exists.
	 *
	 * @param data_name the data_name
	 * @param name the name
	 * @param value the value
	 * @author: Jens Peters
	 */
	
	public void saveOrUpdateAVUMetadataDataObject(String data_name, String name, String value) {
		
		if (getAVUMetadataDataObjectValue(data_name, name).equals("")) {
			logger.debug("adding for the first time " + name +" " + value +" to" + data_name );
			addAVUMetadataDataObject(data_name, name, value);
		} else {
		
		AvuData avunew = new AvuData();
		avunew.setAttribute(name);
		avunew.setValue(value);
		try {
			logger.debug("Modifying AVU Entry named: " + name + " value: " + value);
			getDataObjectAO().modifyAvuValueBasedOnGivenAttributeAndUnit(data_name, avunew);
			
		} catch (JargonException e) { 
			throw new IrodsRuntimeException("Error modifying AVU Metadata to Object named " + data_name +" . Name: " + name + " Value: " + value + " "+ e.getMessage(),e);
		}
		}
	}

	/**
	 * Gets the value of an AVU Metadata name.
	 *
	 * @param data_name the data_name
	 * @param name the name
	 * @return the aVU metadata data object value
	 * @author: Jens Peters
	 */
	
	public String getAVUMetadataDataObjectValue(String data_name, String name) {
		
		String ret = "";
		try {
			List<MetaDataAndDomainData> metadata = getDataObjectAO()
			.findMetadataValuesForDataObject(data_name);
			boolean found = false;
			for (MetaDataAndDomainData metadataEntry : metadata) {
				if (metadataEntry.getAvuAttribute().equals(name)){
					if (found) throw new IrodsRuntimeException("Given Attribute named " + name + " " + " for " + data_name +" found twice");
					ret = metadataEntry.getAvuValue();
					found = true;
				}
			}
		} catch (JargonException e) {
			throw new IrodsRuntimeException("Error adding AVU Metadata to Object named " + data_name +" . Name: " + name + " "+ e.getMessage(),e);
		}
		return ret;
		
	}
	
	
	/**
	 * Gets the DataObjects for logical file.
	 *
	 * @param collection the collection
	 * @param filename the filename
	 * @return the objects for file
	 * @author Jens Peters
	 */
	public List<DataObject> getReplicationsForFile(String collection, String filename) {
		return getReplicationsForFile(collection, filename, "");
	}
	
	
	/**
	 * Gets the DataObjects for logical file on a dedicated Resource named rname.
	 *
	 * @param collection the collection
	 * @param filename the filename
	 * @param rname the rname
	 * @return the objects for file on the given rname
	 * @author Jens Peters
	 */
	public List<DataObject> getReplicationsForFile(String collection, String filename, String rname) {
		if (collection.endsWith("/")) collection = collection.substring(0, collection.length()-1);
		
		List<DataObject> o = null;
		IRODSGenQueryBuilder builder = new IRODSGenQueryBuilder(true, null);
		IRODSQueryResultSetInterface resultSet;
		logger.debug("looking for "+collection+":"+filename+"on rname "+rname);
		
		try {
			DataAOHelper.addDataObjectSelectsToBuilder(builder);
			builder.addConditionAsGenQueryField(RodsGenQueryEnum.COL_COLL_NAME,QueryConditionOperators.EQUAL,collection)
			.addConditionAsGenQueryField(RodsGenQueryEnum.COL_DATA_NAME,QueryConditionOperators.EQUAL,filename);
			if (!rname.equals("")) {
				builder.addConditionAsGenQueryField(RodsGenQueryEnum.COL_R_RESC_NAME, QueryConditionOperators.EQUAL, rname);
			}
			IRODSGenQueryFromBuilder irodsQuery = builder
					.exportIRODSQueryFromBuilder(100);

			resultSet = getGenQueryExecutor().executeIRODSQueryAndCloseResult(
					irodsQuery, 0);
			return DataAOHelper.buildListFromResultSet(resultSet);
		} catch (Exception e) {
			logger.error("Error executing Query getReplicationsForFile() for "+ collection + "/" + filename +" " + e.getMessage(), e);
			
		}
		return o;
	}
	
	
	/**
	 * Gets the DataObjects for logical file on a Resource Group named resgroup.
	 *
	 * @param collection the collection
	 * @param filename the filename
	 * @param resgroup the resgroup
	 * @return the objects for file on the given resgroup name
	 * @author Jens Peters
	 */
	public List<DataObject> getReplicationsForFileInResGroup(String collection, String filename, String resgroup) {
		if (collection.endsWith("/")) collection = collection.substring(0, collection.length()-1);
		
		List<DataObject> o = null;
		IRODSGenQueryBuilder builder = new IRODSGenQueryBuilder(true, null);
		IRODSQueryResultSetInterface resultSet;

		try {
			DataAOHelper.addDataObjectSelectsToBuilder(builder);
			builder.addConditionAsGenQueryField(RodsGenQueryEnum.COL_COLL_NAME,QueryConditionOperators.EQUAL,collection)
			.addConditionAsGenQueryField(RodsGenQueryEnum.COL_DATA_NAME,QueryConditionOperators.EQUAL,filename)
			.addConditionAsGenQueryField(RodsGenQueryEnum.COL_D_RESC_GROUP_NAME, QueryConditionOperators.EQUAL, resgroup);
			
			IRODSGenQueryFromBuilder irodsQuery = builder
					.exportIRODSQueryFromBuilder(100);

			resultSet = getGenQueryExecutor().executeIRODSQueryAndCloseResult(
					irodsQuery, 0);
			return DataAOHelper.buildListFromResultSet(resultSet);
		} catch (Exception e) {
			logger.error("Error executing Query getReplicationsForFile() for "+ collection + "/" + filename +" " + e.getMessage(), e);
			
		}
		return o;
	}

		
	/**
	 * Gets the Number of Replications on Resources, including Cache Ressources
	 * ATTENTION: this shouldn't be called to determine the LZA copies, while listing cache repls too.
	 *
	 * @param collection the collection
	 * @param filename the filename
	 * @return Number of Objects
	 * @author Jens Peters
	 */
	
	public int getTotalNumberOfReplsForDataObject(String collection, String filename) {
		int ret = 0;
		List<Resource> totalres;
		try {
			totalres = getDataObjectAO().getResourcesForDataObject(collection, filename);
			if (!totalres.isEmpty()) {
				return totalres.size();
			}
		} catch (JargonException e) {
			logger.error("Error getting getTotalNumberOfReplsForDataObject() for "+ collection + "/" + filename +" " + e.getMessage(),e );

		}
	
	return ret;
	}
	
	/**
	 * Gets the Number of Replications on Resources, including Cache Ressources.
	 *
	 * @param collection the collection
	 * @param filename the filename
	 * @param resgroup the resgroup
	 * @return Number of Objects
	 * @author Jens Peters
	 */
	
	public int getNumberOfReplsForDataObjectInResGroup(String collection, String filename, String resgroup) {
		int ret = 0;
		logger.trace("Get amount of repls for " + collection +" " +filename + " in " + resgroup);
		List<DataObject> dataobjects;
		dataobjects = getReplicationsForFileInResGroup(collection ,filename, resgroup);
		if (dataobjects==null) {
			throw new IrodsRuntimeException("Cannot get Number of Repls - Connection to iRODS Server not valid?");
		}
		
		if (!dataobjects.isEmpty()) {
			logger.trace("is: " + dataobjects.size());
			return dataobjects.size();
		}return ret;
	}
	
	/**
	 * Trim ressource group.
	 * Trims (deletes) all Replications of DataObj in the given resGroup.
	 * if less then 2 Copies in total exist, the trim does nothing
	 *
	 * @param dataobj the dataobj
	 * @param resGroup the res group
	 * @author Jens Peters
	 */
	public void trimResources(String dataobj, List<Resource> resGroup) {
		if (resGroup != null) {
			Iterator<Resource> iterator = resGroup.iterator();
			while (iterator.hasNext()) {
				Resource res = iterator.next();
				StringBuilder sb = new StringBuilder();
				sb.append("ObjCLEANUP||msiWriteRodsLog(\"Obj-CLEANUP: "
						+ dataobj + " " + res.getName() + "\", *junk)##");
				sb.append("msiDataObjTrim(\"" + dataobj + "\","
						+ res.getName() + ",null,3,null,*outstatus)");
				sb.append("|nop\n");
				sb.append("null\n");
				sb.append("*outstatus");
				executeRule(sb.toString(),
						"*outstatus");
				
				
			}
		}
	}
	
	/**
	 * Trim resource.
	 *
	 * @param dataobj the dataobj
	 * @param resc_name the resc_name
	 * @author Jens Peters
	 */
	public void trimResource(String dataobj, String resc_name) {
		
				StringBuilder sb = new StringBuilder();
				sb.append("ObjCLEANUP||");
				sb.append("msiDataObjTrim(\"" + dataobj + "\","
						+resc_name + ",null,3,null,*outstatus)");
				sb.append("|nop\n");
				sb.append("null\n");
				sb.append("*outstatus");
				executeRule(sb.toString(),
						"*outstatus");
			
	}
	
	
	/**
	 * Checksum all replicas of dataObject.
	 * @deprecated
	 * @param data_name data_name
	 * @return the string
	 * @author Jens Peters
	 */
	
	@Deprecated
	public String verifyChecksumAll(String data_name) {
		if (!fileExists(data_name)) throw new IrodsRuntimeException(data_name + " does not exist");
		
		String verifyCs = "csDaoAll||"
		+"msiDataObjChksum(" + data_name + ",'ChksumAll=++++verifyChksum=',*result)|nop\n"
		+"null\n"
		+"ruleExecOut";
		return executeRule(verifyCs,
		"*result");
	}
	
	/**
	 * Replicate a DAO.
	 *
	 * @param data_name data_name
	 * @param src_resc the src_resc
	 * @param dest_resc the dest_resc
	 * @return the string
	 * @author Jens Peters
	 */
	
	public String replicateDao(String data_name, String src_resc, String dest_resc) {
	String resc = "";
	if (!src_resc.equals("")) {
		resc="++++rescName=" + src_resc;
	}
		
		
	 String repl = "replDAO||msiDataObjRepl("+ data_name +",'++++verifyChksum="  + resc+ "++++backupRescName=" + dest_resc + "',*replStatus)|nop\n"
	 + "null\n"
	 + "ruleExecOut\n";
		return executeRule(repl,
		"*replStatus");
	}
	
	/**
	 * Replicate a DAO.
	 *
	 * @param data_name data_name
	 * @param src_resc the src_resc
	 * @param dest_resc the dest_resc
	 * @return the string
	 * @author Jens Peters
	 */
	
	public String replicateDaoAsynchronously(String data_name, String src_resc, String dest_resc) {
		
	String repl = "replicateDAO||"
		+"delayExec('<PLUSET>10s</PLUSET>',msiWriteRodsLog(\"Replicating *data_name to " + dest_resc + " \",*junk)##"
		+"msiDataObjRepl(*data_name,'++++verifyChksum=" + src_resc + "++++backupRescName=" +dest_resc +"',*replStatus),"
		+"nop)|nop\n"
		+ "%*data_name=" 
		+ data_name
		+"\nruleExecOut";
			return executeRule(repl,"*result");
		}
	
	/**
	 * Replicates a collection to a resource if it not already exists there.
	 *
	 * @param collection logical irods collection path
	 * @param resource the resource
	 * @author Daniel M. de Oliveira
	 */
	public void replicateCollectionToResource(String collection,
			String resource) {
		
		logger.trace("replicateCollectionToResource");
		
		String rule = "replicate||msiCollRepl("
				+ collection +","
				+ "++++verifyChksum=++++backupRescName="
				+ resource +",*stat)|nop\n"
				+ "null\n*result";
		
		logger.debug("Rule: "+rule);
		String result= executeRule(rule,
				"*result");
		logger.debug("Result: "+result);
	}

	
	/**
	 * Replicate a DAO to Resgroup in delayed mode.
	 *
	 * @param data_name data_name
	 * @param resGroup the res group
	 * @return the string
	 * @author Jens Peters
	 */
	
	public String replicateDaoToResGroup(String data_name, String resGroup) {
		String repl = "replicateDAO||"
	+"delayExec('<PLUSET>10s</PLUSET>',msiWriteRodsLog(\"Replicating *data_name to " + resGroup + " \",*junk)##"
	+"msiDataObjRepl(*data_name,'++++verifyChksum=++++backupRescName=" + resGroup + "++++all=',*replStatus),"
	+"nop)|nop\n"
	+ "%*data_name=" 
	+ data_name
	+"\nruleExecOut";
		return executeRule(repl,"*result");
	}
	
	/**
	 * Replicate a DAO to Resgroup in delayed mode.
	 *
	 * @param data_name data_name
	 * @param resGroup the res group
	 * @param hostname the hostname
	 * @return the string
	 * @author Jens Peters
	 */
	
	public String replicateDaoToResGroup(String data_name, String resGroup,String hostname) {
		String repl = "replicateDAO||"
	+"delayExec('<EA>" + hostname + "</EA><PLUSET>10s</PLUSET>',msiWriteRodsLog(\"Replicating *data_name to " + resGroup + " \",*junk)##"
	+"msiDataObjRepl(*data_name,'++++verifyChksum=++++backupRescName=" + resGroup + "++++all=',*replStatus),"
	+"nop)|nop\n"
	+ "%*data_name=" 
	+ data_name
	+"\nruleExecOut";
		return executeRule(repl,"*result");
	}
	
	/**
	 * Replicate a DAO to Resgroup in syncrchronous mode (without delayed action).
	 *
	 * @param data_name data_name
	 * @param resGroup the res group
	 * @return the string
	 * @author Jens Peters
	 * @throws JargonException 
	 * @throws IrodsRuntimeException 
	 */
	
	public String replicateDaoToResGroupSynchronously(String data_name, String resGroup) {
		try {
			getDataObjectAO().replicateIrodsDataObjectToAllResourcesInResourceGroup(data_name, resGroup);
		} catch (JargonException e) {
			throw new IrodsRuntimeException("error in replication to nodes of rg : " + resGroup + " " + e.getUnderlyingIRODSExceptionCode());
		}
		return "";
	}
	
	/**
	 * Replicate a DAO to Resgroup in syncrchronous mode (without delayed action).
	 *
	 * @param data_name data_name
	 * @param resGroup the res group
	 * @return the string
	 * @author Jens Peters
	 * @throws JargonException 
	 * @throws IrodsRuntimeException 
	 */
	
	public String replicateDaoToResGroupSynchronously(String data_name, String resGroup, String srcResc) {
		String repl = "replicateDAOSynch||"
	+"msiWriteRodsLog(\"Replicating *data_name to " + resGroup + " \",*junk)##"
	+"msiDataObjRepl(*data_name,'++++verifyChksum=++++rescName=" + srcResc +"++++backupRescName=" + resGroup + "++++all=',*replStatus)"
	+"|nop\n"
	+ "%*data_name=" 
	+ data_name
	+"\nruleExecOut";
		return executeRule(repl,"*result");
	/*	try {
			getDataObjectAO().replicateIrodsDataObjectToAllResourcesInResourceGroup(data_name, resGroup);
		} catch (JargonException e) {
			throw new IrodsRuntimeException("error in replication to nodes of rg : " + resGroup + " " + e.getUnderlyingIRODSExceptionCode());
		}
		return "";
		*/
	}
	
	

	/**
	 * Copy DAO.
	 *
	 * @param data_name data_name
	 * @param dest_data_name the dest_data_name
	 * @param destResc the dest resc
	 * @return the string
	 * @deprecated
	 * @author Jens Peters
	 */
	@Deprecated
	public String copyDAO(String data_name, String dest_data_name, String destResc) {
		String copy = "copy||msiDataObjCopy(" + data_name +"," + dest_data_name + ", destRescName=" +  destResc +"++++forceFlag=++++verifyChksum=,*copy)|nop\n"
		 + "null\n"
		 + "ruleExecOut\n";
			return executeRule(copy,
			"*copy");
	}
	
	/**
	 * gets File from the Grid.
	 *
	 * @param dataobj the dataobj
	 * @param localFile The file should be stored to
	 * @author Jens Peters
	 */

	public void get (String dataobj, File localFile) {
		
		logger.debug("GET Operation of "+dataobj);
		IRODSFile irf;
		try {
			irf = getIRODSFileFactory().instanceIRODSFile(dataobj);
			if (irf==null) throw new RuntimeException("Error getting Instance of remote file " + dataobj );
			getDataTransferOperations().getOperation(irf, localFile, null, null);
			logger.debug("IRODS Operation caused file Object is : " + irf.exists());
			
		} catch (JargonException e) {
			logger.error("Error getting dataObj " + dataobj + " to local Path: " +localFile + " " + e.getMessage());
			throw new IrodsRuntimeException("Error getting dataObj " + dataobj + " to local Path: " +localFile + " " + e.getMessage(),e);
		}
		
	}
	
	/**
	 * Puts File to theGrid via Jargon std Operations.
	 *
	 * @param localFile the Local file to put
	 * @param remoteDataObjectPath the remote data object path
	 * @author Jens Peters
	 */
	public void put(File localFile, String remoteDataObjectPath) {
		logger.debug("PUT Operation of "+localFile + " to " + remoteDataObjectPath);
		try {
			
			TransferOptions transferOptions = new TransferOptions();
			transferOptions.setComputeAndVerifyChecksumAfterTransfer(true);
			transferOptions.setMaxThreads(0);
			transferOptions.setUseParallelTransfer(false);
			
			TransferControlBlock transferControlBlock = DefaultTransferControlBlock
					.instance();
			transferControlBlock.setTransferOptions(transferOptions);

			DataTransferOperations dataTransferOperationsAO = irodsFileSystem
			.getIRODSAccessObjectFactory().getDataTransferOperations(
					irodsAccount);
			
			IRODSFileFactory irodsFileFactory = irodsFileSystem
			.getIRODSFileFactory(irodsAccount);
			IRODSFile destFile = irodsFileFactory.instanceIRODSFile(remoteDataObjectPath);

			dataTransferOperationsAO.putOperation(localFile, destFile, null, transferControlBlock);

			logger.debug("IRODS Operation caused file Object is : " + destFile.exists());
			
		} catch (JargonException e) {
			logger.error("Error putting localFile " + localFile + " to remote Path: " +remoteDataObjectPath + " " + e.getMessage());
			throw new IrodsRuntimeException("Error putting localFile " + localFile + " to remote Path: " +remoteDataObjectPath + " " + e.getMessage(),e);
		}
	} 
	

	/**
	 * computes the Checksum on DataObj.
	 *
	 * @param dataObj the data obj
	 * @return the string
	 * @author Jens Peters
	 */
	
	public String computeChecksum(String dataObj) {
		IRODSFile irf;
			try {
				irf = getIRODSFileFactory().instanceIRODSFile(dataObj);
				return getDataObjectAO().computeMD5ChecksumOnDataObject(irf);
				
			} catch (JargonException e) {
				throw new IrodsRuntimeException("Checksum Operation for " + dataObj + " failed!");
			}
			
	}
	

	
	/**
	 * Gets the checksum of an object.
	 *
	 * @param data_name the data_name
	 * @return the checksum
	 */
	
	public String getChecksum(String data_name) {
		try {
			DataObject dao = getDataObjectAO().findByAbsolutePath(data_name);
			if (dao.getChecksum().equals("")) throw new IrodsRuntimeException(data_name + " has not a checksum yet!");
			else return dao.getChecksum();
		} catch (JargonException e) {
			throw new IrodsRuntimeException("Get Checksum error on " + data_name,e);
		} catch (java.io.FileNotFoundException e) {
			throw new IrodsRuntimeException("Get Checksum file not found" + data_name,e);
		} 
		
	}

	/**
	 * builds TAR of given collection.
	 *
	 * @param targetDataObject the target data object
	 * @param sourceCollection the source collection
	 * @param workingRes the working res
	 * @deprecated
	 * @author Jens Peters
	 */
	@Deprecated
	public void buildTar(String targetDataObject, String sourceCollection, String workingRes) {
		String tarRule =
			"tar||"+	
			"msiTarFileCreate(\"" + targetDataObject+ "\","+
			sourceCollection + ","+ workingRes +",force)"+	
			"|nop\nnull\nruleExecOut";
		logger.debug("Rule: "+tarRule);
		String result = executeRule(tarRule,
				"*result");
		logger.debug("Result: "+result);
	
	}
	
	/**
	 * Start all (delayed) rules in folder
	 * @author Jens Peters
	 * @param folder
	 * @return
	 */
	public boolean startAllDelayedRules(String folder) {
		stopAllDelayedRules();
		boolean allPass = true;
		logger.debug("Start all rules in folder " + folder);
		 File dir = new File(folder);
		 FileFilter fileFilter = new WildcardFileFilter("*.r");
		 File[] files = dir.listFiles(fileFilter);
		 for (int i = 0; i < files.length; i++) {
			 logger.debug("Executing rule now " + files[i]);
			 try {
				executeRuleFromFile(files[i], null);
			} catch (IOException e) {
			logger.error(files[i].getName() + " execution failed" );
			allPass = false;
			}
		 }
		 return allPass;
	}
	
	/**
	 * Stops all delayed exceuted rules
	 * @author Jens Peters
	 * @return
	 */
	
	public int stopAllDelayedRules() {
		logger.debug("Stop all delayed rules");
		try {
			return getRuleProcessingAO().purgeAllDelayedExecQueue();
		} catch (JargonException e) {
			throw new IrodsRuntimeException("stop delayed rule execution failed");
		}
	}
	
	/**
	 * Federation to other zones.
	 *
	 * @param data_name the data_name
	 * @param zone the zone
	 * @param destPath the dest path
	 * @param destresource the destresource
	 * @deprecated use execute from file instead!
	 * @author Jens Peters
	 */
	
	 @Deprecated
	public void federateDataObjectToZoneAsynchronously(String data_name, String zone, String destPath, String destresource) {
		 	String resc = "null";
			if (!destresource.equals("")) {
				resc=destresource;
			}	
			String fed = "federateDAO||"
				+"delayExec('<PLUSET>10s</PLUSET>',msiWriteRodsLog(\"Federating *data_name to " + zone + " \",*junk)##"
				+"msiDataObjRsync(*data_name,IRODS_TO_IRODS," + resc + "," +destPath+",*replStatus),"
				+"nop)|nop\n"
				+ "%*data_name=" 
				+ data_name
				+"\nruleExecOut";
			logger.debug("Rule: "+fed);
			String result = executeRule(fed,
					"*result");
			logger.debug("Result: "+result);
		
	 }
	 
	 /**
	  * At this point we have trust the IRODS ReServer the feration rules are up'n'runnin 
	  * and it evaluates the AVU correctly 
	  * 
	  * @param data_name
	  * @param zones
	  * @return
	  * @author Jens Peters
	  */
	 public boolean federateDao(String data_name, List<String> zones) {
		 saveOrUpdateAVUMetadataDataObject(data_name, "federated_zones",  StringUtils.collectionToCommaDelimitedString(zones));
		 if (!getAVUMetadataDataObjectValue(data_name, "federated_zones").equals("")) return true;
		 return false;
	 }

	/**
	 * Sets the sets the pam mode.
	 *
	 * @param setPamMode the setPamMode to set
	 */
	public void setPamMode(boolean setPamMode) {
		this.setPamMode = setPamMode;
	}

	/**
	 * Checks if is sets the pam mode.
	 *
	 * @return the setPamMode
	 */
	public boolean isSetPamMode() {
		return setPamMode;
	}

	/**
	 * Sets the key store.
	 *
	 * @param keyStore the keyStore to set
	 */
	public void setKeyStore(String keyStore) {
		this.keyStore = keyStore;
	}

	/**
	 * Gets the key store.
	 *
	 * @return the keyStore
	 */
	public String getKeyStore() {
		return keyStore;
	}

	/**
	 * Sets the trust store.
	 *
	 * @param trustStore the trustStore to set
	 */
	public void setTrustStore(String trustStore) {
		this.trustStore = trustStore;
	}

	/**
	 * Gets the trust store.
	 *
	 * @return the trustStore
	 */
	public String getTrustStore() {
		return trustStore;
	}

	/**
	 * Sets the key store password.
	 *
	 * @param keyStorePassword the keyStorePassword to set
	 */
	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	/**
	 * Gets the key store password.
	 *
	 * @return the keyStorePassword
	 */
	public String getKeyStorePassword() {
		return keyStorePassword;
	}


	 
	 
	 
}
