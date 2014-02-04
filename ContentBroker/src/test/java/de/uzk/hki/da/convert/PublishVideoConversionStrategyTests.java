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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.w3c.dom.Document;

import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PublicationRight;
import de.uzk.hki.da.model.VideoRestriction;
import de.uzk.hki.da.model.PublicationRight.Audience;
import de.uzk.hki.da.service.XPathUtils;
import de.uzk.hki.da.utils.SimplifiedCommandLineConnector;
import de.uzk.hki.da.utils.TESTHelper;


/**
 * The Class PublishVideoConversionStrategyTests.
 *
 * @author Daniel M. de Oliveira
 */
public class PublishVideoConversionStrategyTests {

	/** The base path. */
	String basePath = "src/test/resources/convert/PublishVideoConversionStrategyTests/";
	
	/**
	 * Tear down.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After
	public void tearDown() throws IOException {
		
		if (new File(basePath + "dip").exists())
			FileUtils.deleteDirectory(new File(basePath + "dip"));
		FileUtils.deleteDirectory(new File(basePath + "/TEST"));
	}
	
	
	
	/**
	 * Test.
	 *
	 * @throws FileNotFoundException the file not found exception
	 */
	@Test
	public void test() throws FileNotFoundException {
		
		Document dom = XPathUtils.parseDom(basePath + "premis.xml");
		if (dom==null){
			throw new RuntimeException("Error while parsing premis.xml");
		}
		
		Object o = TESTHelper.setUpObject("1",basePath);
		PublicationRight right = new PublicationRight();
		right.setAudience(Audience.PUBLIC);
		right.setVideoRestriction(new VideoRestriction());
		right.getVideoRestriction().setHeight("360");
		right.getVideoRestriction().setDuration(180);
		o.getRights().getPublicationRights().add(right);
		
		SimplifiedCommandLineConnector cli = mock ( SimplifiedCommandLineConnector.class );
		
		
		String cmdPUBLIC[] = new String[]{
				"HandBrakeCLI",
				"-i",
				"\"" + new File(basePath + "TEST/1/data/a/filename.avi").getAbsolutePath() + "\"",
				"-o",
				"\"" + basePath + "TEST/1/data/dip/public/target/filename.mp4\"",
				"-e","x264","-f","mp4","-E","faac",
				"-l","360","--stop-at","duration:180"
		};
		when(cli.execute(cmdPUBLIC)).thenReturn(true);
		
		String cmdINSTITUTION[] = new String[]{
				"HandBrakeCLI",
				"-i",
				"\"" + new File(basePath + "TEST/1/data/a/filename.avi").getAbsolutePath() + "\"",
				"-o",
				"\"" + basePath + "TEST/1/data/dip/institution/target/filename.mp4\"",
				"-e","x264","-f","mp4","-E","faac"
		};
		when(cli.execute(cmdINSTITUTION)).thenReturn(true);
		
		
		
		PublishVideoConversionStrategy s = new PublishVideoConversionStrategy();
		s.setCLIConnector( cli );
		s.setDom(dom);
		
		ConversionInstruction ci = new ConversionInstruction();
		ci.setSource_file(new DAFile(o.getLatestPackage(),"a","filename.avi"));
		ci.setTarget_folder("target/");

		s.setObject(o);
		s.convertFile(ci);
	}
}
