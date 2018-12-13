package daweb3
 
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

//import grails.compiler.GrailsCompileStatic
//
//@GrailsCompileStatic
@EqualsAndHashCode(includes='authority')
@ToString(includes='authority', includeNames=true, includePackage=false)
class Role implements Serializable {

	private static final long serialVersionUID = 1

	String authority
	
	// 	neu- nicht generiert
	int id
	
	Role(String authority) {
//		this()
		this.authority = authority
	}

	static constraints = {
		authority blank: false, unique: true
	}
 
	static mapping = {
		cache true
		version false
	}

	String toString() {
		return authority + " (ID: "+id+")" ;
	}
}