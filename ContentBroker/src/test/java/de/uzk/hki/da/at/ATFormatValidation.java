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
package de.uzk.hki.da.at;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.pkg.BagitUtils;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;


/**
 * Relates to AK-T/02 Ingest - Sunny Day Scenario.
 * @author Daniel M. de Oliveira
 */
public class ATFormatValidation extends PREMISBase{
	
	static class IdentifiedFormat{
		String puid="";
		String fileName="";
		String reportingModule="";
		String format ="";
		String status="";
		public IdentifiedFormat(String puid, String fileName, String reportingModule, String format, String status) {
			super();
			this.puid = puid;
			this.fileName = fileName;
			this.reportingModule = reportingModule;
			this.format = format;
			this.status = status;
		}
		@Override
		public int hashCode() {
			return toString().hashCode();
		}
		@Override
		public boolean equals(java.lang.Object obj) {
			if (obj == null)
				return false;
			if (this == obj)
				return true;
			if (getClass() != obj.getClass())
				return false;
			IdentifiedFormat other = (IdentifiedFormat) obj;
			return toString().equals(other.toString());
		}
		@Override
		public String toString() {
			return "IdentifiedFormat [puid=" + puid + ", fileName=" + fileName + ", reportingModule=" + reportingModule
					+ ", format=" + format + ", status=" + status + "]";
		}
	};
	

	private static final String originalName = "ATUseCaseIngestTestFormats";
	private static final File unpackedDIP = new File("/tmp/ATUseCaseIngestTestFormats");
	private Object object = null;
	
	private HashMap<String,IdentifiedFormat> identifiedFiles=new HashMap<String,IdentifiedFormat> (){{
		put("waveFile.wav",new IdentifiedFormat("fmt/141","waveFile.wav","WAVE-hul","WAVE","Well-Formed and valid"));
		put("aifFile.aiff",new IdentifiedFormat("fmt/414","aifFile.aiff","AIFF-hul","AIFF","Well-Formed and valid"));
		put("gifFile.gif",new IdentifiedFormat("fmt/4","gifFile.gif","GIF-hul","GIF","Well-Formed and valid"));
		put("htmlFile.html",new IdentifiedFormat("fmt/471","htmlFile.html","HTML-hul","HTML","Well-Formed, but not valid"));
		put("jp2File.jp2",new IdentifiedFormat("x-fmt/392","jp2File.jp2","JPEG2000-hul","JPEG 2000","Well-Formed and valid"));
		put("jpegFile.jpg",new IdentifiedFormat("fmt/43","jpegFile.jpg","JPEG-hul","JPEG","Well-Formed and valid"));
		put("pdfAFile.pdf",new IdentifiedFormat("fmt/95","pdfAFile.pdf","PDF-hul","PDF","Well-Formed and valid"));
		put("pdfFile.pdf",new IdentifiedFormat("fmt/19","pdfFile.pdf","PDF-hul","PDF","Well-Formed and valid"));
		put("pngFile.png",new IdentifiedFormat("fmt/11","pngFile.png","PNG-gdm","PNG","Well-Formed and valid"));
		put("premis.xml",new IdentifiedFormat("fmt/101","premis.xml","XML-hul","XML","Well-Formed, but not valid"));
		put("tifFile.tif",new IdentifiedFormat("fmt/353","tifFile.tif","TIFF-hul","TIFF","Well-Formed and valid"));
		put("txtFile.txt.gz",new IdentifiedFormat("x-fmt/266","txtFile.txt.gz","GZIP-kb","GZIP","Well-Formed and valid"));
	}};
	
	
	@After
	public void tearDown() throws IOException{
		FolderUtils.deleteDirectorySafe(unpackedDIP);
		Path.makeFile("tmp",object.getIdentifier()+".pack_1.tar").delete(); // retrieved dip
	}
	
	@Test
	public void testJhoveProperPREMISCreation() throws Exception {
		System.out.println("begin ATFormatValidation");
		ath.putSIPtoIngestArea(originalName, "tgz", originalName);
		ath.awaitObjectState(originalName,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		object=ath.getObject(originalName);
		
		ath.retrieveAIP(object,unpackedDIP,"1");
		assertThat(object.getObject_state()).isEqualTo(100);
		String unpackedObjectPath = unpackedDIP.getAbsolutePath()+"/";
		
		String folders[] = new File(unpackedObjectPath + "data/").list();
		String repBName="";
		for (String f:folders){
			if (f.contains("+b")) repBName = f;
		}
		
		System.out.println("begin xml evaluation ATFormatValidation");
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		Document doc;
		try {
			doc = builder.build(new File(unpackedObjectPath +  "data/" + repBName + "/premis.xml"));
		} catch (Exception e) {
			throw new RuntimeException("Failed to read premis file", e);
		}
		
		Element rootElement = doc.getRootElement();
		Namespace ns = rootElement.getNamespace();
		//e.getAttribute("type",Namespace.getNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance"))
		List<Element> objectElements = rootElement.getChildren("object", ns);
	
		int checkedObjects = 0;
		for (Element e:objectElements){
			if(!e.getAttributeValue("type",C.XSI_NS," ").equals("file"))
				continue;
			
			Element objectChExt = e.getChild("objectCharacteristics",ns).getChild("objectCharacteristicsExtension",ns);
			if(objectChExt==null)
				continue;
			
			
			Element jhoveData = (Element)objectChExt.getChild("mdSec",ns).getChild("mdWrap",ns).getChild("xmlData",ns).getChildren().get(0);
			Namespace nsJhove = jhoveData.getNamespace();	
			Element jhoveRepInfo = 		jhoveData.getChild("repInfo",nsJhove);
			
			String puid=e.getChild("objectCharacteristics",ns).getChild("format",ns).getChild("formatRegistry",ns).
					getChild("formatRegistryKey",ns).getValue();
			String fileName=new File(jhoveRepInfo.getAttributeValue("uri")).getName();
			String reportingModule=jhoveRepInfo.getChild("reportingModule", nsJhove).getValue();
			String format =jhoveRepInfo.getChild("format", nsJhove).getValue();
			String status=jhoveRepInfo.getChild("status", nsJhove).getValue();
			
			IdentifiedFormat tmpFormat=new IdentifiedFormat(puid,fileName,reportingModule,format,status);
			
			/*System.out.println("put(\""+tmpFormat.fileName+"\",new IdentifiedFormat(\""+tmpFormat.puid+
					"\",\""+tmpFormat.fileName+"\",\""+tmpFormat.reportingModule+
					"\",\""+tmpFormat.format+"\",\""+tmpFormat.status+"\"));");*/
			if(identifiedFiles.containsKey(fileName)) {
				checkedObjects++;
				System.out.println("testJhoveProperPREMISCreation: "+checkedObjects+"/"+identifiedFiles.size()+" Dateiformat in Premis abgleich "+tmpFormat+" ?? "+identifiedFiles.get(fileName));
				assertEquals("Dateiformat in Premis weicht ab "+tmpFormat+" != "+identifiedFiles.get(fileName),identifiedFiles.get(fileName),tmpFormat);
			}else {
				throw new RuntimeException("Unknown File in Premis: "+fileName+" \n "+tmpFormat);
			}
		}
		
		assertEquals("Die Menge der Dateien in Premis stimmt nicht mit der Testdefinition ueberein: "+
				checkedObjects+" != "+identifiedFiles.size(),checkedObjects,identifiedFiles.size());

		System.out.println("end ATFormatValidation");
	}
	
}