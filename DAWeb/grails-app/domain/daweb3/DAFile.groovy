package daweb3

class DAFile {

    static constraints = {
    }
	
	static mapping = {
		table 'dafiles'
		version false
		id column:'id'
	}
	
	int id
	String relative_path
	String rep_name
}
