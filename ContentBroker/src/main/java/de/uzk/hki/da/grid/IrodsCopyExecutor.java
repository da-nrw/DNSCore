package de.uzk.hki.da.grid;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.CopyJob;

public class IrodsCopyExecutor implements JobExecutor {

	private IrodsCommandLineConnector iclc = null;
		
	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(IrodsCopyExecutor.class);
	
	public IrodsCopyExecutor() {
		iclc = new IrodsCommandLineConnector();
	}
	
	private String targetRescName;
	
	private String dirPrefix;
			
	public boolean execute(CopyJob cj ) {
			if (cj==null) {
				logger.error("CopyJob is null");
				return false;
			}
			
			if (dirPrefix== null) {
				logger.error("dirPrefix is null");
				return false;
			}
			if (targetRescName== null) {
				logger.error("targetRescName is null");
				return false;
			}
			
			String targetDir =  FilenameUtils.getFullPath("/" +cj.getDest_name() + "/"+dirPrefix + cj.getSource());
			if (!iclc.exists(targetDir)){
				iclc.mkCollection(targetDir);
			}
			String out = iclc.rsync(cj.getSource(), targetDir, targetRescName);
			logger.debug(out);
			if (out.contains("ERROR")) {
				return false;
			}
			return true;
	}

	public String getTargetRescName() {
		return targetRescName;
	}

	public void setTargetRescName(String targetRescName) {
		this.targetRescName = targetRescName;
	}

	public String getDirPrefix() {
		return dirPrefix;
	}

	public void setDirPrefix(String dirPrefix) {
		this.dirPrefix = dirPrefix;
	}
	

}
