package daweb3
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
    }
    
    static mapping = {
		table 'objects'
		version false
		id column:'data_pk'
		contractor column: 'contractor_id'
		packages joinTable: [key: 'objects_data_pk', column: 'packages_id']
		created column: 'date_created'
		modified column: 'date_modified'
    }
	
	static hasMany = [ packages:Package ]
	
	int id
	String urn
	String identifier
	Contractor contractor
	String origName
	int object_state
	int published_flag
	String created
	String modified
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

		if ( object_state == 50 ) return 2
		if ( object_state == 60 ) return 2
		if ( object_state == 51 ) return 1
		if (object_state == 100 ) return 0
		return 1;
	}
	
	def getPublicPresLink() {
		
		if (identifier!=null && identifier!="" && identifier!="NULL") {
			def grailsApplication = new Object().domainClass.grailsApplication
			def ctx = grailsApplication.mainContext
			def config = grailsApplication.config
			
			def preslink = config.fedora.urlPrefix + "danrw:"+ identifier
			return preslink
		}
		return ""
	}
	def getInstPresLink() {
		
		if (identifier!=null && identifier!="" && identifier!="NULL") {
			def grailsApplication = new Object().domainClass.grailsApplication
			def ctx = grailsApplication.mainContext
			def config = grailsApplication.config
			
			def preslink = config.fedora.urlPrefix + "danrw-closed:"+ identifier
			return preslink
		}
		return ""
	}
	
	
	def getFormattedUrn() {
		if (urn!=null && urn!="" && urn!="NULL") {
			def formurn = urn.replaceAll(~"\\+",":")
			return formurn
		}
		return ""
	}
	
	String toString() {
		return "Objekt " + getFormattedUrn()
	}

	def getFormattedCreatedDate() {
		
	if (created!=null && created!="" && created!="NULL" && created.length()>5) {
		String sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.valueOf(created).longValue() ))
		return sdf
	}
	return "";
	}

	def getFormattedModifiedDate() {
	
	if (modified!=null && modified!="" && modified!="NULL" && modified.length()>5) {
		String sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.valueOf(modified).longValue()) )
		return sdf
	}
	return ""
	
	}
	
}
