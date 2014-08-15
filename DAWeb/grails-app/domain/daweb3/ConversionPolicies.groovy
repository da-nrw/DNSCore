package daweb3

class ConversionPolicies {
	
	int id
	String source_format
	User contractor
	ConversionRoutine conversion_routine;
	
	static mapping = {
		table 'conversion_policies'
		version false
	}
	
    static constraints = {
		contractor column: 'user_id'
		conversion_routine column: 'conversion_routine_id'
	  }
}
