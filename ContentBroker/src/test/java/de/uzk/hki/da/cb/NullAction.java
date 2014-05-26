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

import org.hibernate.Session;

/**
 * The Class NullAction.
 * @author Daniel M. de Oliveira
 */
public class NullAction extends AbstractAction{

	protected Session session;
	
	/**
	 * Implementation.
	 *
	 * @return true, if successful
	 */
	@Override
	boolean implementation() {
		System.out.println("NULL ACTION");
		return true;
	}

	/**
	 * Rollback.
	 *
	 * @throws Exception the exception
	 */
	@Override
	void rollback() throws Exception {}
	
	
	@Override
	public Session openSession() {
		System.out.println("OPEN SESSION");
		return session;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
}
