package de.uzk.hki.da.format;

import java.io.IOException;

import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.service.HibernateUtil;

public class FormatCmdLineErrorsTest {

	
	@BeforeClass
	public static void setUp() throws IOException {
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.ci");

	}
	
	@Test
	public void readCmdErrorsFromDb() {
		KnownFormatCmdLineErrors fcle = new KnownFormatCmdLineErrors();
		fcle.init();
		fcle.getFormatCmdLineErrors();
	}
}
