package ca.corefacility.bioinformatics.irida.ria.web.models.sequenceFile;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.models.IridaBase;
import ca.corefacility.bioinformatics.irida.ria.web.models.ModelKeys;

/**
 * UI Model for a {@link SequenceFile}
 */
public class SequenceFileModel extends IridaBase {
	public SequenceFileModel(SequenceFile file) {
		super(file.getId(), ModelKeys.SequenceFileModel.label, file.getFileName(), file.getCreatedDate(),
				file.getModifiedDate());
	}
}
