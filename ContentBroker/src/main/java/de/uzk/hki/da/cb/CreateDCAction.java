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

import static de.uzk.hki.da.core.C.FILE_EXTENSION_XML;
import static de.uzk.hki.da.core.C.METADATA_STREAM_ID_DC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.input.BOMInputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.SAXException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.PreconditionsNotMetException;
import de.uzk.hki.da.core.SubsystemNotAvailableException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.metadata.XMLUtils;
import de.uzk.hki.da.metadata.XsltEDMGenerator;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.utils.StringUtilities;

/**
 * @author Daniel M. de Oliveira
 */
public class CreateDCAction extends AbstractAction {

	private static final String IDENTIFIER = "identifier";
	private static final String PURL_ORG_DC = "http://purl.org/dc/elements/1.1/";
	
	private String[] repNames;
	private Map<String,String> dcMappings = new HashMap<String,String>();
	private boolean writePackageTypeToDC = false;
	
	@Override
	public void checkConfiguration() {
		// TODO Auto-generated method stub
	}

	
	@Override
	public void checkPreconditions() {
		//if (StringUtilities.isNotSet(o.getPackage_type())) throw new PreconditionsNotMetException("o.getPackage_type()");
		//if (StringUtilities.isNotSet(o.getMetadata_file())) throw new PreconditionsNotMetException("o.getMetadata_file()");
	}

	
	@Override
	public boolean implementation() throws FileNotFoundException, IOException,
			UserException, RepositoryException, JDOMException,
			ParserConfigurationException, SAXException,
			SubsystemNotAvailableException {
//		
//		copyDCdatastreamFromMetadata(o.getPackage_type(), o.getMetadata_file());
//		
//		if (isWritePackageTypeToDC())
//			writePackageTypeToDC(o.getPackage_type());
//		rewriteDCIfExists(urn, packagePath);
		
		// TODO Auto-generated method stub
		return true;
	}

	
	@Override
	public void rollback() throws Exception {
		// TODO Auto-generated method stub
	}
	
	
	
	
	private void rewriteDCIfExists(String urn, Path packagePath)
			throws IOException {
		if (Path.makeFile(packagePath,METADATA_STREAM_ID_DC+FILE_EXTENSION_XML).exists()) {
			String updatedDcContent = readDCAndReplaceURN(urn, packagePath);
			writeDCBackToPIP(packagePath, updatedDcContent);
			logger.info("Successfully added identifiers to DC datastream");
		}
	}


	private void writeDCBackToPIP(Path packagePath, String updatedDcContent)
			throws IOException {
		Path.makeFile(packagePath,METADATA_STREAM_ID_DC+FILE_EXTENSION_XML).delete();
		FileWriter fw = null;
		try {
			fw = new FileWriter(Path.makeFile(packagePath,METADATA_STREAM_ID_DC+FILE_EXTENSION_XML));
			fw.write(updatedDcContent);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (fw!=null) fw.close();
		}
	}


	private String readDCAndReplaceURN(String urn, Path packagePath)
			throws IOException {
		FileInputStream in = null;
		String content="";
		
		try {
			in=new FileInputStream(Path.makeFile(packagePath,METADATA_STREAM_ID_DC+FILE_EXTENSION_XML));
			SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
			Document doc = null;
			doc=builder.build(in);
			doc.getRootElement().addContent(
					new Element(IDENTIFIER,METADATA_STREAM_ID_DC,PURL_ORG_DC)
					.setText(urn));
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
	private void copyDCdatastreamFromMetadata(String packageType,
			String metadataFile) {
		if (packageType != null && metadataFile != null) {
			String xsltFile = getDcMappings().get(packageType);
			if (xsltFile == null) {
				throw new RuntimeException("No conversion available for package type '" + packageType + "'. DC can not be created.");
			}
			try {
				for (String repName : getRepNames()) {
					// 
//					if (!repName.startsWith(C.WA_DIP) 	|| !representationExists(repName)) continue;
					
					FileInputStream inputStream = new FileInputStream(Path.make(o.getDataPath(),repName,metadataFile).toString());
					BOMInputStream bomInputStream = new BOMInputStream(inputStream);
					XsltEDMGenerator xsltGenerator = new XsltEDMGenerator(xsltFile, bomInputStream);
					String result = xsltGenerator.generate();
					File file = new File(o.getDataPath() + "/"+repName + "/DC.xml");
					if (!file.exists()) file.createNewFile();
					FileOutputStream outputStream = new FileOutputStream(file);
					outputStream.write(result.getBytes("utf-8"));
					outputStream.flush();
					outputStream.close();
				}
			} catch (Exception e) {
				throw new RuntimeException("Unable to create DC file.", e);
			}
		}
	}
	
	
	
	void writePackageTypeToDC(String packageType) {
		
//		if (packageType != null) {
//			for (String repName : getRepNames()) {
//				if(representationExists(repName)) {
//					File file = Path.make(o.getDataPath(),repName,"DC.xml").toFile();
//					if (file.exists()) {
//						try {
//							FileInputStream inputStream = new FileInputStream(file);
//							BOMInputStream bomInputStream = new BOMInputStream(inputStream);
//						
//							SAXBuilder builder = new SAXBuilder();
//							Document doc;
//						
//							doc = builder.build(bomInputStream);
//							writeDCForDIP(doc, packageType, file.getAbsolutePath());
//						} catch (Exception e) {
//							throw new RuntimeException("Unable to write package type to DC!", e);
//						} 
//					} else {
//						logger.warn("Unable to locate DC file, creating one ...");
//						Document doc = new Document();
//						doc.setRootElement(new Element("dc", "oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/"));
//						String dcPath = o.getDataPath() +"/"+ repName + "/DC.xml";
//						writeDCForDIP(doc, packageType, dcPath);
//					}
//				}
//			}
//		}	
	}
	
	
	private void writeDCForDIP(Document doc, String packageType, String dcPath) {
		try {
			doc.getRootElement().addContent(
				new Element("format","dc","http://purl.org/dc/elements/1.1/")
				.setText(packageType));
			XMLOutputter outputter = new XMLOutputter();
			outputter.setFormat(Format.getPrettyFormat());
			outputter.output(doc, new FileWriter(dcPath));
		} catch (Exception e) {
			throw new RuntimeException("Unable to write package type to DC!", e);
		} 
	}
	
	/**
	 * Get the names of the representations the action
	 * should work on.
	 * @return an array of representation names
	 */
	public String[] getRepNames() {
		return repNames;
	}

	/**
	 * Set the names of the representations the action
	 * should work on.
	 * @param an array of representation names
	 */
	public void setRepNames(String[] repNames) {
		this.repNames = repNames;
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
