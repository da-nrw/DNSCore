package de.uzk.hki.da.format;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.format.PublishImageConversionStrategy;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.SimplifiedCommandLineConnector;
import de.uzk.hki.da.utils.TESTHelper;
import de.uzk.hki.da.utils.TC;

public class PublishImageMultipageTIFFTests {

	Path workAreaRootPathPath= Path.make(TC.TEST_ROOT_FORMAT,"PublishImageMultipageTiffTests");
	
	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMultipage() throws FileNotFoundException {
		PublishImageConversionStrategy cs = new PublishImageConversionStrategy();
		Object o = TESTHelper.setUpObject("123",new RelativePath(workAreaRootPathPath));
		
		SimplifiedCommandLineConnector cli = mock ( SimplifiedCommandLineConnector.class );		
		when(cli.execute((String[]) anyObject())).thenReturn(true);
		
		cs.setCLIConnector(cli);
		DAFile sourceFile = new DAFile(o.getLatestPackage(),"source","ALVR{}_Nr_4557_Aufn_249.tif");
		
		ConversionInstruction ci = new ConversionInstruction();
		ci.setSource_file(sourceFile);
		ci.setTarget_folder("");
		
		ConversionRoutine cr = new ConversionRoutine();
		cr.setTarget_suffix("jpg");
		ci.setConversion_routine(cr);
		
		cs.setObject(o);
		List<Event> events = cs.convertFile(ci);
		assertEquals(4, events.size());
	}
	
}
