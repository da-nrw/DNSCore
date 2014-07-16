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
import java.io.IOException;
import java.util.List;

import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.SimplifiedCommandLineConnector;


/**
 * 
 * @author Daniel M. de Oliveira
 *
 */
public interface ConversionStrategy {

	/**
	 * Converts a single file based on the information of a ConversionInstruction ci.
	 * 
	 * Implementers make sure:
	 * <li>Any implementation should generate an event stating the details of the conversion.
	 * The events and files are automatically bound to the package later in converter service.
	 * @param ci
	 * @return an event for every conversion that actually happened.
	 */
	public List<Event> convertFile(ConversionInstruction ci) throws IOException, FileNotFoundException;

	public void setParam(String param);
	
	/**
	 * TODO put this into convert file to make the side effects (events for example) more transparent.
	 * TODO or make the package links outside which seems better anyway
	 * @param pkg that correlates with an xip to which the file(s) to be converted belong.
	 */
	public void setObject(Object obj);
	
	

	/**
	 * @param cliConnector
	 */
	public void setCLIConnector(SimplifiedCommandLineConnector cliConnector);

}
