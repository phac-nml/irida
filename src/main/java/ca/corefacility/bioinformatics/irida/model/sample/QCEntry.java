package ca.corefacility.bioinformatics.irida.model.sample;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

/**
 * Abstract class describing quality control entries for a
 * {@link SequencingObject}
 */
@Entity
@Table(name = "qc_entry")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@EntityListeners(AuditingEntityListener.class)
public abstract class QCEntry {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JsonIgnore
	@NotNull
	private SequencingObject sequencingObject;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	@NotNull
	private Date createdDate;
	
	@NotNull
	private boolean positive;

	public QCEntry() {
		positive = false;
	}

	public QCEntry(SequencingObject sequencingObject, boolean positive) {
		this.sequencingObject = sequencingObject;
		this.positive = positive;
	}

	public Long getId() {
		return id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public SequencingObject getSequencingObject() {
		return sequencingObject;
	}
	
	public boolean isPositive() {
		return positive;
	}
	
	public void setPositive(boolean positive) {
		this.positive = positive;
	}

	public abstract String getMessage();

	/**
	 * Return the type of qc entry. This will be used for display and grouping
	 * in the UI.
	 * 
	 * @return the type of qc entry
	 */
	public abstract QCEntryType getType();

	public enum QCEntryType {
		PROCESSING, COVERAGE
	}
}
