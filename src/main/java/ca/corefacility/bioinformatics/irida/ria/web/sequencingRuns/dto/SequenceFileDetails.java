package ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto;

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

	public SequenceFileDetails(SequenceFile file, Long sequencingObjectId) {
		this.id = file.getId();
		this.sequencingObjectId = sequencingObjectId;
		this.fileName = file.getFileName();
		this.fileSize = file.getFileSize();
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

	@Override
	public int compareTo(SequenceFileDetails sequenceFileDetails) {
		return id.compareTo(sequenceFileDetails.id);
	}
}
