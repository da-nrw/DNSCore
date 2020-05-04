package daweb3
import java.awt.PageAttributes.OriginType

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


import grails.plugin.springsecurity.annotation.Secured

/**
 * 
 * @author Gaby Bender DANRW 2020
 * StatisticsController should show different Statics Results
 */

class StatisticsController {

	def springSecurityService
	def qualityList
	User user
	private static final long  GIGABYTE = 1024L * 1024L * 1024L;

	def index = {
		println " Hallo, ich bin der StasticsController"

		user = springSecurityService.currentUser
		def msg = "";
		def admin = 0;
		def objectsAll = null

		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}

		// Speicherbelegung
		def aipSizeGesamt = 0
		def archived = 0
		def formats
		def countFormats = 0
		String[] formatSIPArray
		String[] formatDIPArray
		objectsAll = Object.findAllByUser(user);

		for (int i= 0; i<  objectsAll.size(); i++) {
			// Speiecherbelegung
			aipSizeGesamt = aipSizeGesamt + objectsAll[i].aipSize

			// Anzahl archivierter Dateien
			if (objectsAll[i].objectState == 100) {
				archived = archived + 1
			}

			// Auswertung SIP ueber PUID
			def originalFormat = objectsAll[i].original_formats
			if (originalFormat != null &&  !originalFormat.equals("")) {
				if (formatSIPArray != null ) {
					formatSIPArray = formatSIPArray + (String[]) originalFormat.split(",")
				} else {
					formatSIPArray = (String[]) originalFormat.split(",")
				}

			}

			// Auswertung DIP ueber PUID
			def mostRecentFormat = objectsAll[i].most_recent_formats
			if (mostRecentFormat != null &&  !mostRecentFormat.equals("")) {
				if (formatDIPArray != null ) {
					formatDIPArray = formatDIPArray + (String[]) mostRecentFormat.split(",")
				} else {
					formatDIPArray = (String[]) mostRecentFormat.split(",")
				}

			}

		}
		aipSizeGesamt = aipSizeGesamt / GIGABYTE

		// Qualitaetsangaben
		findQualityLevel()

		// Auswertung SIP ueber PUID
		 Map<String, String> extListSIP = getFormatsAndCountThem(formatSIPArray)

		// Auswertung DIP ueber PUID
		Map<String, String> extListDIP = getFormatsAndCountThem(formatDIPArray)


		render(view:'index', model:[qualityLevels: qualityList,
			aipSizeGesamt: aipSizeGesamt,
			archived: archived,
			formatsSIP: extListSIP,
			formatsDIP: extListDIP]
		);

		println " Hallo, ich bin der StasticsController und nu bin ich fertig :-)"
	}

	private Map<String, String> getFormatsAndCountThem(String[] formatArray) {
		Map<String, String> extList = [:]
		int counter = 0
		def format = null
		println (formatArray.size())
		while (formatArray.size() > counter ) {
			if (format != null) {
				if (formatArray[counter-1].toString().equals(format)) {
					int  getAtPos = 0
					if (extList.getAt(format) != null ) {
						getAtPos = extList.getAt(format)
					}  
					extList.remove(format)
					extList.put(format, getAtPos + 1)
				} else {
					format = formatArray[counter]
				}
			} else {
				format = formatArray[counter];
				extList.put(format, 1)
			}
			// and at last increment the counter
			counter ++
		}

		return extList
	}

	def findQualityLevel() {
		qualityList = Object.executeQuery(" select count (o.quality_flag), o.quality_flag "  +
				"from Object o , User u " +
				"where  u.id = (select id from User where short_name = :shortName) " +
				"group by o.quality_flag",
				[shortName: user.toString()])

	}
}
