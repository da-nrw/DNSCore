package de.uzk.hki.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.model.InitializationError;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import de.uzk.hki.da.at.*;

/**
 * TestSuite to aggregate all acceptance tests in one suite
 * 
 * @author trebunski
 *
 */
@RunWith(CompleteATSuite.class)
/*@SuiteClasses({
	ATContractIngestDelta.class,                      ATIngestValidation.class,          ATMetadataUpdatesRheinlaender.class,        ATRetrieval.class,
	ATCSVQueries.class,                               ATIntegrityCheck.class,            ATMetadataUpdatesXMP.class,                 ATTimeBasedPipGen.class,
	ATDeltaOnURN.class,                               ATInvalidTiffTagsInBigTiff.class,  ATMetadataWithManyRefsToOneFile.class,      ATTimeBasedPublication.class,
	ATDetectUncompletedReferences.class,              ATKeepModDates.class,              ATMetadataWithRelativeReferencesEad.class,  ATUseCaseDeleteObjectFromWorkflow.class,
	ATDetectUnreferencedFilesEAD.class,               ATMailQueue.class,                 ATMetsGenTest.class,                        ATUseCaseIngestArchivDuisburg.class,
	ATDetectUnreferencedFilesLIDO.class,              ATMetadataUpdatesDeltaEAD.class,   ATMigrationDecisionTimeout.class,           ATUseCaseIngestDeltaDuringRetrievalOrigPkg.class,
	ATDetectUnreferencedFilesMETS.class,              ATMetadataUpdatesDeltaLIDO.class,  ATMigrationRight.class,                     ATUseCaseIngestEadMetsVariousRefs.class,
	ATIdentifierAssignment.class,                     ATMetadataUpdatesDeltaMETS.class,  ATMultipageTiff.class,                      ATUseCaseIngestObjectDBProperties.class,
	ATIngestJHoveBrokenContentSIP.class,              ATMetadataUpdatesEAD.class,        ATPremisCreationDelta.class,                ATUseCaseIngestSpecialCases.class,
	ATIngestLav.class,                                ATMetadataUpdatesLIDO.class,       ATPremisCreation.class,                     ATUseCaseUnableToPublishXMPPackage.class,
	ATIngestMultPackagesAndCheckCreatedCopies.class,  ATMetadataUpdatesMetsMods.class,   ATReadUrnFromMets.class,
	ATIngestUnpackedSIP.class,                        ATMetadataUpdatesNewDDBEad.class,  ATRestructureActionScanVirus.class,

})*/
public class CompleteATSuite extends Suite{
	
	 public  CompleteATSuite(Class<?> klass) throws InitializationError, IOException {
		super(klass, getATTestClasses("classpath:/de/uzk/**/AT*.class"));
	}

	public static Class<?>[]  getATTestClasses(String pattern) throws IOException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		PathMatchingResourcePatternResolver scanner = new PathMatchingResourcePatternResolver();
		Resource[] resources = scanner.getResources(pattern);

		for (Resource resource : resources) {
			Class<?> clazz = getClassFromResource(resource);
			classes.add(clazz);
		}
		System.out.println("TestSuite contains : "+resources.length+" TestCases");
		Class<?>[] classListArray=(Class<?>[]) classes.toArray(new Class[classes.size()]);
		return classListArray;
	}

	public static Class<?> getClassFromResource(Resource resource) {
		try {
			String resourceUri = resource.getURI().toString();
			if (resourceUri.indexOf("de") != -1)
				resourceUri = resourceUri.substring(resourceUri.indexOf("de")).replace(".class", "").replace("/", ".");
			System.out.println("Add Test-Case to suite: "+resourceUri);
			return Class.forName(resourceUri);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}


