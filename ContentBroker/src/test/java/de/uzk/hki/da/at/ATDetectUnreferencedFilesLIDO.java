package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.util.Path;

public class ATDetectUnreferencedFilesLIDO extends AcceptanceTest{
	private static String origName = "ATDetectUnreferencedFilesLido";
	private static File contentbrokerLogfile;
	private static String targetFileStr = "";
	
	@BeforeClass
	public static void setUp() throws IOException {
		ath.putSIPtoIngestArea(origName,"tgz",origName);
		ath.awaitObjectState(origName,Object.ObjectStatus.ArchivedAndValid);
		
		contentbrokerLogfile = Path.makeFile(localNode.getLogFolder(), "contentbroker.log");
		FileInputStream fisTargetFile = new FileInputStream(contentbrokerLogfile);
		targetFileStr = IOUtils.toString(fisTargetFile, "UTF-8");
	}
	
	@Test
	public void test() throws IOException {
		assertTrue(contentbrokerLogfile.exists());
		assertTrue(targetFileStr.contains("ist nicht konsistent. Folgende Files sind nicht in den mitgelieferten Metadaten referenziert: "
				+ "[3.bmp, 4.bmp]. Die Verarbeitung findet dennoch statt.") || 
				targetFileStr.contains("ist nicht konsistent. Folgende Files sind nicht in den mitgelieferten Metadaten referenziert: "
						+ "[4.bmp, 3.bmp]. Die Verarbeitung findet dennoch statt."));
	}
}
