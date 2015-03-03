/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2015 LVR-Infokom
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

package de.uzk.hki.da.cb;

import static de.uzk.hki.da.core.C.METADATA_STREAM_ID_DC;
import static de.uzk.hki.da.core.C.WA_INSTITUTION;
import static de.uzk.hki.da.core.C.WA_PUBLIC;
import static de.uzk.hki.da.utils.StringUtilities.isNotSet;
import static de.uzk.hki.da.utils.StringUtilities.isSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.input.BOMInputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.xml.sax.SAXException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.PreconditionsNotMetException;
import de.uzk.hki.da.core.SubsystemNotAvailableException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.metadata.XMLUtils;
import de.uzk.hki.da.metadata.XsltGenerator;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.util.ConfigurationException;

/**
 * @author Daniel M. de Oliveira
 */
public class CreateDCAction extends AbstractAction {

	private static final String FORMAT = "format";
	private static final String IDENTIFIER = "identifier";
	private static final String PURL_ORG_DC = "http://purl.org/dc/elements/1.1/";
	
	private Map<String,String> dcMappings = null;
	private boolean writePackageTypeToDC = false;
	
	@Override
	public void checkConfiguration() {
		if (dcMappings==null) throw new ConfigurationException("dcMappings not set");
	}

	
	@Override
	public void checkPreconditions() {
		
		if (isSet(o.getPackage_type())){
		
			if (dcMappings.get(o.getPackage_type())==null) 
				throw new PreconditionsNotMetException("No xslt mapping for package type available");
			if (! new File(dcMappings.get(o.getPackage_type())).exists()) 
				throw new PreconditionsNotMetException("Missing xslt file for package type");
			
			if (wa.pipFolder(WA_PUBLIC).toFile().exists()&&(! wa.metadataStream(WA_PUBLIC, o.getPackage_type()).exists()))
				throw new PreconditionsNotMetException("Missing metadata file for package type in public pip");
			if (wa.pipFolder(WA_INSTITUTION).toFile().exists()&&(! wa.metadataStream(WA_INSTITUTION, o.getPackage_type()).exists()))
					throw new PreconditionsNotMetException("Missing metadata file for package type in insitution pip");
		}
		
		if (isNotSet(o.getUrn())) throw new PreconditionsNotMetException("urn not set");
	}

	
	@Override
	public boolean implementation() throws FileNotFoundException, IOException,
			UserException, RepositoryException, JDOMException,
			ParserConfigurationException, SAXException,
			SubsystemNotAvailableException {

		if (isNotSet(o.getPackage_type())) return true;
		
		
		if (wa.pipFolder(WA_PUBLIC).toFile().exists()){
			createDCForAudience(WA_PUBLIC);
		}
		if (wa.pipFolder(WA_INSTITUTION).toFile().exists()){
			createDCForAudience(WA_INSTITUTION);
		}
		
		return true;
	}

	
	private void createDCForAudience(String audience) throws IOException {
		File publicDCFile = wa.metadataStream(audience, METADATA_STREAM_ID_DC);
		publicDCFile.createNewFile();
		
		copyDCdatastreamFromMetadata(audience);
		String updatedDcContent = readDCAndReplaceURN(audience,o.getUrn());
		writeDCBackToPIP(audience, updatedDcContent);
	}


	@Override
	public void rollback() throws Exception {
		if (wa.metadataStream(WA_PUBLIC, METADATA_STREAM_ID_DC).exists()) 
			wa.metadataStream(WA_PUBLIC, METADATA_STREAM_ID_DC).delete();
		if (wa.metadataStream(WA_INSTITUTION, METADATA_STREAM_ID_DC).exists()) 
			wa.metadataStream(WA_INSTITUTION, METADATA_STREAM_ID_DC).delete();
	}
	
	
	private void writeDCBackToPIP(String audience, String updatedDcContent)
			throws IOException {
		
		wa.metadataStream(audience, METADATA_STREAM_ID_DC).delete();
		FileWriter fw = null;
		try {
			fw = new FileWriter(wa.metadataStream(audience, METADATA_STREAM_ID_DC));
			fw.write(updatedDcContent);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (fw!=null) fw.close();
		}
	}


	private String readDCAndReplaceURN(String audience,String urn)
			throws IOException {
		FileInputStream in = null;
		String content="";
		
		try {
			in=new FileInputStream(
				wa.metadataStream(audience,METADATA_STREAM_ID_DC));
			
			SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
			Document doc = null;
			doc=builder.build(in);
			doc.getRootElement().addContent(
					new Element(IDENTIFIER,METADATA_STREAM_ID_DC.toLowerCase(),PURL_ORG_DC)
					.setText(urn));
			doc.getRootElement().addContent(
					new Element(FORMAT,METADATA_STREAM_ID_DC.toLowerCase(),PURL_ORG_DC)
					.setText(o.getPackage_type()));
			content = new XMLOutputter().outputString(doc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (in!=null) in.close();
		}
		return content;
	}
	
	
	
	/**
	 * @param packageType
	 * @param metadataFile
	 */
	private void copyDCdatastreamFromMetadata(String audience) {
		
		FileInputStream inputStream = null;
		BOMInputStream bomInputStream = null;
		FileOutputStream outputStream = null;
		
		String xsltFile = getDcMappings().get(o.getPackage_type());
		if (xsltFile == null) {
			throw new RuntimeException(
					"No conversion available for package type '"
							+ o.getPackage_type() + "'. DC can not be created.");
		}
		try {
			inputStream = new FileInputStream(
				wa.metadataStream(audience, o.getPackage_type()));

			bomInputStream = new BOMInputStream(inputStream);
			XsltGenerator xsltGenerator = new XsltGenerator(xsltFile,
					bomInputStream);

			String result = xsltGenerator.generate();

			File file = wa.metadataStream(audience, METADATA_STREAM_ID_DC);
			outputStream = new FileOutputStream(file);
			
			outputStream.write(result.getBytes(C.ENCODING_UTF_8.toLowerCase()));
			outputStream.flush();

		} catch (Exception e) {
			throw new RuntimeException("Unable to create DC file.", e);
		} finally {
			try {
				if (inputStream!=null) inputStream.close();
				if (bomInputStream!=null) bomInputStream.close();
				if (outputStream!=null)   outputStream.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		
	}
	
	
	
	
	/**
	 * Gets the map that describes which XSLTs should be
	 * used to convert Metadata to Dublin Core.
	 * @return a map, keys represent metadata formats,
	 * 	values the path to the XSLT file
	 */
	public Map<String,String> getDcMappings() {
		return dcMappings;
	}

	/**
	 * Sets the map that describes which XSLTs should be
	 * used to convert Metadata to Dublin Core.
	 * @param a map, keys represent metadata formats,
	 * 	values the path to the XSLT file
	 */
	public void setDcMappings(Map<String,String> dcMappings) {
		this.dcMappings = dcMappings;
	}
	
	
	/**
	 * Check if the package type is written to the
	 * Dublin Core metadata file.
	 * @return
	 */
	public boolean isWritePackageTypeToDC() {
		return writePackageTypeToDC;
	}

	/**
	 * Set wether the package type should be written to the
	 * Dublin Core metadata 
	 * @param writePackageTypeToDC
	 */
	public void setWritePackageTypeToDC(boolean writePackageTypeToDC) {
		this.writePackageTypeToDC = writePackageTypeToDC;
	}
}
