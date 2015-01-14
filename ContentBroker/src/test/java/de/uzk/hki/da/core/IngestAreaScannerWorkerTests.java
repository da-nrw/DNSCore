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

package de.uzk.hki.da.core;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.service.HibernateUtil;

//import de.uzk.hki.da.db.BaseThreadDatabaseOperations;


/**
 * The Class IngestAreaScannerWorkerTests.
 */
public class IngestAreaScannerWorkerTests {

	String basePath = "src/test/resources/core/IngestAreaScannerWorker/";
	String ingestAreaRootPath = basePath+"ingest/";
	
	IngestAreaScannerWorker worker = new IngestAreaScannerWorker();
	
	
	
	
	@Before
	public void setUp() throws IOException{
		
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
	}

	
	
	
	@After
	public void tearDown() throws IOException{
		
		new File(basePath+"ingest/TEST/a.tgz").delete();
		new File(basePath+"work/TEST/a.tgz").delete();
		
	}

	
	
	
	@Test
	public void initialization(){
		
		IngestAreaScannerWorker scanner = new IngestAreaScannerWorker();
		scanner.setIngestAreaRootPath(ingestAreaRootPath);
		scanner.init();
	}
	
	
	
	
	
	
	
	
}
