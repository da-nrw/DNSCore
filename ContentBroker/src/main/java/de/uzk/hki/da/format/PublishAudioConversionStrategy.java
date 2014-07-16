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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.AudioRestriction;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.SimplifiedCommandLineConnector;
import de.uzk.hki.da.utils.Utilities;


/**
 * The Class PublishAudioConversionStrategy.
 * @author unknown
 * @author Daniel M. de Oliveira
 */
public class PublishAudioConversionStrategy extends PublishConversionStrategyBase {

	/** The logger. */
	private static Logger logger = 
			LoggerFactory.getLogger(PublishAudioConversionStrategy.class);
	
	/** The cli connector. */
	private SimplifiedCommandLineConnector cliConnector;

	
	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#convertFile(de.uzk.hki.da.model.ConversionInstruction)
	 */
	@Override
	public List<Event> convertFile(ConversionInstruction ci)
			throws FileNotFoundException {
		if (object==null) throw new IllegalStateException("object not set");
		
		if (cliConnector==null) throw new IllegalStateException("cliConnector not set");

		Path.makeFile(object.getDataPath(),pips,"public",ci.getTarget_folder()).mkdirs();
		Path.makeFile(object.getDataPath(),pips,"institution",ci.getTarget_folder()).mkdirs();
		
		List<Event> results = new ArrayList<Event>();
		
		for (String audience:audiences){
		
			Path source = Path.make(ci.getSource_file().toRegularFile().getAbsolutePath());
			Path target = Path.make(object.getDataPath(),pips,audience.toLowerCase(),
					ci.getTarget_folder(),FilenameUtils.getBaseName(ci.getSource_file().toRegularFile().getAbsolutePath())+".mp3");
			logger.debug("source:"+FilenameUtils.getBaseName(ci.getSource_file().toRegularFile().getAbsolutePath()));
			
			String cmdPUBLIC[] = new String[] {
					"sox",
					source.toString(),
					target.toString()
			};
			logger.debug("source:"+source);
			logger.debug("target:"+target);
			if (!cliConnector.execute((String[]) ArrayUtils.addAll(cmdPUBLIC,getDurationRestrictionsForAudience(audience)))){
				throw new RuntimeException("command not succeeded");
			}
			
			DAFile f1 = new DAFile(object.getLatestPackage(), pips+"/"+audience.toLowerCase(), Utilities.slashize(ci.getTarget_folder()) + 
					FilenameUtils.getBaseName(ci.getSource_file().toRegularFile().getAbsolutePath()) + ".mp3");
					
			Event e = new Event();
			e.setType("CONVERT");
			e.setSource_file(ci.getSource_file());
			e.setTarget_file(f1);
			e.setDetail(Utilities.createString(cmdPUBLIC));
			e.setDate(new Date());
			results.add(e);
		}
	
		return results;
	}

	/**
	 * Gets the duration restrictions for audience.
	 *
	 * @param audience the audience
	 * @return the duration restrictions for audience
	 */
	private String[] getDurationRestrictionsForAudience(String audience) {
		
		if (getPublicationRightForAudience(audience) == null)
			return new String[]{};
		
		AudioRestriction audioRestriction = getPublicationRightForAudience(audience).getAudioRestriction();
		
		if (audioRestriction != null && audioRestriction.getDuration() != null) {
			String duration = audioRestriction.getDuration().toString();
				
			logger.debug("duration restriction for audience " + audience + ": " + duration+ " Adding \"trim 0 "+duration+"\" to command ");
			if (!duration.isEmpty())
				return new String[] { "trim", "0", duration };
		}
		
		logger.debug("No resize information found for audience " + audience);
		
		return new String[]{};
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setParam(java.lang.String)
	 */
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
	}

}
