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

/**
 * @author jens Peters
 */
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.uzk.hki.da.model.KnownError;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.Path;

public class FormatCmdLineExecutorTest {

	String fileIptcError = Path.make(TC.TEST_ROOT_FORMAT,"bigTiff","268754.tif").toString(); 

	@Test
	public void testGetRuntimeExceptionForNotPruned() {
		File iptcerror = new File(fileIptcError);
		KnownError ke = new KnownError();
		ke.setStd_err_contains_regex("(?s).*RichTIFFIPTC.*TIFFErrors.*");
		ke.setError_name("WRONG_DATA_TYPE_IPTC");
		ke.setQuestion("IPTC_ERROR_STORE_ALLOWED?");
		ke.setDescription("Probleme mit IPTC Tag im IFD bei BigTiff");
		KnownFormatCmdLineErrors kle = new KnownFormatCmdLineErrors();
		
		List<KnownError> kl = new ArrayList();
		kl.add(ke);
		kle.setFormatCmdLineErrors(kl);
		String[] cmd = new String []{
				"identify","-format","'%C'",iptcerror.getAbsolutePath()};
	FormatCmdLineExecutor cle = new FormatCmdLineExecutor(new CommandLineConnector(),kle);
	try {
	cle.execute(cmd);
	} catch (UserFileFormatException e) {
		assertTrue(e.getMessage().indexOf("Probleme")>=0);
		assertTrue(!e.isWasPruned());
		return;
	}
	fail();
	}


	@Test
	public void testGetRuntimeExceptionForPrunedException() {
		File iptcerror = new File(fileIptcError);
		KnownError ke = new KnownError();
		ke.setStd_err_contains_regex("(?s).*RichTIFFIPTC.*TIFFErrors.*");
		ke.setError_name("WRONG_DATA_TYPE_IPTC");
		ke.setQuestion("IPTC_ERROR_STORE_ALLOWED?");
		ke.setDescription("Probleme mit IPTC Tag im IFD bei BigTiff");
		KnownFormatCmdLineErrors kle = new KnownFormatCmdLineErrors();
		
		List<KnownError> kl = new ArrayList();
		kl.add(ke);
		kle.setFormatCmdLineErrors(kl);
		String[] cmd = new String []{
				"identify","-format","'%C'",iptcerror.getAbsolutePath()};
	FormatCmdLineExecutor cle = new FormatCmdLineExecutor(new CommandLineConnector(),kle);
	cle.setPruneExceptions(true);
	try {
	assertTrue(cle.execute(cmd));
	} catch (UserFileFormatException e) {
		assertTrue(e.getMessage().indexOf("Probleme")>=0);
		assertTrue(e.isWasPruned());
		return;
	}
	fail();
	}

}
