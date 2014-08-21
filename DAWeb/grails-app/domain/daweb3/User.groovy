package daweb3

class User {

	 transient springSecurityService
	 
	 int id
	 String shortName
	 
	 String username 
	 String password 
	 
	 boolean enabled = true
	 boolean accountExpired 
	 boolean accountLocked 
	 boolean passwordExpired
	 String description
	 String email_contact
	 String forbidden_nodes

	 static constraints = {
		 email_contact blank: false
		 shortName blank: false, unique: true
		 username blank: false, unique: true 
		 password blank: false 
		 description(nullable:true)
		 forbidden_nodes(nullable:true)
		 }

	 static mapping = { 
		table 'users'
		version false
		password column: 'password'
		accountExpired column: 'accountexpired'
		accountLocked column: 'accountlocked'
		passwordExpired column: 'passwordexpired'
		}

	 Set<Role> getAuthorities() { UserRole.findAllByUser(this).collect { it.role } as Set }

	 def beforeInsert() { encodePassword() }

	 def beforeUpdate() { if (isDirty('password')) { encodePassword() } }

	 protected void encodePassword() {
		  password = springSecurityService.encodePassword(password) }
	 
	 String toString() {
		 return "$shortName"
	 }
 }
