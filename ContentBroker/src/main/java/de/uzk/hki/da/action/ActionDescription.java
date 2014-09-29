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

package de.uzk.hki.da.action;

import java.io.Serializable;


/**
 * The Class ActionDescription.
 */
@SuppressWarnings("serial")
public class ActionDescription implements Serializable{

	/** The job id. */
	private int jobId;
	
	/** The package id. */
	private int packageId;
	
	/** The action type. */
	private String actionType;
	
	private String startStatus;
	
	private String endStatus;
	
	private String description;

	private String name;
	
	/**
	 * Instantiates a new action description.
	 *
	 * @param actionType the action type
	 * @param jobId the job id
	 * @param packageId the package id
	 */
	public ActionDescription(
			String actionType,
			int jobId,
			int packageId){
		this.actionType=actionType;
		this.jobId=jobId;
		this.packageId=packageId;
	}
	
	/**
	 * Instantiates a new action description.
	 *
	 */
	public ActionDescription() {
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return "ActionDescription["+actionType+",jobId:"+jobId+",packageId:"+packageId+"]";
	}
	
	/**
	 * Gets the job id.
	 *
	 * @return the job id
	 */
	public int getJobId() {
		return jobId;
	}
	
	/**
	 * Sets the job id.
	 *
	 * @param jobId the new job id
	 */
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	
	/**
	 * Gets the package id.
	 *
	 * @return the package id
	 */
	public int getPackageId() {
		return packageId;
	}
	
	/**
	 * Sets the package id.
	 *
	 * @param packageId the new package id
	 */
	public void setPackageId(int packageId) {
		this.packageId = packageId;
	}
	
	/**
	 * Gets the action type.
	 *
	 * @return the action type
	 */
	public String getActionType() {
		return actionType;
	}
	
	/**
	 * Sets the action type.
	 *
	 * @param actionType the new action type
	 */
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	/**
	 * @return the startStatus
	 */
	public String getStartStatus() {
		return startStatus;
	}

	/**
	 * @param startStatus the startStatus to set
	 */
	public void setStartStatus(String startStatus) {
		this.startStatus = startStatus;
	}

	/**
	 * @return the endStatus
	 */
	public String getEndStatus() {
		return endStatus;
	}

	/**
	 * @param endStatus the endStatus to set
	 */
	public void setEndStatus(String endStatus) {
		this.endStatus = endStatus;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name=name;
	}

	public String getName() {
		return name;
	}
	
	
}
