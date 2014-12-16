/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln
  Copyright (C) 2014 LVR-InfoKom
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

package de.uzk.hki.da.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * @author Daniel M. de Oliveira
 */
public abstract class Worker {

	protected static final Logger logger = LoggerFactory.getLogger("de.uzk.hki.da.core");
	protected static final String WORKER_ID = "worker_id";
	
	public abstract void scheduleTaskImplementation();
	
	public abstract void setMDC();
	
	public void scheduleTask(){
		setMDC();
		
		scheduleTaskImplementation();
		MDC.remove(WORKER_ID);
	}
}
