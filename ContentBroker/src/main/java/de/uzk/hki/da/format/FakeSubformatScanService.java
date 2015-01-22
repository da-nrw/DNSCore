package de.uzk.hki.da.format;

import java.io.IOException;
import java.util.List;

public class FakeSubformatScanService implements FormatScanService, Connector{

	@Override
	public boolean isConnectable() {
		return true;
	}

	@Override
	public List<FileWithFileFormat> identify(List<FileWithFileFormat> files)
			throws IOException {
		return files;
	}

}
