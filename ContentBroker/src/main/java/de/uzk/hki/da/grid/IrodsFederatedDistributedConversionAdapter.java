package de.uzk.hki.da.grid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IrodsFederatedDistributedConversionAdapter extends
		IrodsDistributedConversionAdapter {

	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(IrodsFederatedDistributedConversionAdapter.class);
	
	
	public void replicateToLocalNode(String relativePath) {
		
		
		irodsSystemConnector.establishConnect();
		String rule = "syncToLocalNode {\n"
        + "*zones=\"\"\n"
        + "*forbiddenNodes=\"\"\n"
        + "acGetZonesOnGrid(*zones,*forbiddenNodes)\n"
        + "acSynchronizeZonesToCollection(*zones,*srcCollWithoutZone,*destColl,*destResc,1,*status)\n"
		+ "}\n"
		+ "INPUT *destColl=\"/" 
		+  irodsSystemConnector.getZone() 
		+  relativePath + "\", *destResc=\"" 
		+ irodsSystemConnector.getDefaultStorage() + "\", *srcCollWithoutZone=\""
		+ relativePath + "\"\n"
		+ "OUTPUT *status";
		try {
			irodsSystemConnector.executeRule(rule, "*status");
		}  catch (Exception e) {
			logger.error("Ein Fehler ist aufgetreten bei Execute Rule " + rule);
			throw new RuntimeException("Error executing syncing rule",e);
		}
		finally
		{
			irodsSystemConnector.logoff();
		}
	}
}
