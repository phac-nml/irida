package ca.corefacility.bioinformatics.irida.model.run;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;

@Entity
@Table(name = "sequencing_run")
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public abstract class SequencingRun implements IridaThing, Comparable<SequencingRun> {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Lob
	private String description;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private final Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE, CascadeType.MERGE }, mappedBy = "sequencingRun", orphanRemoval = true)
	private Set<SequenceFile> sequenceFiles;

	public SequencingRun() {
		createdDate = new Date();
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public Date getModifiedDate() {
		return modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Override
	public int compareTo(SequencingRun p) {
		return modifiedDate.compareTo(p.modifiedDate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return "SequencingRun " + createdDate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdDate, modifiedDate, description);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SequencingRun)) {
			return false;
		}
		final SequencingRun other = (SequencingRun) obj;
		if (Objects.equals(this.description, other.description) && Objects.equals(this.createdDate, other.createdDate)
				&& Objects.equals(this.modifiedDate, other.modifiedDate)) {
			return true;
		}

		return false;
	}

	/**
	 * Get the name of the sequencer type
	 * 
	 * @return Name of the sequencer
	 */
	public abstract String getSequencerName();

}
