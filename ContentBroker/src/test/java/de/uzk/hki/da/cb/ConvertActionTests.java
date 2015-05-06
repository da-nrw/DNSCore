/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln
  Copyright (C) 2015 LVRInfoKom
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Document;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.util.Path;



/**
 * @author Daniel M. de Oliveira
 * @author Thomas Kleinke
 */
public class ConvertActionTests extends ConcreteActionUnitTest{

	private static final String PREMIS = "premis.xml";

	private static final String REPNAME = "2011+11+01+";

	@ActionUnderTest
	ConvertAction action= new ConvertAction();
	
	private static final Path workAreaRootPath = Path.make(TC.TEST_ROOT_CB,"ConvertAction");
	

	private DAFile tiffile = new DAFile(REPNAME+"a","140864.tif");;
	
	@Before
	public void setUp(){
		
		n.setWorkAreaRootPath(workAreaRootPath);
		j.setStatus("240");
		j.setRep_name(REPNAME);		

		ConversionRoutine im = new ConversionRoutine(
				"IM",
				"de.uzk.hki.da.convert.CLIConversionStrategy",
				"convert input output",
				"png");
		
		ConversionRoutine copy = new ConversionRoutine(
				"COPY",
				"de.uzk.hki.da.convert.CLIConversionStrategy",
				"cp input output",
				"*");
		
		ConversionInstruction ci1 = new ConversionInstruction();
		
		List<Document> documents = new ArrayList<Document>();
		
		DAFile f = new DAFile(REPNAME+"a",PREMIS);
		o.getLatestPackage().getFiles().add(f);
		
		DAFile f1 = new DAFile(REPNAME+"a","abc.xml");
		
		
		ci1.setSource_file(f1);
		ci1.setConversion_routine(copy);
		ci1.setTarget_folder("");
		
		ConversionInstruction ci2 = new ConversionInstruction();
		ci2.setTarget_folder("");
		ci2.setSource_file(tiffile);
		Document document2 = new Document(tiffile);
		documents.add(document2);
		ci2.setConversion_routine(im);
		

		Document document1 = new Document(f1);
		Document document = new Document(f);
		documents.add(document);
		documents.add(document1);
		o.setDocuments(documents);
		
		j.getConversion_instructions().add(ci1);
		j.getConversion_instructions().add(ci2);
	}
	

	@After
	public void tearDown() throws IOException{
		
		if (Path.makeFile(wa.dataPath(),"dip").exists())
			FileUtils.deleteDirectory(Path.makeFile(wa.dataPath(),"dip"));
	}
	
	
	
	
	/**
	 * We manually create a Job with two ConversionInstructions which should be
	 * executed in a distributed fashion. this means only
	 * one of them (the IM thing with the tif file) 
	 * is to be executed on the initial node and one on another node.
	 * @author Daniel M. de Oliveira
	 * @throws IOException 
	 */
	@Test
	public void testConversion() throws IOException{

		action.implementation();
		
		assertTrue(Path.makeFile(wa.dataPath(),REPNAME+"b","140864.png").exists());
		assertTrue(Path.makeFile(wa.dataPath(),REPNAME+"b","abc.xml").exists());
	}
	
	
	@Test
	public void testRollback() throws IOException {

		
		action.implementation();
		action.rollback();
		
		assertFalse(Path.makeFile(wa.dataPath()+REPNAME+"b","140864.png").exists());
		assertFalse(Path.makeFile(wa.dataPath()+REPNAME+"b","abc.xml").exists());
		assertEquals(0, action.getObject().getLatestPackage().getEvents().size());
	}
	
	
	@Test
	public void removeNewDAFilesFromDocuments() throws IOException {
		
		Document d = o.getDocument("140864");
		DAFile daf = new DAFile(REPNAME+"b","140864.jpg");
		d.addDAFile(daf);
		assertEquals(daf,d.getLasttDAFile());
		
		List<Event> events = new ArrayList<Event>();
		Event e = new Event();
		e.setSource_file(tiffile);
		e.setTarget_file(daf);
		e.setType("CONVERT");
		events.add(e);
		action.setEvents(events);
		
		action.rollback();
		
		assertEquals(tiffile,d.getLasttDAFile());
	}
}
