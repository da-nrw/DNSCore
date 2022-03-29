/**
 * 
 */
package de.uzk.hki.da.utils

import daweb3.FormatMapping

/**
 * @author gabender
 *
 */
class MapFormats {
	
	public Map<String, String> formatMapping(String[] formatArray, Map<String, String> extList) {
		FormatMapping fm = new FormatMapping()
		def mappings = null;
		String extension = ""
		int counter = 0;
		def format

		while (formatArray.size() > counter ) {
			format = formatArray[counter];

			/*
			 * now you can read the table format_mapping
			 */

			mappings = fm.findAll("from FormatMapping where puid = :puid", [puid : format])

			// and at last increment the counter
			counter = counter + 1;
			extList.put(format, mappings.extension)
		} // end of format - list
		return	extList 
	}
	
	/**
	 * 
	 * @param formatArray
	 * @return String der verwendeten Formate
	 */
	public String formatMappingStatistik(String[] formatArray) {
		FormatMapping fm = new FormatMapping()
		def mappings = null;
		String mappingExt = ""
		String extList = ""
		int counter = 0;
		def format
		
		while (formatArray.size() > counter ) {
			format = formatArray[counter];

			/*
			 * now you can read the table format_mapping
			 */

			mappings = fm.findAll("from FormatMapping where puid = :puid", [puid : format])

			// and at last increment the counter
			counter = counter + 1;
			mappingExt = mappings.extension
			
			extList = extList + format + "  --  " + mappingExt.replace('[', '').replace(']', '') + ","
		} // end of format - list
		
		return	extList 
	}
}
