package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy;

import java.nio.file.Path;

import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.InputFileType;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class linking together a sequence file path and the Galaxy file type.
 *
 */
public class SequenceFilePathType {
	private InputFileType fileType;
	private Path path;

	/**
	 * Creates a new {@link SequenceFilePathType}.
	 * 
	 * @param sequenceFile The specific sequence file.
	 */
	public SequenceFilePathType(IridaSequenceFile sequenceFile) {
		checkNotNull(sequenceFile, "sequenceFile is null");
		
		this.path = sequenceFile.getFile();
		this.fileType = sequenceFile.isGzipped() ? InputFileType.FASTQ_SANGER_GZ : InputFileType.FASTQ_SANGER;
	}

	public InputFileType getFileType() {
		return fileType;
	}

	public Path getPath() {
		return path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileType == null) ? 0 : fileType.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SequenceFilePathType other = (SequenceFilePathType) obj;
		if (fileType != other.fileType)
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
}
