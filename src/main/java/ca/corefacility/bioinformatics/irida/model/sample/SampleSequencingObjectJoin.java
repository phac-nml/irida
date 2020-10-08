package ca.corefacility.bioinformatics.irida.model.sample;

import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

/**
 * Relationship between a {@link Sample} and a {@link SequencingObject}
 */
@Entity
@Table(name = "sample_sequencingobject", uniqueConstraints = @UniqueConstraint(columnNames = { "sequencingobject_id" }, name = "UK_SEQUENCEOBJECT_SAMPLE_FILE"))
@Audited
@EntityListeners(AuditingEntityListener.class)
public class SampleSequencingObjectJoin implements Join<Sample, SequencingObject> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "sequencingobject_id")
	private final SequencingObject sequencingObject;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "sample_id")
	private final Sample sample;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", updatable = false)
	private Date createdDate;

	/**
	 * Default constructor for hibernate
	 */
	@SuppressWarnings("unused")
	private SampleSequencingObjectJoin() {
		createdDate = new Date();
		sequencingObject = null;
		sample = null;
	}

	public SampleSequencingObjectJoin(Sample subject, SequencingObject object) {
		createdDate = new Date();
		this.sequencingObject = object;
		this.sample = subject;
	}

	public Long getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof SampleSequencingObjectJoin) {
			SampleSequencingObjectJoin j = (SampleSequencingObjectJoin) o;
			return Objects.equals(sequencingObject, j.sequencingObject) && Objects.equals(sample, j.sample);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(sequencingObject, sample);
	}

	@Override
	public Sample getSubject() {
		return sample;
	}

	@Override
	public SequencingObject getObject() {
		return sequencingObject;
	}

	@Override
	public Date getTimestamp() {
		return getCreatedDate();
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

}
