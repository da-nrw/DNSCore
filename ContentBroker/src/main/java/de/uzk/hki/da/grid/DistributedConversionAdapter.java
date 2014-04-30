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

package de.uzk.hki.da.grid;

/**
 * @author Daniel M. de Oliveira
 */
public interface DistributedConversionAdapter {

	/**
	 * 
	 * @param relativePath from work
	 */
	public void create(String relativePath);
	
	/**
	 * 
	 * @param relativePath from work
	 * @param physicalPath on local file system
	 */
	public void register(String relativePath,String physicalPath);
	
	/**
	 * 
	 * @param relativePath from work 
	 */
	public void replicateToLocalNode(String relativePath);
	
	
	/**
	 * @param relative from zone path
	 */
	public void remove(String relativePath);
}
