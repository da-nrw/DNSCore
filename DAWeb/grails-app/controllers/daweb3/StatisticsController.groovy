package daweb3



import java.awt.font.GraphicAttribute
import java.text.SimpleDateFormat

import javax.swing.JFileChooser

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.dom4j.DocumentException

import com.itextpdf.text.Anchor
import com.itextpdf.text.BadElementException
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Chapter
import com.itextpdf.text.Chunk
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.Section
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PRAcroForm
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator

import groovy.util.ObservableList.ElementAddedEvent
import grails.converters.JSON

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

/**
 * StatisticsController should show different Statics Results according to the contractor
 * Also there should be a possibility to generate reports and send them as mail.
 * 
 * @author Gaby Bender DANRW 2020
 */

class StatisticsController {

	def springSecurityService
	def qualityList
	def aipSizeGesamt
	def archived
	def formats
	def msg

	Map<String, String> extListSIP
	Map<String, String> extListDIP
	
	private static final long  GIGABYTE = 1024L * 1024L * 1024L;
	//	private static final long  TERABYTE = 1024L * 1024L * 1024L * 1024L;

	public static final String CSV_File = "/ci/DNSCore/DAWeb/pdf/statistik.csv";
	public static final String PDF_FILE = "/ci/DNSCore/DAWeb/pdf/statistik.pdf";
	//public static final String PDF = "statistik.pdf";
	private static Font TITLE_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 18,
	Font.BOLD);

	private static Font SUB_TITEL_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 16,
	Font.BOLD);
	private static Font NORMAL_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 14);

	def index = {

		User user = springSecurityService.currentUser
		aipSizeGesamt = 0
		archived = 0
		formats
		msg = ""
		String[] formatSIPArray
		String[] formatDIPArray
		def admin = 0;
		def objectsAll = null

		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}

		// Speicherbelegung

		def countFormats = 0
		objectsAll = Object.findAllByUser(user);

		for (int i= 0; i<  objectsAll.size(); i++) {
			// Speicherbelegung
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
		extListSIP = getFormatsAndCountThem(formatSIPArray)

		// Auswertung DIP ueber PUID
		extListDIP = getFormatsAndCountThem(formatDIPArray)


		render(view:'index', model:[qualityLevels: qualityList,
			aipSizeGesamt: aipSizeGesamt,
			archived: archived,
			formatsSIP: extListSIP,
			formatsDIP: extListDIP,
			msg:msg]
		);
	}

	/**
	 * getFormatsAndCountThem: zählt die bisher verwendeten Formate
	 * @param formatArray: StringArray der verwendeten Formate
	 * @return extList: eingehende Liste erweitert um die Anzahl der jeweiligen Formate
	 */
	private Map<String, String> getFormatsAndCountThem(String[] formatArray) {

		Map<String, String> extList = [:]
		int counter = 0
		def format = null
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

	private String getOutgoingDir() {
		User user = springSecurityService.currentUser
		def saveDir = grailsApplication.config.getProperty('localNode.userAreaRootPath') + "/" + user.getShortName() + "/outgoing/" 
		println ("### saveDir: " + saveDir)
		return saveDir;
	}
	
	private String getDateTime() {
		String dateTime = "";
		SimpleDateFormat sdf =  new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
	 	dateTime = sdf.format(new Date())
		return dateTime;
	}

	/**
	 *  csvCreate: Erstellung einer csv Datei
	 * @return
	 */
	def csvCreate() {
		
		
		def saveFile = getOutgoingDir() + "statistik_" + getDateTime() + ".csv";
		println ("### saveFile: " + saveFile)
		def FILE_HEADER = [
			'Speicher',
			'Pakete',
			'Qualitätslevel',
			'Auswertung SIP',
			'Auswertung DIP'
		]

		def speicher =  ['aipSizeGesamt']
		def pakete = ['archived']
		def quality = ['qualityList']
		def sipList = ['extListSIP']
		def dipList =  ['extListDIP']
		try {


			new File(CSV_File).withWriter{ fileWriter ->
				def csvFilePrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT)
				
				csvFilePrinter.printRecord(FILE_HEADER)
				speicher.each { s ->
					pakete.each { p ->
						quality.each { q ->
							extListSIP.each { sip ->
								extListDIP.each { dip ->
									csvFilePrinter.printRecord([s, p, q, sip, dip])
								}
							}
						}
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace()
			msg =" Ups, hier ist etwas schiefgelaufen. Es konnte kein pdf erstellt werden. "
		}

		redirect (action: "index")

	}

	/**
	 * pdfCreate: Erstellung des pdf  
	 * @return
	 */
	def pdfCreate() {

		User user = springSecurityService.currentUser
		def saveFile = getOutgoingDir() + "statistik_" + getDateTime() + ".pdf";
		def result = [success:false]
		def msg = ""
		try {
			// 1. Erzeugen eines Document-Objects
			Document document = new Document();

			// 2. Erzeugen eines Writers, der in die PDF-Datei schreibt
			PdfWriter.getInstance(document,	new FileOutputStream(saveFile));

			// 3. Öffnen der PDF-Datei
			document.open();

			// 4. Schreiben des Textes
			addMetaData(document, user);
			addTitlePage(document, user);
			addContentPage(document);

			// 5. Schließen der PDF-Datei
			document.close();

			result.msg = "Die Erzeugung des pdf " +saveFile + " wurde erfolgreich abgeschlossen."
			result.success = true
		} catch(Exception e) {
			e.printStackTrace()
			result.msg =" Ups, hier ist etwas schiefgelaufen. Es konnte kein pdf erstellt werden. "
			result.success = false
		}
		
		render result as JSON
		//redirect (action: "index")
	}

	// under File -> Properties
	private void addMetaData(Document document, def user) {
		document.addTitle("DANRW-Statistiken");
		document.addSubject("Statistiken");
		document.addKeywords("DANRW, PDF, iText");
		document.addAuthor("LVR InfoKom");
		document.addCreator("LVR InfoKom");
	}

	/**
	 * Titel-Seite erstellen
	 * @param document
	 * @throws DocumentException
	 */
	private void addTitlePage(Document document, def user)	throws DocumentException {
		Paragraph paragraph = new Paragraph();

		// We add one empty line
		addEmptyLine(paragraph, 1);
		addHeader(paragraph)
		addEmptyLine(paragraph, 1)

		// Lets write a big header
		Paragraph title = new Paragraph(50, "DA NRW Statistiken", TITLE_FONT);
		title.setSpacingBefore(30)
		title.setAlignment(Element.ALIGN_CENTER)
		paragraph.add(title);

		addEmptyLine(paragraph, 1);
		// Will create: Report generated by: _name, _date
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
		String date = simpleDateFormat.format(new Date());
		Paragraph angefordert = new Paragraph(
				"Der Statistikreport wurde angefordert von "
				+ user.toString() + " am " + date, SUB_TITEL_FONT);

		angefordert.setSpacingBefore(200)
		angefordert.setAlignment(Element.ALIGN_CENTER)
		paragraph.add(angefordert);

		addEmptyLine(paragraph, 3);

		document.add(paragraph);
		// Start a new page
		document.newPage();
	}

	private addHeader(Paragraph paragraph) {
		Image logoLeft = Image.getInstance("/ci/DNSCore/DAWeb/grails-app/assets/images/DA_NRW268x80.png");
		logoLeft.scalePercent(30);
		logoLeft.setAlignment(Element.ALIGN_LEFT)

		Image logoRight = Image.getInstance("/ci/DNSCore/DAWeb/grails-app/assets/images/DANRW_P_OBEN.png");
		logoRight.scalePercent(30);
		logoRight.setAlignment(Element.ALIGN_RIGHT)
		paragraph.add(new Chunk (logoLeft, 0, 0,true))
		paragraph.add(new Chunk (logoRight, 350, 0,true))

		addEmptyLine(paragraph, 1)
		LineSeparator  line = new LineSeparator()
		line.setPercentage(100)
		Chunk lineBreak = new Chunk(line)
		paragraph.add(lineBreak)

	}

	private void addContentPage(Document document) {
		Paragraph paragraph = new Paragraph();
		addHeader(paragraph)

		// Speicherverwaltung
		Paragraph speicherTitle = new Paragraph("Speicherbelegung", SUB_TITEL_FONT);
		paragraph.add(speicherTitle)

		addEmptyLine(paragraph, 1)

		Paragraph aipSize = new Paragraph("AIP-Size aller bisher eingelieferten Pakete: " +
				aipSizeGesamt + " in TeraByte",	 NORMAL_FONT)

		paragraph.add(aipSize)
		addEmptyLine(paragraph, 1)

		Paragraph belegterSpeicher = new Paragraph ("bisher belgter Speicher: " +
				" XXX  in GigaByte", NORMAL_FONT)
		paragraph.add(belegterSpeicher)
		addEmptyLine(paragraph, 1)

		// Dateien
		Paragraph dateien = new Paragraph("Pakete", SUB_TITEL_FONT);
		paragraph.add(dateien)
		addEmptyLine(paragraph, 1)

		Paragraph bisherArchDateien = new Paragraph("Anzahl der bisher archivierten Pakete: " +
				archived, NORMAL_FONT)
		paragraph.add(bisherArchDateien)
		addEmptyLine(paragraph, 1)


		// Qualität der archivierten Dokumente
		Paragraph qualitaet = new Paragraph("Qualitative Situation des Archivgutes ", SUB_TITEL_FONT);
		paragraph.add(qualitaet)
		addEmptyLine(paragraph, 1)

		Paragraph qualityLevelBisher = new Paragraph("Qualität der bisher archivierten Pakete ", NORMAL_FONT)
		createTableQuality(qualityLevelBisher)


		paragraph.add(qualityLevelBisher)
		addEmptyLine(paragraph, 1)

		// Auswertung über puid
		Paragraph puid = new Paragraph("Auswertung über PUID ", SUB_TITEL_FONT);
		paragraph.add(puid)
		addEmptyLine(paragraph, 1)

		Paragraph puidSip = new Paragraph("Dateiformate im SIP", NORMAL_FONT)

		println(" extListSIP 2: " + extListSIP)
		createTablePuid(puidSip, extListSIP)
		paragraph.add(puidSip)
		addEmptyLine(paragraph, 1)


		Paragraph puidDip = new Paragraph("Dateiformate im DIP", NORMAL_FONT)
		createTablePuid(puidDip, extListDIP)
		paragraph.add(puidDip)

		addEmptyLine(paragraph, 1)


		document.add(paragraph)
	}

	private void createTablePuid(Paragraph qualityLevelBisher,
			Map<String, String> puidMap) throws BadElementException {
		PdfPTable table = new PdfPTable(2)
		table.setHorizontalAlignment(Element.ALIGN_LEFT)

		PdfPCell tableHeader = new PdfPCell(new Phrase("Eingelieferte Dateiformate"))
		tableHeader.setHorizontalAlignment(Element.ALIGN_CENTER)
		tableHeader.setBackgroundColor(BaseColor.LIGHT_GRAY)
		table.addCell(tableHeader)

		tableHeader = new PdfPCell(new Phrase("Anzahl pro PUID"))
		tableHeader.setHorizontalAlignment(Element.ALIGN_CENTER)
		tableHeader.setBackgroundColor(BaseColor.LIGHT_GRAY)
		table.addCell(tableHeader)
		table.setHeaderRows(1)

		for(String puid : puidMap.keySet())
		{
			PdfPCell c1 = new PdfPCell( new Phrase(puid))
			c1.setHorizontalAlignment(Element.ALIGN_CENTER)

			PdfPCell c2 = new PdfPCell( new Phrase(puidMap.get(puid).toString()))
			c2.setHorizontalAlignment(Element.ALIGN_CENTER)
			table.addCell(c1)
			table.addCell(c2)
		}

		qualityLevelBisher.add(table)
	}

	private void createTableQuality(Paragraph qualityLevelBisher) throws BadElementException {
		PdfPTable table = new PdfPTable(2)
		table.setHorizontalAlignment(Element.ALIGN_LEFT)

		PdfPCell tableHeader = new PdfPCell(new Phrase("Qualitätslevel"))
		tableHeader.setHorizontalAlignment(Element.ALIGN_CENTER)
		tableHeader.setBackgroundColor(BaseColor.LIGHT_GRAY)
		table.addCell(tableHeader)

		tableHeader = new PdfPCell(new Phrase("Anzahl"))
		tableHeader.setHorizontalAlignment(Element.ALIGN_CENTER)
		tableHeader.setBackgroundColor(BaseColor.LIGHT_GRAY)
		table.addCell(tableHeader)
		table.setHeaderRows(1)

		if (qualityList instanceof ArrayList ) {

			ArrayList ql = (ArrayList) qualityList
			for (int i = 0; i < ql.size(); i++ ) {
				PdfPCell c1 = new PdfPCell( new Phrase(ql.get(i).getAt(1).toString()))
				c1.setHorizontalAlignment(Element.ALIGN_CENTER)
				PdfPCell c2 = new PdfPCell(new Phrase(ql.get(i).getAt(0).toString()))
				c2.setHorizontalAlignment(Element.ALIGN_CENTER)
				table.addCell(c1)
				table.addCell(c2)
			}
		}

		qualityLevelBisher.add(table)
	}

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}


	/**
	 * findQualityLevel sucht in der Tabelle objects die zu dem 
	 * Contractor erzeugten Qualitätsstufen und zählt diese je Qualitätsstufe durch
	 * @return qualityList
	 */
	def findQualityLevel() {
		User user = springSecurityService.currentUser
		qualityList = Object.executeQuery(" select count (o.quality_flag), o.quality_flag "  +
				"from Object o , User u " +
				"where  u.id = (select id from User where short_name = :shortName) " +
				"group by o.quality_flag",
				[shortName: user.toString()])

	}
}
