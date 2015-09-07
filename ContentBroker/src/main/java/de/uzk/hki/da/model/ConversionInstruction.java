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
import java.security.InvalidParameterException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;




/**
 * The Class ConversionInstruction.
 */
@Entity
@Table(name="conversion_queue")
public class ConversionInstruction {

	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/** The additional_params. */
	@Column(columnDefinition="varchar(200)")
	private String additional_params;
	
	/** The conversion_routine. */
	@ManyToOne(targetEntity=ConversionRoutine.class)
	private ConversionRoutine conversion_routine;
	
	/** The target_folder. */
	@Column(columnDefinition="varchar(500)")
	private String target_folder;
	
	/** The node. */
	/** The target_folder. */
	@Column(columnDefinition="varchar(50)")
	private String node;
	
	/** The source_file. */
	@OneToOne(targetEntity=DAFile.class)
	private DAFile source_file = null;
	
	
	/**
	 * Instantiates a new conversion instruction.
	 */
	public ConversionInstruction(){}
	
	
	/**
	 * Instantiates a new conversion instruction.
	 *
	 * @param rhs the rhs
	 */
	public ConversionInstruction(ConversionInstruction rhs){
		this.target_folder = rhs.target_folder;
		this.conversion_routine = rhs.conversion_routine;
		this.additional_params = rhs.additional_params;
		this.source_file = rhs.source_file;
	}
	
	
	/**
	 * Instantiates a new conversion instruction.
	 *
	 * @param jobId the job id
	 * @param target_folder the target_folder
	 * @param routine the routine
	 * @param additionalParams the additional params
	 */
	public ConversionInstruction(
		int jobId,
		String target_folder,
		ConversionRoutine routine,
		String additionalParams
		){
		this.target_folder=target_folder;
		this.conversion_routine=routine;
		this.additional_params=additionalParams;
	};
	
	/**
	 * Sets the conversion_routine.
	 *
	 * @param conversion_routine the new conversion_routine
	 */
	public void setConversion_routine(ConversionRoutine conversion_routine) {
		this.conversion_routine = conversion_routine;
	}
	
	/**
	 * Gets the conversion_routine.
	 *
	 * @return the conversion_routine
	 */
	public ConversionRoutine getConversion_routine() {
		return conversion_routine;
	}
	
	/**
	 * Sets the target_folder.
	 *
	 * @param target_folder the new target_folder
	 */
	public void setTarget_folder(String target_folder) {
		this.target_folder = target_folder;
	}
	
	/**
	 * Gets the target_folder.
	 *
	 * @return the target_folder
	 */
	public String getTarget_folder() {
		return target_folder;
	}
	
	/**
	 * Sets the node.
	 *
	 * @param node the new node
	 */
	public void setNode(String node) {
		this.node = node;
	}
	
	/**
	 * Gets the node.
	 *
	 * @return the node
	 */
	public String getNode() {
		return node;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String conversionRoutineName = "null";
		if (getConversion_routine()!=null)
			conversionRoutineName = getConversion_routine().getName();
		
		return "ConversionInstruction["+source_file.getRep_name()+"/"+source_file.getRelative_path()+"->"+getTarget_folder()+
			",CRoutine:"+conversionRoutineName+",Node:"+getNode()+",AdditionalParams:"+
				getAdditional_params()+"]";
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the additional_params.
	 *
	 * @return the additional_params
	 */
	public String getAdditional_params() {
		return additional_params;
	}

	/**
	 * Sets the additional_params.
	 *
	 * @param additional_params the new additional_params
	 */
	public void setAdditional_params(String additional_params) {
		this.additional_params = additional_params;
	}

	
	/** 
	 * @throws InvalidParameterException if o is null or this or o lacks a conversion_routine or source_file.
	 * @author Daniel M. de Oliveira
	 */
	@Override
	public boolean equals(java.lang.Object o) {
		
		if (o==null) throw new IllegalArgumentException("o is null");
		ConversionInstruction oInstr = (ConversionInstruction) o;
		
		if (oInstr.getSource_file()==null) throw new IllegalArgumentException("oInstr.getSource_file is null");
		if (this.getSource_file()  ==null) throw new IllegalArgumentException("this.getSource_file is null");
		if (oInstr.getConversion_routine()==null) throw new IllegalArgumentException("oInstr.getConversionRoutine is null");
		if (this.getConversion_routine()==null) throw new IllegalArgumentException("this.getConversionRoutine is null");
		
		if (this.conversion_routine == oInstr.conversion_routine &&
			this.getSource_file().getRelative_path().equals(oInstr.getSource_file().getRelative_path()) &&
			this.getSource_file().getRep_name().equals(oInstr.getSource_file().getRep_name())
			)
			return true;
		else
			return false;
	}


	/**
	 * Gets the source_file.
	 *
	 * @return the source_file
	 */
	@XmlTransient
	public DAFile getSource_file() {
		return source_file;
	}


	/**
	 * Sets the source_file.
	 *
	 * @param source_file the new source_file
	 */
	public void setSource_file(DAFile source_file) {
		this.source_file = source_file;
	}
}
