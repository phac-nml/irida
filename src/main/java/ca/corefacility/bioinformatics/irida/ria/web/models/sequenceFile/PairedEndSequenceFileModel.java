package ca.corefacility.bioinformatics.irida.ria.web.models.sequenceFile;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.ria.web.models.IridaBase;

import com.google.common.collect.ImmutableList;

/**
 * Describes a {@link SequenceFilePair} for the UI
 */
public class PairedEndSequenceFileModel extends IridaBase {
	/*
	This will always be a pair of:
	  [ forward, reverse ]
	 */
	private final List<SequenceFileModel> files;

	public PairedEndSequenceFileModel(SequenceFilePair pair) {
		super(pair.getId(), pair.getLabel(), pair.getCreatedDate(), pair.getModifiedDate());
		this.files = ImmutableList.of(new SequenceFileModel(pair.getForwardSequenceFile()),
				new SequenceFileModel(pair.getReverseSequenceFile()));

	}

	public List<SequenceFileModel> getFiles() {
		return files;
	}
}