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
package de.uzk.hki.da.fs;



import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.uzk.hki.da.utils.CommaSeparatedList;


/**
 * The Class CommaSeparatedListTests.
 *
 * @author Daniel M. de Oliveira
 */
public class CommaSeparatedListTests {

	/**
	 * To list.
	 */
	@Test
	public void toList(){
		
		CommaSeparatedList cs = new CommaSeparatedList("a,b,c,");
		
		List<String> list = cs.toList();
		
		int contains = 0;
		for (String s:list){
			if (s.equals("a")) contains++;
			if (s.equals("b")) contains++;
			if (s.equals("c")) contains++;
		}
		assertThat(contains).isEqualTo(3);
	}
	
	
	/**
	 * To string_.
	 */
	@Test
	public void toString_(){
		
		List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		list.add("c");
		
		CommaSeparatedList cs = new CommaSeparatedList(list);
		
		assertThat(cs.toString()).isEqualTo("a,b,c");
	}
	
	
	
	
	
}
