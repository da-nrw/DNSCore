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

package de.uzk.hki.da.convert;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;
import de.uzk.hki.da.utils.StringUtilities;



/**
 * Scans a Tiff file for compression and in case a compression has been detected it converts 
 * the file to a non-compressed version. 
 * In case the file is a multipage tif multiple output files get generated (TODO true?).
 * An event for each target file gets created.
 * 
 * @author Daniel M. de Oliveira
 * @author Jens Peters
 *
 */
public class TiffConversionStrategy implements ConversionStrategy {

	/** The encoding. */
	String encoding;
	
	/** The logger. */
	private static Logger logger = 
			LoggerFactory.getLogger(TiffConversionStrategy.class);
		
	/** The pkg. */
	private Package pkg;
	
	/** The object. */
	private Object object;
	
	private CommandLineConnector cliConnector;
	
	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#convertFile(de.uzk.hki.da.model.ConversionInstruction)
	 */
	@Override
	public List<Event> convertFile(ConversionInstruction ci) {
		
		List<Event> resultEvents = new ArrayList<Event>();
		
		String input  = ci.getSource_file().toRegularFile().getAbsolutePath();
		if (getEncoding(input).equals("None")) return resultEvents;
		
		// create subfolder if necessary
		Path.make(object.getPath("newest"),ci.getTarget_folder()).toFile().mkdirs();
		
		String[] commandAsArray = new String [] {"convert","+compress",input,generateTargetFilePath(ci)};
		logger.info("Executing conversion command: {}", commandAsArray);
		ProcessInformation pi;
		try {
			pi = cliConnector.runCmdSynchronously( commandAsArray );
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		if (pi.getExitValue()!=0) {
			logger.error( this.getClass()+": Recieved return code from terminal based command: "+
					pi.getExitValue() );
			throw new RuntimeException("cli conversion failed!\n\nstdOut: ------ \n\n\n"+
					pi.getStdOut()+"\n\n ----- end of stdOut\n\nstdErr: ------ \n\n\n"+
					pi.getStdErr()+"\n\n ----- end of stdErr");
		}
				
		File result = new File(generateTargetFilePath(ci));
		
		String baseName = FilenameUtils.getBaseName(result.getAbsolutePath());
		String extension = FilenameUtils.getExtension(result.getAbsolutePath());
		logger.info("Finding files matching wildcard expression \""+baseName+"*."+extension+"\" in order to check them and test if conversion was successful");
		List<File> results = findFilesWithWildcard(
				new File(FilenameUtils.getFullPath(result.getAbsolutePath())), baseName+"*."+extension);
		
		for (File f : results){
			DAFile daf = new DAFile(pkg,object.getPath("newest").getLastElement(),StringUtilities.slashize(ci.getTarget_folder())+f.getName());
			logger.debug("new dafile:"+daf);
								
			Event e = new Event();
			e.setType("CONVERT");
			e.setDetail(StringUtilities.createString(commandAsArray));
			e.setSource_file(ci.getSource_file());
			e.setTarget_file(daf);
			e.setDate(new Date());
			
			resultEvents.add(e);
		}
		
		return resultEvents;
	}

	/**
	 * Find files with wildcard.
	 *
	 * @param folderToScan the folder to scan
	 * @param wildcardExpression the wildcard expression
	 * @return all files matching wildcardExpression
	 */
	private List<File> findFilesWithWildcard(File folderToScan,String wildcardExpression){

		List<File> result = new ArrayList<File>();
		
		FileFilter fileFilter = new WildcardFileFilter(wildcardExpression);
		File[] files = folderToScan.listFiles(fileFilter);
		for (int i = 0; i < files.length; i++) {
			result.add(files[i]);
		}
		return result;
	}
	
	
	/**
	 * Gets the encoding.
	 *
	 * @param input the input
	 * @return the encoding
	 */
	private String getEncoding(String input) {
		
		String[] cmd = new String []{
					"identify","-format","'%C'",input};
		ProcessInformation pi;
		System.out.println(input);
		try {
			pi = cliConnector.runCmdSynchronously(cmd);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (pi.getExitValue()!=0){
			logger.error("recieved exit code " + pi.getExitValue());
			parseStdErrAndThrowAdaquateEx(pi.getStdErr());
			
		}
		String compression = pi.getStdOut().trim();
		if (compression.length()>0) 
		compression = compression.substring( 1, compression.length() - 1 );
		
		return compression;
		
	}
	
	/**
	 * Parses StdErr and throws corresponding Error
	 * @author Jens Peters
	 * @param errorCode
	 * @throws Exception
	 */

	private void parseStdErrAndThrowAdaquateEx(String stdErr) {
		if (stdErr.indexOf("RichTIFFIPTC")>=0) {
			throw new UserException(UserExceptionId.WRONG_DATA_TYPE_IPTC, "Probleme mit RichTIFFIPTC. Ausgabe des StdErr: "+ stdErr);
		} else throw new RuntimeException("Stderr: "+ stdErr);
	}
	
	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setParam(java.lang.String)
	 */
	@Override
	public void setParam(String param) {}


	/**
	 * Generate target file path.
	 *
	 * @param ci the ci
	 * @return the string
	 */
	public String generateTargetFilePath(ConversionInstruction ci) {
		String input  = ci.getSource_file().toRegularFile().getAbsolutePath();
		return object.getPath("newest")+"/"+StringUtilities.slashize(ci.getTarget_folder())
				+ FilenameUtils.getName(input);
	}
	

	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setCLIConnector(de.uzk.hki.da.convert.CLIConnector)
	 */
	@Override
	public void setCLIConnector(CommandLineConnector cliConnector) {
		this.cliConnector = cliConnector;
		
	}

	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setObject(de.uzk.hki.da.model.Object)
	 */
	@Override
	public void setObject(Object obj) {
		this.object = obj;
		this.pkg = obj.getLatestPackage();
	}
}
