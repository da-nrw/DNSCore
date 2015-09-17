/*
 DA-NRW Software Suite | ContentBroker
 Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
 Universität zu Köln
 Copyright (C) 2015 LVR-InfoKom
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

package de.uzk.hki.da.model;

import static de.uzk.hki.da.utils.C.*;
import static de.uzk.hki.da.utils.StringUtilities.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;

/**
 * Knows how the WorkArea is structured and how files and objects are organized on it.
 * Knows about the DNSCore data model.
 * An instance of WorkArea is used to identify one object on the WorkArea of a specific node.
 * 
 * @author Daniel M. de Oliveira
 *
 */
public class WorkArea {

	private static final String UNDERSCORE = "_";
	private static final String ERR_MSG_NULL_OR_EMPTY = "Must not be null or empty: ";
	public static final String REPRESENTATION_FILTER = "^.*[+][ab]";
	
	
	private Object o;
	private Node n;
	public static final String TMP_JHOVE = "jhove_temp";
	public static final String WA_INSTITUTION = "institution";
	public static final String PUBLIC = "public";
	public static final String WORK = "work";
	public static final String REPL = "repl";
	public static final String PIPS = "pips";
	public static final String DATA = "data";
	public static final String AIP = "aip";
	// WorkArea organization
	public static final String TMP_PIPS = "temp_pips";
	
	public WorkArea(Node n,Object o){
		this.o = o;
		this.n = n;
		
		if (o.getContractor()==null) throw new IllegalArgumentException(ERR_MSG_NULL_OR_EMPTY+"o.getContractor()");
		if (o.getIdentifier()==null) throw new IllegalArgumentException(ERR_MSG_NULL_OR_EMPTY+"o.getIdentifier()");
		if (isNotSet(n.getWorkAreaRootPath())) throw new IllegalArgumentException(ERR_MSG_NULL_OR_EMPTY+"n.getWorkAreaRootPath()");
		if (isNotSet(o.getContractor().getShort_name())) throw new IllegalArgumentException(ERR_MSG_NULL_OR_EMPTY+"o.getContractor().getShort_name()");
		if (o.getPackages()==null||o.getPackages().isEmpty()) throw new IllegalStateException(ERR_MSG_NULL_OR_EMPTY+"o.getPackages()");
		o.getLatestPackage(); // throws IllegalStateException
	}

	public File toFile(DAFile daf) {
		
		return Path.make(contractorWorkDirPath(),o.getIdentifier(),WorkArea.DATA,daf.getRep_name(),daf.getRelative_path()).toFile();
	}
	
	public File toFile(String repName, String relPath) {
		return Path.make(contractorWorkDirPath(),o.getIdentifier(),WorkArea.DATA,repName,relPath).toFile();
	}

	public Path pipFolder(String audience) {
		return Path.make(contractorPipsDirPath(audience),o.getIdentifier());
	}

	public Path pipSourceFolderPath(String audience) {
		return Path.make(n.getWorkAreaRootPath(),pipSourceFolderRelativePath(audience));
	}
	
	public Path pipSourceFolderRelativePath(String audience) {
		return new RelativePath(WorkArea.PIPS,audience,o.getContractor().getShort_name(),o.getIdentifier()+UNDERSCORE+o.getLatestPackage().getId());
	}
	
	public File pipSourceFolder(String audience) {
		return pipSourceFolderPath(audience).toFile();
	}
	
	public File pipMetadataFile(String audience,String fileName) {
		return Path.makeFile(contractorPipsDirPath(audience),o.getIdentifier(),fileName+FILE_EXTENSION_XML);
	}

	private Path contractorPipsDirPath(String audience) {
		return Path.make(n.getWorkAreaRootPath(),WorkArea.PIPS,audience,o.getContractor().getShort_name());
	}

	private Path contractorWorkDirPath() {
		return Path.make(n.getWorkAreaRootPath(),WorkArea.WORK,o.getContractor().getShort_name());
	}

	/**
	 * Copies a SIP to the ingest area
	 * @param makeFile
	 * @throws IOException 
	 */
	public void ingestSIP(File sip) throws IOException {
		if (!sip.exists()) throw new IllegalArgumentException("Missing file: "+sip);
		FileUtils.copyFile(sip, sipFile());
	}
	
	public File sipFile() {
		return Path.makeFile(n.getWorkAreaRootPath(),WorkArea.WORK,o.getContractor().getShort_name(),o.getLatestPackage().getContainerName());
	}
	
	public Path objectPath() {
		return Path.make(n.getWorkAreaRootPath(),WorkArea.WORK,o.getContractor().getShort_name(),o.getIdentifier());
	}
	
	public Path replPath() {
		return Path.make(n.getWorkAreaRootPath(),WorkArea.REPL,o.getContractor().getShort_name(),o.getIdentifier());
	}
	
	public Path dataPath() {
		return Path.make(objectPath(),"data");
	}
	
	/**
	 * Checks if for every DAFile attached to the db there is a physical file inside the object folder on the file system.
	 * @return false if there is at least one DAFile which lacks a correspondent physical file. true otherwise.
	 */
	public boolean isDBtoFSconsistent() {

		boolean consistent = true;
		
		for (Package pkg: o.getPackages())
			for (DAFile f: pkg.getFiles()){
				if (!f.getRep_name().matches(REPRESENTATION_FILTER)) continue;
				if (!toFile(f).exists()) consistent = false;
			}
		
		return consistent;
	}


	/**
	 * Checks if for every physical file inside the object on the file system there is a DAFile attached to one of the packages belonging to the object.
	 * @return false if there is at least one existent file the DAfile is missing. true otherwise.
	 */
	public boolean isFStoDBconsistent() {
		
		boolean consistent = true;
		
		for (File rep : getRepsFromFS()) {
			
			for (File fileSystemFile:getFilesOfRepresentationFS(rep.getName())){
				
				if (!existsAsAttachedDAFile(
						new DAFile(rep.getName(),getRelativePath(fileSystemFile, rep.getName()))))
					consistent = false;
			}
		}
		return consistent;
	}
	
	
	
	/**
	 * Gets the files of a representation. Operates on the basis of the FS.
	 * @param repName
	 * @return
	 */
	private Collection<File> getFilesOfRepresentationFS(String repName){
		
		return FileUtils.listFiles(Path.makeFile(Path.make(objectPath(),"data"),repName),
				TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
	}
	
	
	private List<File> getRepsFromFS() {

		FileFilter fileFilter = new RegexFileFilter(REPRESENTATION_FILTER);
		
		File[] representations = Path.make(objectPath(),"data").toFile().listFiles(fileFilter);
		if (representations==null)
			return new ArrayList<File>();
		
		Arrays.sort(representations);
		return Arrays.asList(representations);
	}
	
	
	
	/**
	 * @param f
	 * @param repName
	 * @return
	 */
	private String getRelativePath(File f,String repName){
		
		return f.getPath().replace(objectPath().toString()+"/data/"+repName,"");
	}
	
	
	/**
	 * @return
	 */
	private boolean existsAsAttachedDAFile(DAFile toCompare){
		
		for (Package p: o.getPackages()){
			for (DAFile f:p.getFiles()){
				if (f.equals(toCompare)) return true;
			}
		}
		return false;
	}
	
	public Node getNode() {
		return n;
	}
	
}
