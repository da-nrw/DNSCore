/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
  Lanschaftsverband Rheinland

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

package de.uzk.hki.da.format;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.utils.C;

/**
 * @author Daniel M. de Oliveira
 */
public class FakeFormatScanService implements FormatScanService {
	
	@Override
	public List<DAFile> identify(List<DAFile> files) throws IOException{
		
		for (DAFile f:files){
			
			if (f.getRelative_path().endsWith(".tif")){
				f.setFormatPUID("fmt/353");
				continue;
			}
			
			if (f.getRelative_path().endsWith(".bmp")){
				f.setFormatPUID("fmt/116");
				continue;
			}
			
			if (f.getRelative_path().endsWith(".jp2")){
				f.setFormatPUID("x-fmt/392");
				continue;
			}
			
			if (f.getRelative_path().endsWith(".gif")){
				f.setFormatPUID("fmt/4");
				continue;
			}
			
			if (f.getRelative_path().endsWith(".pdf")){
				f.setFormatPUID("fmt/16");
				continue;
			}
			
			BufferedReader br=new BufferedReader(new FileReader(files.get(0).toRegularFile()));
	        String line;
	        while((line=br.readLine())!=null){
		        if (patternFound(line,"<mets .*>")){
		        	f.setFormatPUID(C.METS_PUID);
		        	break;
		        	}
		        if (patternFound(line,"<ead .*>")){
		        	f.setFormatPUID(C.EAD_PUID);
		        	break;
		        	}
		        if (patternFound(line,"<lido:lido>")){
		        	f.setFormatPUID(C.LIDO_PUID);
		        	break;
		        	}
		        if (patternFound(line,"<x:xmpmeta.*")){
		        	f.setFormatPUID(C.XMP_PUID);
		        	break;
		        	}
	        }		
	        br.close();
		}
		return files;
	}
	
	
	private boolean patternFound(String line,String pattern){
		Pattern p=Pattern.compile(pattern);
        Matcher m=p.matcher(line);
        if (m.find()) return true;
        return false;
	}
	
}
