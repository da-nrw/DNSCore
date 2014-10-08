/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln
  Copyright (C) 2014 LVRInfoKom
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

package de.uzk.hki.da.format;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.core.RelativePath;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PublicationRight;

/**
 * @author Daniel M. de Oliveira
 */
public abstract class PublishConversionStrategyBase implements ConversionStrategy{
	
	/** The audiences. */
	protected String[] audiences = new String [] {"PUBLIC", "INSTITUTION" };
	
	protected Path pips = new RelativePath("dip");
	
	protected Object object;
	
	@Override
	public abstract List<Event> convertFile(ConversionInstruction ci) 
			throws IOException, FileNotFoundException;
	
	
	/**
	 * @author Daniel M. de Oliveira
	 * @param audience
	 * @return
	 */
	protected PublicationRight getPublicationRightForAudience(String audience){
		if (object==null) throw new IllegalStateException("object not set");
		if (object.getRights()==null) throw new IllegalStateException("object rights not set");
		if (object.getRights().getPublicationRights()==null) throw new IllegalStateException("object publication rights not set");
		
		for (PublicationRight right:object.getRights().getPublicationRights()){
			if (right.getAudience().toString().equals(audience)) return right;
		}
		return null;
	}
}
