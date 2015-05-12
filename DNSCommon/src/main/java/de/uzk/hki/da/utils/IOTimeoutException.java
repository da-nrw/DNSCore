/*
 DA-NRW Software Suite | ContentBroker
 Copyright (C) 2015 LVRInfoKom
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


package de.uzk.hki.da.utils;

import java.io.IOException;

/**
 * Used to signal that a process has not been 
 * finished after a	predefined amount of time.
 * 
 * @author Daniel M. de Oliveira
 */
public class IOTimeoutException extends IOException {

	private static final long serialVersionUID = 670010576858353531L;

	public IOTimeoutException(String msg) {
		super(msg);
	}
	
}
