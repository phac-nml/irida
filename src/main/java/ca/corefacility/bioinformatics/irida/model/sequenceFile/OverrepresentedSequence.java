package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;

/**
 * A {@link SequenceFile} may have 0 or more over-represented sequences.
 * 
 */
@Entity
@Table(name = "overrepresented_sequence")
@EntityListeners(AuditingEntityListener.class)
public class OverrepresentedSequence implements IridaThing, Comparable<OverrepresentedSequence> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final Long id;

	@NotNull(message = "{overrepresented.sequence.sequence.notnull}")
	private final String sequence;

	@NotNull(message = "{overrepresented.sequence.sequence.count.notnull}")
	private final int overrepresentedSequenceCount;

	@NotNull(message = "{overrepresented.sequence.percentage.notnull}")
	private final BigDecimal percentage;

	@NotNull(message = "{overrepresented.sequence.possibleSource.notnull}")
	private final String possibleSource;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private final Date createdDate;

	/**
	 * for hibernate
	 */
	@SuppressWarnings("unused")
	private OverrepresentedSequence() {
		this.id = null;
		this.sequence = null;
		this.overrepresentedSequenceCount = -1;
		this.percentage = null;
		this.possibleSource = null;
		this.createdDate = null;
	}

	public OverrepresentedSequence(String sequence, int count, BigDecimal percentage, String possibleSource) {
		this.sequence = sequence;
		this.overrepresentedSequenceCount = count;
		this.percentage = percentage;
		this.possibleSource = possibleSource;
		this.createdDate = new Date();
		this.id = null;
	}

	@Override
	public int hashCode() {
		return Objects.hash(sequence, overrepresentedSequenceCount, percentage, possibleSource);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof OverrepresentedSequence) {
			OverrepresentedSequence s = (OverrepresentedSequence) o;
			return Objects.equals(sequence, s.sequence)
					&& Objects.equals(overrepresentedSequenceCount, s.overrepresentedSequenceCount)
					&& Objects.equals(percentage, s.percentage) && Objects.equals(possibleSource, s.possibleSource);
		}

		return false;
	}

	@Override
	public int compareTo(OverrepresentedSequence o) {
		return createdDate.compareTo(o.createdDate);
	}

	public String getSequence() {
		return sequence;
	}

	public int getOverrepresentedSequenceCount() {
		return overrepresentedSequenceCount;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public String getPossibleSource() {
		return possibleSource;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return toString();
	}
	
	@Override
	public String toString() {
		return sequence;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}
}
