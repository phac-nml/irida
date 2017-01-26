package ca.corefacility.bioinformatics.irida.model.sample;

import javax.persistence.Entity;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

@Entity
public class CoverageQCEntry extends QCEntry {

	private int coverage;

	public CoverageQCEntry() {
		super();
	}

	public CoverageQCEntry(SequencingObject sequencingObject, int coverage, boolean positive) {
		super(sequencingObject, positive);
		this.coverage = coverage;
	}

	@Override
	public QCEntryType getType() {
		return QCEntryType.COVERAGE;
	}

	@Override
	public String getMessage() {
		return coverage + "x";
	}

	/**
	 * Get the coverage number for this {@link QCEntry}
	 * 
	 * @return coverage
	 */
	public int getCoverage() {
		return coverage;
	}

}
