package daweb3

class Event {

    static constraints = {
		
    }
	
	static mapping = {
		table 'premis_events'
		version false
		id column:'id'
		agentName column: 'agent_name'
		agentType column: 'agent_type'
		sourceFile column: 'source_file_id'
		targetFile column: 'target_file_id'
	}
	
	int id
	String agentName
	String agentType
	String identifier
	Date date
	String detail
	String type
	DAFile sourceFile
	DAFile targetFile
	int pkg_id
}
