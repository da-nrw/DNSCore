package de.uzk.hki.da.cb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.validation.constraints.AssertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.apache.commons.io.FileUtils;

import com.mchange.v2.log.PackageNames;

import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.grid.FakeDistributedConversionAdapter;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.junit.Assert.*;

/**
 * 
 * @author Polina Gubaidullina
 *
 */

public class FetchPIPsActionTest{
	
	/** The action. */
	static FetchPIPsAction action = new FetchPIPsAction();

	/** The irods. */
	private static DistributedConversionAdapter distributedConversionAdapter;
	
	/** The node. */
	private static Node localNode = new Node();
	
	/** The object. */
	private static Object object = new Object();
	
	/** The package. */
	private static Package testPackage = new Package();
	
	/** The packages. */
	private static List<Package> packages = new ArrayList<Package>(); 
	
	String sourceDIPName;
	
	
	private static Path testDir = new RelativePath("src", "test", "resources", "cb", "FetchPIPsActionTest");
	private static Path sourcePIPsPath = Path.make(testDir, "sourceDir"); 
	private static Path institutionPartialPath = Path.make("pips", "institution", "TEST");
	private static Path publicPartialPath = Path.make("pips", "public", "TEST");
	private static Path workAreaRootPartialPath = Path.make(testDir, "localNodeWorkAreaRootPath");
	private static String packageName = "1";
	private static String objectId = "1";
	
	@BeforeClass
	public static void cleanUp() {
		FileUtils.deleteQuietly(Path.make(workAreaRootPartialPath, institutionPartialPath, objectId).toFile());
		FileUtils.deleteQuietly(Path.make(workAreaRootPartialPath, publicPartialPath, objectId).toFile());
	}
	
	@BeforeClass
	public static void mockDca() {
		distributedConversionAdapter = mock(FakeDistributedConversionAdapter.class);
	}
	
	@BeforeClass
	public static void initLocalNode() {
		localNode.setWorkAreaRootPath(workAreaRootPartialPath);
	}
	
	@BeforeClass
	public static void initObject() {
		Contractor contractor = new Contractor();
		contractor.setShort_name("TEST");
		object.setContractor(contractor);
		object.setIdentifier(objectId);
		testPackage.setId(1);
		testPackage.setName(packageName);
		packages.add(testPackage);
		object.setPackages(packages);
	}
	
	@Before
	public void initImpementation() throws FileNotFoundException, IOException {
		actionSetup();
		manReplFromSourceToWorkingResource();
		action.implementation();
	}
	
	@Before
	public void actionSetup() {
		action.setLocalNode(localNode);
		action.setObject(object);
		action.setDistributedConversionAdapter(distributedConversionAdapter);
	}
	
	@BeforeClass
	public static void manReplFromSourceToWorkingResource() {
		try {
			FileUtils.copyDirectory(Path.make(sourcePIPsPath, institutionPartialPath).toFile(), Path.make(workAreaRootPartialPath, institutionPartialPath).toFile());
			FileUtils.copyDirectory(Path.make(sourcePIPsPath, publicPartialPath).toFile(), Path.make(workAreaRootPartialPath, publicPartialPath).toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRenamePIPs(){
		assertTrue(Path.make(workAreaRootPartialPath, institutionPartialPath, objectId).toFile().exists());
	}
}
