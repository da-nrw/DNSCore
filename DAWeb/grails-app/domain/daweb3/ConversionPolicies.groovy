package daweb3

class ConversionPolicies {
	
	int id
	String source_format
	Contractor contractor
	ConversionRoutine conversion_routine;
	
	static mapping = {
		table 'conversion_policies'
		version false
	}
	
    static constraints = {
		contractor column: 'contractor_id'
		conversion_routine column: 'conversion_routine_id'
	  }
}
