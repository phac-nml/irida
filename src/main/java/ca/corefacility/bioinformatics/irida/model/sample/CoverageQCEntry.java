package ca.corefacility.bioinformatics.irida.model.sample;

import javax.persistence.Entity;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

@Entity
public class CoverageQCEntry extends QCEntry {

	public CoverageQCEntry() {
		super();
	}

	public CoverageQCEntry(SequencingObject sequencingObject, Long coverage, boolean positive) {
		super(sequencingObject, positive, coverage + "x");
	}

	@Override
	public QCEntryType getType() {
		return QCEntryType.COVERAGE;
	}

}
