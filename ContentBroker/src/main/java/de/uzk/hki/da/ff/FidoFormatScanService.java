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

package de.uzk.hki.da.ff;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.C;




/**
 * Provides funcionalities for adding file format information to DAFile objects.
 *
 * @author Daniel M. de Oliveira
 */
public class FidoFormatScanService implements FormatScanService {
	
	private static final Logger logger = LoggerFactory.getLogger(FidoFormatScanService.class);
	
	private PronomFormatIdentifierWrapper pronom;
	
	/**
	 * Instantiates a new format scan service.
	 *
	 * @param ops the ops
	 */
	public FidoFormatScanService(){
		
		pronom = new PronomFormatIdentifierWrapper(new File(C.FIDO_GLUE_SCRIPT));
	}
	
	
	/**
	 * Iterates over files and determines PUIDs and, if necessary, codec or compression related information.
	 * This means, that if the method runs successfully (without throwing exceptions) each file_format
	 * property is set to a PRONOM-PUID. Furthermore, if the file format is marked for second stage scanning
	 * (via configuration in the second_stage_scans table), the format_second_attribute of the dafile object is also set.
	 * In such cases the attribute has a proper value, which means it contains one of the values listed in the
	 * expected_values column of the row for the puid in question.
	 *
	 * @param files the files
	 * @return files. return value needed for mockup usage in unit tests.
	 * @throws FileNotFoundException if any of the files can not be found on the file system.
	 * @author Daniel M. de Oliveira
	 */
	@Override
	public List<IFileWithFileFormat> identify(List<IFileWithFileFormat> files) throws FileNotFoundException {
//		for (FileWithFileFormat f:files){
//			if (!f.toRegularFile().exists()) throw new FileNotFoundException("file "+f.toRegularFile().getPath()+" doesn't exist");
//		}	
		for (IFileWithFileFormat f:files){

			f.setFormatPUID(pronom.getPuidForFile(f));
			logger.debug(f+" has puid "+f.getFormatPUID()+". Now searching if second stage scan policy is applicable");
			
		}
		return files;
	}
}
