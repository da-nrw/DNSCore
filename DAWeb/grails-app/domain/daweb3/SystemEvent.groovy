package daweb3

class SystemEvent {
	
	int id;
	String type;
	User user;
	CbNode node;
	String parameter;
	String period;
	Date last_executed
	
    static constraints = {
		parameter(nullable:true)
		period(nullable:true)
		last_executed(nullable:true)
    }
	static mapping = {
		table 'systemevent'
		version false
	}

}
