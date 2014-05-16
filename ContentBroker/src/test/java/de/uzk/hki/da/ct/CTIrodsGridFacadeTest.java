package de.uzk.hki.da.ct;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.grid.IrodsGridFacade;
import de.uzk.hki.da.grid.IrodsSystemConnector;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.StoragePolicy;

/**
 * @author Daniel M. de Oliveira
 */
public class CTIrodsGridFacadeTest {

	private IrodsSystemConnector isc;

	@Before
	public void setUp(){
		isc.connect();
	}
	
	@After
	public void tearDown(){
		isc.logoff();
	}
	
	@Test
	public void test() throws IOException{
		
		isc = new IrodsSystemConnector("rods", "WpXlLLg3a4/S/iYrs6UhtQ==", "cihost", "c-i", "ciWorkingResource");
		IrodsGridFacade grid = new IrodsGridFacade();
		grid.setIrodsSystemConnector(isc);

		ArrayList<String> dest = new ArrayList<String>();
		dest.add("c-i");
		Node node = mock (Node.class);
		
		StoragePolicy sp = new StoragePolicy(node);
		sp.setMinNodes(1);
		
		
		grid.put(new File("src/test/resources/at/AT_CON1.tar"), "TEST/AT_CON1/AT_CON1.part_1.tar", sp);
	}
}
