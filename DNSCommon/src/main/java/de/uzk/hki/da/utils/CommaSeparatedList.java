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
package de.uzk.hki.da.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;



/**
 * The Class CommaSeparatedList.
 *
 * @author Daniel M. de Oliveira
 */
public class CommaSeparatedList implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The csl. */
	List<String> csl = new ArrayList<String>();
	
	/**
	 * Instantiates a new comma separated list.
	 *
	 * @param string the string
	 */
	public CommaSeparatedList(String string) {
		
		StringTokenizer tokens = new StringTokenizer(string,",");
		while(tokens.hasMoreTokens()){
			csl.add(tokens.nextToken());
		}
	}

	/**
	 * Instantiates a new comma separated list.
	 *
	 * @param list the list
	 */
	public CommaSeparatedList(List<String> list) {
		
		csl = list;
	}

	/**
	 * To list.
	 *
	 * @return the list
	 */
	public List<String> toList() {
		
		return csl;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		
		String result="";
		if (csl.isEmpty()) return result;
		
		for (String f : csl)
		{
			result += f;
			if (!f.equals(""))
				result += ',';
		}
		return result.substring(0,result.length()-1);
	}
}
