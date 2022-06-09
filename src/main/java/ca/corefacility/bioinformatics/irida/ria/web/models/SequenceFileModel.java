package ca.corefacility.bioinformatics.irida.ria.web.models;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * UI Model for a {@link SequenceFile}
 */
public class SequenceFileModel extends IridaBase {
	public SequenceFileModel(SequenceFile file) {
		super(file.getId(), ModelKeys.SequenceFileModel.label, file.getFileName(), file.getCreatedDate(),
				file.getModifiedDate());
	}
}
