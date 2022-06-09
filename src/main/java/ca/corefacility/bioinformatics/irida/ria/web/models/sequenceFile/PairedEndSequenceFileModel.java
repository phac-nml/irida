package ca.corefacility.bioinformatics.irida.ria.web.models.sequenceFile;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.ria.web.models.IridaBase;
import ca.corefacility.bioinformatics.irida.ria.web.models.ModelKeys;

import com.google.common.collect.ImmutableList;

public class PairedEndSequenceFileModel extends IridaBase {
	/*
	This will always be a pair of:
	  [ forward, reverse ]
	 */
	private final List<SequenceFileModel> files;

	public PairedEndSequenceFileModel(SequenceFilePair pair) {
		super(pair.getId(), ModelKeys.PairedEndSequenceFileModel.label, pair.getLabel(), pair.getCreatedDate(),
				pair.getModifiedDate());
		this.files = ImmutableList.of(new SequenceFileModel(pair.getForwardSequenceFile()),
				new SequenceFileModel(pair.getReverseSequenceFile()));

	}

	public List<SequenceFileModel> getFiles() {
		return files;
	}
}
