package de.uzk.hki.da.grid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.Node;


public class IrodsFederatedDistributedConversionAdapter extends
		IrodsDistributedConversionAdapter {

	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(IrodsFederatedDistributedConversionAdapter.class);
	
	@Override
	public void replicateToLocalNode(String relativePath, Node node) {
		
		logger.debug("sync foreign Conversions to Local node named " + node.getName());
		IrodsCommandLineConnector iclc = new IrodsCommandLineConnector();
		for (Node fn : node.getCooperatingNodes()) {
			String src 	= "/"+fn.getIdentifier() + relativePath;
			String dest = "/"+node.getIdentifier()+ relativePath;
			logger.debug("sync " + src + " to " + dest );
			iclc.rsync(src ,dest , node.getWorkingResource());
			iclc.remove(src);
		}
	}
}
