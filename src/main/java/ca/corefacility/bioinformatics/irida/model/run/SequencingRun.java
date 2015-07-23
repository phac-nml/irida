package ca.corefacility.bioinformatics.irida.model.run;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.enums.SequencingRunUploadStatus;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

@Entity
@Table(name = "sequencing_run")
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public abstract class SequencingRun extends IridaResourceSupport implements IridaThing, Comparable<SequencingRun> {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Lob
	private String description;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private final Date createdDate;

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE }, mappedBy = "sequencingRun")
	private Set<SequenceFile> sequenceFiles;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "upload_status")
	private SequencingRunUploadStatus uploadStatus;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "layout_type")
	private LayoutType layoutType;

	protected SequencingRun() {
		createdDate = new Date();
	}
	
	public SequencingRun(final LayoutType layoutType, final SequencingRunUploadStatus uploadStatus) {
		this();
		this.layoutType = layoutType;
		this.uploadStatus = uploadStatus;
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
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
		return Objects.hash(createdDate, description);
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
		return Objects.equals(this.description, other.description) && Objects.equals(this.createdDate, other.createdDate);
	}

	public SequencingRunUploadStatus getUploadStatus() {
		return uploadStatus;
	}

	public LayoutType getLayoutType() {
		return layoutType;
	}

	/**
	 * Get the sequencer type
	 * 
	 * @return Name of the sequencer type
	 */
	public abstract String getSequencerType();

	/**
	 * The type of layout for the run. Single/Paired end
	 * 
	 *
	 */
	public static enum LayoutType {
		SINGLE_END, PAIRED_END
	}

}
