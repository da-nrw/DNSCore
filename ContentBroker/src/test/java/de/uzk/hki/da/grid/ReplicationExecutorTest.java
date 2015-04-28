/**
 * 
 */
package de.uzk.hki.da.grid;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.StoragePolicy;

/**
 * @author Jens Peters
 * Tests the Replication Executor
 *
 */
public class ReplicationExecutorTest {

	/**
	 * @author Jens Peters
	 * @throws java.lang.Exception
	 */
	ReplicationExecutor re;
	IrodsSystemConnector isc;
	StoragePolicy sp;
	
	@Before
	public void setUp() throws Exception {
		
		isc = mock(IrodsSystemConnector.class);	
		
		StoragePolicy sp = new StoragePolicy();
		
		sp.setWorkingResource("cacheresc");
		sp.setReplDestinations("rescA,rescB");
		String data_name = "/zone/aip/Cont/aip.tar";
		re = new ReplicationExecutor(isc, data_name, sp, sp.getWorkingResource());
	}

	/**
	 * @author Jens Peters
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link de.uzk.hki.da.grid.ReplicationExecutor#run()}.
	 * Normal run 
	 */
	@Test
	public void testRunNormally() {
		when (isc.replicateDaoToResGroupSynchronously(anyString(), anyString(), anyString())).thenReturn("ok");
		re.run();
		
	}
	
	/**
	 * Test method for {@link de.uzk.hki.da.grid.ReplicationExecutor#run()}.
	 * Test implicates failing on all nodes
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testRunFailure() {
		re.setTimeout(100l);
		when (isc.replicateDaoToResGroupSynchronously(anyString(), anyString(), anyString())).thenThrow(RuntimeException.class);

		re.run();
		verify(isc, times(6)).replicateDaoToResGroupSynchronously(anyString(), anyString(), anyString());
		
	}

}
