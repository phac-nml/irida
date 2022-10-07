package ca.corefacility.bioinformatics.irida.ria.web.models.sequenceFile;

import java.util.List;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.ria.web.models.BaseModel;

import com.google.common.collect.ImmutableList;

/**
 * Describes a {@link SequenceFilePair} for the UI
 */
public class PairedEndSequenceFileModel extends BaseModel {
	/*
	This will always be a pair of:
	  [ forward, reverse ]
	 */
	private final List<SequenceFileModel> files;
	private final Set<QCEntry> qcEntries;

	public PairedEndSequenceFileModel(SequenceFilePair pair) {
		super(pair.getId(), pair.getLabel(), pair.getCreatedDate(), pair.getModifiedDate());
		this.files = ImmutableList.of(new SequenceFileModel(pair.getForwardSequenceFile()),
				new SequenceFileModel(pair.getReverseSequenceFile()));
		this.qcEntries = pair.getQcEntries();
	}

	public List<SequenceFileModel> getFiles() {
		return files;
	}

	public Set<QCEntry> getQcEntries() {
		return qcEntries;
	}
}