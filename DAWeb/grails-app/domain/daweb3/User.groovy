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

	 static constraints = {
		 username blank: false, unique: true 
		 password blank: true }

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
