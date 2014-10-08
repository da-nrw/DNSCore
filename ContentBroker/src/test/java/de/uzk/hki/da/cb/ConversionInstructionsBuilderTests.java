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
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.core.RelativePath;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionInstructionBuilder;
import de.uzk.hki.da.model.ConversionPolicy;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.test.TESTHelper;


/**
 * The Class ConversionInstructionsBuilderTests.
 *
 * @author Daniel M. de Oliveira
 */
public class ConversionInstructionsBuilderTests {

	/** The ci b. */
	private final ConversionInstructionBuilder ciB = new ConversionInstructionBuilder();
	
	/** The base path. */
	Path basePath=new RelativePath("src/test/resources/cb/GenerateConversionInstructionsTests/");
	
	/** The data path. */
	Path dataPath=Path.make(basePath,"/RecursiveStructured_SIP/data/2011+11+01+a");
	
	/** The pkg. */
	private Package pkg;
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp(){
		Object o = new Object();
		pkg = mock (Package.class);
		when(pkg.getTransientBackRefToObject()).thenReturn(o);
		
	}
	
	
	
	
	/**
	 * Checks wheter the right input and output paths in ConversionInstructions get generated for a
	 * recursively strucured input SIP.
	 */
	@Test
	public void generateConversionInstructionsRecursively() {
		
		Object o = TESTHelper.setUpObject("1177",basePath);
		
		ConversionPolicy policy = new ConversionPolicy();
		policy.setId(0);
		policy.setSource_format("fmt/10");
		
		ConversionRoutine routine = new ConversionRoutine();
		routine.setParams("convert input output");
		policy.setConversion_routine(routine);
		
		ConversionInstruction ci1 = ciB.assembleConversionInstruction(
				new DAFile(o.getLatestPackage(),"2011_01_01+00_01+a","a.tif"),
				policy);
		assertEquals("",ci1.getTarget_folder());
		
		ConversionInstruction ci2 = ciB.assembleConversionInstruction(
				new DAFile(o.getLatestPackage(),"2011_01_01+00_01+a","subfolder/b.tif"),
				policy);
		assertEquals("subfolder",ci2.getTarget_folder());
		
		ConversionInstruction ci3 = ciB.assembleConversionInstruction(
				new DAFile(o.getLatestPackage(),"2011_01_01+00_01+a","subfolder/subsubfolder/c.tif"),
				policy);
		assertEquals("subfolder/subsubfolder",ci3.getTarget_folder());
	}
	
	
	
	/**
	 * Prefix folders helper test.
	 */
	@Test
	public void prefixFoldersHelperTest(){
		
		List<ConversionInstruction> cis = new ArrayList<ConversionInstruction>();
		cis.add(new ConversionInstruction(0, "targetPath", null, ""));
		
		ciB.prefixFolders(cis,"tPref/");
		
		if (!cis.get(0).getTarget_folder().equals("tPref/targetPath")) fail();
	}
	
	
	
	
	/**
	 * ŧøðø testIntermediateFolderNullDoesntGetCreated()
	 *
	 * Addresses a bug that introduces an intermediate folder named "null" when there isn't a database
	 * entry for a ConversionRoutine selected by a policy for User PRESENTER.
	 */
	
	
}
