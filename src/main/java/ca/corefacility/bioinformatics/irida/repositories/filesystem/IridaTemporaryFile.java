package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.nio.file.Path;

/**
 * Used as a response for encapsulating a temporary file and it's directory. This is
 * used to download a file from a cloud object store to a temporary file location
 * on the local file system. If this is dto is used then we must remember to cleanup
 * the temporary file and/or directory using the cleanupDownloadedLocalTemporaryFiles
 * in the iridaFileStorageUtility.
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
