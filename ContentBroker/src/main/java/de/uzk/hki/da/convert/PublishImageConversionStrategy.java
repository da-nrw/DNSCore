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
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.service.CommandLineConnector;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.service.XPathUtils;
import de.uzk.hki.da.utils.ProcessInformation;
import de.uzk.hki.da.utils.Utilities;


/**
 * tested by {@link PublishImageConversionStrategyTest}.
 *
 * @author Jens Peters
 * @author Daniel M. de Oliveira
 */
public class PublishImageConversionStrategy implements ConversionStrategy {

	/** The pkg. */
	private Package pkg;
	
	/** The logger. */
	private static Logger logger = 
			LoggerFactory.getLogger(PublishImageConversionStrategy.class);
	
	/** The audiences. */
	private String[] audiences = new String [] {"PUBLIC", "INSTITUTION" };
	
	/** The dom. */
	Document dom;
	
	/** The cli connector. */
	private CLIConnector cliConnector;
	
	/** The object. */
	private Object object;
	
	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#convertFile(de.uzk.hki.da.model.ConversionInstruction)
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

		String tf = ci.getTarget_folder();
		
		String input  = ci.getSource_file().toRegularFile().getAbsolutePath();
		
		// Convert 
		ArrayList<String> commandAsList  = null;
		for (String audience: audiences ) {

			
			String audience_lc = audience.toLowerCase();
			
			// standard
			commandAsList = new ArrayList<String>();
			commandAsList.add("convert");
			commandAsList.add(ci.getSource_file().toRegularFile().getAbsolutePath());
	
			new File(object.getDataPath() + "dip/" + audience_lc + "/" + ci.getTarget_folder()).mkdirs();

			if (!getResizeDimensionsForAudience(audience).equals("")) {
				commandAsList.add("-resize");
				commandAsList.add(getResizeDimensionsForAudience(audience));
			}
			
			commandAsList = getWatermark(commandAsList,audience);
			
			String footerText = getFooterText(audience);
			if (footerText != null && !footerText.isEmpty()) {
				String width;
				if (!getResizeDimensionsForAudience(audience).equals("")) {
					// set footer width to resize width if resizing happened
					width = getResizeDimensionsForAudience(audience).split("x")[0];
				} else {
					// get image width with prepended identify and use shell variable for footer width
					String[] cmd = new String[]{"identify", "-format", "%w",
							ci.getSource_file().toRegularFile().getAbsolutePath()};
					ProcessInformation pi = CommandLineConnector.runCmdSynchronously(cmd);
					if (pi.getExitValue() != 0) {
						throw new RuntimeException("Unable to get image width. " + pi.getStdErr());
					}
					width = pi.getStdOut().trim();	
					width = pi.getStdOut().trim();			
				}
				commandAsList = buildFooterTextCmd(commandAsList, audience, width);	
			}
			
			DAFile target = new DAFile(pkg,"dip/"+audience_lc,Utilities.slashize(ci.getTarget_folder())+
					FilenameUtils.getBaseName(input)+"."+ci.getConversion_routine().getTarget_suffix());
			commandAsList.add(target.toRegularFile().getAbsolutePath());
			logger.debug(commandAsList.toString());
			
			Event e = new Event();
			e.setDetail(Utilities.createString(commandAsList));
			e.setSource_file(ci.getSource_file());
			e.setTarget_file(target);
			e.setType("CONVERT");
			e.setDate(new Date());
			results.add(e);
			
			String[] commandAsArray = new String[commandAsList.size()];
			commandAsArray = commandAsList.toArray(commandAsArray);
			
			
			if (!cliConnector.execute(commandAsArray))
				throw new RuntimeException("convert did not succeed: " + Arrays.toString(commandAsArray));
			
			
			// thumbnail; purposely commented out -scuy
			// should be replaced by action that creates one thumb per package
			/*commandAsList = new ArrayList<String>();
			commandAsList.add("convert");
			commandAsList.add("-resize");
			commandAsList.add("256x256");
			commandAsList.add(ci.getSource_file().toRegularFile().getAbsolutePath());
	
			DAFile target2 = new DAFile(pkg,"dip/"+audience_lc,Utilities.slashize(ci.getTarget_folder())+
					FilenameUtils.getBaseName(input)+".thumb."+ci.getConversion_routine().getTarget_suffix());
			commandAsList.add(target2.toRegularFile().getAbsolutePath());
			logger.debug(commandAsList.toString());
			
			Event e2 = new Event();
			e2.setDetail(Utilities.createString(commandAsList));
			e2.setSource_file(ci.getSource_file());
			e2.setTarget_file(target2);
			e2.setType("CONVERT");
			e2.setDate(new Date());
			results.add(e2);
			
			commandAsArray = new String[commandAsList.size()];
			commandAsArray = commandAsList.toArray(commandAsArray);
			
			if (!cliConnector.execute(commandAsArray)) throw new RuntimeException("convert did not succeed");
			*/
		}
		
		new File(object.getDataPath()+Utilities.slashize(tf)+"thumbnail").mkdirs();
		
		return results;
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
	private ArrayList<String> buildFooterTextCmd(ArrayList<String> commandAsList, String audience, String width){
		 
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
		commandAsList.add("-size");
		commandAsList.add(width +"x30");
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
		return XPathUtils.getXPathElementText(dom, "/premis:premis/premis:rights/premis:rightsExtension/contract:rightsGranted/contract:publicationRight[contract:audience/text()='" + audience + "']"
				+"/contract:restrictions/contract:restrictImage/contract:footerText/text()");
	}	
	
	
	/**
	 * Adds a Text Watermark to the operation.
	 *
	 * @param commandAsList the command as list
	 * @param audience the audience
	 * @return the watermark
	 * @author Jens Peters
	 * TODO: opacity really needed? Adding composite image (rendered text image + overlay)
	 */
	
	/* Watermarking will not work on Java Versions less than 6u24 due to java bug: http://bugs.sun.com/view_bug.do?bug_id=7032109!
	 * 
	 *  convert -size 300x50 xc:grey30 -font Arial -pointsize 20 -gravity center \
          -draw "fill grey70  text 0,0  'Copyright'" \
          stamp_fgnd.png
  		convert -size 300x50 xc:black -font Arial -pointsize 20 -gravity center \
          -draw "fill white  text  1,1  'Copyright'  \
                             text  0,0  'Copyright'  \
                 fill black  text -1,-1 'Copyright'" \
          +matte stamp_mask.png
  		composite -compose CopyOpacity  stamp_mask.png  stamp_fgnd.png  stamp.png
  		mogrify -trim +repage stamp.png
	 * 
	 * 
	 */
	private ArrayList<String> getWatermark(ArrayList<String> commandAsList, String audience) {
		String text = XPathUtils.getXPathElementText(dom, "/premis:premis/premis:rights/premis:rightsExtension/contract:rightsGranted/contract:publicationRight[contract:audience/text()='" + audience + "']"
				+"/contract:restrictions/contract:restrictImage/contract:watermark/contract:watermarkString/text()");
		if (text == null || text.equals("")) {
			logger.debug("Adding Watermark: text not found for audience " + audience);
			return commandAsList;
		} 	
		commandAsList.add("-font");
		commandAsList.add("Arial");
		commandAsList.add("-pointsize");
		
		String psize = XPathUtils.getXPathElementText(dom, "/premis:premis/premis:rights/premis:rightsExtension/contract:rightsGranted/contract:publicationRight[contract:audience/text()='" + audience + "']"
				+"/contract:restrictions/contract:restrictImage/contract:watermark/contract:pointSize/text()");
		if (psize == null) {
			logger.debug("Adding watermark: point size not found for audience " + audience);
			throw new UserException(UserExceptionId.WATERMARK_NO_POINTSIZE, "No setting for pointsize given while adding watermark");
		}
		commandAsList.add(psize);
		String position = XPathUtils.getXPathElementText(dom, "/premis:premis/premis:rights/premis:rightsExtension/contract:rightsGranted/contract:publicationRight[contract:audience/text()='" + audience + "']"
				+"/contract:restrictions/contract:restrictImage/contract:watermark/contract:position/text()");
		if (position == null) {
			logger.debug("Adding watermark: gravity not found for audience " + audience);
			throw new UserException(UserExceptionId.WATERMARK_NO_GRAVITY, "No setting for gravity given while adding watermark");
		}
		
		String opacity = XPathUtils.getXPathElementText(dom, "/premis:premis/premis:rights/premis:rightsExtension/contract:rightsGranted/contract:publicationRight[contract:audience/text()='" + audience + "']"
				+"/contract:restrictions/contract:restrictImage/contract:watermark/contract:opacity/text()");
		if (opacity == null) {
			logger.debug("Adding watermark: opacity not found for audience " + audience);
			throw new UserException(UserExceptionId.WATERMARK_NO_OPACITY, "No setting for opacity given while adding watermark");
		}
		
		String opacityHex = Long.toHexString(Math.round(Integer.parseInt(opacity) * 2.55));
		if (opacityHex.length() == 1) opacityHex = "0" + opacityHex;
		
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
	private String getResizeDimensionsForAudience(String audience) {
		
		String width= XPathUtils.getXPathElementText(dom, "/premis:premis/premis:rights/premis:rightsExtension/contract:rightsGranted/contract:publicationRight[contract:audience/text()='" +
															audience + "']/contract:restrictions/contract:restrictImage/contract:width/text()");
		String height = XPathUtils.getXPathElementText(dom, "/premis:premis/premis:rights/premis:rightsExtension/contract:rightsGranted/contract:publicationRight[contract:audience/text()='" +
															audience + "']/contract:restrictions/contract:restrictImage/contract:height/text()");
		if (width != null && !width.isEmpty() && height != null && !height.isEmpty()) {
			return width+"x"+height;
		} else {
			logger.debug("No resize information found for audience " + audience);
		} 
		return "";
	}
		
	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setParam(java.lang.String)
	 */
	@Override
	public void setParam(String param) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setDom(org.w3c.dom.Document)
	 */
	@Override 
	public void setDom(Document dom){
		this.dom = dom;
	}
	
	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setCLIConnector(de.uzk.hki.da.convert.CLIConnector)
	 */
	@Override
	public void setCLIConnector(CLIConnector cliConnector) {
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
