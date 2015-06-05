package de.uzk.hki.da.grid;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.model.User;

public class FederationExecutorTest {

	IrodsSystemConnector isc;
	FederationExecutor fe;
	String data_name = "/zone/aip/Cont/aip.tar";
	
	@Before
	public void setUp() throws Exception {
		
		isc = mock(IrodsSystemConnector.class);	
		Node node = new Node();
		User nodeadmin = new User(); nodeadmin.setEmailAddress("noreply@zone.de");
		node.setAdmin(nodeadmin);
		StoragePolicy sp = new StoragePolicy();
		sp.setReplDestinations("lza");
		sp.setCommonStorageRescName("lza");
		sp.setNodeName("testnode");
		sp.setAdminEmail(nodeadmin.getEmailAddress());
		fe = new FederationExecutor(isc,sp,data_name);
	}

	@Test
	public void testOk() {
		when (isc.federateDataObjectToConnectedZones(data_name, "lza", 3)).thenReturn(true);
		when (isc.isConnected()).thenReturn(true);
		fe.run(); 
		Mockito.verify(isc).federateDataObjectToConnectedZones(data_name, "lza", 3);	
	}
	@Test
	public void NOk() {
		fe.setTimeout(100l);
		when (isc.isConnected()).thenReturn(true);
		Mockito.doThrow(new IrodsRuntimeException("")).when(isc).federateDataObjectToConnectedZones(eq(data_name), eq("lza"),eq(3));
		 
		fe.run();
		verify(isc, times(3)).federateDataObjectToConnectedZones(eq(data_name), eq("lza"),eq(3));
	}
	
}
