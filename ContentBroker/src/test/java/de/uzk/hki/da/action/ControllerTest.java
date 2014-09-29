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

package de.uzk.hki.da.action;

import org.junit.Test;

import de.uzk.hki.da.action.ActionFactory;
import de.uzk.hki.da.action.ActionInformation;
import de.uzk.hki.da.core.Controller;
import de.uzk.hki.da.core.HibernateUtil;


/**
 * This is actually not a test but is used to launch 
 * the controller server locally.
 * @author Daniel M. de Oliveira
 *
 */
public class ControllerTest {

	/**
	 * Test.
	 */
	@Test
	public void test(){
		HibernateUtil.init("conf/hibernateCentralDbWithInmem.cfg.xml");
		
		ActionFactory factory = new ActionFactory();
		Controller controller = new Controller("localhost",
				4455, factory,new ActionInformation(), null,null);
		controller.run();
	}
}
