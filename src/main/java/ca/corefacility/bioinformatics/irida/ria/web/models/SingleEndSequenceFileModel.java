package ca.corefacility.bioinformatics.irida.ria.web.models;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;

/**
 * IRIDA UI model to represent {@link SingleEndSequenceFile}
 */
public class SingleEndSequenceFileModel extends IridaBase {
	private final SequenceFileModel file;

	public SingleEndSequenceFileModel(SingleEndSequenceFile file) {
		super(file.getId(), ModelKeys.SingleEndSequenceFileModel.label, file.getLabel(), file.getCreatedDate(),
				file.getModifiedDate());
		this.file = new SequenceFileModel(file.getSequenceFile());
	}

	public SequenceFileModel getFile() {
		return file;
	}
}
