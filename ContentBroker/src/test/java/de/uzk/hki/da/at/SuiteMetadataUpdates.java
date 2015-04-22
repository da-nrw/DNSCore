package de.uzk.hki.da.at;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
	
	_ATMetadataUpdatesEAD.class,
	_ATMetadataUpdatesLIDO.class,
	_ATMetadataUpdatesMetsMods.class,
	ATMetadataUpdatesRheinlaender.class,
	_ATMetadataUpdatesDeltaEAD.class,
	_ATMetadataUpdatesDeltaLIDO.class,
	_ATMetadataUpdatesDeltaMETS.class
})

/**
 * @author Daniel M. de Oliveira
 */
public class SuiteMetadataUpdates {

}
