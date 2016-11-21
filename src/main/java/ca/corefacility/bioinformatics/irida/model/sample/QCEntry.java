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

/**
 * Abstract class describing quality control entries for a {@link Sample}
 */
@Entity
@Table(name = "qc_entry")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@EntityListeners(AuditingEntityListener.class)
public abstract class QCEntry {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JsonIgnore
	public Sample sample;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	@NotNull
	public Date createdDate;

	public QCEntry() {
	}

	public QCEntry(Sample sample) {
		this.sample = sample;
	}

	public Long getId() {
		return id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * Return the type of qc entry. This will be used for display and grouping
	 * in the UI.
	 * 
	 * @return the type of qc entry
	 */
	public abstract QCEntryType getType();

	public enum QCEntryType {
		PROCESSING
	}
}
