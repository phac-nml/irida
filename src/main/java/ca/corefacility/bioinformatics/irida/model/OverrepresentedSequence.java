package ca.corefacility.bioinformatics.irida.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.Audited;

/**
 * A {@link SequenceFile} may have 0 or more over-represented sequences.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Entity
@Table(name = "overrepresented_sequence")
@Audited
public class OverrepresentedSequence implements IridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	private String sequence;
	@NotNull
	private int count;
	@NotNull
	private BigDecimal percentage;
	@NotNull
	private String possibleSource;
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;
	@NotNull
	private boolean enabled;

	public OverrepresentedSequence(String sequence, int count, BigDecimal percentage, String possibleSource) {
		this.sequence = sequence;
		this.count = count;
		this.percentage = percentage;
		this.possibleSource = possibleSource;
	}

	@Override
	public int hashCode() {
		return Objects.hash(sequence, count, percentage, possibleSource);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof OverrepresentedSequence) {
			OverrepresentedSequence s = (OverrepresentedSequence) o;
			return Objects.equals(sequence, s.sequence) && Objects.equals(count, s.count)
					&& Objects.equals(percentage, s.percentage) && Objects.equals(possibleSource, s.possibleSource);
		}

		return false;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getSequence() {
		return sequence;
	}

	public int getCount() {
		return count;
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

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public Date getTimestamp() {
		return createdDate;
	}

	@Override
	public void setTimestamp(Date timestamp) {
		this.createdDate = timestamp;
	}

}
