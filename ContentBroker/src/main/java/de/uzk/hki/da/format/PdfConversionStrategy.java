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
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.SimplifiedCommandLineConnector;
import de.uzk.hki.da.utils.Utilities;



/**
 * LZA Conversion Strategy for PDF Files.
 * Does a conversion using the Ghostscript Library. 
 * @author Jens Peters
 *
 */
public class PdfConversionStrategy implements ConversionStrategy {

	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(PdfConversionStrategy.class);

	/** The command line. */
	protected String commandLine;

	/** The object. */
	private Object object;

	/** The cli connector. */
	protected SimplifiedCommandLineConnector cliConnector;

	/**
	 * Convert file.
	 *
	 * @param ci the ci
	 * @return the list
	 * @throws FileNotFoundException the file not found exception
	 * @Author : jens Peters
	 */
	public List<Event> convertFile(ConversionInstruction ci)
			throws FileNotFoundException {
		if (object.getLatestPackage() == null)
			throw new IllegalStateException("Package not set");
		Path.make(object.getDataPath(),object.getNameOfNewestRep(),
				ci.getTarget_folder()).toFile().mkdirs();
		List<Event> results = new ArrayList<Event>();
		File result = new File(generateTargetFilePath(ci));
		String commandAsArray[] = new String[] { "gs", "-q", "-dPDFA",
				"-dPDFACompatibilityPolicy=1", "-dBATCH", "-dNOPAUSE",
				"-dNOOUTERSAVE", "-dUseCIEColor",
				"-sProcessColorModel=DeviceCMYK", "-sDEVICE=pdfwrite",
				"-sOutputFile=" + result.getAbsolutePath(), "conf/PDFA_def.ps",
				ci.getSource_file().toRegularFile().getAbsolutePath() };
		if (!cliConnector.execute(commandAsArray)) {
			throw new RuntimeException("GS command not succeeded");
		}

		if (result.exists()) {
			DAFile daf = new DAFile(object.getLatestPackage(), object.getNameOfNewestRep(),
					Utilities.slashize(ci.getTarget_folder())
							+ result.getName());
			logger.debug("new dafile:" + daf);
			// TODO: we don't do anything if validation fails!
			boolean pdfa = PdfService.validatePdfA(daf.toRegularFile());
			Event e = new Event();
			e.setType("CONVERT");
			e.setDetail(Utilities.createString(commandAsArray)
					+ " PDFA1/b Validation (pdfbox): " + pdfa);
			e.setSource_file(ci.getSource_file());
			e.setTarget_file(daf);
			e.setDate(new Date());

			results.add(e);
		} else
			logger.error(result.getAbsolutePath() + " does not exist");
		return results;

	
	}

	
	/**
	 * Generate target file path.
	 *
	 * @param ci the ci
	 * @return the string
	 */
	public String generateTargetFilePath(ConversionInstruction ci) {
		String input  = ci.getSource_file().toRegularFile().getAbsolutePath();
		return object.getDataPath()+"/"+object.getNameOfNewestRep()+"/"+Utilities.slashize(ci.getTarget_folder())
				+ FilenameUtils.getBaseName(input)+"."+ci.getConversion_routine().getTarget_suffix();
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

	

