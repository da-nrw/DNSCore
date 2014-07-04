package de.uzk.hki.da.grid;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.grid.IrodsGridFacade;
import de.uzk.hki.da.grid.IrodsSystemConnector;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.utils.Path;

/**
 * @author Daniel M. de Oliveira
 */
public class CTIrodsGridFacadeTest {

	private IrodsSystemConnector isc;

	@Before
	public void setUp() throws IOException{
		isc = new IrodsSystemConnector("rods", "WpXlLLg3a4/S/iYrs6UhtQ==", "cihost", "c-i", "ciWorkingResource");
	}
	
	@After
	public void tearDown() throws IOException{
		
		FileUtils.deleteDirectory(new File("/ci/storage/GridCacheArea/aip/TEST/AT_CON1"));
		isc.removeCollection("/c-i/aip/TEST/AT_CON1");
		new File("/tmp/AT_CON1.part_1.tar").delete();
	}
	
	@Test
	public void test() throws IOException{
		
		IrodsGridFacade grid = new IrodsGridFacade();
		grid.setIrodsSystemConnector(isc);
		Node node = new Node();
		node.setGridCacheAreaRootPath(Path.make("/ci/storage/GridCacheArea"));
		node.setWorkAreaRootPath(Path.make("/ci/storage/WorkArea"));
		node.setWorkingResource("ciWorkingResource");		
		node.setReplDestinations("ciArchiveResource");
		
		grid.setLocalNode(node);

		ArrayList<String> dest = new ArrayList<String>();
		dest.add("c-i");
		
		StoragePolicy sp = new StoragePolicy(node);
		sp.setMinNodes(1);
		
		grid.put(new File("src/test/resources/at/AT_CON1.tar"), "TEST/AT_CON1/AT_CON1.part_1.tar", sp);
		grid.get(new File("/tmp/AT_CON1.part_1.tar"), "TEST/AT_CON1/AT_CON1.part_1.tar");
		
		assertTrue(new File("/tmp/AT_CON1.part_1.tar").exists());
	}
}
