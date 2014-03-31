package daweb3

class ConversionRoutine {

	int id
	String paramss
	String name
	String target_suffix
	
   	static mapping = {
		table 'conversion_routines'
		version false
		paramss column: 'params'
	}
	   
	String toString() {
		if (paramss!=null)
		return name + " " + paramss;
		else return name;
	}
}
