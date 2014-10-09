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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fake implementation
 * @author Daniel M. de Oliveira
 */
public class FakeFileFormatFacade implements FileFormatFacade {

	@SuppressWarnings("unchecked")
	@Override
	public List<IFileWithFileFormat> identify(List<? extends IFileWithFileFormat> files) throws FileNotFoundException{
		
		for (IFileWithFileFormat f:files){
			
			if (f.toRegularFile().getAbsolutePath().toLowerCase().endsWith(".avi")){
				f.setFormatPUID("fmt/5");
				f.setFormatSecondaryAttribute("cinepak");
				continue;
			}
			
			if (f.toRegularFile().getAbsolutePath().toLowerCase().endsWith(".mxf")){
				f.setFormatPUID("fmt/200");
				f.setFormatSecondaryAttribute("dvvideo");
				continue;
			}
			
			if (f.toRegularFile().getAbsolutePath().toLowerCase().endsWith(".mov")){
				f.setFormatPUID("x-fmt/384");
				f.setFormatSecondaryAttribute("svq1");
				continue;
			}
			
			if (f.toRegularFile().getAbsolutePath().toLowerCase().endsWith(".tif")){
				f.setFormatPUID("fmt/353");
				continue;
			}
			
			if (f.toRegularFile().getAbsolutePath().toLowerCase().endsWith(".bmp")){
				f.setFormatPUID("fmt/116");
				continue;
			}
			
			if (f.toRegularFile().getAbsolutePath().toLowerCase().endsWith(".jp2")){
				f.setFormatPUID("x-fmt/392");
				continue;
			}
			
			if (f.toRegularFile().getAbsolutePath().toLowerCase().endsWith(".gif")){
				f.setFormatPUID("fmt/4");
				continue;
			}
			
			if (f.toRegularFile().getAbsolutePath().toLowerCase().endsWith(".pdf")){
				f.setFormatPUID("fmt/16");
				continue;
			}

			if (f.toRegularFile().getAbsolutePath().toLowerCase().endsWith(".xml")){
				f.setFormatPUID("fmt/101");
			}
			
			if (f.toRegularFile().getAbsolutePath().toLowerCase().endsWith(".rdf")){
				f.setFormatPUID("fmt/101");
			}
			if (f.toRegularFile().getAbsolutePath().toLowerCase().endsWith(".jpg")){
				f.setFormatPUID("fmt/43");
			}
			
			BufferedReader br=new BufferedReader(new FileReader(f.toRegularFile()));
	        String line;
	        try {
				while((line=br.readLine())!=null){
				    if (patternFound(line,"<mets.*>")){
				    	f.setFormatSecondaryAttribute(FFConstants.SUBFORMAT_IDENTIFIER_METS);
				    	f.setFormatPUID(FFConstants.XML_PUID);
				    	break;
				    	}
				    if (patternFound(line,"<ead .*>")){
				    	f.setFormatSecondaryAttribute(FFConstants.SUBFORMAT_IDENTIFIER_EAD);
				    	f.setFormatPUID(FFConstants.XML_PUID);
				    	break;
				    	}
				    if (patternFound(line,"<lido:lido>")){
				    	
				    	f.setFormatSecondaryAttribute(FFConstants.SUBFORMAT_IDENTIFIER_LIDO);
				    	f.setFormatPUID(FFConstants.XML_PUID);
				    	break;
				    	}
				    if (patternFound(line,"<x:xmpmeta.*")){
				    	f.setFormatSecondaryAttribute(FFConstants.SUBFORMAT_IDENTIFIER_XMP);
				    	f.setFormatPUID(FFConstants.XML_PUID);
				    	break;
				    	}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}		
	        try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        
	    	
		}
		return (List<IFileWithFileFormat>) files;
	}
	
	
	private boolean patternFound(String line,String pattern){
		Pattern p=Pattern.compile(pattern);
        Matcher m=p.matcher(line);
        if (m.find()) return true;
        return false;
	}


	@Override
	public void extract(File file, File targetFile) throws IOException {
		PrintWriter writer = new PrintWriter(targetFile, "UTF-8");
		writer.println("<jhove>abc</jhove>");
		writer.close();
	}
	
	


	@Override
	public void setSubformatIdentificationPolicies(
			List<ISubformatIdentificationPolicy> subformatIdentificationPolicies) {
	}
}
