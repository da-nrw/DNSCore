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

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import de.uzk.hki.da.model.Node;

/**
 * @author Daniel M. de Oliveira
 */
public class PathTypeEditorSupportTests {

	@Test
	public void test(){
		
		AbstractApplicationContext context = 
				new FileSystemXmlApplicationContext("src/test/resources/utils/PathTypeEditorSupportTests.beans.xml");
		Node localNode = (Node) context.getBean("localNode");
		context.close();
		assertEquals("/expectedresult", localNode.getWorkAreaRootPath().toString() );
	}
}
