/*
  DA-NRW Software Suite | ContentBroker
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

package de.uzk.hki.da.model;

import de.uzk.hki.da.core.Path;

/**
 * Knows how the WorkArea is structured and how files and objects are organized on it.
 * 
 * @author Daniel M. de Oliveira
 *
 */
public class WorkArea {

	private Object o;
	private Node n;
	
	public WorkArea(Node n,Object o){
		this.o = o;
		this.n = n;
		if (o.getContractor()==null) throw new IllegalStateException("o.getContractor() is null");
		if (o.getIdentifier()==null) throw new IllegalStateException("o.getIdentifier() is null");
		if (o.getContractor().getShort_name()==null) throw new IllegalStateException("o.getContractor().getShort_name() is null");
	}

	public Path getPath(Object o2) {
		
		
		return Path.make(n.getWorkAreaRootPath(),"work",o.getContractor().getShort_name(),o.getIdentifier());
	}
	
	
	
}
