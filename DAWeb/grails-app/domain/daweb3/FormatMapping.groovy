package daweb3
import java.text.SimpleDateFormat;


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
/**
 *  
 *@Author Gaby Bender
 */
class FormatMapping {

//    static constraints = {
//		
//    }
    
    static mapping = {
		table 'format_mapping'
		version false
		id column:'fm_id'
		mimeType column:'mime_type'
		formatName column:'format_name'
		creationDate column:'modified_date'
    }	
	int id
	String puid
	String extension
	String mimeType
	String formatName
	Date creationDate
}
