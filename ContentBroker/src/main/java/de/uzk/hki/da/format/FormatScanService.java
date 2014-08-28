package de.uzk.hki.da.format;

import java.io.IOException;
import java.util.List;

import de.uzk.hki.da.model.DAFile;

public interface FormatScanService {

	public List<DAFile> identify(List<DAFile> files) throws IOException;
}
