package de.uzk.hki.da.at;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.StringUtilities;

public class ATIngestUnpackedSIPNoBagit extends AcceptanceTest {

	private static final String ORIG_NAME = "ATIngestUnpackedSIPNoBagit";
	@BeforeClass
	public static void setUp() throws IOException, InterruptedException {
		
		
	}
	@Test
	public void test() throws IOException {
		putSIPtoIngestAreaNoBagit(ORIG_NAME, null, ORIG_NAME);
		ath.awaitObjectState(ORIG_NAME,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		
	}
	
	/**
	 * putSIPtoIngestAreaNoBagit
	 * @param sourcePackageName
	 * @param ext
	 * @param originalName
	 * @throws IOException
	 */
	private void putSIPtoIngestAreaNoBagit(String sourcePackageName,String ext,String originalName)  throws IOException {
		if (localNode==null) throw new IllegalStateException();
		if (localNode.getIngestAreaNoBagitRootPath()==null) throw new IllegalStateException();
		File source;
		File target;
		if (StringUtilities.isSet(ext)){
		source = Path.makeFile(AcceptanceTestHelper.TEST_DATA_ROOT_PATH, sourcePackageName + "." + ext);
		target = Path.makeFile(localNode.getIngestAreaNoBagitRootPath(),testContractor.getShort_name(),originalName+"."+ext);
		FileUtils.copyFile( source, target );
		} else {
			source = Path.makeFile(AcceptanceTestHelper.TEST_DATA_ROOT_PATH,sourcePackageName);
			target = Path.makeFile(localNode.getIngestAreaNoBagitRootPath(),testContractor.getShort_name(),originalName);
			if (target.exists()) FolderUtils.deleteDirectorySafe(target);
			FileUtils.copyDirectory(source, target, false);
		}	
	}	
	
}
