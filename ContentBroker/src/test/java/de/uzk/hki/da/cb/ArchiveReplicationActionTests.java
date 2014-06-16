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

import static org.mockito.Mockito.mock;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.utils.Path;

/**
 * @author Daniel M. de Oliveira
 */
public class ArchiveReplicationActionTests {
	
	private static String workAreaRootPath;
	private static Node node = new Node();

	Object object = new Object();
	
	@BeforeClass
	public static void prepareNode() {
		workAreaRootPath = "src/test/resources/cb/ArchiveReplicationTests/fork/";
		node.setWorkAreaRootPath(Path.make(workAreaRootPath));
	}
	
	@Before
	public void setUp(){
		setUpObject();
	}
	
	@Test
	public void testHappyPath() throws FileNotFoundException, IOException{
		node.setReplDestinations("");
		Contractor c = new Contractor();
		c.setShort_name("TEST");
		c.setForbidden_nodes("");
		object.setContractor(c);
		
		AbstractAction action = setUpAction(node);
		action.implementation();
	}
	
	@Test
	public void testReplDestsNotSet() throws FileNotFoundException, IOException{
		node.setReplDestinations(null);
		Contractor c = new Contractor();
		c.setShort_name("TEST");
		c.setForbidden_nodes("");
		object.setContractor(c);
		AbstractAction action = setUpAction(node);
		action.implementation();
	}
	
	@Test
	public void testForbiddenNodesNotSet() throws FileNotFoundException, IOException{
		node.setReplDestinations("");
		Contractor c = new Contractor();
		c.setShort_name("TEST");
		c.setForbidden_nodes(null);
		object.setContractor(c);
		AbstractAction action = setUpAction(node);
		action.implementation();
	}
	
	private void setUpObject(){
		object.setIdentifier("identifier");
		Package pkg = new Package();
		pkg.setName("1");
		object.getPackages().add(pkg);
		pkg.setTransientBackRefToObject(object);
	}
	
	private AbstractAction setUpAction(Node node){
		ArchiveReplicationAction action = new ArchiveReplicationAction();
		GridFacade grid = mock (GridFacade.class);
		action.setGridRoot(grid);
		action.setObject(object);
		action.setNode(node);
		return action;
	}
}
