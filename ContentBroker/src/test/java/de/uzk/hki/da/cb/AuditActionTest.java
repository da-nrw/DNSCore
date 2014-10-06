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

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.grid.IrodsGridFacade;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;


/**
 * The Class AuditActionTest.
 *
 * @author Jens Peters
 */
public class AuditActionTest {

	/** The action. */
	public static AuditAction action = new AuditAction();
	
	/**
	 * Sets the up before class.
	 */
	@BeforeClass
	public static void setUpBeforeClass(){
		
	}
		
	/**
	 * Test implementation.
	 */
	@Test
	public void testImplementation(){
		

		Node node = new Node("vm3","01-vm3");
//		node.setLza_resource("lzares");
		
		action.setLocalNode(node);
		
		
		
		Job job = new Job ("csn","vm3");
		
		
		job.setStatus("6000");
		Package pkg = new Package();
		User cont = new User("csn","sam-fs,elsewhere","test@test.de");
		pkg.setName ( "1" );
		pkg.setName ( "2" );
		
		Object obj = new Object();
		obj.setContractor(cont);
		
		IrodsGridFacade grid = mock (IrodsGridFacade.class);
		
		
		
		when ( grid.isValid( anyString()) )
			.thenReturn( true ).thenReturn(false);
			
		action.setJob (job);
		
		action.implementation();
		
	}
}
