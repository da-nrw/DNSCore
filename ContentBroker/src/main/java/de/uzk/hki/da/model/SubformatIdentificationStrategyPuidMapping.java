/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * @author Daniel M. de Oliveira
 */
@Entity
@Table(name="subformat_identification_strategy_puid_mappings")
public class SubformatIdentificationStrategyPuidMapping {

	/** 
	 * Primary key.
	 * */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	
	/** 
	 * PRONOM Unique Identifier.
	 */
	@Column(name="format_puid")
	private String formatPuid;
	
	
	/**
	 * Name of the conversion script to determine the outcome 
	 * (which then should be one of the values listed in allowedValues.
	 */
	@Column(name="subformat_identification_strategy_name")
	private String subformatIdentificationStrategyName;
	
	
	/**
	 * Gets the id.
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return "SubformatIdentificationStrategyPuidMapping["+getFormatPuid()+","+getSubformatIdentificationStrategyName()+"]";
	}
	
	
	/**
	 * Gets the puid.
	 * @return the puid
	 */
	public String getFormatPuid() {
		return formatPuid;
	}

	
	/*
	 * Sets the puid.
	 * @param PUID the new puid
	 */
	public void setFormatPuid(String puid) {
		this.formatPuid = puid;
	}

	
	/**
	 * Gets the format identifier script name.
	 * @return the format identifier script name
	 */
	public String getSubformatIdentificationStrategyName() {
		return subformatIdentificationStrategyName;
	}

	
	/**
	 * Sets the format identifier script name.
	 * @param subformatIdentificationStrategyName the new format identifier script name
	 */
	public void setSubformatIdentificationStrategyName(String subformatIdentificationStrategyName) {
		this.subformatIdentificationStrategyName = subformatIdentificationStrategyName;
	}
}
