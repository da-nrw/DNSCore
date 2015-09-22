package de.uzk.hki.da.service;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

public class CSVFormat {
	
	public static final String[] header = new String[] { "identifier", "origName",
			"statuscode","erfolg","bemerkung" };
	
	public static final CellProcessor[] getProcessors() {

		final CellProcessor[] processors = new CellProcessor[] {
				new Optional(), // identifier
				new NotNull(), // origName
				new Optional(), // statuscode
				new Optional(), // erfolg
				new Optional(), // bemerkung	
		};

		return processors;
	}
}
