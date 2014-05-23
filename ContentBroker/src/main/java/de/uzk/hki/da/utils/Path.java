/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 

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



package de.uzk.hki.da.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Polina Gubaidullina
 *
 */

public class Path {
	
	List<String> pathArray = new ArrayList<String>();
	
	public Path(List<String> currentPathArray) {
		this.pathArray = currentPathArray;
	}
	
	
	@Override
	public String toString() {
		String directoryString = "";
		for (int i=0; i<this.pathArray.size(); i++) {
			directoryString = directoryString + File.separator + pathArray.get(i);
		}
		return directoryString;
	}
}
