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
package de.uzk.hki.da.cb;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.model.Object; 
import de.uzk.hki.da.model.Package; 

/**
 * @author Daniel M. de Oliveira
 */
public class DeleteObjectActionTests {

	@Test
	public void testSetDeleteObjectFlag() throws FileNotFoundException, UserException, IOException{
		Object o = new Object();
		Package p = new Package(); p.setName("1");
		o.getPackages().add(p);
		DeleteObjectAction action = new DeleteObjectAction();
		action.setObject(o);
		
		action.implementation();

		assertTrue(action.DELETEOBJECT);
	}
	
	@Test
	public void testSetDeletePackage() throws FileNotFoundException, UserException, IOException{
		Object o = new Object();
		Package p = new Package(); p.setName("1"); 
		Package p2 = new Package(); p2.setName("2"); 
		o.getPackages().add(p);
		o.getPackages().add(p2);
		DeleteObjectAction action = new DeleteObjectAction();
		action.setObject(o);
		
		action.implementation();

		assertFalse(action.DELETEOBJECT);
		assertEquals(1,o.getPackages().size());
		assertEquals("1",o.getPackages().get(0).getName());
	}
}
