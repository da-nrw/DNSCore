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

package de.uzk.hki.da.convert;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.model.AudioRestriction;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PublicationRight;
import de.uzk.hki.da.model.PublicationRight.Audience;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.test.TESTHelper;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.util.RelativePath;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;



/**
 * The Class PublishAudioConversionStrategyTests.
 *
 * @author Daniel M. de Oliveira
 */
public class PublishAudioConversionStrategyTests {

	/** The base path. */
	private Path workAreaRootPathPath = new RelativePath("src/test/resources/convert/PublishAudioConversionStrategyTests/");
	
	
	
	/**
	 * Test.
	 * @throws IOException 
	 */
	@Test
	public void test() throws IOException{
		
		Node n = new Node();
		n.setWorkAreaRootPath(workAreaRootPathPath);
		
		
		CommandLineConnector cli = mock ( CommandLineConnector.class );
		String cmdPUBLIC[] = new String[]{
				"sox",
				new File(workAreaRootPathPath+"/work/TEST/123/data/a/audiofile.wav").getAbsolutePath(),
				workAreaRootPathPath+"/work/TEST/123/data/"+C.WA_DIP+"/public/target/audiofile.mp3",
				"trim","0","10"
		};
		
		ProcessInformation pi = new ProcessInformation();
		pi.setExitValue(0);
		
		when(cli.runCmdSynchronously(cmdPUBLIC)).thenReturn(pi);
		
		String cmdINSTITUTION[] = new String[]{
				"sox",
				new File(workAreaRootPathPath+"/work/TEST/123/data/a/audiofile.wav").getAbsolutePath(),
				workAreaRootPathPath+"/work/TEST/123/data/"+C.WA_DIP+"/institution/target/audiofile.mp3"
		};
		when(cli.runCmdSynchronously(cmdINSTITUTION)).thenReturn(pi);
		
		PublishAudioConversionStrategy strategy = new PublishAudioConversionStrategy();
		strategy.setCLIConnector( cli );
		
		
		Object o = TESTHelper.setUpObject("123",workAreaRootPathPath);
		PublicationRight right = new PublicationRight();
		right.setAudience(Audience.PUBLIC);
		right.setAudioRestriction(new AudioRestriction());
		right.getAudioRestriction().setDuration(10);
		o.getRights().getPublicationRights().add(right);
		strategy.setObject(o);
		
		ConversionInstruction ci = new ConversionInstruction();
		ci.setSource_file(new DAFile("a","audiofile.wav"));
		ci.setTarget_folder("target/");
		strategy.convertFile(new WorkArea(n,o),ci);
	}
}
