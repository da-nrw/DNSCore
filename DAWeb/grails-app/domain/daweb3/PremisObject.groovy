package daweb3

class PremisObject {
	static constraints = {
	
	}

	static hasMany = [packages: PremisPackage]
	
	static mapping = {
		table 'premis_objects'
		version false
		id column:'data_pk'
		packages joinTable: [key: 'premis_objects_data_pk', column: 'packages_id']
	}

	int id
	String identifier
	String urn
	File xml

}
