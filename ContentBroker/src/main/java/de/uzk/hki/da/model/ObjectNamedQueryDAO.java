/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
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

import java.util.List;

import org.hibernate.Session;

import de.uzk.hki.da.service.HibernateUtil;

/**
 * @author Daniel M. de Oliveira
 */
public class ObjectNamedQueryDAO {

	/**
	 * Retrieves Object from the Object Table for a given orig_name and contractor short name.
	 *
	 * @param orig_name the orig_name
	 * @param csn the csn
	 * @return Object object or null if no object with the given combination of orig_name and
	 * contractor short name could be found
	 * @author Stefan Kreinberg
	 * @author Thomas Kleinke
	 * @author Daniel M. de Oliveira
	 */
	public Object getUniqueObject(String orig_name, String csn) {

		Session session = HibernateUtil.openSession();
		session.getTransaction().begin();
		User contractor = getContractor(session, csn);
		
		@SuppressWarnings("rawtypes")
		List l = null;
		l = session.createQuery("from Object where orig_name=?1 and user_id=?2")
						.setParameter("1", orig_name)
						.setParameter("2", contractor.getId())
						.list();
		session.close();
		
		
		if (l.size() > 1) {
			throw new RuntimeException("Found more than one object with name " + orig_name +
					" for user " + csn + "!");
		}
		try {
			Object o = (Object) l.get(0);
			o.setContractor(contractor);
			return o;
		} catch (IndexOutOfBoundsException e1) {
			// no result set - no object
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Gets the contractor.
	 *
	 * @param contractorShortName the contractor short name
	 * @return null if no contractor for short name could be found
	 */
	private User getContractor(Session session, String contractorShortName) {
		@SuppressWarnings("rawtypes")
		List list;	
		list = session.createQuery("from User where short_name=?1")
	
				.setParameter("1",contractorShortName).setReadOnly(true).list();
		
		if (list.isEmpty())
			return null;
	
		return (User) list.get(0);
	}
}
