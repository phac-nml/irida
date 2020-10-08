package ca.corefacility.bioinformatics.irida.model.sample;

import java.util.Date;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.corefacility.bioinformatics.irida.model.project.Project;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JsonIgnore
	@NotNull
	private SequencingObject sequencingObject;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", updatable = false)
	@NotNull
	private Date createdDate;

	public QCEntry() {
	}

	public QCEntry(SequencingObject sequencingObject) {
		this.sequencingObject = sequencingObject;
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

	/**
	 * Get the message for the qc entry
	 *
	 * @return the qc entry message to display
	 */
	public abstract String getMessage();

	/**
	 * Return the type of qc entry. This will be used for display and grouping
	 * in the UI.
	 * 
	 * @return the type of qc entry
	 */
	public abstract QCEntryType getType();

	/**
	 * Enhance the {@link QCEntry} with a {@link Project} if required. This way
	 * a {@link QCEntry} can read project's settings to decide whether it's
	 * positive or negative.
	 * 
	 * @param project
	 *            the {@link Project} to read from
	 */
	public abstract void addProjectSettings(Project project);

	/**
	 * Get the {@link QCEntry} status.
	 * 
	 * @return a {@link QCEntryStatus}
	 */
	public abstract QCEntryStatus getStatus();

	/**
	 * The type of {@link QCEntry}
	 */
	public enum QCEntryType {
		PROCESSING, COVERAGE
	}

	/**
	 * Status of a {@link QCEntry}, whether checks are positive, negative, or can't be calculated.
	 */
	public enum QCEntryStatus {
		POSITIVE("POSITIVE"), NEGATIVE("NEGATIVE"), UNAVAILABLE("UNAVAILABLE");

		public String value;

		private QCEntryStatus(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}

	}

	@Override
	public int hashCode() {
		return Objects.hash(getStatus(), getType(), createdDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof QCEntry) {
			QCEntry other = (QCEntry) obj;

			return Objects.equals(getStatus(), other.getStatus()) && Objects.equals(getType(), other.getType())
					&& Objects.equals(createdDate, other.getCreatedDate());
		}
		return false;
	}
}
