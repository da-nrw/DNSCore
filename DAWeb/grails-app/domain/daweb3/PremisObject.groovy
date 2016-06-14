package daweb3

class PremisObject {
	
	//List packages
	
	static constraints = {
	
	}

	//List packages = new ArrayList<PremisPackage>()
	//def packages
	
	
	static hasMany = [packages: PremisPackage]
	
	static mapping = {
		table 'premis_objects'
		version false
		id column:'data_pk'
		packages joinTable: [key: 'premis_objects_data_pk', column: 'packages_id']
		//packages sort: 'name', order:'asc'
	}

	
	int id
	String identifier
	String urn
	File xml

}
