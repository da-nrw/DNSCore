package de.uzk.hki.da.cb;

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.repository.ElasticsearchMetadataIndex;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.util.Path;

public class IndexMetadataActionTests extends ConcreteActionUnitTest{

	@ActionUnderTest
	IndexMetadataAction action = new IndexMetadataAction();
	
	private static final Path WORK_AREA_ROOT_PATH = Path.make(TC.TEST_ROOT_CB,"IndexMetadataAction");
	
	@Before
	public void setUp() {
		n.setWorkAreaRootPath(WORK_AREA_ROOT_PATH);
		action.setIndexName("collection-open");
		action.setTestContractors(new HashSet<String>());
		ElasticsearchMetadataIndex mi = mock(ElasticsearchMetadataIndex.class);
		action.setMetadataIndex(mi);
		action.setWorkArea(new WorkArea(n,o));
	}
	
	@Test
	public void test() throws RepositoryException, IOException {
		action.implementation();
	}
	
}
