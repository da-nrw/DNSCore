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

package de.uzk.hki.da.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import de.uzk.hki.da.ff.ISubformatIdentificationPolicy;


/**
 * The Class SecondStageScanPolicy.
 */
@Entity
@Table(name="second_stage_scans")
public class SecondStageScanPolicy implements ISubformatIdentificationPolicy {

	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	/** The puid. */
	@Column(name="puid")
	private String PUID;
	
	/** The allowed values. */
	@Column(name="allowed_values")
	private String allowedValues;
	
	/**
	 * Name of the conversion script to determine the outcome (which then should be one of the values listed in allowedValues.
	 */
	@Column(name="format_identifier_script_name")
	private String formatIdentifierScriptName;
	
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
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
		return "SecondStageScanPolicy["+getPUID()+","+getFormatIdentifierScriptName()+","+getAllowedValues()+"]";
	}
	
	
	
	/**
	 * Gets the puid.
	 *
	 * @return the puid
	 */
	public String getPUID() {
		return PUID;
	}

	/**
	 * Sets the puid.
	 *
	 * @param PUID the new puid
	 */
	public void setPUID(String PUID) {
		this.PUID = PUID;
	}

	
	/**
	 * Gets the allowed values.
	 *
	 * @return the allowed values
	 */
	public String getAllowedValues() {
		return allowedValues;
	}

	/**
	 * Sets the allowed values.
	 *
	 * @param expectedValues the new allowed values
	 */
	public void setAllowedValues(String expectedValues) {
		this.allowedValues = expectedValues;
	}

	/**
	 * Gets the format identifier script name.
	 *
	 * @return the format identifier script name
	 */
	public String getFormatIdentifierScriptName() {
		return formatIdentifierScriptName;
	}

	/**
	 * Sets the format identifier script name.
	 *
	 * @param conversionScriptName the new format identifier script name
	 */
	public void setFormatIdentifierScriptName(String conversionScriptName) {
		this.formatIdentifierScriptName = conversionScriptName;
	}
	
	
}
