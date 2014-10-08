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

package de.uzk.hki.da.cb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.core.RelativePath;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.test.TESTHelper;



/**
 * UNDER TEST IS: ConvertAction.
 *
 * @author Daniel M. de Oliveira
 */
public class ConvertActionTests {

	/** The Constant action. */
	private static final ConvertAction action= new ConvertAction();
	
	/** The Constant vaultPath. */
	private static final Path workAreaRootPath = new RelativePath("src/test/resources/cb/ConvertActionTests");
	
	/** The Constant dataPath. */
	private static final String dataPath= workAreaRootPath + "/work/TEST/123/";
	
	/** The job. */
	private static Job job = null;
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp(){
		
		
		Object object = TESTHelper.setUpObject("123", workAreaRootPath);
		action.setObject(object);
		
		job = new Job();
		job.setObject(object);
		job.setId(123);
		
		job.setStatus("240");
		job.setRep_name("2011+11+01+");		
		
		final Node vm2 = new Node("vm2","01-vm2");
		final Node vm3 = new Node("vm3","01-vm3");
		final Set<Node> allNodes = new HashSet<Node>();
		allNodes.add(vm2);
		allNodes.add(vm3);
		
		ConversionRoutine im = new ConversionRoutine(
				"IM",
				"de.uzk.hki.da.format.CLIConversionStrategy",
				"convert input output",
				"png");
		
		ConversionRoutine copy = new ConversionRoutine(
				"COPY",
				"de.uzk.hki.da.format.CLIConversionStrategy",
				"cp input output",
				"*");
		
		ConversionInstruction ci1 = new ConversionInstruction();
		ci1.setTarget_folder("");
		
		DAFile f = new DAFile(object.getLatestPackage(),"2011+11+01+a","premis.xml");
		object.getLatestPackage().getFiles().add(f);
		
		DAFile f1 = new DAFile(object.getLatestPackage(),"2011+11+01+a","abc.xml");
		ci1.setSource_file(f1);
		ci1.setNode("vm3");
		ci1.setConversion_routine(copy);
		
		// copy
		
		ConversionInstruction ci2 = new ConversionInstruction();
		ci2.setTarget_folder("");
		
		DAFile f2 = new DAFile(object.getLatestPackage(),"2011+11+01+a","140864.tif");
		ci2.setSource_file(f2);
		
		
		ci2.setNode("vm2");
		ci2.setConversion_routine(im);
		// im 
		
		job.getConversion_instructions().add(ci1);
		job.getConversion_instructions().add(ci2);
		
		
		action.setDistributedConversionAdapter(mock(DistributedConversionAdapter.class));
		
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		
	}
	
	/**
	 * Tear down.
	 * @throws IOException 
	 */
	@After
	public void tearDown() throws IOException{
//		if (new File(dataPath+"data/2011+11+01+b/").exists())
//			FileUtils.deleteDirectory(new File(dataPath+"data/2011+11+01+b/"));
		
		if (new File(dataPath+"data/dip/").exists())
			FileUtils.deleteDirectory(new File(dataPath+"data/dip/"));
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

		action.setJob(job);
		Node localNode = new Node("vm2","01-vm2");
		localNode.setWorkAreaRootPath(new RelativePath(workAreaRootPath));
		action.setLocalNode(localNode);
		
		action.implementation();
		
		assertTrue(new File(dataPath+"data/2011+11+01+b/140864.png").exists());
		assertTrue(new File(dataPath+"data/2011+11+01+b/abc.xml").exists());
	}
	
	
	/**
	 * @author Thomas Kleinke
	 * @throws IOException
	 */
	@Test
	public void testRollback() throws IOException {

		action.setJob(job);
		Node localNode = new Node("vm2","01-vm2");
		localNode.setWorkAreaRootPath(Path.make(workAreaRootPath));
		action.setLocalNode(localNode);
		
		action.implementation();
		action.rollback();
		
		assertFalse(new File(dataPath+"data/2011+11+01+b/140864.png").exists());
		assertFalse(new File(dataPath+"data/2011+11+01+b/abc.xml").exists());
		assertEquals(0, action.getObject().getLatestPackage().getEvents().size());
	}
}
