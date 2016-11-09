package de.uzk.hki.da.format;

import java.util.List;

import de.uzk.hki.da.model.KnownError;
import de.uzk.hki.da.utils.Path;

public class FileWithFileFormatImpl implements FileWithFileFormat {

	private String formatPUID;
	private String subformatIdentifier;
	private Path path;
	private List<KnownError> knownErrors;

	@Override
	public String getFormatPUID() {
		return formatPUID;
	}

	@Override
	public void setFormatPUID(String formatPUID) {
		this.formatPUID = formatPUID;
	}

	@Override
	public String getSubformatIdentifier() {
		return subformatIdentifier;
	}

	@Override
	public void setSubformatIdentifier(String formatSecondaryAttribute) {
		this.subformatIdentifier = formatSecondaryAttribute;
	}

	@Override
	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	@Override
	public List<KnownError> getKnownErrors() {
		return knownErrors;
	}

	@Override
	public void setKnownErrors(List<KnownError> knownError) {
		this.knownErrors = knownError;
	}

	@Override
	public String toString() {
		return "FileWithFileFormatImpl [" + (formatPUID != null ? "formatPUID=" + formatPUID + ", " : "")
				+ (subformatIdentifier != null ? "subformatIdentifier=" + subformatIdentifier + ", " : "")
				+ (path != null ? "path=" + path + ", " : "")
				+ (knownErrors != null ? "knownErrors=" + knownErrors : "") + "]";
	}


}
