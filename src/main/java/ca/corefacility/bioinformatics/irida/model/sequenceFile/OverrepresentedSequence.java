package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaThing;

/**
 * A {@link SequenceFile} may have 0 or more over-represented sequences.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Entity
@Table(name = "overrepresented_sequence")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class OverrepresentedSequence implements IridaThing, Comparable<OverrepresentedSequence> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull(message = "{overrepresented.sequence.sequence.notnull}")
	private String sequence;

	@NotNull(message = "{overrepresented.sequence.sequence.count.notnull}")
	private int overrepresentedSequenceCount;

	@NotNull(message = "{overrepresented.sequence.percentage.notnull}")
	private BigDecimal percentage;

	@NotNull(message = "{overrepresented.sequence.possibleSource.notnull}")
	private String possibleSource;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private final Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

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
	public Date getCreatedDate() {
		return createdDate;
	}
}
