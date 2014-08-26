package de.uzk.hki.da.format;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeMetadataExtractor implements MetadataExtractor{

	/** The Constant logger. */
	static final Logger logger = LoggerFactory.getLogger(FakeMetadataExtractor.class);
	
	@Override
	public String extract(File file, int jobId) throws IOException {
		logger.debug("EXTRACT JHOVE FAKE");
		return "/tmp/abc.txt";
	}

	@Override
	public String getJhoveFolder() {
		return "abc";
	}

	@Override
	public void setJhoveFolder(String jhoveFolder) {}

}
