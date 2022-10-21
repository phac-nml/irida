package ca.corefacility.bioinformatics.irida.ria.web.models.sequenceFile;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.models.BaseModel;

/**
 * UI Model for a {@link SequenceFile}
 */
public class SequenceFileModel extends BaseModel {
	public String getFileSize() {
		return fileSize;
	}

	public SequenceFileModel(SequenceFile file) {
		super(file.getId(), file.getFileName(), file.getCreatedDate(), file.getModifiedDate());
		this.fileSize = file.getFileSize();
	}

	private final String fileSize;
}