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

package de.uzk.hki.da.model.contract;

import java.util.Date;


/**
 * The Class MigrationRight.
 */
public class MigrationRight {
	
	/**
	 * The Enum Condition.
	 */
	public static enum Condition {
		
		/** The none. */
		NONE, 
 /** The notify. */
 NOTIFY, 
 /** The confirm. */
 CONFIRM
	}
	
	/** The condition. */
	private Condition condition;
	
	/** The start date. */
	private Date startDate;

	/**
	 * Gets the condition.
	 *
	 * @return the condition
	 */
	public Condition getCondition() {
		return condition;
	}

	/**
	 * Sets the condition.
	 *
	 * @param condition the new condition
	 */
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	
	/**
	 * Gets the start date.
	 *
	 * @return the start date
	 */
	public Date getStartDate() {
		return startDate;
	}
	
	/**
	 * Sets the start date.
	 *
	 * @param startDate the new start date
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;		
	}
	
}
