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

 * Adds DOXC Conversion by calling a Webservice for conversion. 
 * Actually, the webservice does a conversion to PDF1.5 by using MS Office(R). 
 * 
 * The webservice has to be deployed on a Windows Server(R) compliant host.
 * The recieved file is converted by calling the Ghostscript PDF/A conversion.
 * 
 * The webservice itself is not part of this sourcecode package.
 *  
 * @Author: Jens Peters
 * 
 */

package de.uzk.hki.da.format;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.utils.SimplifiedCommandLineConnector;
import de.uzk.hki.da.utils.Utilities;
import de.uzk.hki.da.webservice.HttpFileTransmissionClient;


/**
 * The Class DocxConversionStrategy.
 */
public class DocxConversionStrategy  implements ConversionStrategy {

	/** The logger. */
	private static Logger logger = 
		LoggerFactory.getLogger(DocxConversionStrategy.class);
	
	/** The cli connector. */
	private SimplifiedCommandLineConnector cliConnector;

	/** The pkg. */
	Package pkg;
	
	/** The object. */
	Object object;

	private HttpFileTransmissionClient httpclient;
	
	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setParam(java.lang.String)
	 */
	@Override
	public void setParam(String param) {}

	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setCLIConnector(de.uzk.hki.da.convert.CLIConnector)
	 */
	@Override
	public void setCLIConnector(SimplifiedCommandLineConnector cliConnector) {
		this.cliConnector = cliConnector;
		
	}
	
	public DocxConversionStrategy() {
		setHttpclient(new HttpFileTransmissionClient());
	}
	
	
	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#convertFile(de.uzk.hki.da.model.ConversionInstruction)
	 */
	@Override
	public List<Event> convertFile(ConversionInstruction ci)
			throws FileNotFoundException {

		if (ci.getConversion_routine()==null) throw new IllegalStateException("conversionRoutine not set");
		if (ci.getConversion_routine().getTarget_suffix().isEmpty()) throw new IllegalStateException("target suffix in conversionRoutine not set");
		if (ci.getConversion_routine().getParams()==null) throw new IllegalStateException("add params not set");
		if (cliConnector==null) throw new IllegalStateException("Cli Connector is not set");
				
		File result = new File(generateTargetFilePath(ci));
		
		List<Event> results = new ArrayList<Event>();
		
		getHttpclient().setUrl(ci.getConversion_routine().getParams());
		getHttpclient().setSourceMimeType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		getHttpclient().postFileAndReadResponse(ci.getSource_file().toRegularFile(), result);
		
		if (result.exists()) {
		DAFile daf = new DAFile(pkg,object.getPath("newest").getLastElement(),Utilities.slashize(ci.getTarget_folder())+result.getName());
		logger.debug("new dafile:"+daf);
							
		// TODO Doing PDF/A Conversion with a temp file, due to recieve PDF 1.5 from MS instead of PDF/A . 
		// This should be the same command for "normal" PDFs
		// read it from Database? 
		File tempFile = new File("/"+ FilenameUtils.getPath(daf.toRegularFile().getAbsolutePath()) + "_" + daf.toRegularFile().getName()); 
		
		String commandAsArray[] = new String[]{
				"gs","-q", "-dPDFA", "-dPDFACompatibilityPolicy=1", "-dBATCH" ,"-dNOPAUSE" ,"-dNOOUTERSAVE",
				"-dUseCIEColor","-sProcessColorModel=DeviceCMYK","-sDEVICE=pdfwrite", 
				"-sOutputFile=" + tempFile.getAbsolutePath() 
				,"conf/PDFA_def.ps",daf.toRegularFile().getAbsolutePath()};
		if (!cliConnector.execute(commandAsArray)){
			throw new RuntimeException("GS command not succeeded");
		}
		
		try {
			if (tempFile.exists()) {
				result.delete();
				FileUtils.moveFile(tempFile, daf.toRegularFile());
			} else throw new RuntimeException("temp file "  + tempFile.getAbsolutePath() + " was not created!");
			} catch (IOException e1) {
			throw new RuntimeException("MV command not succeeded on temp file " +tempFile.getAbsolutePath(), e1);
		}
		
		PdfService.validatePdfA(daf.toRegularFile());
		
		Event e = new Event();
		e.setType("CONVERT");
		e.setDetail("Webservice DOCX2PDF "+ ci.getConversion_routine().getParams() + " AND "+Utilities.createString(commandAsArray));
		e.setSource_file(daf);
		e.setTarget_file(daf);
		e.setDate(new Date());	
		results.add(e);
		
		} else throw new RuntimeException("Target File not found! " + result.getAbsolutePath());
		return results;
	}
	
	/**
	 * Generate target file path.
	 *
	 * @param ci the ci
	 * @return the string
	 */
	public String generateTargetFilePath(ConversionInstruction ci) {
		String input  = ci.getSource_file().toRegularFile().getAbsolutePath();
		return object.getPath("newest")+"/"+Utilities.slashize(ci.getTarget_folder())
				+ FilenameUtils.getBaseName(input)+"."+ci.getConversion_routine().getTarget_suffix();
	}

	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setObject(de.uzk.hki.da.model.Object)
	 */
	@Override
	public void setObject(Object obj) {
		this.object = obj;
		this.pkg = obj.getLatestPackage();
	}

	/**
	 * @return the httpclient
	 */
	public HttpFileTransmissionClient getHttpclient() {
		return httpclient;
	}

	/**
	 * @param httpclient the httpclient to set
	 */
	public void setHttpclient(HttpFileTransmissionClient httpclient) {
		this.httpclient = httpclient;
	}

}
