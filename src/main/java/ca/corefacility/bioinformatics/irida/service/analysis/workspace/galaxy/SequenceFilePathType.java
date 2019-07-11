package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

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
	 * @throws IOException If there was an error reading the sequence file.
	 */
	public SequenceFilePathType(IridaSequenceFile sequenceFile) throws IOException {
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
		return Objects.hash(fileType, path);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SequenceFilePathType) {
			SequenceFilePathType other = (SequenceFilePathType) obj;

			return Objects.equals(fileType, other.fileType) && Objects.equals(path, other.path);
		}

		return false;
	}
}
