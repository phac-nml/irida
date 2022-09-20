package ca.corefacility.bioinformatics.irida.ria.web.models.sequenceFile;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.models.BaseModel;

/**
 * IRIDA UI model to represent {@link SingleEndSequenceFile}
 */
public class SingleEndSequenceFileModel extends BaseModel {
	private final SequenceFileModel file;
	private final Set<QCEntry> qcEntries;

	public SingleEndSequenceFileModel(SingleEndSequenceFile file) {
		super(file.getId(), file.getLabel(), file.getCreatedDate(), file.getModifiedDate());
		this.file = new SequenceFileModel(file.getSequenceFile());
		this.qcEntries = file.getQcEntries();
	}
	public SequenceFileModel getFile() {
		return file;
	}

	public Set<QCEntry> getQcEntries() {
		return qcEntries;
	}
}