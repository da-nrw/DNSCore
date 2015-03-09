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

package de.uzk.hki.da.model;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a ConversionInstruction for a file.
 * 
 * The generated source_file and target_folder properties
 * of a ConversionInstruction are generated as shown
 * in the following example. Suppose our base folder is "/data/p1/source/": Then we
 * get (input file -> source_file, target_folder):
 * <ul>
 * <li>/data/p1/source/subfolder/aFile.txt -> subfolder/aFile.txt, subfolder
 * <li>/data/p1/source/bFile.txt -> bFile.txt, ""
 * </ul>
 * 
 * The reason for formatting the relative sources and targets this way is to be independent of
 * the physical pathes of the concrete packages (here /data/p1/) which can be different
 * at the time the actual conversion is done
 * (maybe the path on another machine is then /data/storage/fs/p1).
 *
 * @param f the f
 * @param cp the cp
 * @return the conversion instruction
 * @author Daniel M. de Oliveira
 */
public class ConversionInstructionBuilder {

	/** The Constant logger. */
	static final Logger logger = LoggerFactory.getLogger(ConversionInstructionBuilder.class);
	
	
	public ConversionInstruction assembleConversionInstruction(
			WorkArea wa,
			DAFile f,
			ConversionPolicy cp){
		
		logger.trace("Building conversion instruction for file: \""+wa.toFile(f).getAbsolutePath()+"\"");
			
		ConversionRoutine cr = cp.getConversion_routine();
		if ( cr == null ){
			logger.error("ConversionRoutine not set for Policy.");
			throw new RuntimeException("Error: ConversionRoutine not set for Policy.");
		}
		
		String targetFolderRelativePath = f.getRelative_path()
				.substring(0, f.getRelative_path().length()-wa.toFile(f).getName().length());
		
		if (targetFolderRelativePath.startsWith("/"))
			targetFolderRelativePath = targetFolderRelativePath.substring(1);
		
		if (targetFolderRelativePath.endsWith("/"))
			targetFolderRelativePath = targetFolderRelativePath.substring(0,
					targetFolderRelativePath.length()-1);
		
		return new ConversionInstruction
				(0, 
					targetFolderRelativePath, 
					cr, 
					"");
	}
	
	
	/**
	 * Iterates over a list of ConversionInstructions and transforms
	 * each of its entries fields sourceFile and targetFolder in that manner
	 * that sourceFile = sAdd + sourceFile
	 * and targetFolder = tAdd + targetFolder.
	 *
	 * @param cis the cis
	 * @param tAdd the t add
	 */
	public void prefixFolders(List<ConversionInstruction> cis, String tAdd){
		for (ConversionInstruction ci : cis){
			ci.setTarget_folder(tAdd+ci.getTarget_folder());
		}
	}
}
