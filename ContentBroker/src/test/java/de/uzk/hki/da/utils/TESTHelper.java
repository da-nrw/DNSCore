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
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;


/**
 * The Class TESTHelper.
 *
 * @author Daniel M. de Oliveira
 */
public class TESTHelper {

	/**
	 * Sets up the object.
	 *
	 * @param pkgId the pkg id
	 * @param workAreaRootPath the base path
	 * @return the object
	 */
	public static Object setUpObject(String identifier,Path workAreaRootPath){
		
		return setUpObject(identifier,workAreaRootPath,workAreaRootPath);
	}
	
	/**
	 * Sets up the object.
	 *
	 * @param pkgId the pkg id
	 * @param workAreaRootPath the base path
	 * @return the object
	 */
	public static Object setUpObject(String identifier,Path workAreaRootPath,Path ingestAreaRootPath){
		
		Node node = new Node(); 
		node.setName("testnode");
		node.setWorkAreaRootPath(workAreaRootPath);
		node.setIngestAreaRootPath(ingestAreaRootPath);
		
		Contractor contractor = new Contractor();
		contractor.setShort_name("TEST");
		
		Package pkg = new Package();
		pkg.setName("1");
		pkg.setId(1);
		pkg.setContainerName("testcontainer.tgz");
		
		Object o = new Object();
		o.setContractor(contractor);
		o.setTransientNodeRef(node);
		o.setIdentifier(identifier);
		o.getPackages().add(pkg);
		o.reattach();
		
		return o;
	}
}
