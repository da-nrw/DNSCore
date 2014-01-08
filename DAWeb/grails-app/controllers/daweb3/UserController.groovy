package daweb3
/**
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
import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.connection.IRODSProtocolManager
import org.irods.jargon.core.connection.IRODSSession
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.UserAO
import org.irods.jargon.core.connection.IRODSSimpleProtocolManager
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSAccessObjectFactoryImpl;

class UserController {
	
	def login = {
		redirect(controller: "contractor", action:"login")
	}

	def logout = {
		redirect(controller: "contractor", action:"logout")
		
	}

	def index = {
		redirect(controller: "contractor", action:"login")
	}
}
