/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
  Landschaftsverband Rheinland

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

package de.uzk.hki.da.ff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * DNSCore supports four metadata structures that enable proper publication via the presentation repository. 
 * Each of these structures is based on a specific xml metadata format (EAD,METS,LIDO,XMP). 
 * This class provides a method to test xml files if they conform to one of these metadata format standards.  
 * 
 * @author Daniel M. de Oliveira
 */
public class PublicationMetadataSubformatIdentifier {

	private final static String eadPattern = ".*(?s)\\A.{0,500}\\x3cead[^\\x3c]{0,1000}\\x3ceadheader.*";
	private final static String metsPattern = ".*(?s)\\A.{0,100}\\x3c([^: ]+:)?mets[^\\xce]{0,100}xmlns:?[^=]{0,10}=\"http://www.loc.gov/METS.*";
	private final static String lidoPattern = ".*(?s)\\A.{0,500}\\x3c([^: ]+:)?lidoWrap[^\\xce]{0,100}xmlns:?[^=]{0,10}=\"http://www.lido-schema.org.*";
	
	/**
	 * @param f
	 * @return
	 * @throws IOException 
	 */
	public String identify(File f) throws IOException{

		String beginningOfFile = convertFirst10LinesOfFileToString(f);
		
		if (beginningOfFile.matches(eadPattern))  return FFConstants.SUBFORMAT_IDENTIFIER_EAD;
		if (beginningOfFile.matches(metsPattern)) return FFConstants.SUBFORMAT_IDENTIFIER_METS;
		if (beginningOfFile.matches(lidoPattern)) return FFConstants.SUBFORMAT_IDENTIFIER_LIDO;
		
		return "";
	}
	
	private String convertFirst10LinesOfFileToString(File f) throws IOException {
		
		String result = "";
		
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		int lineCount=0;
		while ((line = br.readLine()) != null) {
		   // process the line.
			result+=line;
			lineCount++;
			if (lineCount==10) break;
		}
		br.close();
		return result;
	}
	
	
}
