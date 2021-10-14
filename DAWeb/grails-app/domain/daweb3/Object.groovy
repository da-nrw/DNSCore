package daweb3

import java.text.DateFormat
import java.text.SimpleDateFormat;

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
/**
 * AIP
 *@Author Jens Peters
 * @Author Scuy
 */
class Object {

	static constraints = {
		dynamic_nondisclosure_limit nullable : true
		static_nondisclosure_limit nullable: true
	}

	static hasMany = [packages: Package]

	static mapping = {
		table 'objects'
		version false
		id column:'data_pk'
		user column: 'user_id'
		packages joinTable: [key: 'objects_data_pk', column: 'packages_id']
		createdAt column: 'created_at'
		modifiedAt column: 'modified_at'
		objectState column: 'object_state'
		aipSize column: 'aip_size'
	}


	int id
	String urn
	String identifier
	User user
	String origName
	String initialNode

	int objectState
	long aipSize
	int published_flag
	int quality_flag

	// due to now unused iRODS functions these fields are still strings, should be
	// refactored to normal Dates
	Date createdAt
	Date modifiedAt

	Date static_nondisclosure_limit
	String dynamic_nondisclosure_limit
	Date last_checked
	String original_formats
	String most_recent_formats;
	String most_recent_secondary_attributes
	Boolean ddb_exclusion

	String getIdAsString() {
		return id.toString();
	}
	/**
	 * retrieves a status code based on the object_state
	 * 0 means normal (grey)
	 * 1 means error (red)
	 * 2 means currently running (yellow)
	 * https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/object_model.md
	 */
	def getStatusCode() {
		if ( objectState == 50 ) return 2
		if ( objectState == 60 ) return 2
		if ( objectState == 51 ) return 1
		if (objectState == 100 ) return 0
		return 1;
	}

	def getPublicPresLink() {

		if (identifier!=null && identifier!="" && identifier!="NULL") {
			def grailsAppl =  new ObjectController().grailsApplication //.getDomainClass("Object").grailsAppl
			def ctx = grailsAppl.mainContext
			def config = grailsAppl.config

			def preslink = config.fedora.urlPrefix + "danrw:"+ identifier
			return preslink
		}
		return ""
	}
	def getInstPresLink() {

		if (identifier!=null && identifier!="" && identifier!="NULL") {
			def grailsApplication = new ObjectController().grailsApplication
			def ctx = grailsApplication.mainContext
			def config = grailsApplication.config

			def preslink = config.fedora.urlPrefix + "danrw-closed:"+ identifier
			return preslink
		}
		return ""
	}

	/**
	 * Ask for Workflow state
	 * @author jpeters
	 * @return
	 */

	boolean isInWorkflowButton() {
		if (objectState==50) {
			return true;
		}
		return false;
	}

	String getTextualObjectState() {
		String state = (String)objectState
		if (objectState==100) {
			state = "archived"
		} else if (objectState==50) {
			state = "Object is in transient state"
		} else {
			state = "archived - but check needed"
		}
		return state;
	}

	def getFormattedUrn() {
		if (urn!=null && urn!="" && urn!="NULL") {
			def formurn = urn.replaceAll(~"\\+",":")
			return formurn
		}
		return ""
	}

	def getFormattedQualityLevel() {
		if (quality_flag!=null && quality_flag!="" && quality_flag!="NULL" && quality_flag!="-1"&& quality_flag!=-1) {
			return ""+quality_flag
		}
		return ""
	}

	def getFormattedQualityLevelNoZero() {
		if (quality_flag!=null && quality_flag!="" && quality_flag!="NULL" &&
		quality_flag!="-1"&& quality_flag!=0&& quality_flag!=-1&& quality_flag!="0") {
			return ""+quality_flag
		}
		return ""
	}

	String toString() {
		return "Objekt " + getFormattedUrn()
	}

	def getFormattedCreatedDate() {

		if (createdAt!=null) {
			String sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(createdAt)
			return sdf
		}
		return "";
	}

	def getFormattedModifiedDate() {

		if (modifiedAt) {
			String sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(modifiedAt)
			return sdf
		}
		return ""

	}

	static Date convertDateIntoDate(String sDate) {
		if (sDate!=null && sDate!="") {
			try {
				SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm")
				Date dt = df.parse(sDate)
				return dt
			} catch (Exception ex) {
				println ("convertDateIntoDate: " + ex);
				return null;
			}
		}
		return null;
	}

	static Date convertStringIntoDate(String sDate) {
		
		if (sDate!=null && sDate!="") {
			try {
				DateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy" )
				dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
				Date dt =  (Date)dateFormat.parse(sDate)
				return dt
			} catch (Exception ex) {
				return null;
			}
		}
	}

	static String convertDateIntoStringDate(String sDate) {
		if (sDate!=null && sDate!="") {
			try {
				SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
				Date dt = df.parse(sDate)
				return String.valueOf(Math.round(dt.getTime()/1000L))
			} catch (Exception ex) {
				return null;
			}
		}

		return null;
	}
}
