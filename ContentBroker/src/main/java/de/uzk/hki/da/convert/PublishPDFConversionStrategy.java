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
package de.uzk.hki.da.convert;

import java.io.File;
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
import de.uzk.hki.da.model.contract.PublicationRight;
import de.uzk.hki.da.utils.Utilities;


/**
 * Converts PDF files for the presentation layer.
 *
 * @author Sebastian Cuy
 */
public class PublishPDFConversionStrategy implements ConversionStrategy {

	/** The pkg. */
	private Package pkg;
	
	/** The logger. */
	private static Logger logger = 
			LoggerFactory.getLogger(PublishPDFConversionStrategy.class);
	
	/** The audiences. */
	private String[] audiences = new String [] {"PUBLIC", "INSTITUTION" };

	private Object object;
	
	/**
	 * TODO remove code duplication with publishimage
	 * @author Daniel M. de Oliveira
	 * @param audience
	 * @return
	 */
	private PublicationRight getPublicationRightForAudience(String audience){
		for (PublicationRight right:object.getRights().getPublicationRights()){
			if (right.getAudience().toString().equals(audience)) return right;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#convertFile(de.uzk.hki.da.model.ConversionInstruction)
	 */
	@Override
	public List<Event> convertFile(ConversionInstruction ci)
			throws IOException {
		
		if (ci.getConversion_routine()==null) throw new IllegalStateException("conversionRoutine not set");
		if (ci.getConversion_routine().getTarget_suffix().isEmpty()) throw new IllegalStateException("target suffix in conversionRoutine not set");
		
		List<Event> results = new ArrayList<Event>();

		String input  = ci.getSource_file().toRegularFile().getAbsolutePath();
		
		for (String audience: audiences ) {
			
			String audience_lc = audience.toLowerCase();
			
			DAFile target = new DAFile(pkg,"dip/"+audience_lc,Utilities.slashize(ci.getTarget_folder())+
					FilenameUtils.getBaseName(input)+"."+ci.getConversion_routine().getTarget_suffix());
			target.toRegularFile().getParentFile().mkdirs();
			
			String numberOfPagesText=null;
			if (getPublicationRightForAudience(audience)!=null) 
				numberOfPagesText=getPublicationRightForAudience(audience).getTextRestriction().getPages().toString();
			
			String certainPagesText=null;
			if (getPublicationRightForAudience(audience)!=null){
				certainPagesText="";
				if (getPublicationRightForAudience(audience).getTextRestriction()!=null)
					if (getPublicationRightForAudience(audience).getTextRestriction().getCertainPages()!=null)
						for (int i=0;i<getPublicationRightForAudience(audience).getTextRestriction().getCertainPages().length;i++){
							certainPagesText+=getPublicationRightForAudience(audience).getTextRestriction().getCertainPages()[i];
							logger.debug(":"+i);
						}
				
			}
					
			// copy whole file if no restrictions are found
			if (numberOfPagesText == null && certainPagesText == null) {
				try {
					FileUtils.copyFileToDirectory(new File(input),
							target.toRegularFile().getParentFile());
					Event e = new Event();
					e.setDetail("Copied PDF");
					e.setSource_file(ci.getSource_file());
					e.setTarget_file(target);
					e.setType("CONVERT");
					e.setDate(new Date());
					results.add(e);
				} catch (IOException e) {
					throw new RuntimeException("Could not copy PDF!", e);
				}
				continue;
			}
			PdfService pdf = new PdfService(new File(input),target.toRegularFile());
		    pdf.reduceToCertainPages(numberOfPagesText, certainPagesText);
			Event e = new Event();
			e.setDetail("converted with PDFBox");
			e.setSource_file(ci.getSource_file());
			e.setTarget_file(target);
			e.setType("CONVERT");
			e.setDate(new Date());
			results.add(e);
		
		}
		
		return results;
		
	}

	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setParam(java.lang.String)
	 */
	@Override
	public void setParam(String param) {
		// not needed
	}

	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setCLIConnector(de.uzk.hki.da.convert.CLIConnector)
	 */
	@Override
	public void setCLIConnector(CLIConnector cliConnector) {
		// not needed
	}

	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setObject(de.uzk.hki.da.model.Object)
	 */
	@Override
	public void setObject(Object obj) {
		this.pkg = obj.getLatestPackage();
		this.object = obj;
	}

}
