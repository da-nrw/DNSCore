/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2015 LVR-InfoKom
  Landschaftsverband Rheinland

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

package de.uzk.hki.da.model;

import org.hibernate.Session;

import de.uzk.hki.da.core.RegisterObjectService;
import de.uzk.hki.da.service.HibernateUtil;

/**
 * Used to update a {@link Node}s association to copy without
 * touching the urn_index field which only {@link RegisterObjectService}
 * is allowed to do. 
 * 
 * @author Daniel M. de Oliveira
 *
 */
public class NodeNamedQueryDAO {

	private Node node;

	public NodeNamedQueryDAO(Node node) {
		this.node = node;
	}

	public void addCopy(Copy copy) {
		if (copy==null) throw new IllegalArgumentException("Must not be null: copy");
		if (copy.getId()<=0) throw new IllegalArgumentException("Must be a value greater than 0: copy.getId()");
		if (node==null) throw new IllegalStateException("Must not be null: node");
		if (node.getId()<=0) throw new IllegalStateException("Must be a value greater than 0: node.getId()");
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.createSQLQuery("UPDATE copies SET node_id="+node.getId()+" WHERE id = "+copy.getId()).executeUpdate();
		session.getTransaction().commit();
		session.close();
	}
	
	

}
