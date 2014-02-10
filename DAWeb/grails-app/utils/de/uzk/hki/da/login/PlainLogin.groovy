package de.uzk.hki.da.login
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

import org.codehaus.groovy.grails.web.util.WebUtils	
	/**
	 *
	 * @author Jens Peters
	 * Plain Authentication for DevMode
	 *
	 */
	class PlainLogin implements LoginFactory {
		
		public boolean login (String login, String password, grailsApplication) {
			def webutils = WebUtils.retrieveGrailsWebRequest();
			def session = webutils.getSession();
			
			return true;
			
		}
		public void logout() {
			def webutils = WebUtils.retrieveGrailsWebRequest();
			def session = webutils.getSession();
			session.contractor = null;
		}
	}
