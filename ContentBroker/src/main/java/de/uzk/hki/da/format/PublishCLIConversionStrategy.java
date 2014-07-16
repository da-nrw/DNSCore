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
import java.util.regex.Matcher;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.Utilities;


/**
 * The Class PublishCLIConversionStrategy.
 */
public class PublishCLIConversionStrategy extends CLIConversionStrategy {

	/** The logger. */
	@SuppressWarnings("unused")
	private static Logger logger = 
			LoggerFactory.getLogger(PublishCLIConversionStrategy.class);
	
	/** The audiences. */
	private String[] audiences = new String [] {"PUBLIC", "INSTITUTION" };

	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.CLIConversionStrategy#convertFile(de.uzk.hki.da.model.ConversionInstruction)
	 */
	public List<Event> convertFile(ConversionInstruction ci) throws FileNotFoundException {
		if (pkg==null) throw new IllegalStateException("Package not set");
		
		List<Event> results = new ArrayList<Event>();
		
		for (String audience: audiences ) {
			
			String audience_lc = audience.toLowerCase();
			String repName = "dip/"+audience_lc;
		
			Path.make(object.getDataPath(),repName,ci.getTarget_folder()).toFile().mkdirs();
			
			String[] commandAsArray = assemble(ci, repName);
			if (!cliConnector.execute(commandAsArray)) throw new RuntimeException("convert did not succeed");
			
			String targetSuffix= ci.getConversion_routine().getTarget_suffix();
			if (targetSuffix.equals("*")) targetSuffix= FilenameUtils.getExtension(ci.getSource_file().toRegularFile().getAbsolutePath());
			DAFile result = new DAFile(pkg, repName,
					ci.getTarget_folder()+"/"+FilenameUtils.removeExtension(Matcher.quoteReplacement(
					FilenameUtils.getName(ci.getSource_file().toRegularFile().getAbsolutePath()))) + "." + targetSuffix);
			
			Event e = new Event();
			e.setType("CONVERT");
			e.setDetail(Utilities.createString(commandAsArray));
			e.setSource_file(ci.getSource_file());
			e.setTarget_file(result);
			e.setDate(new Date());
			
			results.add(e);
		
		}
		
		return results;
		
	}
	
}
