package ca.corefacility.bioinformatics.irida.model.sample;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

@Entity
public class CoverageQCEntry extends QCEntry {

	@NotNull
	Long coverage;

	public CoverageQCEntry() {
		super();
	}

	public CoverageQCEntry(SequencingObject sequencingObject, Long coverage, boolean positive) {
		super(sequencingObject, positive);
		this.coverage = coverage;
	}

	@Override
	public String getMessage() {
		return coverage + "x";
	}

	public void setCoverage(Long coverage) {
		this.coverage = coverage;
	}

	public Long getCoverage() {
		return coverage;
	}

	@Override
	public QCEntryType getType() {
		return QCEntryType.COVERAGE;
	}

}
