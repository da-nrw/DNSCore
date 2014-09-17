package daweb3
/*
 DA-NRW Software Suite | ContentBroker
 Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
 Universität zu Köln, 2014 LVRInfoKom

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
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
