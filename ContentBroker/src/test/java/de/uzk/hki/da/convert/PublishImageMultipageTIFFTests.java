package de.uzk.hki.da.convert;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.test.TESTHelper;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.ProcessInformation;
import de.uzk.hki.da.utils.RelativePath;

public class PublishImageMultipageTIFFTests {

	Path workAreaRootPathPath= Path.make(TC.TEST_ROOT_CONVERT,"PublishImageMultipageTiffTests");
	private Node n;
	
	@Before
	public void setUp() throws Exception {
		
		n = new Node();
		n.setWorkAreaRootPath(workAreaRootPathPath);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMultipage() throws IOException {
		PublishImageConversionStrategy cs = new PublishImageConversionStrategy();
		Object o = TESTHelper.setUpObject("123",new RelativePath(workAreaRootPathPath));
		ProcessInformation pi = new ProcessInformation();
		pi.setExitValue(0);
		CommandLineConnector cli = mock ( CommandLineConnector.class );
		DAFile sourceFile = new DAFile("source","ALVR{}_Nr_4557_Aufn_249.tif");
		
		String cmdPublic[] = new String[]{
				"convert",
				new File(workAreaRootPathPath+"/work/TEST/123/data/source/ALVR{}_Nr_4557_Aufn_249.tif[0]").getAbsolutePath(),
				new File(workAreaRootPathPath+"/work/TEST/123/data/"+WorkArea.TMP_PIPS+"/public/ALVR{}_Nr_4557_Aufn_249.jpg").getAbsolutePath()
		};
		when(cli.runCmdSynchronously(cmdPublic)).thenReturn(pi);
		
		String cmdInstitution[] = new String[]{
				"convert",
				new File(workAreaRootPathPath+"/work/TEST/123/data/source/ALVR{}_Nr_4557_Aufn_249.tif[0]").getAbsolutePath(),
				new File(workAreaRootPathPath+"/work/TEST/123/data/"+WorkArea.TMP_PIPS+"/institution/ALVR{}_Nr_4557_Aufn_249.jpg").getAbsolutePath()
		};
		when(cli.runCmdSynchronously(cmdInstitution)).thenReturn(pi);
		
		
		cs.setCLIConnector(cli);
		
		ConversionInstruction ci = new ConversionInstruction();
		ci.setSource_file(sourceFile);
		ci.setTarget_folder("");
		
		ConversionRoutine cr = new ConversionRoutine();
		cr.setTarget_suffix("jpg");
		ci.setConversion_routine(cr);
		
		cs.setObject(o);
		List<Event> events = cs.convertFile(new WorkArea(n,o),ci);
		assertEquals(2, events.size());
	}
	
}
