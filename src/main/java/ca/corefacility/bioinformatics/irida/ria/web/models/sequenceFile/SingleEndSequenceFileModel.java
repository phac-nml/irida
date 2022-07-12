package ca.corefacility.bioinformatics.irida.ria.web.models.sequenceFile;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.models.BaseRecord;

/**
 * IRIDA UI model to represent {@link SingleEndSequenceFile}
 */
public class SingleEndSequenceFileModel extends BaseRecord {
	private final SequenceFileModel file;

	public SingleEndSequenceFileModel(SingleEndSequenceFile file) {
		super(file.getId(), file.getLabel(), file.getCreatedDate(), file.getModifiedDate());
		this.file = new SequenceFileModel(file.getSequenceFile());
	}
	public SequenceFileModel getFile() {
		return file;
	}
}