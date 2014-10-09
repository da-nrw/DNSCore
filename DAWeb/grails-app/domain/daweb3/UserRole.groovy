/* 
DA-NRW Software Suite | ContentBroker
 Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
 Universität zu Köln

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

package daweb3
import org.apache.commons.lang.builder.HashCodeBuilder

class UserRole implements Serializable {

private static final long serialVersionUID = 1

User user 
Role role

static constraints = { 
	role validator: { Role r, UserRole ur ->
	if (ur.user == null) return
	boolean existing = false
	UserRole.withNewSession { existing = UserRole.exists(ur.user.id, r.id) }
	if (existing) { return 'userRole.exists' } } }

static mapping = {
	id composite: ['user', 'role']
	version false
	 }

boolean equals(other) { 
	if (!(other instanceof UserRole)) {
		 return false 
	}
	other.user?.id == user?.id && 
	other.role?.id == role?.id 
	}

int hashCode() { 
	def builder = new HashCodeBuilder() 
	if (user) builder.append(user.id) 
	if (role) builder.append(role.id) builder.toHashCode() 
	}

static UserRole get(long userId, long roleId) { 
	UserRole.where { user == User.load(userId) && role == Role.load(roleId) }.get() 
	}

static boolean exists(long userId, long roleId) { 
	UserRole.where { 
		user == User.load(userId) && role == Role.load(roleId) }.count() > 0 
	}

static UserRole create(User user, Role role, boolean flush = true) { 
	def instance = new UserRole(user: user, role: role) 
	instance.save(flush: flush, insert: true,failOnError: true) 
	instance 
	}

static boolean remove(User u, Role r) { 
	if (u == null || r == null) return false
		int rowCount = UserRole.where { user == User.load(u.id) && role == Role.load(r.id) }.deleteAll()
		rowCount > 0 
	}

static void removeAll(User u) { 
	if (u == null) return
	UserRole.where { user == User.load(u.id) }.deleteAll() }

static void removeAll(Role r) { 
	if (r == null) return
	UserRole.where { role == Role.load(r.id) }.deleteAll() }

def getPKId() {
	return ['userId': user.id, 'roleId': role.id]
	}

}
