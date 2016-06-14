package daweb3

class PremisPackage {
	
	
	static constraints = {
	}
	
	static mapping = {
		table 'premis_packages'
		version false
		sort: "name"
	}
	
	//static belongsTo = [object: PremisObject]
	
	int id
	String name
	String container_name
	
	String toString() {
		return "Paket Nr. " + name + " (ID: "+id+" : " + container_name + ")" ;
	}
}
