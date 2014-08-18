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
import java.lang.Object;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;




/**
 * The Class ConversionRoutine.
 */
@Entity
@Table(name="conversion_routines")
public class ConversionRoutine {

	
	/** The id. */
	private int id;
	
	/** The name. */
	private String name;
	
	/** The target_suffix. */
	private String target_suffix;
	
	/** The type. */
	private String type;
	
	// commmand to be executed on the command line
	/** The params. */
	private String params;
	


	/**
	 * Instantiates a new conversion routine.
	 */
	public ConversionRoutine(){}
	
	/**
	 * Instantiates a new conversion routine.
	 *
	 * @param name the name
	 * @param nodes the nodes
	 * @param type the type
	 * @param params the params
	 * @param target_suffix the target_suffix
	 * @param intermediate_folder the intermediate_folder
	 */
	public ConversionRoutine(
			String name,
			String type,
			String params,
			String target_suffix){
		
		this.name=name;
		this.type=type;
		this.params=params;
		this.target_suffix=target_suffix;
	}
	
	

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(final Integer id) {
		this.id = id;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public // just hibernate
	void setName(String name) {
		this.name = name;
	}

	
	/**
	 * An identifier used as a business key. Should be unique across DA-NRW.
	 *
	 * @return the name
	 * @author Daniel M. de Oliveira
	 */
	public String getName() {
		return name;
	}

	
	
	/**
	 * Sets the params.
	 *
	 * @param params the new params
	 */
	public void setParams(String params) {
		this.params = params;
	}

	/**
	 * Gets the params.
	 *
	 * @return the params
	 */
	public String getParams() {
		return params;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	@SuppressWarnings("unused")
	private // just hibernate
	void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return "ConversionRoutine["+getName()+"], "+getParams()+", "+getTarget_suffix()+"]";
	}

	
	/**
	 * Sets the target_suffix.
	 *
	 * @param target_suffix the new target_suffix
	 */
	public void setTarget_suffix(String target_suffix) {
		this.target_suffix = target_suffix;
	}

	/**
	 * Gets the target_suffix.
	 *
	 * @return the target_suffix
	 */
	public String getTarget_suffix() {
		return target_suffix;
	}

	
	/**
	 * Its equality is simply based on the equality of the name since we
	 * assume the name to be our unique business key for ConversionRoutines;.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	@Override public boolean equals(Object obj){
		if (obj==this) return true;
		if (obj==null) return false;
		if (obj.getClass()!=this.getClass()) return false;
		
		if (this.name.equals(  ((ConversionRoutine) obj).getName()  )) return true;
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override public int hashCode(){
		
		// Since we assume the name field which represents the business key to
		// be unique we delegate the computation to name.hashCode()
		return name.hashCode();
	}
}
