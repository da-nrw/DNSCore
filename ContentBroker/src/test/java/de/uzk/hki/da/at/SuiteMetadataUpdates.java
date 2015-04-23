package de.uzk.hki.da.at;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
	
	ATMetadataUpdatesEAD.class,
	ATMetadataUpdatesLIDO.class,
	ATMetadataUpdatesMetsMods.class,
	ATMetadataUpdatesRheinlaender.class,
	ATMetadataUpdatesDeltaEAD.class,
	ATMetadataUpdatesDeltaLIDO.class,
	ATMetadataUpdatesDeltaMETS.class
})

/**
 * @author Daniel M. de Oliveira
 */
public class SuiteMetadataUpdates {

}
