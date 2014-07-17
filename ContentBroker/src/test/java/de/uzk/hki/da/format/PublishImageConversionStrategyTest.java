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

package de.uzk.hki.da.format;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.w3c.dom.Document;

import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.ImageRestriction;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PublicationRight;
import de.uzk.hki.da.model.PublicationRight.Audience;
import de.uzk.hki.da.service.XPathUtils;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.SimplifiedCommandLineConnector;
import de.uzk.hki.da.utils.TESTHelper;
import de.uzk.hki.da.utils.TC;


/**
 * The Class PublishImageConversionStrategyTest.
 *
 * @author Daniel M. de Oliveira
 */
public class PublishImageConversionStrategyTest {

	Path workAreaRootPath = Path.make(TC.TEST_ROOT_FORMAT,"PublishImageConversionStrategyTests");
	Path dataPath         = Path.make(workAreaRootPath,"work/TEST/123/data/");
	
	
	/** The cr. */
	ConversionRoutine cr;
	
	/**
	 * Tear down.
	 */
	@After
	public void tearDown() {
	try {
			if (Path.makeFile(dataPath,"dip").exists())
				FileUtils.deleteDirectory(Path.makeFile(dataPath,"dip"));			
		} catch (IOException e) {}
	}
	


	
	/**
	 * Test resize and watermark.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testResizeAndWatermark() throws Exception {
		
		Object o = TESTHelper.setUpObject("123",new RelativePath(workAreaRootPath));
		PublicationRight right = new PublicationRight();
		right.setAudience(Audience.PUBLIC);
		right.setImageRestriction(new ImageRestriction());
		right.getImageRestriction().setWidth("480");
		right.getImageRestriction().setHeight("360");
		right.getImageRestriction().setWatermarkOpacity("50");
		right.getImageRestriction().setWatermarkPosition("north");
		right.getImageRestriction().setWatermarkPointSize("10");
		right.getImageRestriction().setWatermarkString("Hallo");
		o.getRights().getPublicationRights().add(right);
		
		SimplifiedCommandLineConnector cli = mock ( SimplifiedCommandLineConnector.class );
		
		String cmdPUBLIC[] = new String[]{
				"convert",
				Path.makeFile(dataPath,"a/filename.tif").getAbsolutePath(),
				 "-resize","480x360", // ! ImageMagick expects this to be 2 params 
				 "-font", "Arial", "-pointsize", "10", "-draw",
				 "gravity north fill #0000007f text 0,15 'Hallo' fill #ffffff7f text 0,14 'Hallo'",
				Path.makeFile(dataPath,"dip/public/target/filename.jpg").getAbsolutePath(),
		};		
		when(cli.execute(cmdPUBLIC)).thenReturn(true);
		
		String cmdINST[] = new String[]{
				"convert",
				Path.makeFile(dataPath,"a/filename.tif").getAbsolutePath(),
				Path.makeFile(dataPath,"dip/institution/target/filename.jpg").getAbsolutePath()
		};
		when(cli.execute(cmdINST)).thenReturn(true);		
		
		PublishImageConversionStrategy s = new PublishImageConversionStrategy();
		s.setCLIConnector( cli );
		
		DAFile sourceFile = new DAFile(o.getLatestPackage(),"a","filename.tif");
		
		ConversionInstruction ci = new ConversionInstruction();
		ci.setSource_file(sourceFile);
		ci.setTarget_folder("target/");
		
		ConversionRoutine cr = new ConversionRoutine();
		cr.setTarget_suffix("jpg");
		ci.setConversion_routine(cr);
		
		
		s.setObject(o);
		List<Event> events = s.convertFile(ci);
		
		assertEquals(sourceFile,events.get(0).getSource_file());
		assertEquals(sourceFile,events.get(1).getSource_file());
		
		assertEquals(new DAFile(null,"dip/public","target/filename.jpg"),events.get(0).getTarget_file());
		assertEquals(new DAFile(null,"dip/institution","target/filename.jpg"),events.get(1).getTarget_file());
	}
	
	/**
	 * Test footer text with resize.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testFooterTextWithResize() throws Exception {
		
		Object o = TESTHelper.setUpObject("123",new RelativePath(workAreaRootPath));
		PublicationRight right = new PublicationRight();
		right.setAudience(Audience.PUBLIC);
		right.setImageRestriction(new ImageRestriction());
		right.getImageRestriction().setWidth("480");
		right.getImageRestriction().setHeight("360");
		right.getImageRestriction().setFooterText("Hallo");
		o.getRights().getPublicationRights().add(right);
		
		SimplifiedCommandLineConnector cli = mock ( SimplifiedCommandLineConnector.class );
		
		String cmdPUBLIC[] = new String[]{
				"convert",
				Path.makeFile(dataPath,"a/filename.tif").getAbsolutePath(),
					"-resize","480x360", // ! ImageMagick expects this to be 2 params 
					"-background", "'#0008'",
					"-fill", "white",
					"-gravity", "center",
					"-size","480x30",
					"caption:\"Hallo\"",
					"-gravity", "south",
					"-composite",
					Path.makeFile(dataPath,"dip/public/target/filename.jpg").getAbsolutePath()
		};		
		when(cli.execute(cmdPUBLIC)).thenReturn(true);
		
		String cmdINST[] = new String[]{
				"convert",
				Path.makeFile(dataPath,"a/filename.tif").getAbsolutePath(),
				Path.makeFile(dataPath,"dip/institution/target/filename.jpg").getAbsolutePath()
		};
		when(cli.execute(cmdINST)).thenReturn(true);
		
		PublishImageConversionStrategy s = new PublishImageConversionStrategy();
		s.setCLIConnector( cli );
		
		DAFile sourceFile = new DAFile(o.getLatestPackage(),"a","filename.tif");
		
		ConversionInstruction ci = new ConversionInstruction();
		ci.setSource_file(sourceFile);
		ci.setTarget_folder("target/");
		
		ConversionRoutine cr = new ConversionRoutine();
		cr.setTarget_suffix("jpg");
		ci.setConversion_routine(cr);
		
		
		s.setObject(o);
		List<Event> events = s.convertFile(ci);
		
		assertEquals(sourceFile,events.get(0).getSource_file());
		assertEquals(sourceFile,events.get(1).getSource_file());
		
		assertEquals(new DAFile(null,"dip/public","target/filename.jpg"),events.get(0).getTarget_file());
		assertEquals(new DAFile(null,"dip/institution","target/filename.jpg"),events.get(1).getTarget_file());
	}
	
	
	
	/**
	 * Test footer text without resize.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testFooterTextWithoutResize() throws Exception {
		Object o = TESTHelper.setUpObject("123",new RelativePath(workAreaRootPath));
		PublicationRight right = new PublicationRight();
		right.setAudience(Audience.PUBLIC);
		right.setImageRestriction(new ImageRestriction());
		right.getImageRestriction().setFooterText("Hallo");
		o.getRights().getPublicationRights().add(right);
		
		SimplifiedCommandLineConnector cli = mock ( SimplifiedCommandLineConnector.class );
		
		String cmdPUBLIC[] = new String[]{
				"convert",
				Path.makeFile(dataPath,"a/filename.tif").getAbsolutePath(),
					"-background", "'#0008'",
					"-fill", "white",
					"-gravity", "center",
					"-size","520x30",
					"caption:\"Hallo\"",
					"-gravity", "south",
					"-composite",
					Path.makeFile(dataPath,"dip/public/target/filename.jpg").getAbsolutePath()
		};		
		when(cli.execute(cmdPUBLIC)).thenReturn(true);
		
		String cmdINST[] = new String[]{
				"convert",
				Path.makeFile(dataPath,"a/filename.tif").getAbsolutePath(),
				Path.makeFile(dataPath,"dip/institution/target/filename.jpg").getAbsolutePath()
		};
		when(cli.execute(cmdINST)).thenReturn(true);
		
		PublishImageConversionStrategy s = new PublishImageConversionStrategy();
		s.setCLIConnector( cli );
		
		DAFile sourceFile = new DAFile(o.getLatestPackage(),"a","filename.tif");
		
		ConversionInstruction ci = new ConversionInstruction();
		ci.setSource_file(sourceFile);
		ci.setTarget_folder("target/");
		
		ConversionRoutine cr = new ConversionRoutine();
		cr.setTarget_suffix("jpg");
		ci.setConversion_routine(cr);
		
		
		s.setObject(o);
		List<Event> events = s.convertFile(ci);
		
		assertEquals(sourceFile,events.get(0).getSource_file());
		assertEquals(sourceFile,events.get(1).getSource_file());
		
		assertEquals(new DAFile(null,"dip/public","target/filename.jpg"),events.get(0).getTarget_file());
		assertEquals(new DAFile(null,"dip/institution","target/filename.jpg"),events.get(1).getTarget_file());
	}
	
	//@Test
	/**
	 * Test watermark with real image.
	 *
	 * @throws FileNotFoundException the file not found exception
	 */
	public void testWatermarkWithRealImage() throws FileNotFoundException {
		
		Document dom = XPathUtils.parseDom(TC.TEST_ROOT_FORMAT+"/PublishImageConversionStrategyTests/premis.xml");
		if (dom==null){
			throw new RuntimeException("Error while parsing premis.xml");
		}
		
		Object o = TESTHelper.setUpObject("123",new RelativePath(workAreaRootPath));
		
		PublishImageConversionStrategy s = new PublishImageConversionStrategy();
		s.setCLIConnector( new SimplifiedCommandLineConnector() );
		
		DAFile sourceFile = new DAFile(o.getLatestPackage(),"a","filename.tif");
		
		ConversionInstruction ci = new ConversionInstruction();
		ci.setSource_file(sourceFile);
		ci.setTarget_folder("target/");
		
		ConversionRoutine cr = new ConversionRoutine();
		cr.setTarget_suffix("jpg");
		ci.setConversion_routine(cr);
		
		s.setObject(o);
		List<Event> events = s.convertFile(ci);
		
		System.out.println(events.toString());
		
	}

}
