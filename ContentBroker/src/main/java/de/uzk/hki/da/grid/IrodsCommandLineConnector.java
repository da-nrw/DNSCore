package de.uzk.hki.da.grid;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;


/**
 * Interface for the classic ICommands of IRODS
 * @author Jens Peters
 */
public class IrodsCommandLineConnector {

	/** CommandLineConnector */
	private CommandLineConnector clc;
	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(IrodsCommandLineConnector.class);
	
	/** Constructor */
	public IrodsCommandLineConnector() {
		this.clc = new CommandLineConnector();
	}
	
	/**
	 * Executes given Icommand 
	 * @author Jens Peters
	 * @param commandAsArray
	 * @return
	 */
	public String executeIcommand(String[] commandAsArray) {
		ProcessInformation pi = null;
		try {
			pi = clc.runCmdSynchronously(commandAsArray);
		} catch (IOException e1) {
			throw new RuntimeException("Icommand did not succeed, not found: " + Arrays.toString(commandAsArray));
		}
		if (pi.getExitValue()!=0) {
			logger.error("Icommand did not succeed: " + Arrays.toString(commandAsArray) + " returned " +pi.getStdErr() );
			return "ERROR";
		}	
		logger.debug (pi.getStdOut());
		return pi.getStdOut();
	}

	/**
	 * put file to irods
	 * @author Jens Peters
	 * @param file
	 * @param dest
	 * @return
	 */
	public boolean put(File file, String dest) {
		return put (file, dest, "");
	}

	/**
	 * Get File from irods
	 * @author Jens Peters
	 * @param file
	 * @param sourceGridFile
	 * @return
	 */
	public boolean get(File file, String sourceGridFile) {
		return get (file, sourceGridFile, "");
	}
	
	/**
	 * Get File from irods
	 * @author Jens Peters
	 * @param file
	 * @param sourceGridFile
	 * @param sourceRescName
	 * @return
	 */
	
	public boolean get(File file, String sourceGridFile, String sourceRescName) {
		ArrayList<String>  commandAsList = new ArrayList<String>();
		commandAsList.add("iget");
		commandAsList.add("-K");
		if (!sourceRescName.equals("")) {
			commandAsList.add("-R");
			commandAsList.add(sourceRescName);
		}
		commandAsList.add(sourceGridFile);
		commandAsList.add(file.getAbsoluteFile().toString());
		String[] commandAsArray = new String[commandAsList.size()];
		commandAsArray = commandAsList.toArray(commandAsArray);
		String out = executeIcommand(commandAsArray);
		if (out.indexOf("ERROR")>=0) return false;
		if (!file.exists()) return false;
		return true;
	}
	
	/**
	 * Put file to destination
	 * @author Jens Peters
	 * @param file
	 * @param dest
	 * @param resourceName
	 * @return
	 */
	public boolean put(File file, String dest, String resourceName) {
		ArrayList<String>  commandAsList = new ArrayList<String>();
		commandAsList.add("iput");
		commandAsList.add("-K");
		if (!resourceName.equals("")) {
			commandAsList.add("-R");
			commandAsList.add(resourceName);
		}
		commandAsList.add(file.getAbsoluteFile().toString());
		commandAsList.add(dest);
		String[] commandAsArray = new String[commandAsList.size()];
		commandAsArray = commandAsList.toArray(commandAsArray);
		String out = executeIcommand(commandAsArray);
		if (out.indexOf("ERROR")>=0) return false;
		return true;
	}
	
	/**
	 * Removes Destination
	 * @author Jens Peters
	 * @param dest
	 * @return
	 */
	public boolean remove(String dest) {
		String commandAsArray[] = new String[]{
				"irm", "-r" , dest 
		}; 
		String out = executeIcommand(commandAsArray);
		if (out.indexOf("ERROR")>=0) return false;
		return true;
	}
	
	/**
	 * Tests if given dest DAO exists
	 * @author Jens Peters
	 * @param dest
	 * @return
	 */
	public boolean exists(String dest) {
		String data_name = FilenameUtils.getName(dest);
		String commandAsArray[] = new String[]{
				"ils", dest 
		}; 
		String out = executeIcommand(commandAsArray);
		if (out.indexOf(data_name)>=0) return true;
		return false;
	}
	
	/**
	 * Checks whether the dataobject exists at that given dest with the given 
	 * md5sum. 
	 * @author Jens Peters
	 * @param dest
	 * @param md5sum
	 * @return
	 */
	public boolean existsWithChecksum(String dest, String md5sum) {
		if (md5sum.equals(null)) throw new RuntimeException("md5sum not given");
		if (md5sum.equals("")) throw new RuntimeException("md5sum not given");
		String data_name = FilenameUtils.getName(dest);
		String commandAsArray[] = new String[]{
				"ils","-L", dest 
		}; 
		String out = executeIcommand(commandAsArray);
		if (out.indexOf(data_name)>=0 && out.indexOf(md5sum)>0) return true;
		return false;
	}
	
	/**
	 * Computes Checksum and stores it to ICAT and returns it. 
	 * @author Jens Peters
	 * @param destDao
	 * @return
	 */
	public String computeChecksumForce(String destDao) {
		String commandAsArray[] = new String[]{
				"ichksum","-f",destDao
		};	
		executeIcommand(commandAsArray);
		return getChecksum(destDao);
	}
	
	/**
	 * Gets checksum from ICAT for destDao
	 * @author Jens Peters
	 * @param destDao
	 * @return
	 */
	public String getChecksum(String destDao) {
		String ret = "";
		String commandAsArray[] = new String[]{
				"ichksum",destDao
		};	
		String out = executeIcommand(commandAsArray);
		if (out.indexOf("ERROR")>=0) throw new RuntimeException(" Get Checksum of " + destDao + " failed !" );
		Scanner scanner = new Scanner(out);
		String data_name = FilenameUtils.getName(destDao);
		while (scanner.hasNextLine()) {
		  String line = scanner.nextLine();
		  if (line.contains(data_name)) {
			  ret = line.substring(38,line.length());
			  break;
		  }
		}
		scanner.close();
		return ret;
	}
	
	/**
	 * Validates all Copies in the zone, checks against stored entry in ICAT
	 * @author Jens Peters
	 * @param dao
	 * @return
	 */
	public boolean isValid(String dao) {
		String commandAsArray[] = new String[]{
				"ichksum","-Ka",dao
		};	
		String result = executeIcommand(commandAsArray);
		if (result.indexOf("-314000")>0) {
			return false;
		}
		if (result.indexOf("-808000")>0) {
			return false;
		}
		if (result.indexOf("Failed checksum = 0")>0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Creates Collection 
	 * @author Jens Peters
	 * @param destColl
	 */
	public void mkCollection(String destColl) {
		String mKcommandAsArray[] = new String[]{
				"imkdir","-p",destColl
		};
		executeIcommand(mKcommandAsArray);	
	}
	
	/**
	 * irsyncs Dataobject to destination on given resource
	 * @author Jens Peters
	 * @param dao
	 * @param destDaoif (!resourceName.equals("")) {
			commandAsList.add("-R");
			commandAsList.add(resourceName);
		}
	 * @param destRescName
	 * @return
	 */
	public String rsync(String dao, String destDao, String destRescName) {
		String commandAsArray[] = new String[]{
				"irsync","-KVR",destRescName, "i:"  + dao,"i:"+ destDao
		};	
		return executeIcommand(commandAsArray);	
	}
	
	/**
	 * Sets AVU Triptlet to ICAT
	 * @author Jens Peters
	 * @param dao
	 * @param name
	 * @param value
	 */
	public void setIMeta(String dao, String name, String value) {
		String commandAsArray[] = new String[]{
				"imeta","set","-d", dao, name, value
		};	
		executeIcommand(commandAsArray);
	}
	
	/**
	 * Deletes AVU triplet from ICAT
	 * @author Jens Peters
	 * @param dao
	 * @param name
	 * @param value
	 */
	public void removeIMeta(String dao, String name, String value) {
		String commandAsArray[] = new String[]{
				"imeta","rm","-d", dao, name, value
		};	
		executeIcommand(commandAsArray);
	}
	
	
	/**
	 * Iquests ICAT for the given AVU
	 * @author Jens Peters
	 * @param dao
	 * @param avufield
	 * @return
	 */
	public String iquestDataObjectForAVU(String dao, String avufield) {
		String coll_name = FilenameUtils.getFullPath(dao);
		String data_name =  FilenameUtils.getName(dao);
		coll_name = coll_name.substring(0,coll_name.length()-1);

		String commandAsArray[] = new String[]{
				"iquest", "\"SELECT DATA_NAME, COLL_NAME, META_DATA_ATTR_NAME, META_DATA_ATTR_VALUE where COLL_NAME = \'"+ coll_name+"\' and DATA_NAME = \'"+ data_name+"\' and META_DATA_ATTR_NAME = \'"  + avufield + "\'" 
		}; 
		String iquest = executeIcommand(commandAsArray);
		try {
			return parseResultForAVUField(iquest, "META_DATA_ATTR_VALUE");
		} catch (IOException e) {
			logger.error("Error in parsing Resultlist searching for " + avufield,e);
		}
		return "";
	}
	
	/**
	 * Iquests ICAT for a given field on the DataObject
	 * @author Jens Peters
	 * @param dao
	 * @param field
	 * @return
	 */

	public String iquestDataObjectField(String dao, String field) {
		String coll_name = FilenameUtils.getFullPath(dao);
		String data_name =  FilenameUtils.getName(dao);
		coll_name = coll_name.substring(0,coll_name.length()-1);

		String commandAsArray[] = new String[]{
				"iquest", "\"SELECT "  + field.toUpperCase() +"  where COLL_NAME = \'"+ coll_name+"\' and DATA_NAME = \'"+ data_name+"\'" 
		}; 
		String iquest = executeIcommand(commandAsArray);
		try {
			return parseResultForAVUField(iquest, field);
		} catch (IOException e) {
			logger.error("Error in parsing Resultlist searching for " + field,e);
		}
		return "";
	}

	/**
	 * Parses parses result set for a given AVU 
	 * @author Jens Peters
	 * @param result
	 * @param field
	 * @return
	 * @throws IOException
	 */
	private String parseResultForAVUField(String result, String field) throws IOException {
		String[] props = result.split("------------------------------------------------------------");
		for (int i=0;i<props.length; i++) {
			Properties properties = new Properties();
			StringReader sr = new StringReader(props[i]);
			properties.load(sr);
			sr.close();
			if (properties.get(field)!=null) {
				return properties.get(field).toString();
			}
		}
		return "";
	}
	
	/**
	 * executes given rule against the iRODS Server  
	 * @author Jens Peters
	 * @param rule
	 * @return
	 */
	public String executeIrule(File rule) {
		String commandAsArray[] = new String[]{
				"irule","-F",rule.getAbsoluteFile().toString()
		};	
		return executeIcommand(commandAsArray);
	}
	
	/**
	 * repl to resource name 
	 * @author Jens Peters
	 * @param rule
	 * @return
	 */
	public String repl(String dao, String resourceName) {
		String commandAsArray[] = new String[]{
				"irepl","-aBR",resourceName,dao
		};	
		return executeIcommand(commandAsArray);
	}	
	
	/**
	 * ireg on rescName 
	 * @author Jens Peters
	 * @param rule
	 * @return
	 */
	public String ireg(File source, String targetRescName, String dao, boolean isDirectory) {
		ArrayList<String>  commandAsList = new ArrayList<String>();
		commandAsList.add("ireg");
		commandAsList.add("-f");
		commandAsList.add("-K");
		if (!targetRescName.equals("")) {
			commandAsList.add("-R");
			commandAsList.add(targetRescName);
		}
		if (isDirectory) {
			commandAsList.add("-C");
		}
		commandAsList.add(source.getAbsoluteFile().toString());
		commandAsList.add(dao);
		String[] commandAsArray = new String[commandAsList.size()];
		commandAsArray = commandAsList.toArray(commandAsArray);
		return executeIcommand(commandAsArray);
	}
}
