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

package de.uzk.hki.da.format;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.SimplifiedCommandLineConnector;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;
import de.uzk.hki.da.utils.Utilities;


/**
 * tested by {@link PublishImageConversionStrategyTest}.
 * @author Jens Peters
 * @author Daniel M. de Oliveira
 */
public class PublishImageConversionStrategy extends PublishConversionStrategyBase {

	/** The pkg. */
	private Package pkg;
	
	/** The logger. */
	private static Logger logger = 
			LoggerFactory.getLogger(PublishImageConversionStrategy.class);
	
	/** The cli connector. */
	private SimplifiedCommandLineConnector cliConnector;
	
	private String resizeWidth = null;
	
	
	/**
	 */
	@Override
	public List<Event> convertFile(ConversionInstruction ci)
			throws FileNotFoundException {
		if (cliConnector==null) throw new IllegalStateException("cliConnector not set");
		if (ci.getConversion_routine()==null) throw new IllegalStateException("conversionRoutine not set");
		if (ci.getConversion_routine().getTarget_suffix()==null||
				ci.getConversion_routine().getTarget_suffix().isEmpty()) 
			throw new IllegalStateException("target suffix in conversionRoutine not set");
		
		List<Event> results = new ArrayList<Event>();
		
		// connect dafile to package

		String input  = ci.getSource_file().toRegularFile().getAbsolutePath();
		
		// Convert 
		ArrayList<String> commandAsList  = null;
		for (String audience: audiences ) {
			
			Path.makeFile(object.getDataPath(),pips,audience.toLowerCase(),ci.getTarget_folder()).mkdirs();

			commandAsList = new ArrayList<String>();
			commandAsList.add("convert");
			commandAsList.add(ci.getSource_file().toRegularFile().getAbsolutePath());
			logger.debug(commandAsList.toString());
			commandAsList = assembleResizeDimensionsCommand(commandAsList,audience);
			commandAsList = assembleWatermarkCommand(commandAsList,audience);
			commandAsList = assembleFooterTextCommand(commandAsList, audience, ci.getSource_file().toRegularFile().getAbsolutePath());
			
			DAFile target = new DAFile(pkg,pips+"/"+audience.toLowerCase(),Utilities.slashize(ci.getTarget_folder())+
					FilenameUtils.getBaseName(input)+"."+ci.getConversion_routine().getTarget_suffix());
			commandAsList.add(target.toRegularFile().getAbsolutePath());
			
			logger.debug(commandAsList.toString());
			String[] commandAsArray = new String[commandAsList.size()];
			commandAsArray = commandAsList.toArray(commandAsArray);
			if (!cliConnector.execute(commandAsArray))
				throw new RuntimeException("convert did not succeed: " + Arrays.toString(commandAsArray));
			
			// In order to support multipage tiffs, we check for files by wildcard expression
			String extension = FilenameUtils.getExtension(target.toRegularFile().getAbsolutePath());
			List<File> wild = findFilesWithRegex(
					new File(FilenameUtils.getFullPath(target.toRegularFile().getAbsolutePath())), 
					Pattern.quote(FilenameUtils.getBaseName(target.getRelative_path()))+"-\\d+\\."+extension);
			if (!target.toRegularFile().exists() && !wild.isEmpty()){
				for (File f : wild){
					DAFile multipageTarget = new DAFile(pkg,pips+"/"+audience.toLowerCase(),Utilities.slashize(ci.getTarget_folder())+f.getName());
					
					Event e = new Event();
					e.setDetail(Utilities.createString(commandAsList));
					e.setSource_file(ci.getSource_file());
					e.setTarget_file(multipageTarget);
					e.setType("CONVERT");
					e.setDate(new Date());
					results.add(e);
				}
			}
			else{
				Event e = new Event();
				e.setDetail(Utilities.createString(commandAsList));
				e.setSource_file(ci.getSource_file());
				e.setTarget_file(target);
				e.setType("CONVERT");
				e.setDate(new Date());
				results.add(e);
			}
		}
		
		return results;
	}
	
	
	/**
	 * Find files with wildcard.
	 *
	 * @param folderToScan the folder to scan
	 * @param regexExpression the wildcard expression
	 * @return all files matching wildcardExpression
	 */
	private List<File> findFilesWithRegex(File folderToScan,String regexExpression){
		logger.debug("scan_folder:"+folderToScan);
		List<File> result = new ArrayList<File>();
		
		FileFilter fileFilter = new RegexFileFilter(regexExpression);
		File[] files = folderToScan.listFiles(fileFilter);
		for (int i = 0; i < files.length; i++) {
				result.add(files[i]);
		}
		return result;
	}
	
	
	private String getImageWidth(String absolutePath) {
		String[] cmd = new String[]{"identify", "-format", "%w",
				absolutePath};
		ProcessInformation pi = CommandLineConnector.runCmdSynchronously(cmd);
		if (pi.getExitValue() != 0) {
			throw new RuntimeException("Unable to get image width. " + pi.getStdErr());
		}
		return pi.getStdOut().trim();
	}

	/**
	 * Builds the footer text cmd.
	 *
	 * @param commandAsList the command as list
	 * @param audience the audience
	 * @param width the width
	 * @return the array list
	 * @author Jens Peters
	 */
	private ArrayList<String> assembleFooterTextCommand(ArrayList<String> commandAsList, String audience, String pathToFile){
		if (getPublicationRightForAudience(audience)==null) return commandAsList;
		if (getPublicationRightForAudience(audience).getImageRestriction()==null) return commandAsList;
		
		
		String text = getFooterText(audience);
		if (text == null || text.equals("")) {
			logger.debug("Adding Footertext: Footertext not found for audience " + audience );
			return commandAsList;
		} 	
		
		commandAsList.add("-background");
		commandAsList.add("'#0008'");
		commandAsList.add("-fill");
		commandAsList.add("white");
		commandAsList.add("-gravity");
		commandAsList.add("center");
		
		String footerText = getFooterText(audience);
		if (footerText != null && !footerText.isEmpty()) {

			if (resizeWidth!=null){
				commandAsList.add("-size");
				commandAsList.add(resizeWidth + "x30");
			}
			else{ 
				commandAsList.add("-size");
				commandAsList.add(getImageWidth(pathToFile) + "x30");
			}
		}
		
		
		commandAsList.add("caption:\""+text+"\"");
		commandAsList.add("-gravity");
		commandAsList.add("south");
		commandAsList.add("-composite");
		return commandAsList;
	}
	
	/**
	 * Gets the footer text.
	 *
	 * @param audience the audience
	 * @return the footer text
	 */
	private String getFooterText(String audience) {
		if ((getPublicationRightForAudience(audience)==null) ||
				(getPublicationRightForAudience(audience).getImageRestriction()==null) ||  
				(getPublicationRightForAudience(audience).getImageRestriction().getFooterText()==null))
			return "";
		else
			return getPublicationRightForAudience(audience).getImageRestriction().getFooterText();
	}	
	
	
	/**
	 * Adds a Text Watermark to the operation.
	 *
	 * @param commandAsList the command as list
	 * @param audience the audience
	 * @return the watermark
	 * @author Jens Peters
	 */
	private ArrayList<String> assembleWatermarkCommand(ArrayList<String> commandAsList, String audience) {
		if (getPublicationRightForAudience(audience)==null) return commandAsList;
		if (getPublicationRightForAudience(audience).getImageRestriction()==null) return commandAsList;
		
		String text = getPublicationRightForAudience(audience).getImageRestriction().getWatermarkString();
		if (text == null || text.equals("")) {
			logger.debug("Adding Watermark: text not found for audience " + audience);
			return commandAsList;
		} 	
		String psize = getPublicationRightForAudience(audience).getImageRestriction().getWatermarkPointSize();
		if (psize == null) {
			logger.debug("Adding watermark: point size not found for audience " + audience);
			throw new UserException(UserExceptionId.WATERMARK_NO_POINTSIZE, "No setting for pointsize given while adding watermark");
		}
		String position = getPublicationRightForAudience(audience).getImageRestriction().getWatermarkPosition();
		if (position == null) {
			logger.debug("Adding watermark: gravity not found for audience " + audience);
			throw new UserException(UserExceptionId.WATERMARK_NO_GRAVITY, "No setting for gravity given while adding watermark");
		}
		String opacity = getPublicationRightForAudience(audience).getImageRestriction().getWatermarkOpacity();
		if (opacity == null) {
			logger.debug("Adding watermark: opacity not found for audience " + audience);
			throw new UserException(UserExceptionId.WATERMARK_NO_OPACITY, "No setting for opacity given while adding watermark");
		}
		
		String opacityHex = Long.toHexString(Math.round(Integer.parseInt(opacity) * 2.55));
		if (opacityHex.length() == 1) opacityHex = "0" + opacityHex;
		
		commandAsList.add("-font");
		commandAsList.add("Arial");
		commandAsList.add("-pointsize");
		commandAsList.add(psize);
		commandAsList.add("-draw");
		commandAsList.add("gravity "+ position +" fill #000000" + opacityHex + " text 0,15 '"+ text +"' fill #ffffff" + opacityHex + " text 0,14 '"+ text +"'");
		
		return commandAsList;
	}
	
	
	/**
	 * Gets the resize dimensions for audience.
	 *
	 * @param audience the audience
	 * @return the resize dimensions for audience
	 */
	private ArrayList<String> assembleResizeDimensionsCommand(ArrayList<String> commandAsList,String audience) {
		if (getPublicationRightForAudience(audience)==null) return commandAsList;
		if (getPublicationRightForAudience(audience).getImageRestriction()==null) return commandAsList;
		
		String width= getPublicationRightForAudience(audience).getImageRestriction().getWidth();
		String height= getPublicationRightForAudience(audience).getImageRestriction().getHeight();

		if (width != null && !width.isEmpty() && height != null && !height.isEmpty()) {
			commandAsList.add("-resize");
			commandAsList.add(width+"x"+height);
			resizeWidth = width;
			return commandAsList;
		} else {
			logger.debug("No resize information found for audience " + audience);
		} 
		return commandAsList;
	}
		
	@Override
	public void setParam(String param) {}

	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setCLIConnector(de.uzk.hki.da.convert.CLIConnector)
	 */
	@Override
	public void setCLIConnector(SimplifiedCommandLineConnector cliConnector) {
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
