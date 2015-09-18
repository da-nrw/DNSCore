package de.uzk.hki.da.core;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.service.CSVStatusReport;
import de.uzk.hki.da.service.HibernateUtil;

public class CSVFileHandlerTest {

	String csvFileName = "src/test/resources/core/StatusReport/StatusReportIncoming.csv"; 
	
	@Before
	public void before() throws IOException {
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
	}	
	@Test
	public void testParseFile() throws IOException, SubsystemNotAvailableException {
		CSVStatusReport sr = new CSVStatusReport();
		sr.generateReportBasedOnFile(new File(csvFileName));
		
	}

}
