package ca.corefacility.bioinformatics.irida.ria.web.dto;

import java.nio.file.Path;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

/**
 * Used as a response for encapsulating a temporary file and it's directory
 * for a concatenator object
 */

public class IridaConcatenatorTemporaryFile {
	private Path filePath;
	private Path directoryPath;
	private SequencingObject sequencingObject;

	public IridaConcatenatorTemporaryFile(Path filePath, Path directoryPath, SequencingObject sequencingObject) {
		this.filePath = filePath;
		this.directoryPath = directoryPath;
		this.sequencingObject = sequencingObject;
	}

	public Path getFilePath() {
		return filePath;
	}

	public void setFilePath(Path filePath) {
		this.filePath = filePath;
	}

	public Path getDirectoryPath() {
		return directoryPath;
	}

	public void setDirectoryPath(Path directoryPath) {
		this.directoryPath = directoryPath;
	}

	public SequencingObject getSequencingObject() {
		return sequencingObject;
	}

	public void setSequencingObject(SequencingObject sequencingObject) {
		this.sequencingObject = sequencingObject;
	}
}
