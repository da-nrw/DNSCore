package daweb3

class SystemEvent {
	
	int id;
	String type;
	User user;
	CbNode node;
	String parameter
	
    static constraints = {
    }
	static mapping = {
		table 'systemevent'
		version false
	}

}
