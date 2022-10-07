package ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto;

import java.util.Objects;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Data transfer object for retrieving {@link SequenceFile} details from a {@link SequencingRun}.
 */
public class SequenceFileDetails implements Comparable<SequenceFileDetails> {
	private Long id;
	private Long sequencingObjectId;
	private String fileName;
	private String fileSize;

	private String processingState;

	public SequenceFileDetails(SequenceFile file, Long sequencingObjectId, String processingState) {
		this.id = file.getId();
		this.sequencingObjectId = sequencingObjectId;
		this.fileName = file.getFileName();
		this.fileSize = file.getFileSize();
		this.processingState = processingState;
	}

	public Long getId() {
		return id;
	}

	public Long getSequencingObjectId() {
		return sequencingObjectId;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getProcessingState() { return processingState; }


	@Override
	public int compareTo(SequenceFileDetails sequenceFileDetails) {
		return id.compareTo(sequenceFileDetails.id);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SequenceFileDetails that = (SequenceFileDetails) o;
		return Objects.equals(id, that.id) && Objects.equals(sequencingObjectId, that.sequencingObjectId)
				&& Objects.equals(fileName, that.fileName) && Objects.equals(fileSize, that.fileSize);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, sequencingObjectId, fileName, fileSize);

	}
}
