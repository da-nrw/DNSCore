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
package de.uzk.hki.da.at;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import de.uzk.hki.da.model.Object;

/**
 * @author Thomas Kleinke
 *
 */
public class ATReadURNFromSIP extends AcceptanceTest {

	@Test
	public void test() throws IOException, InterruptedException {
		String originalName = "ATReadURNFromSIP";
		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+originalName+".tgz"), 
				new File(localNode.getIngestAreaRootPath()+"/TEST"));
		ath.waitForJobsToFinish(originalName);
		Object object = ath.fetchObjectFromDB(originalName);
		
		assertEquals("urn:nbn:de:xyz-1-20131008367735", object.getUrn());
	}

}
