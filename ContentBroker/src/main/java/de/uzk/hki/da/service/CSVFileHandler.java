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

/**
 * @author jens Peters
 * Service class for manipulating CSV Files (reading and writing)
 */
package de.uzk.hki.da.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;



public class CSVFileHandler {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass()
			.getName());

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ArrayList<Map> csvEntries = new ArrayList();

	private CsvPreference preference = CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE;

	// standard: assume windows charset from excel docs.
	private String encoding = "CP1252";

	private CellProcessor[] getProcessors() {

		final CellProcessor[] processors = new CellProcessor[] {
				new Optional(), // identifier
				new NotNull(), // origName
				new Optional(), // statuscode
				new Optional(), // erfolg
				new Optional(), // bemerkung	
		};

		return processors;
	}

	public void parseFile(File csvFile) throws IOException {
		logger.debug("parsing " + csvFile.getAbsolutePath());
		Map<String, java.lang.Object> csvEntry;
		ICsvMapReader mapReader = null;
		InputStreamReader isr = null;

		try {
			isr = new InputStreamReader(new FileInputStream(
					csvFile.getAbsolutePath()), encoding);
			mapReader = new CsvMapReader(isr, preference);
			final String[] header = mapReader.getHeader(true);
			final CellProcessor[] processors = getProcessors();

			while ((csvEntry = mapReader.read(header, processors)) != null) {
				logger.debug(String.format(
						"lineNo=%s, rowNo=%s, csvEntries=%s",
						mapReader.getLineNumber(), mapReader.getRowNumber(),
						csvEntry));
				csvEntries.add(csvEntry);
			}
			logger.debug("read " + csvEntries.size() + " rows (incl. header)!");
		} finally {
			if (mapReader != null) {
				mapReader.close();
				isr.close();
			}
		}
	}

	

	public void persistStates(File csvFile) throws IOException {
		ICsvMapWriter mapWriter = null;
		final String[] header = new String[] { "identifier", "origName",
				"statuscode","erfolg","bemerkung" };
		try {

			mapWriter = new CsvMapWriter(new FileWriter(csvFile, false),
					preference);

			final CellProcessor[] processors = getProcessors();
			mapWriter.writeHeader(header);
			for (Map<String, Object> csvEntry : csvEntries) {
				mapWriter.write(csvEntry, header, processors);
			} 
			logger.debug("wrote " + csvEntries.size() + " Entries in " + csvFile);

		} finally {
			if (mapWriter != null) {
				mapWriter.close();
			}
		}
	}
	


	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public ArrayList<Map> getCsvEntries() {
		return csvEntries;
	}

	public void setCsvEntries(ArrayList<Map> csvEntries) {
		this.csvEntries = csvEntries;
	}

}
