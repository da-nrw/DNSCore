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
 * @author Daniel M. de Oliveira
 */
@Entity
@Table(name="messages")
public class Message {

	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String msg_short;
	
	private String ref_identifier_type;
	
	private String ref_identifier;
	
	private String q;

	private String a;
	
	private Date date;
	
	private Date expirationDate;
	
	private Date acknowledgedDate;
	
	
	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public String getMsg_short() {
		return msg_short;
	}

	public void setMsg_short(String msg_short) {
		this.msg_short = msg_short;
	}

	public String getIdentifier() {
		return ref_identifier;
	}

	public void setIdentifier(String identifier) {
		this.ref_identifier = identifier;
	}

	public String getRef_Identifier_type() {
		return ref_identifier_type;
	}

	public void setRef_Identifier_type(String ref_identifier_type) {
		this.ref_identifier_type = ref_identifier_type;
	}

	public Date getAcknowledgedDate() {
		return acknowledgedDate;
	}

	public void setAcknowledgedDate(Date acknowledgedDate) {
		this.acknowledgedDate = acknowledgedDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
}
