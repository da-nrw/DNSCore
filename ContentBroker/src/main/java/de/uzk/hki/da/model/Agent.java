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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * The Class Agent.
 *
 * @author Daniel M. de Oliveira
 */
public class Agent {

	/** The identifier type. */
	private String identifierType;

	/** The name. */
	private String name;
	
	/** The long name. */
	private String longName;
	
	/** The type. */
	private String type;
	
	/**
	 * Gets the identifier type.
	 *
	 * @return the identifier type
	 */
	public String getIdentifierType() {
		return identifierType;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * When called, automatically sets identifier type accordingly.
	 *
	 * @param type the new type
	 * @author Daniel M. de Oliveira
	 */
	public void setType(String type) {
		if ("NODE".equals(type)){
			this.identifierType="NODE_NAME";
		}else if ("CONTRACTOR".equals(type)){
			this.identifierType="CONTRACTOR_SHORT_NAME";
		}else if ("APPLICATION".equals(type)){
			this.identifierType="APPLICATION_NAME";
		}
		this.type = type;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the long name.
	 *
	 * @return the long name
	 */
	public String getLongName() {
		return longName;
	}

	/**
	 * Sets the long name.
	 *
	 * @param longName the new long name
	 */
	public void setLongName(String longName) {
		this.longName = longName;
	}
	
	/**
	 * Gets the type for id type.
	 *
	 * @param idType the id type
	 * @return the type for id type
	 */
	public static String getTypeForIdType(String idType) {
		if ("NODE_NAME".equals(idType)){
			return "NODE";
		}else if ("CONTRACTOR_SHORT_NAME".equals(idType)){
			return "CONTRACTOR";
		}else if ("APPLICATION_NAME".equals(idType)){
			return "APPLICATION";
		} else
			return null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(java.lang.Object obj) {
		if (obj == null)
            return false;
		
		if (!(obj instanceof Agent))
            return false;
		
		if (obj == this)
            return true;       

		Agent agent = (Agent) obj;
        return new EqualsBuilder().
            append(name, agent.name).
            append(type, agent.type).
            isEquals();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(11, 19)
		.append(name)
		.append(type)
		.toHashCode();	
	}
}
