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

package de.uzk.hki.da.format;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import de.uzk.hki.da.utils.Path;

/**
 * Provides access to the file format subsystem, which is responsible for 
 * <ol>
 * <li>identification of file format (PUID) 
 * <li>identification of file subformats
 * <li>extraction of metadata from files
 * <li>and validation of file formats.
 * </ol>
 * 
 * @author Daniel M. de Oliveira
 */
public interface FileFormatFacade {

	/**
	 * Scans files and determines the PUID and, if possible, the sub-format, 
	 * depending on the configuration of the FileFormatFacade (see @link #registerSubformatIdentificationMethod(String, String)}).
	 * The method modifies the PUID and SecondaryFormatAttribute fields of files.
	 * 
	 * @param files
	 * @return return files. Used for easier testing.
	 * @throws FileNotFoundException if one or more of the files cannot be found.
	 * @throws FileFormatException if format could not get determined.
	 * @throws IOException if one or more of the files formats 
	 * could not get determined as a result of IO problems. This can has something to do with 
	 * the files to identify, but also with the helper programs used to identify the files. 
	 */
	public List<FileWithFileFormat> identify(Path workPath,List<? extends FileWithFileFormat> files, boolean pruneExceptions) 
			throws FileNotFoundException, FileFormatException, IOException;

	
	/**
	 * Extracts metadata from file and creates a xml file which contains the results.
	 * 
	 * @param file the file to extract the metadata from.
	 * @param extractedMetadata parent folder of the file must exist.
	 * @param extractedMetadata the resulting xml .
	 * 
	 * @throws ConnectionException 
	 * @throws IllegalArgumentException if parent folder of extracted Metadata does not exist.
	 */
	public boolean extract(File file, File extractedMetadata, String expectedMimeType) throws ConnectionException, IOException;
	
	
	/**
	 * Lets the user specify which strategy {@link #identify(List)} uses  
	 * to determine the subformat for files of format <i>formatPuid</i>.
	 * A strategy can be used for subformat identification of files of different primary formats.  
	 * {@link #registerSubformatIdentificationStrategyPuidMapping(String, String)} should be called 
	 * separately for each <i>formatPuid</i> to associate more formatPUIDs to the same strategy.
	 * 
	 * @param subformatIdentificationStrategyName identifier of the piece of code which is used for subformat identification for files of format <i>formatPuid</i>. 
	 * @param formatPuid Identification of a file's format inside {@link #identify(List)} as <i>formatPuid</i> triggers the execution of the strategy.
	 * 
	 * @throws IllegalArgumentException if it is not possible to instantiate the strategy. 
	 */
	public void registerSubformatIdentificationStrategyPuidMapping(
			String subformatIdentificationStrategyName,
			String formatPuid);
	
	
	
	/**
	 * Implementations of FileFormatFacade typically depend upon external programs. 
	 * These connectors are either a fixed part of a specific implementation or are 
	 * configured at runtime, which is normally the case when plugging in SubformatIdentificationStrategies on demand
	 * (via {@link #registerSubformatIdentificationStrategyPuidMapping(String, String)}).
	 * 
	 * {@link #connectivityCheck()} provides a way to let the implementation of FileFormatFacade check itself 
	 * whether its connectors can reach the necessary external programs and if these programs are present in versions supported 
	 * by the connectors.
	 *  
	 * @return true if necessary external programs are present in versions supported by the connectors. false otherwise.  
	 */
	public boolean connectivityCheck();


	public FormatScanService getFormatScanService();


	public MetadataExtractor getMetadataExtractor();


	public void setMetadataExtractor(MetadataExtractor metadataExtractor);


	public void setFormatScanService(FormatScanService formatScanService);


	public FormatScanService getSubformatScanService();


	public void setSubformatScanService(FormatScanService subformatScanService);
	
	public void setKnownFormatCommandLineErrors(KnownFormatCmdLineErrors knownErrors);
		

}
