package de.uzk.hki.da.grid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.path.Path;
import de.uzk.hki.da.utils.C;


public class IrodsFederatedDistributedConversionAdapter extends
		IrodsDistributedConversionAdapter {

	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(IrodsFederatedDistributedConversionAdapter.class);
	
	
	public void replicateToLocalNode(String relativePath) {
		
		
		if (!irodsSystemConnector.connect()){
			throw new RuntimeException("Couldn't establish iRODS-Connection");
		}
		String rule = "syncPIPS {\n"
        + "*zones=\"\"\n"
        + "*forbiddenNodes=\"\"\n"
        + "acGetZonesOnGrid(*zones,*forbiddenNodes)\n"
        + "acSynchronizeZonesToCollection(*zones,*srcCollWithoutZone,*destColl,*destResc)\n"
		+ "}\n"
		+ "INPUT *destColl=\"/" 
		+  irodsSystemConnector.getZone() 
		+  relativePath + "\", *destResc=\"" 
		+ irodsSystemConnector.getDefaultStorage() + "\", *srcCollWithoutZone=\""
		+ relativePath + "\"\n"
		+ "OUTPUT ruleExecOut";
		try {
			irodsSystemConnector.executeRule(rule, "");
		}  catch (Exception e) {
			logger.error("Ein Fehler ist aufgetreten bei Execute Rule " + rule);
		}
		finally
		{
			irodsSystemConnector.logoff();
		}
	}
}
