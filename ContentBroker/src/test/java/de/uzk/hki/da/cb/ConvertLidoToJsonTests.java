package de.uzk.hki.da.cb;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.repository.FakeRepositoryFacade;
import de.uzk.hki.da.repository.Fedora3RepositoryFacade;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.repository.RepositoryFacade;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.TESTHelper;

public class ConvertLidoToJsonTests {
	
	private static final Path workAreaRootPathPath = new RelativePath("src/test/resources/cb/ConvertLidoToJSON/");
	private static String packageType = "LIDO";
	private static Object object = null;
	private static PreservationSystem pSystem;
	private static FakeRepositoryFacade repo;
	
	@BeforeClass
	public static void setUp() throws RepositoryException, IOException {
		
		pSystem = new PreservationSystem();
		pSystem.setUrisCho("cho");
		pSystem.setUrisAggr("aggr");
		pSystem.setUrisLocal("local");
		pSystem.setOpenCollectionName("danrw");
		
		repo = new FakeRepositoryFacade();
		
		object = TESTHelper.setUpObject("42", workAreaRootPathPath);
		lidoToEDM();
		edmToJSON();

	}
	
	@AfterClass
	public static void cleanUp() {
		System.out.println("CleanUp");
		Path.makeFile(workAreaRootPathPath,"work/_data/danrw/42/DC").delete();
		Path.makeFile(workAreaRootPathPath,"work/_data/danrw/42/LIDO").delete();
		Path.makeFile(workAreaRootPathPath,"work/_data/danrw/42/EDM").delete();
		Path.makeFile(workAreaRootPathPath,"work/_data/danrw/42/").delete();
		Path.makeFile(workAreaRootPathPath,"work/_data/danrw/").delete();
		Path.makeFile(workAreaRootPathPath,"work/_data/").delete();
	} 
	
	@Test
	public void convertLidoToEDM() {
		
	}
	
	@Test
	public void convertEdmToJSON() {
		
	}
	
	private static void lidoToEDM() throws IOException, RepositoryException {
		CreateEDMAction createEDMAction = new CreateEDMAction();
		repo.setWorkAreaRootPath(workAreaRootPathPath.toString());
		repo.createObject(object.getIdentifier(), "danrw", "42");
		
		FileUtils.copyFileToDirectory(Path.make(workAreaRootPathPath,"data/LIDO").toFile(), Path.make(workAreaRootPathPath,"work/_data/danrw/42/").toFile());
		
		
		String fakeDCFile = "<root xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n"+
				"<dc:format>"+packageType+"</dc:format>\n"+
				"</root>";
		Path.makeFile(workAreaRootPathPath,"work/_data/danrw/42/DC").delete();
		
		PrintWriter  pWriter = new PrintWriter(new BufferedWriter(new FileWriter(Path.make(workAreaRootPathPath,"work/_data/danrw/42/DC").toString()))); 
		
        pWriter.println(fakeDCFile); 
        pWriter.flush(); 
        pWriter.close();
		
		Map<String,String> edmMappings = new HashMap<String,String>();
		edmMappings.put("LIDO", "src/main/xslt/edm/lido_to_edm.xsl");
		createEDMAction.setEdmMappings(edmMappings);

		createEDMAction.setPSystem(pSystem);
		createEDMAction.setRepositoryFacade(repo);
		createEDMAction.setObject(object);
		
		try {
			createEDMAction.implementation();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (RepositoryException e) {
			e.printStackTrace();
			fail();
		}
		
		FileUtils.copyFileToDirectory(Path.make(workAreaRootPathPath,"work/_data/danrw/42/EDM").toFile(), Path.make(workAreaRootPathPath,"results/").toFile());
	}
	
	public static void edmToJSON() throws RepositoryException, IOException {
		Map<String,String> frames = new HashMap<String,String>();
		frames.put("src/main/resources/frame.jsonld","EDM");
		Set<String> testContractors = new HashSet<String>();
		
		IndexMetadataAction indexMetadataAction = new IndexMetadataAction();
		indexMetadataAction.setPSystem(pSystem);
		indexMetadataAction.setObject(object);
		indexMetadataAction.setRepositoryFacade(repo);
		indexMetadataAction.setTestContractors(testContractors);
		indexMetadataAction.setFrames(frames);
		indexMetadataAction.setIndexName("indexName");
		indexMetadataAction.setRepositoryFacade(repo);
		indexMetadataAction.implementation();
	}
}
