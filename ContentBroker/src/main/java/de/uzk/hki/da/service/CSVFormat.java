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
 */
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
