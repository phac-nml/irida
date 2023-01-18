package ca.corefacility.bioinformatics.irida.model.sample;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

/**
 * a {@link QCEntry} relating to the coverage of a sequence
 */
@Entity
public class CoverageQCEntry extends QCEntry {

	@Column(name = "total_bases")
	private long totalBases;

	@Transient
	private Project project;

	public CoverageQCEntry() {
		super();
	}

	public CoverageQCEntry(SequencingObject sequencingObject, long totalBases) {
		super(sequencingObject);
		this.totalBases = totalBases;
	}

	@Override
	public QCEntryType getType() {
		return QCEntryType.COVERAGE;
	}

	@Override
	public String getMessage() {
		return getCoverage() + "x";
	}

	/**
	 * Get the coverage number for this {@link QCEntry}
	 * 
	 * @return coverage
	 */
	public int getCoverage() {
		if (getStatus().equals(QCEntryStatus.UNAVAILABLE)) {
			return 0;
		}

		return calculateCoverage();
	}

	public long getTotalBases() {
		return totalBases;
	}

	public void setTotalBases(long totalBases) {
		this.totalBases = totalBases;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addProjectSettings(Project project) {
		this.project = project;
	}

	/**
	 * {@inheritDoc}
	 */
	public QCEntryStatus getStatus() {
		if (project == null || project.getGenomeSize() == null
				|| (project.getMinimumCoverage() == null && project.getMaximumCoverage() == null)) {
			return QCEntryStatus.UNAVAILABLE;
		}

		int coverage = calculateCoverage();

		QCEntryStatus status = QCEntryStatus.POSITIVE;

		Integer minimumCoverage = project.getMinimumCoverage();
		Integer maximumCoverage = project.getMaximumCoverage();

		// if minimum is set, check if coverage is over it
		if (minimumCoverage != null && coverage < minimumCoverage) {
			status = QCEntryStatus.NEGATIVE;
		}

		// if maximum is set, check if coverage is over it
		if (maximumCoverage != null && coverage > maximumCoverage) {
			status = QCEntryStatus.NEGATIVE;
		}

		return status;
	}

	private int calculateCoverage() {
		return Math.round((float) totalBases / project.getGenomeSize());
	}

}
