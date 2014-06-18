package ca.corefacility.bioinformatics.irida.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

/**
 * A {@link SequenceFile} may have 0 or more over-represented sequences.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Entity
@Table(name = "overrepresented_sequence")
@Audited
public class OverrepresentedSequence implements IridaThing, Comparable<OverrepresentedSequence> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull(message = "{overrepresented.sequence.sequence.notnull}")
	@Column(nullable = false)
	private String sequence;
	@NotNull(message = "{overrepresented.sequence.sequence.count.notnull}")
	@Column(nullable = false)
	private int overrepresentedSequenceCount;
	@NotNull(message = "{overrepresented.sequence.percentage.notnull}")
	@Column(nullable = false)
	private BigDecimal percentage;
	@NotNull(message = "{overrepresented.sequence.possibleSource.notnull}")
	@Column(nullable = false)
	private String possibleSource;
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private final Date createdDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "sequenceFile_id")
	private SequenceFile sequenceFile;

	public OverrepresentedSequence() {
		this.createdDate = new Date();
	}

	public OverrepresentedSequence(String sequence, int count, BigDecimal percentage, String possibleSource) {
		this();
		this.sequence = sequence;
		this.overrepresentedSequenceCount = count;
		this.percentage = percentage;
		this.possibleSource = possibleSource;
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
		return modifiedDate.compareTo(o.modifiedDate);
	}

	public String getSequence() {
		return sequence;
	}

	public int getOverrepresentedSequenceCount() {
		return overrepresentedSequenceCount;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public void setOverrepresentedSequenceCount(int overrepresentedSequenceCount) {
		this.overrepresentedSequenceCount = overrepresentedSequenceCount;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public void setPossibleSource(String possibleSource) {
		this.possibleSource = possibleSource;
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

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getLabel() {
		return toString();
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Override
	public Date getTimestamp() {
		return createdDate;
	}

	public SequenceFile getSequenceFile() {
		return sequenceFile;
	}

	public void setSequenceFile(SequenceFile sequenceFile) {
		this.sequenceFile = sequenceFile;
	}

}
