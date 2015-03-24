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

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Instances of Copy represent secondary copies of {@link Package}s.
 * 
 * @author Jens Peters
 * @author Daniel M. de Oliveira
 */
@Entity
@Table(name="copies")
public class Copy {

	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String checksum;

	private Date checksumDate;
	
	private String path;
	
	
	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public Date getChecksumDate() {
		return checksumDate;
	}

	public void setChecksumDate(Date checksumDate) {
		this.checksumDate = checksumDate;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	
}
