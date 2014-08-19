package daweb3

class ConversionPolicies {
	
	int id
	String source_format
	ConversionRoutine conversion_routine;
	
	static mapping = {
		table 'conversion_policies'
		version false
	}
	
    static constraints = {
		conversion_routine column: 'conversion_routine_id'
	  }
}
