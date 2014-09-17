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


import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class UserRoleController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond UserRole.list(params), model:[userRoleInstanceCount: UserRole.count()]
    }

    def show(UserRole userRoleInstance) {
        respond userRoleInstance
    }

    def create() {
        respond new UserRole(params)
    }

    @Transactional
    def save(UserRole userRoleInstance) {
        if (userRoleInstance == null) {
            notFound()
            return
        }

        if (userRoleInstance.hasErrors()) {
            respond userRoleInstance.errors, view:'create'
            return
        }

        userRoleInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'userRoleInstance.label', default: 'UserRole'), userRoleInstance.id])
                redirect userRoleInstance
            }
            '*' { respond userRoleInstance, [status: CREATED] }
        }
    }

    def edit(UserRole userRoleInstance) {
        respond userRoleInstance
    }

    @Transactional
    def update(UserRole userRoleInstance) {
        if (userRoleInstance == null) {
            notFound()
            return
        }

        if (userRoleInstance.hasErrors()) {
            respond userRoleInstance.errors, view:'edit'
            return
        }

        userRoleInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'UserRole.label', default: 'UserRole'), userRoleInstance.id])
                redirect userRoleInstance
            }
            '*'{ respond userRoleInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(UserRole userRoleInstance) {

        if (userRoleInstance == null) {
            notFound()
            return
        }

        userRoleInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'UserRole.label', default: 'UserRole'), userRoleInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'userRoleInstance.label', default: 'UserRole'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
