package daweb3

import grails.gorm.DetachedCriteria
import groovy.transform.ToString

import org.codehaus.groovy.util.HashCodeHelper

//import grails.compiler.GrailsCompileStatic

//@GrailsCompileStatic
@ToString(cache=true, includeNames=true, includePackage=false)
class UserRole implements Serializable {

	private static final long serialVersionUID = 1

	User user
	Role role
	static constraints = {
		role validator: { Role r, UserRole ur ->
			if (ur.user == null || ur.user.id == null) return
			boolean existing = false
			if (ur.user?.id) {
				UserRole.withNewSession {
					existing = UserRole.exists(ur.user.id, r.id)
				}		
			}
			if (existing) {
				return 'userRole.exists'
			}
		}
	}

	static mapping = {
 		id composite: ['user', 'role']
		version false
	}
	
	
	@Override
	boolean equals(other) {
		print "UserRole: equals: "
		if (!(other instanceof UserRole)) {
			return false
	   }
	   if (other instanceof UserRole)  {
		   other.user?.id == user?.id &&
		   other.role?.id == role?.id
	   }

	}

	@Override
	int hashCode() {
		
		print "UserRole: hashCode: "
		int hashCode = HashCodeHelper.initHash()
		if (user) {
			hashCode = HashCodeHelper.updateHash(hashCode, user.id)
		}
		if (role){
			hashCode = HashCodeHelper.updateHash(hashCode, role.id)
		}
	}

	static UserRole get(long userId, long roleId) {
		print "BEGIN UserRole: get: "
//		criteriaFor(userId, roleId).get()
		UserRole.where { user == User.load(userId) && role == Role.load(roleId) }.get()
		print "END UserRole: get: "
	}

	static boolean exists(long userId, long roleId) {
//		criteriaFor(userId, roleId).count()
		print "Begin exists UserRole"
		UserRole.where {
			user == User.load(userId) && role == Role.load(roleId) }.count() > 0
	}

	private static DetachedCriteria criteriaFor(long userId, long roleId) {
		print "Begin DetachedCriteria UserRole"
		UserRole.where {
			user == User.load(userId) &&
			role == Role.load(roleId)
		}
	}

	static UserRole create(User user, Role role, boolean flush = false) {
		print "UserRole: create: "
		def instance = new UserRole(user: user, role: role)
//		instance.save(flush: flush, insert: true, failOnError: true)
		instance.save(flush: flush)
		instance
	}

	static boolean remove(User u, Role r, boolean flush = false) {
		print "UserRole: remove: "
//		if (u == null || r == null) return false
//
//		int rowCount = UserRole.where { user == u && role == r }.deleteAll()
//
//		if (flush) { UserRole.withSession { it.flush() } }
//
//		rowCount
//	}
//		if (u == null || r == null) return false
//		int rowCount = UserRole.where { user == User.load(u.id) && role == Role.load(r.id) }.deleteAll()
//		rowCount > 0
		
		if (u != null && r != null) {
			UserRole.where { user == u && role == r }.deleteAll()
		}
	}

	static void removeAll(User u, boolean flush = false) {
		
		print "UserRole: removeAll: "
		if (u == null) return

		UserRole.where { user == u }.deleteAll() as int
//		UserRole.where { user == User.load(u.id) }.deleteAll()

//		if (flush) { UserRole.withSession { it.flush() } }
	}

	static void removeAll(Role r, boolean flush = false) {
		if (r == null) return

		UserRole.where { role == r }.deleteAll() as int
//		UserRole.where { role == Role.load(r.id) }.deleteAll()

//		if (flush) { UserRole.withSession { it.flush() } }
	}
	
	def getPKId() {
		
		print "UserRole: getPKId: "
		return ['userId': user.id, 'roleId': role.id]
		}
}
