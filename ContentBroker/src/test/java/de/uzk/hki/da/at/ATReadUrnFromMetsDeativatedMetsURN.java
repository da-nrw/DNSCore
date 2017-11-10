package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.Object;

/**
* @author Eugen Trebunski
 */

public class ATReadUrnFromMetsDeativatedMetsURN extends AcceptanceTest{
	
	private static String origNameMets = "ATReadUrnFromMets";
	private static Object objectMetsUrn;
	String premisUrn = "urn:nbn:de:xyz-1-20131008367735";
	String metsUrn = "urn:nbn:de:danrw:de2190-2ddee995-9878-4a76-8a7a-3d135dbded198";
	
	@BeforeClass
	public static void setUp() throws IOException {
		ath.putSIPtoIngestArea(origNameMets, "tgz", origNameMets);
		ath.awaitObjectState(origNameMets,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(origNameMets);
	}
	
	@AfterClass
	public static  void tearDown() throws IOException{
	}
	
	@Test
	public void readUrnFromMets() {
		objectMetsUrn=ath.getObject(origNameMets);
		assertTrue(!objectMetsUrn.getUrn().equals(metsUrn));
		assertTrue(objectMetsUrn.getUrn().startsWith(preservationSystem.getUrnNameSpace()));
	}
}
