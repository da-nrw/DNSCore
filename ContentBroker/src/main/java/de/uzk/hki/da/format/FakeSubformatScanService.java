package de.uzk.hki.da.format;

import java.io.IOException;
import java.util.List;

import de.uzk.hki.da.utils.Path;

public class FakeSubformatScanService implements FormatScanService, Connector{

	@Override
	public boolean isConnectable() {
		return true;
	}

	@Override
	public List<FileWithFileFormat> identify(Path workPath,List<FileWithFileFormat> files, boolean pruneExceptions)
			throws IOException {
		return files;
	}

	@Override
	public void setKnownFormatCmdLineErrors(KnownFormatCmdLineErrors knownerrors) {
		
	}

}
