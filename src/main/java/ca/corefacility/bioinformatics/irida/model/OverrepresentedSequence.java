package ca.corefacility.bioinformatics.irida.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.joins.impl.SequenceFileOverrepresentedSequenceJoin;

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
	private String sequence;
	@NotNull(message = "{overrepresented.sequence.sequence.count.notnull}")
	private int overrepresentedSequenceCount;
	@NotNull(message = "{overrepresented.sequence.percentage.notnull}")
	private BigDecimal percentage;
	@NotNull(message = "{overrepresented.sequence.possibleSource.notnull}")
	private String possibleSource;
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.REMOVE,mappedBy = "sequence")
	private List<SequenceFileOverrepresentedSequenceJoin> squenceFiles;
	
	public OverrepresentedSequence(){}

	public OverrepresentedSequence(String sequence, int count, BigDecimal percentage, String possibleSource) {
		this.sequence = sequence;
		this.overrepresentedSequenceCount = count;
		this.percentage = percentage;
		this.possibleSource = possibleSource;
		this.createdDate = new Date();
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

	@Override
	public void setTimestamp(Date timestamp) {
		this.createdDate = timestamp;
	}

	public List<SequenceFileOverrepresentedSequenceJoin> getSquenceFiles() {
		return squenceFiles;
	}

	public void setSquenceFiles(List<SequenceFileOverrepresentedSequenceJoin> squenceFiles) {
		this.squenceFiles = squenceFiles;
	}

}
