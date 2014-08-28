package daweb3

class PreservationSystem {
	
	int id
	
	int minRepls
	String sidecarExtensions
	String presServer
	
	String closedCollectionName
	String openCollectionName
	String urisCho
	String urisFile
	String urisAggr
	String urnNameSpace
	
	static constraints = {
		minRepls(nullable:false)
	}
	
    static mapping = {
		table 'preservation_system'
		version false
		id column: 'id'
		minRepls column: 'min_repls'
		sidecarExtensions column: 'sidecar_extensions'
		presServer column: 'pres_server'
		closedCollectionName column: 'closed_collection_name'
		openCollectionName column: 'open_collection_name'
		urisCho column: 'uris_cho'
		urisFile column: 'uris_file'
		urisAggr column: 'uris_aggr'
		urnNameSpace column: 'urn_name_space'
	}
}
