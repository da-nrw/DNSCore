package daweb3

import java.io.Serializable
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
//import grails.compiler.GrailsCompileStatic

//@GrailsCompileStatic
@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
class User  implements Serializable {

	private static final long serialVersionUID = 1

	transient springSecurityService

	String username
	String password
	boolean enabled = true
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

	
	// ab hier nicht generiert
	int id
	String shortName
	boolean mailsPooled
	boolean usePublicMets
	boolean useVirusScan = true //DANRW-1511: standardmäßig wird gescannt
	boolean deltaOnUrn
	
	String description
	String friendly_file_exts
	String email_contact
	String forbidden_nodes
	
	User(String username, String password) {
		this.username = username
		this.password = password
	}

	static constraints = {
		username blank: false, unique: true
		password blank: false, password:true
		email_contact blank: false
		shortName blank: false, unique: true
		description(nullable:true)
		forbidden_nodes(nullable:true)
		friendly_file_exts(nullable:true)
		
	}

	static mapping = {
		table 'users'
		version false
		password column: 'password'
		accountExpired column: 'accountexpired'
		accountLocked column: 'accountlocked'
		passwordExpired column: 'passwordexpired'
		mailsPooled column: 'mails_pooled'
		usePublicMets column: 'use_public_mets'
		useVirusScan column: 'use_virus_scan'//DANRW-1511
		deltaOnUrn column: 'delta_on_urn'
		friendly_file_exts column: 'friendly_file_exts'
	}
	
	Set<Role> getAuthorities() { 
		 UserRole.findAllByUser(this).collect { it.role } as Set
		
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
	}

	static transients = ['springSecurityService']

	String toString() {
		return "$shortName"
	}
}
