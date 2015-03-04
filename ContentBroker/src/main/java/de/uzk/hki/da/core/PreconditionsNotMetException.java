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
package de.uzk.hki.da.core;

/**
 * Used to signal that the object is not in a state appropriate for the action to perform
 * its actions as intended.
 * 
 * @author Daniel M. de Oliveira
 */
public class PreconditionsNotMetException extends RuntimeException{

	public PreconditionsNotMetException(String msg) {
		super("The conditions to execute the actual action are not given. Problem description: "+msg);
	}
	
	private static final long serialVersionUID = 2L;

}
