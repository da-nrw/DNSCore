/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
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

package de.uzk.hki.da.format;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.utils.CTTestHelper;

/**
 * @author Daniel M. de Oliveira
 */
public class CTFileFormatFacadeTests {

	private static final StandardFileFormatFacade sfff = new StandardFileFormatFacade();
	
	
	@BeforeClass
	public static void setUp() throws IOException{
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		sfff.setDao(new CentralDatabaseDAO());

		CTTestHelper.prepareWhiteBoxTest();
	}

	
	@AfterClass
	public static void tearDownAfterClass(){
		CTTestHelper.cleanUpWhiteBoxTest();
	}
	
	
	@Test
	public void test() throws FileNotFoundException{
		FakeFileWithFileFormat ffff = new FakeFileWithFileFormat(new File("conf/healthCheck.tif"));
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();
		files.add(ffff);
		sfff.identify(files);
		
		assertEquals("fmt/353",files.get(0).getFormatPUID());
	}
}
