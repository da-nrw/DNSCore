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
 * Structure of path: /.../.../...
 * file separator at the beginning of a path
 * no file separator at the end of a path
 *
 * @author Polina Gubaidullina
 * @author Daniel M. de Oliveira
 */
public class Path{
	
	List<String> finalPathArray = new ArrayList<String>();

	/** 
	 * @author Polina Gubaidullina
	 * @param list
	 * @throws IllegalArgumentException if an element of list is not of type string or path
	 */
	protected Path(Object ... list) {
		
		int argNo=0;
		for (Object o: list) {
			argNo++;
			List<String> partialPathArray;
			if (o != null) {
				partialPathArray = new ArrayList<String>();
				if(o instanceof Path || o instanceof RelativePath || o instanceof String) {
					String s = o.toString();
					partialPathArray = stringToPathArray(s);
					for(int i=0; i<partialPathArray.size(); i++) {
						finalPathArray.add(partialPathArray.get(i));
					}					
				} else {
					throw new IllegalArgumentException("the "+argNo+"th (counting from 1) argument was of incorrect data type. path or string expected");
				}
			} else throw new NullPointerException("the "+argNo+"th (counting from 1) argument was null");
		}
	}
	
	private List<String> stringToPathArray(String currentString) {
		List<String> currentPathArray = new ArrayList<String>();
		String s = currentString;
		String [] newString = s.split(File.separator);
		for(int i=0; i<newString.length; i++) {
			if(!newString[i].isEmpty() || !newString[i].equals("")) {
				currentPathArray.add(newString[i]);
			}
		}
		return currentPathArray;
	}

	/**
	 * Creates a Path instance that is either of type Path or of its subtype RelativePath,
	 * dependent if its first element is of type RelativePath. If you assemble Path instances
	 * from existing instances, using this method should always be prefered over using the constructor.
	 * 
	 * @author Daniel M. de Oliveira
	 * @param list
	 * @return
	 */
	public static Path make(Object ... list) {
		
		if ((list[0]) instanceof RelativePath)
			return new RelativePath(list);
		else
			return new Path(list);
	}
	
	/**
	 * @author Daniel M. de Oliveira
	 * @param list
	 * @return
	 */
	public static File makeFile(Object ... list) {
		
		return make(list).toFile();
	}
	

	
	
	/**
	 * @return path /../../..
	 */
	@Override
	public String toString() {
		String directoryString = "";
		for (String i: finalPathArray) {
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
		
		if (finalPathArray.size()!=otherPath.finalPathArray.size()) return false;
		if (!areOfSameType(this, otherPath)) return false;
			
		if (!containSameElements(finalPathArray, otherPath.finalPathArray)) return false;
		return true;
	}

	/**
	 * @param lhs 
	 * @param rhs has to have the same size as lhs
	 * @author Daniel M. de Oliveira
	 */
	private boolean containSameElements(List<String> lhs,List<String> rhs){
		
		boolean equals = true;
		for (int i=0; i<finalPathArray.size(); i++ ){
			if (!finalPathArray.toArray()[i].equals(rhs.toArray()[i])) equals = false; 
		}
		return equals;
	}
	
	/**
	 * @author Daniel M. de Oliveira
	 */
	private boolean areOfSameType(Path t,Path o){
		
		if (t instanceof RelativePath){
			if (!(o instanceof RelativePath)) return false;
		}else{
			if (o instanceof RelativePath) return false;
		}
		return true;
	}
	
	/**
	 * @author Daniel M. de Oliveira
	 */
	@Override 
	public int hashCode(){
		
		return this.toString().hashCode();
	}
	
}
