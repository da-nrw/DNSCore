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
 * @author Polina Gubaidullina
 * @author Daniel M. de Oliveira
 */ 

/**
 * 
 * Structure of path: /.../.../...
 * file separator at the beginning of a path
 * no file separator at the end of a path
 *
 */

public class Path{
	
	List<String> pathArray = new ArrayList<String>();
	
	
	
	
	/** 
	 * 
	 * @author Polina Gubaidullina
	 * @param list
	 * @throws IllegalArgumentException if an element of list is not of type string or path
	 */
	
	public Path(Object ... list) {
		for (Object o: list) {
			if (o != null) {
				if(o instanceof Path || o instanceof RelativePath) {

					String s = o.toString();
					if (!(o  instanceof RelativePath)){
						s = s.substring(1);
					}

					pathArray.add(s); // this could be problematic since there now could be complex elements in path array 
					                  // perhaps it would be better to add the single elements one by one
					
				} else if (o instanceof String) {
					
					String s = o.toString();
					String [] newString = s.split(File.separator);
					for(int i=0; i<newString.length; i++) {
						if(!newString[i].isEmpty()) {
							pathArray.add(newString[i]);
						}
					}
				} else {
					throw new IllegalArgumentException("Incorrect data type: path or string expected");
				}
			} else throw new NullPointerException();
		}
	}

	/**
	 * Creates a Path instance that is either of type Path or of its subtype RelativePath,
	 * dependent if its first element is of type RelativePath. If you assemble Path instances
	 * from existing instances, using this method should always be prefered over using the constructor.
	 * TODO make constructor private
	 * 
	 * @author Daniel M. de Oliveira
	 * @param list
	 * @return
	 */
	public static Path make(Object ... list) {
		
		Path path = null;
		if ((list[0]) instanceof RelativePath){
			path = new RelativePath(list);
		}else
			path = new Path(list);
		
		return path;
	}

	
	
	
	@Override
	public String toString() {
		String directoryString = "";
		for (String i: pathArray) {
			directoryString = directoryString + File.separator + i;
		}
		return directoryString;
	}

	/**
	 * @author Daniel M. de Oliveira
	 * @return the path converted to a regular java file object
	 */
	public File toFile(){
		return new File(this.toString());
	}
	
	
	/**
	 * @author Daniel M. de Oliveira
	 */
	@Override
	public boolean equals(Object o){
		if (!(o instanceof Path)) return false;
		Path otherPath = (Path) o;
		
		if (this instanceof RelativePath){
			if (!(o instanceof RelativePath)){
				return false;
			}
		}
		else{
			if (o instanceof RelativePath){
				return false;
			}
		}
		
		if (pathArray.size()!=otherPath.pathArray.size())
			return false;
		
		boolean equals = true;
		for (int i=0; i<pathArray.size(); i++ ){
			if (!pathArray.toArray()[i].equals(otherPath.pathArray.toArray()[i])) equals= false; 
		}
		
		return equals;
	}
}
