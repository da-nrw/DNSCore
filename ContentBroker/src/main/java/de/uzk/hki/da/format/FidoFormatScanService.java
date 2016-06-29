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

package de.uzk.hki.da.format;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;


/**
 * Provides funcionalities for adding file format information to DAFile objects.
 *
 * @author Daniel M. de Oliveira
 */
public class FidoFormatScanService implements FormatScanService, Connector {
	
	private ScriptWrappedPronomFormatIdentifier pronom;
	/**
	 * Instantiates a new format scan service.
	 *
	 * @param ops the ops
	 */
	public FidoFormatScanService(){
		
		pronom = new ScriptWrappedPronomFormatIdentifier(new File(C.FIDO_GLUE_SCRIPT));
	}
	
	
	/**
	 * Iterates over files and determines PUIDs.
	 * This means, that if the method runs successfully (without throwing exceptions) each file_format
	 * property is set to a PRONOM-PUID.
	 *
	 * @param files the files
	 * @return files. return value needed for mockup usage in unit tests.
	 * @author Daniel M. de Oliveira
	 * @throws IOException 
	 */
	@Override
	public
	List<FileWithFileFormat> identify(Path workPath,List<FileWithFileFormat> files,boolean pruneExceptions) throws IOException {
		for (FileWithFileFormat f:files){
			f.setFormatPUID(pronom.identify(Path.makeFile(workPath,f.getPath()),pruneExceptions));
		}
		return files;
	}


	@Override
	public boolean isConnectable() {
		System.out.print("CONNECTIVITY CHECK - FidoFormatScanService - fido.sh ");
		String puid;
		try {
			puid = pronom.identify(new File("conf/healthCheck.tif"), false);
		} catch (IOException e) {
			System.out.println(".... FAIL (check fido installation and fido.sh)");
			return false;
		}
		if (Arrays.asList(new String[] {FFConstants.FMT_353}).contains(puid)) {
			System.out.println(".... OK");
			return true;
		}
		else {
			System.out.println(".... FAIL (check fido installation and fido.sh)");
			return false;
		}
	}


	@Override
	public void setKnownFormatCmdLineErrors(KnownFormatCmdLineErrors knownerrors) {
		
	}

}
