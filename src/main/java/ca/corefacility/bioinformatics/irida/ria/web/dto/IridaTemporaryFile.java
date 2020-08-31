package ca.corefacility.bioinformatics.irida.ria.web.dto;

import java.nio.file.Path;

/**
 * Used as a response for encapsulating a temporary file and it's directory
 */

public class IridaTemporaryFile {
	private Path filePath;
	private Path directoryPath;

	public IridaTemporaryFile(Path filePath, Path directoryPath) {
		this.filePath = filePath;
		this.directoryPath = directoryPath;
	}

	public Path getFile() {
		return filePath;
	}

	public void setFile(Path filePath) {
		this.filePath = filePath;
	}

	public Path getDirectoryPath() {
		return directoryPath;
	}

	public void setDirectoryPath(Path directoryPath) {
		this.directoryPath = directoryPath;
	}
}
